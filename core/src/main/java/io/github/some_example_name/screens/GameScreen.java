package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.some_example_name.AiController;
import io.github.some_example_name.AirHockeyGame;
import io.github.some_example_name.PhysicsFactory;
import io.github.some_example_name.PlayerInputHandler;
import io.github.some_example_name.ScoreManager;
import io.github.some_example_name.entities.Paddle;
import io.github.some_example_name.entities.Puck;

public class GameScreen implements Screen {

    public static final float WORLD_WIDTH  = 3f;
    public static final float WORLD_HEIGHT = 5f;

    private final AirHockeyGame game;

    private boolean paused   = false;
    private boolean gameOver = false;
    private String  gameOverMessage = "";

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;

    private Puck   puck;
    private Paddle playerPaddle;
    private Paddle aiPaddle;

    private PlayerInputHandler inputHandler;
    private AiController       aiController;

    private BitmapFont font;
    private SpriteBatch batch;
    private ScoreManager scoreManager;

    public GameScreen(AirHockeyGame game) {
        this.game = game;

        // 1. UI
        scoreManager = new ScoreManager(7);
        font  = new BitmapFont();
        font.getData().setScale(2.5f);
        batch = new SpriteBatch();

        // 2. Mundo y paredes
        world = new World(new Vector2(0, 0), true);
        PhysicsFactory.createWalls(world, WORLD_WIDTH, WORLD_HEIGHT);

        // 3. Entidades
        puck         = new Puck(world, WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        puck.launch(0.5f, -2f);
        playerPaddle = new Paddle(world, WORLD_WIDTH / 2f, WORLD_HEIGHT * 0.18f, true);
        aiPaddle     = new Paddle(world, WORLD_WIDTH / 2f, WORLD_HEIGHT * 0.82f, true);
        aiController = new AiController(aiPaddle, puck, WORLD_WIDTH, WORLD_HEIGHT);

        // 4. Cámara
        camera   = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // 5. Input
        inputHandler = new PlayerInputHandler(playerPaddle, camera, WORLD_HEIGHT);
        setupInput();

        // 6. Renderers
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
    }

    // ─── Input ────────────────────────────────────────────────────────────────

    private void setupInput() {
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                float tx = sx;
                float ty = Gdx.graphics.getHeight() - sy; // invertir Y

                if (gameOver) {
                    restartGame();
                    return true;
                }

                // Zona del botón pausa (esquina superior derecha)
                if (tx > Gdx.graphics.getWidth() - 80f
                    && ty > Gdx.graphics.getHeight() - 80f) {
                    paused = !paused;
                    return true;
                }

                if (paused) {
                    // Mitad superior → continuar | mitad inferior → menú
                    if (ty > Gdx.graphics.getHeight() / 2f) {
                        paused = false;
                    } else {
                        game.setScreen(new MenuScreen(game));
                        dispose();
                    }
                    return true;
                }

                return inputHandler.touchDown(sx, sy, p, b);
            }

            @Override
            public boolean touchDragged(int sx, int sy, int p) {
                if (paused || gameOver) return false;
                return inputHandler.touchDragged(sx, sy, p);
            }

            @Override
            public boolean touchUp(int sx, int sy, int p, int b) {
                return inputHandler.touchUp(sx, sy, p, b);
            }
        });
    }

    // ─── Render ───────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        // Lógica solo si el juego está activo
        if (!gameOver && !paused) {
            inputHandler.update(delta, WORLD_WIDTH, Paddle.RADIUS);
            aiController.update(delta);
            world.step(1 / 60f, 6, 2);
            puck.clampSpeed();
            checkGoal();
        }

        camera.update();
        drawWorld();   // ShapeRenderer

        batch.begin();
        drawHUD();
        if (paused)   drawPauseOverlay();
        if (gameOver) drawGameOverOverlay();
        batch.end();
    }

    private void drawWorld() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fondo mesa
        shapeRenderer.setColor(0.1f, 0.45f, 0.15f, 1f);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Línea central
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(0, WORLD_HEIGHT / 2f, WORLD_WIDTH, WORLD_HEIGHT / 2f, 0.03f);

        // Pala jugador (cyan)
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(playerPaddle.getPosition().x,
            playerPaddle.getPosition().y, Paddle.RADIUS, 32);

        // Pala IA (rojo)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(aiPaddle.getPosition().x,
            aiPaddle.getPosition().y, Paddle.RADIUS, 32);

        // Disco (blanco)
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(puck.getPosition().x,
            puck.getPosition().y, Puck.RADIUS, 24);

        // Bordes
        shapeRenderer.setColor(Color.WHITE);
        float borde = 0.06f;
        float goalWidth = WORLD_WIDTH * PhysicsFactory.GOAL_WIDTH_RATIO;
        float sideW     = (WORLD_WIDTH - goalWidth) / 2f;

        shapeRenderer.rectLine(0, 0, 0, WORLD_HEIGHT, borde);
        shapeRenderer.rectLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, borde);
        shapeRenderer.rectLine(0, 0, sideW, 0, borde);
        shapeRenderer.rectLine(WORLD_WIDTH - sideW, 0, WORLD_WIDTH, 0, borde);
        shapeRenderer.rectLine(0, WORLD_HEIGHT, sideW, WORLD_HEIGHT, borde);
        shapeRenderer.rectLine(WORLD_WIDTH - sideW, WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT, borde);

        shapeRenderer.end();
    }

    private void drawHUD() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        // Marcador centrado
        font.getData().setScale(2.5f);
        font.setColor(Color.WHITE);
        font.draw(batch,
            scoreManager.getAiScore() + " : " + scoreManager.getPlayerScore(),
            screenW / 2f - 40, screenH / 2f + 20);

        // Botón pausa (esquina superior derecha)
        font.draw(batch, "||", screenW - 60f, screenH - 10f);
    }

    private void drawPauseOverlay() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        font.getData().setScale(3.5f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "PAUSA", screenW / 2f - 60f, screenH / 2f + 80f);

        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Toca arriba: continuar",  screenW / 2f - 130f, screenH / 2f + 20f);
        font.draw(batch, "Toca abajo: menu",         screenW / 2f - 100f, screenH / 2f - 50f);

        font.getData().setScale(2.5f); // restaurar
    }

    private void drawGameOverOverlay() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        font.getData().setScale(3.5f);
        font.setColor(Color.YELLOW);
        font.draw(batch, gameOverMessage, screenW / 2f - 80f, screenH / 2f + 80f);

        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Toca para reiniciar", screenW / 2f - 110f, screenH / 2f + 20f);

        font.getData().setScale(2.5f); // restaurar
    }

    // ─── Lógica de juego ──────────────────────────────────────────────────────

    private void checkGoal() {
        float puckY = puck.getPosition().y;
        if (puckY > WORLD_HEIGHT) {
            scoreManager.playerScores();
            if (scoreManager.playerWins()) { showGameOver("¡Ganaste!"); return; }
            resetRound();
        } else if (puckY < 0) {
            scoreManager.aiScores();
            if (scoreManager.aiWins()) { showGameOver("IA gana"); return; }
            resetRound();
        }
    }

    private void showGameOver(String message) {
        gameOver        = true;
        gameOverMessage = message;
        puck.body.setLinearVelocity(0, 0);
        playerPaddle.body.setLinearVelocity(0, 0);
        aiPaddle.body.setLinearVelocity(0, 0);
    }

    private void resetRound() {
        puck.reset(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        float vx = (Math.random() > 0.5 ? 1 : -1) * 1.0f;
        float vy = (Math.random() > 0.5 ? 1 : -1) * 1.5f;
        puck.launch(vx, vy);
        playerPaddle.body.setTransform(WORLD_WIDTH / 2f, WORLD_HEIGHT * 0.18f, 0);
        playerPaddle.body.setLinearVelocity(0, 0);
        aiPaddle.body.setTransform(WORLD_WIDTH / 2f, WORLD_HEIGHT * 0.82f, 0);
        aiPaddle.body.setLinearVelocity(0, 0);
    }

    private void restartGame() {
        gameOver = false;
        gameOverMessage = "";
        scoreManager.reset();
        resetRound();
    }

    // ─── Ciclo de vida ────────────────────────────────────────────────────────

    @Override public void show()   { setupInput(); } // re-registrar al volver de pausa
    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        font.dispose();
        batch.dispose();
    }
}
