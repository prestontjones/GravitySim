package io.github.gravitygame.physics;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.gravitygame.entities.PhysicsBody;

public class GravityManager {
    private static final float GRAVITATIONAL_CONSTANT = 100f; // Adjust as needed

    public static void updateGravity(List<PhysicsBody> bodies) {
        for (int i = 0; i < bodies.size(); i++) {
            PhysicsBody bodyA = bodies.get(i);
            Body bodyAPhysics = bodyA.getBody();

            for (int j = i + 1; j < bodies.size(); j++) {
                PhysicsBody bodyB = bodies.get(j);
                Body bodyBPhysics = bodyB.getBody();

                // Calculate the distance between the two bodies
                Vector2 positionA = bodyAPhysics.getPosition();
                Vector2 positionB = bodyBPhysics.getPosition();
                Vector2 delta = positionB.cpy().sub(positionA);
                float distanceSquared = delta.len2();

                // Avoid division by zero (if bodies are too close)
                if (distanceSquared < 0.1f) continue;

                // Calculate the gravitational force
                float forceMagnitude = GRAVITATIONAL_CONSTANT * bodyA.getMass() * bodyB.getMass() / distanceSquared;
                Vector2 force = delta.nor().scl(forceMagnitude);

                // Apply the force to both bodies
                bodyAPhysics.applyForceToCenter(force, true);
                bodyBPhysics.applyForceToCenter(force.scl(-1), true);
            }
        }
    }
}