package io.github.some_example_name;

import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.entities.Paddle;
import io.github.some_example_name.entities.Puck;

public class AiController {
    private static final float MAX_SPEED     = 3.5f;  // velocidad máxima de la IA
    private static final float DEFENSE_X     = 0f;    // se calcula en update como centro
    private static final float ATTACK_THRESHOLD = 0.5f; // margen para considerar "en posición"

    private final Paddle paddle;
    private final Puck puck;
    private final float  worldWidth;
    private final float  worldHeight;

    public AiController(Paddle paddle, Puck puck, float worldWidth, float worldHeight) {
        this.paddle      = paddle;
        this.puck        = puck;
        this.worldWidth  = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(float delta) {
        Vector2 puckPos   = puck.getPosition();
        Vector2 paddlePos = paddle.getPosition();

        float targetX, targetY;

        if (puckPos.y > worldHeight / 2f) {
            // MODO ATAQUE: el disco está en la mitad de la IA → perseguirlo
            targetX = puckPos.x;
            targetY = puckPos.y;
        } else {
            // MODO DEFENSA: el disco está abajo → volver al centro de su portería
            targetX = worldWidth / 2f;
            targetY = worldHeight * 0.82f;
        }

        // Clamp: la IA no puede salir de su mitad ni de los bordes
        float radius = paddle.body.getFixtureList().first().getShape().getRadius();
        targetX = Math.max(radius, Math.min(worldWidth - radius, targetX));
        targetY = Math.max(worldHeight / 2f + radius, Math.min(worldHeight - radius, targetY));

        // Calcular velocidad limitada hacia el objetivo
        float dx = targetX - paddlePos.x;
        float dy = targetY - paddlePos.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > ATTACK_THRESHOLD * 0.1f) {
            float speed = Math.min(MAX_SPEED, dist / delta);
            float vx = (dx / dist) * speed;
            float vy = (dy / dist) * speed;
            paddle.body.setLinearVelocity(vx, vy);
        } else {
            paddle.body.setLinearVelocity(0, 0);
        }
    }
}
