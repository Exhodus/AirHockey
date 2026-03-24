package io.github.some_example_name;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import io.github.some_example_name.entities.Paddle;

public class PlayerInputHandler extends InputAdapter {
    private final Paddle paddle;
    private final OrthographicCamera camera;
    private final float worldHeight;

    private boolean touching = false;
    private float targetX, targetY;

    public PlayerInputHandler(Paddle paddle, OrthographicCamera camera, float worldHeight) {
        this.paddle      = paddle;
        this.camera      = camera;
        this.worldHeight = worldHeight;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        // Solo responde si el toque es en la mitad INFERIOR de la mesa
        if (worldPos.y < worldHeight / 2f) {
            touching = true;
            targetX  = worldPos.x;
            targetY  = worldPos.y;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!touching) return false;
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        // Seguimos restringiendo a la mitad inferior
        if (worldPos.y < worldHeight / 2f) {
            targetX = worldPos.x;
            targetY = worldPos.y;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touching = false;
        return true;
    }

    /**
     * Llamar cada frame desde GameScreen para mover la pala si hay toque activo.
     * Aplica límites para que la pala no salga de su mitad.
     */
    public void update(float delta, float worldWidth, float paddleRadius) {
        if (!touching) {
            paddle.body.setLinearVelocity(0, 0); // detener si no hay toque
            return;
        }

        // Clamp: limitar la pala dentro de los bordes
        float minX = paddleRadius;
        float maxX = worldWidth - paddleRadius;
        float minY = paddleRadius;
        float maxY = worldHeight / 2f - paddleRadius; // no pasa la línea central

        float clampedX = Math.max(minX, Math.min(maxX, targetX));
        float clampedY = Math.max(minY, Math.min(maxY, targetY));

        paddle.moveTo(clampedX, clampedY, delta);
    }

    public boolean isTouching() { return touching; }
}
