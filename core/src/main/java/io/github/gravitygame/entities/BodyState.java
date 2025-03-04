package io.github.gravitygame.entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

// Immutable state snapshot
public final class BodyState {
    private static final float PHYSICS_STEP = 1/60f;
    private final Vector2 position;
    private final Vector2 velocity;
    private final float radius;
    private final float mass;
    private final Color color;
    private final UUID id;

    public BodyState(Vector2 position, Vector2 velocity, float radius, 
                    float mass, Color color, UUID id) {
        this.position = position.cpy();
        this.velocity = velocity.cpy();
        this.radius = radius;
        this.mass = mass;
        this.color = color;
        this.id = id;
    }

    public void applyForce(Vector2 force) {
        Vector2 acceleration = force.scl(1f/mass);
        velocity.add(acceleration.scl(PHYSICS_STEP));
    }

    public void integrate(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
    }

    public BodyState copy() {
        return new BodyState(position.cpy(), velocity.cpy(), radius, mass, new Color(color), id);
    }

    // Getters (no setters - immutable)
    public Vector2 getPosition() { return position.cpy(); }
    public Vector2 getVelocity() { return velocity.cpy(); }
    public float getRadius() { return radius; }
    public float getMass() { return mass; }
    public Color getColor() { return color; }
    public UUID getId() { return id; }
}