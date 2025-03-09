package io.github.gravitygame.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Calculates estimated trajectories for bodies in the physics simulation
 * without requiring full state captures.
 */
public class TrajectoryEstimator {
    // Configuration
    private static final int MAX_PREDICTION_STEPS = 1000; // Can predict much further ahead
    private static final float PREDICTION_STEP_TIME = 0.05f; // Larger time steps for faster simulation
    private static final float SKIP_INTERVAL = 5; // Only record every Nth position to save memory
    
    // Storage for predicted trajectories
    private final Map<UUID, Array<Vector2>> trajectories = new HashMap<>();
    private boolean needsUpdate = true;
    private Array<PhysicsBody> lastBodies = new Array<>();
    
    // Number of points to generate per body trajectory
    private int trajectoryPoints = 200; // Configurable, higher = longer trajectories
    
    /**
     * Updates the trajectory predictions for all bodies in the simulation.
     * This performs a simplified physics calculation to predict future positions.
     *
     * @param bodies The current simulation bodies to predict trajectories for
     */
    public void updateTrajectories(Array<PhysicsBody> bodies) {
        // Skip if no bodies or no update needed
        if (bodies.size == 0 || (!needsUpdate && bodiesUnchanged(bodies))) {
            return;
        }
        
        // Clear existing trajectories
        trajectories.clear();
        
        // Copy bodies to avoid modifying the original simulation
        Array<PhysicsBody> predictedBodies = new Array<>(bodies.size);
        for (PhysicsBody body : bodies) {
            predictedBodies.add(new PhysicsBody(body)); // Create a copy
            // Initialize trajectory array for this body
            trajectories.put(body.getId(), new Array<Vector2>(trajectoryPoints));
        }
        
        // Calculate trajectories using simplified physics
        for (int step = 0; step < MAX_PREDICTION_STEPS; step++) {
            // Apply gravity between bodies
            applyGravityForces(predictedBodies);
            
            // Update positions based on velocities
            for (PhysicsBody body : predictedBodies) {
                Vector2 velocity = body.getVelocity();
                Vector2 position = body.getPosition();
                
                // Update position based on velocity
                position.add(
                    velocity.x * PREDICTION_STEP_TIME,
                    velocity.y * PREDICTION_STEP_TIME
                );
                
                // Store position in trajectory if it's a recording step
                if (step % SKIP_INTERVAL == 0 && step / SKIP_INTERVAL < trajectoryPoints) {
                    UUID id = body.getId();
                    if (trajectories.containsKey(id)) {
                        trajectories.get(id).add(position.cpy());
                    }
                }
            }
            
            // Stop if we've collected enough points
            if (step / SKIP_INTERVAL >= trajectoryPoints) {
                break;
            }
        }
        
        // Remember the bodies we calculated for
        lastBodies.clear();
        lastBodies.addAll(bodies);
        needsUpdate = false;
    }
    
    /**
     * Simplified version of gravity calculations. This should mirror your
     * GravityManager's core algorithm but can be simplified for prediction.
     */
    private void applyGravityForces(Array<PhysicsBody> bodies) {
        final float G = 6.67f; // Gravitational constant (adjust to match your simulation)
        
        // Apply forces between each pair of bodies
        for (int i = 0; i < bodies.size; i++) {
            PhysicsBody bodyA = bodies.get(i);
            Vector2 posA = bodyA.getPosition();
            float massA = bodyA.getMass();
            
            for (int j = i + 1; j < bodies.size; j++) {
                PhysicsBody bodyB = bodies.get(j);
                Vector2 posB = bodyB.getPosition();
                float massB = bodyB.getMass();
                
                // Calculate direction and distance
                Vector2 direction = new Vector2(posB).sub(posA);
                float distance = direction.len();
                
                // Avoid division by zero and limit extreme gravity at very close distances
                if (distance < 0.1f) {
                    distance = 0.1f;
                }
                
                // Calculate force magnitude (F = G * m1 * m2 / r^2)
                float forceMagnitude = G * massA * massB / (distance * distance);
                
                // Normalize direction and scale by force
                direction.nor().scl(forceMagnitude);
                
                // Apply forces to both bodies (equal and opposite)
                Vector2 forceOnA = new Vector2(direction);
                Vector2 forceOnB = new Vector2(direction).scl(-1);
                
                // F = ma, so a = F/m
                bodyA.getVelocity().add(
                    forceOnA.x / massA * PREDICTION_STEP_TIME,
                    forceOnA.y / massA * PREDICTION_STEP_TIME
                );
                
                bodyB.getVelocity().add(
                    forceOnB.x / massB * PREDICTION_STEP_TIME,
                    forceOnB.y / massB * PREDICTION_STEP_TIME
                );
            }
        }
    }
    
    /**
     * Returns the predicted trajectory points for a specific body.
     */
    public Array<Vector2> getTrajectoryForBody(UUID bodyId) {
        return trajectories.getOrDefault(bodyId, new Array<>());
    }
    
    /**
     * Returns all trajectory data.
     */
    public Map<UUID, Array<Vector2>> getAllTrajectories() {
        return trajectories;
    }
    
    /**
     * Force a recalculation on next update.
     */
    public void invalidate() {
        needsUpdate = true;
    }
    
    /**
     * Sets the number of trajectory points to generate.
     * Higher values = longer predictions.
     */
    public void setTrajectoryPoints(int points) {
        this.trajectoryPoints = points;
        invalidate();
    }
    
    /**
     * Determine if the bodies have changed since last calculation.
     */
    private boolean bodiesUnchanged(Array<PhysicsBody> bodies) {
        if (bodies.size != lastBodies.size) {
            return false;
        }
        
        // Simple check to see if all body IDs match (not checking positions/velocities)
        for (PhysicsBody body : bodies) {
            boolean found = false;
            for (PhysicsBody lastBody : lastBodies) {
                if (body.getId().equals(lastBody.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
}