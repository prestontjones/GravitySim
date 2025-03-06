package io.github.gravitygame.entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsBody {
    private final Body body;
    private final BodyState currentState;
    
        public PhysicsBody(Body box2DBody, BodyState initialState) {
            this.body = box2DBody;
            this.currentState = initialState;
        }
    
        public void render(ShapeRenderer shapeRenderer) {
            Vector2 position = body.getPosition();
            float radius = currentState.getRadius();
            Color color = currentState.getColor();
    
            // Original rendering logic
            shapeRenderer.setColor(color);
            shapeRenderer.circle(position.x, position.y, radius);
        }
    
        public BodyState getCurrentState() {
            return new BodyState(
                body.getPosition(),
                body.getLinearVelocity(),
                currentState.getRadius(),
                currentState.getMass(),
                currentState.getColor(),
                currentState.getId()
            );
        }

    public PhysicsBody(PhysicsBody other) {
        this.body = other.body; // Keep reference to Box2D body (optional)
        this.currentState = other.getCurrentState(); // Copy immutable state
    }

    // Delegate methods
    public float getMass() { return currentState.getMass(); }
    public Color getColor() { return currentState.getColor(); }
    public UUID getId() { return currentState.getId(); }
    public Body getBody() { return body; }
    public Vector2 getVelocity() { return body.getLinearVelocity(); }
    public Vector2 getPosition() { return body.getPosition(); }
    public float getRadius() { return currentState.getRadius(); }
    
}