package io.github.some_example_name;

import com.badlogic.gdx.physics.box2d.*;

public class PhysicsFactory {

    public static final float WALL_THICKNESS = 0.3f;
    public static final float GOAL_WIDTH_RATIO = 0.4f; // hueco = 40% del ancho

    public static void createWalls(World world, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.7f;
        fixtureDef.friction    = 0.0f;
        fixtureDef.density     = 0f;

        float hw = WALL_THICKNESS / 2f;

        // --- Paredes laterales COMPLETAS (sin hueco) ---
        // Izquierda
        bodyDef.position.set(hw, height / 2f);
        Body leftWall = world.createBody(bodyDef);
        shape.setAsBox(hw, height / 2f);
        leftWall.createFixture(fixtureDef);

        // Derecha
        bodyDef.position.set(width - hw, height / 2f);
        Body rightWall = world.createBody(bodyDef);
        shape.setAsBox(hw, height / 2f);
        rightWall.createFixture(fixtureDef);

        // --- Paredes superior e inferior CON hueco (porterías) ---
        float goalWidth = width * GOAL_WIDTH_RATIO;         // ancho del hueco
        float sideW     = (width - goalWidth) / 2f;         // cada segmento lateral

        // Inferior — segmento izquierdo
        bodyDef.position.set(sideW / 2f, hw);
        Body bottomLeft = world.createBody(bodyDef);
        shape.setAsBox(sideW / 2f, hw);
        bottomLeft.createFixture(fixtureDef);

        // Inferior — segmento derecho
        bodyDef.position.set(width - sideW / 2f, hw);
        Body bottomRight = world.createBody(bodyDef);
        shape.setAsBox(sideW / 2f, hw);
        bottomRight.createFixture(fixtureDef);

        // Superior — segmento izquierdo
        bodyDef.position.set(sideW / 2f, height - hw);
        Body topLeft = world.createBody(bodyDef);
        shape.setAsBox(sideW / 2f, hw);
        topLeft.createFixture(fixtureDef);

        // Superior — segmento derecho
        bodyDef.position.set(width - sideW / 2f, height - hw);
        Body topRight = world.createBody(bodyDef);
        shape.setAsBox(sideW / 2f, hw);
        topRight.createFixture(fixtureDef);

        shape.dispose();
    }
}
