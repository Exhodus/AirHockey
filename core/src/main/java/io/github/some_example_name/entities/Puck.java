package io.github.some_example_name.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Puck {
    public static final float RADIUS = 0.18f;
    private static final float MAX_SPEED = 6f;

    public Body body;

    public Puck(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 0.6f;   // rozamiento suave (simula la mesa de aire)
        bodyDef.fixedRotation = true;   // el disco no rota visualmente

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density     = 1.0f;
        fixtureDef.friction    = 0.0f;  // sin fricción: mesa de aire
        fixtureDef.restitution = 0.95f; // rebota casi perfectamente

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    /** Devuelve la posición actual (referencia al vector interno de Box2D). */
    public com.badlogic.gdx.math.Vector2 getPosition() {
        return body.getPosition();
    }

    /** Lanza el disco con una velocidad inicial (en m/s en coordenadas mundo). */
    public void launch(float vx, float vy) {
        body.setLinearVelocity(vx, vy);
    }

    /** Reposiciona el disco y detiene su movimiento (para reinicio de ronda). */
    public void reset(float x, float y) {
        body.setTransform(x, y, 0);
        body.setLinearVelocity(0, 0);
    }

    public void resetToCenter(float worldWidth, float worldHeight) {
        reset(worldWidth / 2f, worldHeight / 2f);
        // Dirección aleatoria hacia un lado u otro
        float vx = (Math.random() > 0.5 ? 1 : -1) * 1.5f;
        float vy = (Math.random() > 0.5 ? 1 : -1) * 2f;
        launch(vx, vy);
    }

    public void clampSpeed() {
        Vector2 vel = body.getLinearVelocity();
        float speed = vel.len();
        if (speed > MAX_SPEED) {
            body.setLinearVelocity(vel.scl(MAX_SPEED / speed));
        }
    }
}
