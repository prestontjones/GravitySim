package io.github.gravitygame.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;

public final class GravityManager {
    // Gravitational constant with distance scaling
    // This value is tuned to work across different scales
    private static final float G_BASE = 2000.0f;
    
    // Separate G values for different distance ranges
    private static final float G_CLOSE = G_BASE * 0.8f;    // Close range (< 100 units)
    private static final float G_MEDIUM = G_BASE;          // Medium range (100-1000 units)
    private static final float G_FAR = G_BASE * 5.0f;      // Far range (> 1000 units)
    
    // Distance thresholds for scaling G
    private static final float CLOSE_THRESHOLD = 100.0f;
    private static final float FAR_THRESHOLD = 1000.0f;
    
    // Minimum distance to prevent singularities
    private static final float MIN_DISTANCE = 15.0f;
    
    // Maximum distance for gravity calculations
    private static final float MAX_DISTANCE = 5000.0f;
    
    // Velocity dampening to prevent numerical issues
    // 1.0 = no change, 0.999 = slight dampening, 1.001 = slight boosting
    // private static final float VELOCITY_FACTOR = 1.0f;
    
    // Temporary vectors for calculations
    private static final Vector2 tmpDelta = new Vector2();
    // private static final Vector2 tmpForce = new Vector2();
    private static final Vector2 tmpPerp = new Vector2();
    
    /**
     * Apply gravity forces between all physics bodies
     */
    public static void updateGravity(Array<PhysicsBody> bodies) {
        // Wake up all bodies
        for (int i = 0; i < bodies.size; i++) {
            PhysicsBody body = bodies.get(i);
            body.getBody().setAwake(true);
            
            // Apply tiny velocity adjustment to prevent numerical drift
            // if (VELOCITY_FACTOR != 1.0f) {
            //     Vector2 vel = body.getBody().getLinearVelocity();
            //     if (vel.len2() > 0.1f) {
            //         vel.scl(VELOCITY_FACTOR);
            //         body.getBody().setLinearVelocity(vel);
            //     }
            // }
        }
        
        // Calculate forces between all unique pairs of bodies
        for (int i = 0; i < bodies.size; i++) {
            PhysicsBody bodyA = bodies.get(i);
            Vector2 posA = bodyA.getPosition();
            float massA = bodyA.getMass();
            
            for (int j = i + 1; j < bodies.size; j++) {
                PhysicsBody bodyB = bodies.get(j);
                Vector2 posB = bodyB.getPosition();
                float massB = bodyB.getMass();
                
                // Vector from A to B
                tmpDelta.set(posB).sub(posA);
                float distance = tmpDelta.len();
                
                // Skip if too far or too close
                if (distance > MAX_DISTANCE || distance < 0.01f) continue;
                
                // Use a minimum effective distance to prevent extreme forces
                float effectiveDistance = Math.max(distance, MIN_DISTANCE);
                
                // Choose appropriate G value based on distance
                float gValue;
                if (distance < CLOSE_THRESHOLD) {
                    gValue = G_CLOSE;
                } else if (distance < FAR_THRESHOLD) {
                    gValue = G_MEDIUM;
                } else {
                    gValue = G_FAR;
                }
                
                // Calculate force magnitude: F = G * (m1 * m2) / r^2
                float forceMagnitude = gValue * massA * massB / (effectiveDistance * effectiveDistance);
                
                // Apply gravitational force
                applyGravitationalForce(bodyA, bodyB, tmpDelta.nor(), forceMagnitude, distance);
            }
        }
    }
    
    /**
     * Apply gravitational force between two bodies with stabilization for orbits
     */
    private static void applyGravitationalForce(PhysicsBody bodyA, PhysicsBody bodyB, 
                                              Vector2 direction, float forceMagnitude, float distance) {
        
        // Get current velocities
        Vector2 velA = bodyA.getBody().getLinearVelocity();
        Vector2 velB = bodyB.getBody().getLinearVelocity();
        
        // Calculate relative velocity component along the connecting line
        Vector2 relativeVel = new Vector2(velB).sub(velA);
        float approachSpeed = relativeVel.dot(direction);
        
        // Calculate mass ratio for force distribution
        float massA = bodyA.getMass();
        float massB = bodyB.getMass();
        float totalMass = massA + massB;
        
        // Base gravitational forces
        Vector2 forceOnA = new Vector2(direction).scl(forceMagnitude);
        Vector2 forceOnB = new Vector2(direction).scl(-forceMagnitude);
        
        // For close encounters, add orbital stabilization
        if (distance < CLOSE_THRESHOLD * 1.5f && approachSpeed < 0) {
            // Bodies are approaching each other
            
            // Calculate perpendicular vector for orbital motion
            tmpPerp.set(-direction.y, direction.x);
            
            // Calculate perpendicular component of relative velocity
            float perpVelocity = relativeVel.dot(tmpPerp);
            
            // If perpendicular velocity exists, enhance it to encourage orbiting
            if (Math.abs(perpVelocity) > 0.5f) {
                // Determine orbit direction
                float perpForce = forceMagnitude * 0.25f;
                if (perpVelocity > 0) {
                    // Apply perpendicular force to encourage existing orbital direction
                    forceOnA.add(new Vector2(tmpPerp).scl(perpForce * (massB / totalMass)));
                    forceOnB.sub(new Vector2(tmpPerp).scl(perpForce * (massA / totalMass)));
                } else {
                    // Apply perpendicular force to encourage existing orbital direction (opposite)
                    forceOnA.sub(new Vector2(tmpPerp).scl(perpForce * (massB / totalMass)));
                    forceOnB.add(new Vector2(tmpPerp).scl(perpForce * (massA / totalMass)));
                }
            }
            
            // Also reduce radial component for very close encounters to prevent collapse
            if (distance < MIN_DISTANCE * 2.0f && approachSpeed < -5.0f) {
                // Apply mild radial dampening to slow the approach
                float dampFactor = 0.8f;
                Vector2 approachDamp = new Vector2(direction).scl(approachSpeed * dampFactor);
                
                // Apply dampening proportional to mass
                bodyA.getBody().setLinearVelocity(new Vector2(velA).add(
                        approachDamp.cpy().scl(massB / totalMass)));
                bodyB.getBody().setLinearVelocity(new Vector2(velB).sub(
                        approachDamp.cpy().scl(massA / totalMass)));
            }
        }
        
        // Apply the calculated forces with inverseMass to get proper acceleration
        applyForceWithMassScaling(bodyA, forceOnA);
        applyForceWithMassScaling(bodyB, forceOnB);
    }
    
    /**
     * Apply force accounting for body mass (Box2D should do this, but just to be sure)
     */
    private static void applyForceWithMassScaling(PhysicsBody body, Vector2 force) {
        // Calculate acceleration: a = F/m
        float inverseMass = 1.0f / body.getMass();
        Vector2 acceleration = force.cpy().scl(inverseMass);
        
        // Apply the acceleration as a force
        body.getBody().applyForceToCenter(acceleration, true);
    }
    
    /**
     * Set up a stable orbital relationship between two bodies
     *
     * @param orbiter The body that will orbit
     * @param primary The central body being orbited
     * @param distanceMultiplier Factor to multiply current distance (1.0 = keep current)
     * @param clockwise Direction of orbit
     * @param eccentricity 0-1 value for orbit shape (0 = circular, 1 = highly elliptical)
     */
    public static void setupOrbit(PhysicsBody orbiter, PhysicsBody primary, 
                                float distanceMultiplier, boolean clockwise, float eccentricity) {
        
        // Get positions
        Vector2 primaryPos = primary.getPosition();
        Vector2 orbiterPos = orbiter.getPosition();
        
        // Calculate current distance and direction
        Vector2 displacement = new Vector2(orbiterPos).sub(primaryPos);
        float currentDistance = displacement.len();
        
        // Apply distance multiplier if requested
        if (distanceMultiplier != 1.0f && distanceMultiplier > 0) {
            float newDistance = currentDistance * distanceMultiplier;
            displacement.nor().scl(newDistance);
            orbiterPos.set(primaryPos).add(displacement);
            orbiter.getBody().setTransform(orbiterPos, 0);
        }
        
        // Recalculate distance after potential adjustment
        displacement.set(orbiterPos).sub(primaryPos);
        float distance = displacement.len();
        Vector2 direction = displacement.nor();
        
        // Choose appropriate G value based on distance
        float gValue;
        if (distance < CLOSE_THRESHOLD) {
            gValue = G_CLOSE;
        } else if (distance < FAR_THRESHOLD) {
            gValue = G_MEDIUM;
        } else {
            gValue = G_FAR;
        }
        
        // Calculate orbital velocity using v = sqrt(G*M/r)
        float primaryMass = primary.getMass();
        float orbitalSpeed = (float) Math.sqrt(gValue * primaryMass / distance);
        
        // Create perpendicular vector for orbit direction
        Vector2 perpVector = new Vector2();
        if (clockwise) {
            perpVector.set(direction.y, -direction.x);
        } else {
            perpVector.set(-direction.y, direction.x);
        }
        
        // Adjust for eccentricity if needed
        // For elliptical orbits: v = sqrt(G*M * (2/r - 1/a))
        // Where a is the semi-major axis
        if (eccentricity > 0) {
            // For simplicity, we'll just scale the velocity
            // An elliptical orbit at periapsis (closest point) has higher velocity
            float speedFactor = 1.0f + (eccentricity * 0.3f);
            orbitalSpeed *= speedFactor;
        }
        
        // Set the velocity
        perpVector.nor().scl(orbitalSpeed);
        orbiter.getBody().setLinearVelocity(perpVector);
        
        // Force Box2D to update
        orbiter.getBody().setAwake(true);
    }
    
    /**
     * Create a complex system with multiple orbiting bodies
     * 
     * @param world Box2D world reference
     * @param primaryBody The central body
     * @param secondaryBodies Array of bodies to place in orbit
     */
    public static void setupSystemWithRandomOrbits(
            PhysicsBody primaryBody, Array<PhysicsBody> secondaryBodies) {
        
        // Set up orbits with increasing distances
        float baseDistance = 200.0f;
        
        for (int i = 0; i < secondaryBodies.size; i++) {
            PhysicsBody orbiter = secondaryBodies.get(i);
            
            // Calculate a distance based on index
            float distance = baseDistance * (1.0f + i * 0.5f);
            
            // Place at random angle
            float angle = (float)(Math.random() * Math.PI * 2);
            Vector2 position = new Vector2(
                primaryBody.getPosition().x + distance * (float)Math.cos(angle),
                primaryBody.getPosition().y + distance * (float)Math.sin(angle)
            );
            
            // Set position
            orbiter.getBody().setTransform(position, 0);
            
            // Calculate random eccentricity and orbit direction
            float eccentricity = (float)Math.random() * 0.3f;
            boolean clockwise = Math.random() > 0.5;
            
            // Setup the orbit
            setupOrbit(orbiter, primaryBody, 1.0f, clockwise, eccentricity);
        }
    }
}