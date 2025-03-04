package io.github.gravitygame.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;

public final class GravityManager {
    private static final float G = 6.67430e-11f; // Realistic gravitational constant
    private static final float MIN_DISTANCE = 1f; // Prevent singularity
    private static final Vector2 tmpForce = new Vector2();
    private static final Vector2 tmpDelta = new Vector2();

    public static void updateGravity(Array<PhysicsBody> bodies) {
        // Clear previous forces
        for(PhysicsBody body : bodies) {
            body.getBody().setLinearVelocity(body.getBody().getLinearVelocity());
        }

        // N-body gravity calculation
        for(int i = 0; i < bodies.size; i++) {
            PhysicsBody a = bodies.get(i);
            Vector2 posA = a.getPosition();
            float massA = a.getMass();

            for(int j = i + 1; j < bodies.size; j++) {
                PhysicsBody b = bodies.get(j);
                Vector2 posB = b.getPosition();
                
                tmpDelta.set(posB).sub(posA);
                float distance = tmpDelta.len();
                
                if(distance < MIN_DISTANCE) continue;
                
                float forceMag = (G * massA * b.getMass()) / (distance * distance);
                tmpForce.set(tmpDelta).nor().scl(forceMag);
                
                a.getBody().applyForceToCenter(tmpForce, true);
                b.getBody().applyForceToCenter(tmpForce.scl(-1), true);
            }
        }
    }
}