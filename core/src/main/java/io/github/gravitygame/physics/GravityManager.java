package io.github.gravitygame.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;

public final class GravityManager {
    private static final float G = 6.67430f; // Realistic gravitational constant
    private static final float SOFTENING = 0.1f; // Softening parameter to prevent singularity
    private static final Vector2 tmpForce = new Vector2();
    private static final Vector2 tmpDelta = new Vector2();

    public static void updateGravity(Array<PhysicsBody> bodies) {
        // N-body gravity calculation using gravitational softening.
        for (int i = 0; i < bodies.size; i++) {
            PhysicsBody a = bodies.get(i);
            Vector2 posA = a.getPosition();
            float massA = a.getMass();

            for (int j = i + 1; j < bodies.size; j++) {
                PhysicsBody b = bodies.get(j);
                Vector2 posB = b.getPosition();

                tmpDelta.set(posB).sub(posA);
                float distance = tmpDelta.len();
                
                // Calculate force magnitude with softening to avoid infinite forces
                float forceMag = (G * massA * b.getMass()) / (distance * distance + SOFTENING * SOFTENING);
                
                tmpForce.set(tmpDelta).nor().scl(forceMag);
                
                // Apply equal and opposite forces to the bodies
                a.getBody().applyForceToCenter(tmpForce, true);
                // Reuse tmpForce for the opposite direction
                b.getBody().applyForceToCenter(tmpForce.scl(-1), true);
            }
        }
    }
}
