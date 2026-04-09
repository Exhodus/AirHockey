package io.github.some_example_name.screens;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.some_example_name.AirHockeyGame;

public class MenuScreen implements Screen {

    private final AirHockeyGame game;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;

    // Área táctil del botón Play (en píxeles de pantalla)
    private final float btnW = 300f, btnH = 80f;
    private float btnPlayX, btnPlayY;
    private float btnExitX, btnExitY;

    public MenuScreen(AirHockeyGame game) {
        this.game   = game;
        batch       = new SpriteBatch();

        titleFont   = new BitmapFont();
        titleFont.getData().setScale(5f);

        buttonFont  = new BitmapFont();
        buttonFont.getData().setScale(3f);
    }

    @Override
    public void show() {
        // Centrar botones al mostrarse (por si cambia el tamaño)
        float cx = Gdx.graphics.getWidth()  / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;
        btnPlayX = cx - btnW / 2f;
        btnPlayY = cy - 20f;
        btnExitX = cx - btnW / 2f;
        btnExitY = cy - 130f;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.08f, 0.12f, 1f);

        // Detectar toque
        if (Gdx.input.justTouched()) {
            float tx = Gdx.input.getX();
            float ty = Gdx.graphics.getHeight() - Gdx.input.getY(); // invertir Y
            if (isTouching(tx, ty, btnPlayX, btnPlayY)) {
                game.setScreen(new GameScreen(game));
            } else if (isTouching(tx, ty, btnExitX, btnExitY)) {
                Gdx.app.exit();
            }
        }

        batch.begin();

        // Título
        titleFont.setColor(Color.CYAN);
        titleFont.draw(batch, "AIR HOCKEY",
            Gdx.graphics.getWidth() / 2f - 160f,
            Gdx.graphics.getHeight() * 0.75f);

        // Botón PLAY
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(batch, "▶  JUGAR",
            btnPlayX + 40f, btnPlayY + 55f);

        // Botón SALIR
        buttonFont.setColor(new Color(0.8f, 0.3f, 0.3f, 1f));
        buttonFont.draw(batch, "✕  SALIR",
            btnExitX + 40f, btnExitY + 55f);

        batch.end();
    }

    private boolean isTouching(float tx, float ty, float bx, float by) {
        return tx >= bx && tx <= bx + btnW && ty >= by && ty <= by + btnH;
    }

    @Override public void resize(int w, int h) { show(); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() {
        batch.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
