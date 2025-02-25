package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsBody {
    private final Body body;
    private final float radius;
    private final Color color;
    private final float mass;

    public PhysicsBody(World world, float x, float y, float radius, float mass, Color color) {
        this.radius = radius;
        this.color = color;
        this.mass = mass;

        // Create a dynamic body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Change to DynamicBody for gravity
        bodyDef.position.set(x, y);

        // Create a body in the world
        body = world.createBody(bodyDef);

        // Create a circle shape
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);

        // Create a fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = mass / (radius * radius * (float) Math.PI); // Density based on mass and area
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0;

        // Attach the fixture to the body
        body.createFixture(fixtureDef);

        // Clean up the shape
        circle.dispose();
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.circle(body.getPosition().x, body.getPosition().y, radius);
    }

    public Body getBody() {
        return body;
    }

    public float getMass() {
        return mass;
    }
}
