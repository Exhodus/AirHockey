package io.github.some_example_name.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Paddle {
    public static final float RADIUS = 0.28f;


    public Body body;

    /**
     * @param isKinematic  true = controlado por input/IA (no afectado por fuerzas),
     *                     false = dinámico (útil si quieres físicas más realistas en el futuro)
     */
    public Paddle(World world, float x, float y, boolean isKinematic) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isKinematic
            ? BodyDef.BodyType.KinematicBody   // se mueve por velocidad, no por fuerzas
            : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density     = 5.0f;   // pala más pesada que el disco
        fixtureDef.friction    = 0.0f;
        fixtureDef.restitution = 0.7f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    /**
     * Mueve la pala hacia una posición destino en este frame.
     * Calcula la velocidad necesaria para llegar en un paso de física (1/60s).
     */
    public void moveTo(float targetX, float targetY, float delta) {
        Vector2 pos = body.getPosition();
        float vx = (targetX - pos.x) / delta;
        float vy = (targetY - pos.y) / delta;
        body.setLinearVelocity(vx, vy);
    }
}
