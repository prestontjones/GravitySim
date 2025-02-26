package io.github.gravitygame.entities;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsBody {
    private final Body body;
    private final World world;
    private final float radius;
    private final Color color;
    private final float mass;
    private final UUID id;

    public PhysicsBody(World world, float x, float y, float radius, float mass, Color color) {
        this.world = world;
        this.radius = radius;
        this.color = color;
        this.mass = mass;
        this.id = UUID.randomUUID();

        // Create a dynamic body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Change to DynamicBody for gravity
        bodyDef.position.set(x, y);

        // Create a body in the world
        body = world.createBody(bodyDef);
        body.setUserData(this.id);

        // Create a circle shape
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);

        // Create a fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = mass / (radius * radius * (float) Math.PI); // Density based on mass and area
        fixtureDef.friction = 0;
        fixtureDef.restitution = .5f;

        // Attach the fixture to the body
        body.createFixture(fixtureDef);

        // Clean up the shape
        circle.dispose();
    }

    public void render(ShapeRenderer shapeRenderer) {
        float x = body.getPosition().x;
        float y = body.getPosition().y;
        
        // Draw a thicker white outline
        Gdx.gl.glLineWidth(4f);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(x, y, radius);
        
        // Draw a colored outline with a slightly thinner line
        Gdx.gl.glLineWidth(2f);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, y, radius);
        
        // Reset the line width to default (usually 1)
        Gdx.gl.glLineWidth(1f);
    }

    public Body getBody() {
        return body;
    }

    public Color getColor() {
        return color;
    }

    public World getWorld() {
        return world;
    }

    public UUID getId() { return id; }

    public float getMass() {
        return mass;
    }
}
