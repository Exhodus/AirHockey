package io.github.some_example_name.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import io.github.some_example_name.entities.Paddle;
import io.github.some_example_name.entities.Puck;

// imports nuevos
import com.badlogic.gdx.Gdx;


public class GameScreen implements Screen {

    public static final float WORLD_WIDTH  = 3f;
    public static final float WORLD_HEIGHT = 5f;

    private final AirHockeyGame game;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;

    // --- Entidades ---
    private Puck   puck;
    private Paddle playerPaddle;
    private PlayerInputHandler inputHandler;
    private AiController aiController;


    private Paddle aiPaddle;

    public GameScreen(AirHockeyGame game) {
        this.game = game;

        // 1. Mundo y paredes
        world = new World(new Vector2(0, 0), true);
        PhysicsFactory.createWalls(world, WORLD_WIDTH, WORLD_HEIGHT);

        // 2. Entidades
        puck = new Puck(world, WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        puck.launch(0.5f, -2f);
        playerPaddle = new Paddle(world, WORLD_WIDTH / 2f, WORLD_HEIGHT * 0.18f, true);
        aiPaddle     = new Paddle(world, WORLD_WIDTH / 2f, WORLD_HEIGHT * 0.82f, true);
        aiPaddle.body.setLinearVelocity(0, 0);
        aiController = new AiController(aiPaddle, puck, WORLD_WIDTH, WORLD_HEIGHT);

        // 3. Cámara PRIMERO
        camera   = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // 4. Input DESPUÉS de camera (sin redeclarar el tipo)
        inputHandler = new PlayerInputHandler(playerPaddle, camera, WORLD_HEIGHT);
        Gdx.input.setInputProcessor(inputHandler);

        // 5. Renderers
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        inputHandler.update(delta, WORLD_WIDTH, Paddle.RADIUS);
        aiController.update(delta);

        world.step(1 / 60f, 6, 2);
        puck.clampSpeed();
        camera.update();

        // --- Dibujar entidades con ShapeRenderer ---
        shapeRenderer.setProjectionMatrix(camera.combined); // ¡imprescindible!
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Mesa (fondo verde oscuro)
        shapeRenderer.setColor(0.1f, 0.45f, 0.15f, 1f);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Línea central
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(0, WORLD_HEIGHT / 2f, WORLD_WIDTH, WORLD_HEIGHT / 2f, 0.03f);

        // Pala del jugador (azul)
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.circle(
            playerPaddle.getPosition().x,
            playerPaddle.getPosition().y,
            Paddle.RADIUS, 32
        );

        // Pala de la IA (rojo)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(
            aiPaddle.getPosition().x,
            aiPaddle.getPosition().y,
            Paddle.RADIUS, 32
        );

        // Disco (blanco)
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(
            puck.getPosition().x,
            puck.getPosition().y,
            Puck.RADIUS, 24
        );

        // Borde de la mesa
        // Borde de la mesa
        shapeRenderer.setColor(Color.WHITE);
        float borde = 0.06f;

        float goalWidth = WORLD_WIDTH * PhysicsFactory.GOAL_WIDTH_RATIO;
        float sideW = (WORLD_WIDTH - goalWidth) / 2f;

// Laterales completos
        shapeRenderer.rectLine(0, 0, 0, WORLD_HEIGHT, borde);                         // izquierdo
        shapeRenderer.rectLine(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT, borde);     // derecho

// Inferior — dos segmentos con hueco central
        shapeRenderer.rectLine(0, 0, sideW, 0, borde);
        shapeRenderer.rectLine(WORLD_WIDTH - sideW, 0, WORLD_WIDTH, 0, borde);

// Superior — dos segmentos con hueco central
        shapeRenderer.rectLine(0, WORLD_HEIGHT, sideW, WORLD_HEIGHT, borde);
        shapeRenderer.rectLine(WORLD_WIDTH - sideW, WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT, borde);

        shapeRenderer.end();

        // Debug Box2D (opcional, puedes comentarlo cuando quieras)
        //debugRenderer.render(world, camera.combined);
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
    }
}
