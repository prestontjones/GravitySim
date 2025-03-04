package io.github.gravitygame.entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsBody {
    private final Body body;
    private BodyState currentState;

    public PhysicsBody(Body box2DBody, BodyState initialState) {
        this.body = box2DBody;
        this.currentState = initialState;
        syncBox2DState();
    }

    public void updateState(BodyState newState) {
        this.currentState = newState;
        syncBox2DState();
    }

    private void syncBox2DState() {
        body.setTransform(currentState.getPosition(), 0);
        body.setLinearVelocity(currentState.getVelocity());
    }

    public void render(ShapeRenderer shapeRenderer) {
        Vector2 position = currentState.getPosition();
        float radius = currentState.getRadius();
        Color color = currentState.getColor();

        // Original rendering logic
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

    // Delegate methods
    public float getMass() { return currentState.getMass(); }
    public Color getColor() { return currentState.getColor(); }
    public UUID getId() { return currentState.getId(); }
    public Body getBody() { return body; }
    public Vector2 getVelocity() { return currentState.getVelocity(); }
    public Vector2 getPosition() { return currentState.getPosition(); }
    public float getRadius() { return currentState.getRadius(); }
    
}