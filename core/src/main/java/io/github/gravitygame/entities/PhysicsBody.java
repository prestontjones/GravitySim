package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsBody {
    private final Body body;
    private final Sprite sprite;

    public PhysicsBody(World world, float x, float y, float radius, Texture texture) {
        // Create a dynamic body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // Create a body in the world
        body = world.createBody(bodyDef);

        // Create a circle shape
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);

        // Create a fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;

        // Attach the fixture to the body
        body.createFixture(fixtureDef);

        // Clean up the shape
        circle.dispose();

        // Create a sprite for rendering
        sprite = new Sprite(texture);
        sprite.setSize(radius * 2, radius * 2);
        sprite.setOrigin(radius, radius);
    }

    public void update() {
        // Update the sprite's position and rotation based on the body
        sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }
}
