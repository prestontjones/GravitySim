package io.github.gravitygame.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;

public final class GravityManager {
    // Base gravitational constant - higher values create more dramatic motion
    private static final float G = 3000.0f;
    
    // Minimum distance to prevent extreme forces
    private static final float MIN_DISTANCE = 15.0f;
    
    // Dance factor - enhances perpendicular motion to create orbital variety
    private static final float DANCE_FACTOR = 0.4f;
    
    // Velocity boost - occasionally increases energy in the system
    private static final float ENERGY_BOOST = 1.005f;
    
    // Reusable vectors to avoid garbage collection
    private static final Vector2 delta = new Vector2();
    private static final Vector2 perp = new Vector2();
    private static final Vector2 force = new Vector2();
    
    /**
     * Apply gravity forces between all physics bodies with "dance" enhancements
     */
    public static void updateGravity(Array<PhysicsBody> bodies) {
        // Wake all bodies
        for (int i = 0; i < bodies.size; i++) {
            PhysicsBody body = bodies.get(i);
            body.getBody().setAwake(true);
            
            // Occasional tiny energy boost to prevent orbital decay
            // This keeps the system lively over longer simulations
            Vector2 vel = body.getBody().getLinearVelocity();
            if (vel.len2() > 0.5f && Math.random() < 0.02) {
                vel.scl(ENERGY_BOOST);
                body.getBody().setLinearVelocity(vel);
            }
        }
        
        // Calculate forces between all unique pairs
        for (int i = 0; i < bodies.size; i++) {
            PhysicsBody bodyA = bodies.get(i);
            
            for (int j = i + 1; j < bodies.size; j++) {
                PhysicsBody bodyB = bodies.get(j);
                applyDynamicGravity(bodyA, bodyB);
            }
        }
    }
    
    /**
     * Apply gravity with dance-enhancing adjustments
     */
    private static void applyDynamicGravity(PhysicsBody bodyA, PhysicsBody bodyB) {
        // Calculate displacement vector between bodies
        delta.set(bodyB.getPosition()).sub(bodyA.getPosition());
        float distance = delta.len();
        
        // Skip if too close to prevent extreme forces
        if (distance < 0.01f) return;
        
        // Use minimum effective distance
        float effectiveDistance = Math.max(distance, MIN_DISTANCE);
        
        // Get velocities for dancing calculations
        Vector2 velA = bodyA.getBody().getLinearVelocity();
        Vector2 velB = bodyB.getBody().getLinearVelocity();
        
        // Direction from A to B
        Vector2 direction = delta.nor();
        
        // Perpendicular vector for "dance" component
        perp.set(-direction.y, direction.x);
        
        // Calculate base gravitational force
        float massProduct = bodyA.getMass() * bodyB.getMass();
        float forceMagnitude = G * massProduct / (effectiveDistance * effectiveDistance);
        
        // Calculate velocity differences
        float relativeSpeed = new Vector2(velB).sub(velA).len();
        float danceIntensity = DANCE_FACTOR * Math.min(1.0f, relativeSpeed / 20.0f);
        
        // Create dynamism with mass-based dance component
        float massRatio = bodyA.getMass() / (bodyA.getMass() + bodyB.getMass());
        float dancePulse = (float)(Math.sin(distance * 0.01 + System.currentTimeMillis() * 0.001) * danceIntensity);
        
        // Apply force to body A (gravitational + dance component)
        force.set(direction).scl(forceMagnitude / bodyA.getMass());
        force.add(perp.cpy().scl(forceMagnitude * dancePulse * (1-massRatio) / bodyA.getMass()));
        bodyA.getBody().applyForceToCenter(force, true);
        
        // Apply force to body B (gravitational + dance component)
        force.set(direction).scl(-forceMagnitude / bodyB.getMass());
        force.add(perp.cpy().scl(-forceMagnitude * dancePulse * massRatio / bodyB.getMass()));
        bodyB.getBody().applyForceToCenter(force, true);
    }
    
    /**
     * Set up an interesting orbit with slight eccentricity
     */
    public static void setupDynamicOrbit(PhysicsBody orbiter, PhysicsBody primary) {
        // Get position of the primary body
        Vector2 primaryPos = primary.getPosition();
        
        // Calculate a distance based on masses
        float distance = 150.0f + (primary.getMass() + orbiter.getMass()) * 2.0f;
        
        // Place orbiter at random angle
        float angle = (float)(Math.random() * Math.PI * 2);
        Vector2 orbiterPos = new Vector2(
            primaryPos.x + distance * (float)Math.cos(angle),
            primaryPos.y + distance * (float)Math.sin(angle)
        );
        orbiter.getBody().setTransform(orbiterPos, 0);
        
        // Calculate base orbital velocity
        float orbitalSpeed = (float) Math.sqrt(G * primary.getMass() / distance);
        
        // Add variance to create interesting paths
        orbitalSpeed *= 0.9f + (Math.random() * 0.3f);
        
        // Create perpendicular vector with slight inward/outward component
        Vector2 direction = new Vector2(orbiterPos).sub(primaryPos).nor();
        Vector2 perpendicular = new Vector2(-direction.y, direction.x);
        
        // Add slight radial component for non-circular orbits
        float radialComponent = (float)(Math.random() * 0.2 - 0.1);
        perpendicular.add(direction.cpy().scl(radialComponent));
        
        // Set velocity for dynamic orbit
        orbiter.getBody().setLinearVelocity(perpendicular.nor().scl(orbitalSpeed));
        orbiter.getBody().setAwake(true);
    }
    
    /**
     * Create a dynamic system with interesting orbits
     */
    public static void setupDynamicSystem(PhysicsBody primaryBody, Array<PhysicsBody> orbiters) {
        for (int i = 0; i < orbiters.size; i++) {
            setupDynamicOrbit(orbiters.get(i), primaryBody);
        }
    }
}