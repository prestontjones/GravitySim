package io.github.gravitygame.managers;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyFactory;
import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.physics.GravityManager;
import io.github.gravitygame.utils.WorldState;

public class SimulationManager {
    private final World simulationWorld;
    private final Array<PhysicsBody> bodies = new Array<>();
    private boolean isPaused = false;
    private float accumulatedTime = 0;
    private static final float STEP_TIME = 1 / 60f;
    private WorldStateManager worldStateManager;

    public SimulationManager() {
        this.simulationWorld = new World(Vector2.Zero, false);
    }

    public void setWorldStateManager(WorldStateManager manager) {
        this.worldStateManager = manager;
    }

    public void update(float delta) {
        if (isPaused) {
            return;
        }
        accumulatedTime += delta;
        while (accumulatedTime >= STEP_TIME) {
            GravityManager.updateGravity(bodies);
            simulationWorld.step(STEP_TIME, 6, 2);
            accumulatedTime -= STEP_TIME;
        }
    }

    public void addBody(float x, float y, float radius, Vector2 velocity, Color color) {
        WorldState oldestState = worldStateManager.getOldestState();
    
        if (oldestState != null) {
            // 1. Reset to oldest state
            resetToState(oldestState);
    
            // 2. Clear history
            worldStateManager.clearHistory();
    
            // 3. Add new body FIRST
            PhysicsBody newBody = BodyFactory.createBody(
                simulationWorld,
                velocity,
                x, y,
                radius,
                calculateMass(radius),
                color
            );
            bodies.add(newBody);
    
            // 4. Fast-forward WITH new body
            fastForwardToPresent();
            
            // 5. Notify the WorldStateManager about the new body
            worldStateManager.bodyAdded();
        } else {
            // Initial body case
            // 4. Add new body
            PhysicsBody body = BodyFactory.createBody(
                simulationWorld,
                velocity,
                x, y,
                radius,
                calculateMass(radius),
                color
            );
            bodies.add(body);
            
            // 5. Notify the WorldStateManager about the new body
            worldStateManager.bodyAdded();
        }
    }

    public void resetToState(WorldState state) {
        // Clear existing bodies
        for (PhysicsBody body : bodies) {
            simulationWorld.destroyBody(body.getBody());
        }
        bodies.clear();

        // Restore bodies from saved state
        for (BodyState bodyState : state.getBodyStates()) {
            PhysicsBody body = BodyFactory.createBody(
                simulationWorld,
                bodyState.getVelocity(),
                bodyState.getPosition().x, bodyState.getPosition().y,
                bodyState.getRadius(),
                bodyState.getMass(),
                bodyState.getColor()
            );
            bodies.add(body);
        }
    }

    private void fastForwardToPresent() {
        // Reset physics world time
        pause();
        simulationWorld.step(0f, 0, 0);
        // Simulate exactly 60 steps
        for (int i = 0; i < WorldStateManager.MAX_STATES; i++) {
            GravityManager.updateGravity(bodies);
            simulationWorld.step(
                WorldStateManager.CAPTURE_INTERVAL,
                6,
                2
            );
            worldStateManager.update(WorldStateManager.CAPTURE_INTERVAL); // Update the world state
        }
        resume();
    }

    // Add this method to SimulationManager.java
public PhysicsBody findBodyAtPosition(Vector2 position) {
    for (PhysicsBody body : bodies) {
        Vector2 bodyPosition = body.getBody().getPosition();
        float radius = body.getRadius();
        
        // Check if the click position is within the body's radius
        if (position.dst(bodyPosition) <= radius) {
            return body;
        }
    }
    return null;
}

    public void removeBody(UUID id) {
        // Find the body to remove
        PhysicsBody toRemove = null;
        for (PhysicsBody body : bodies) {
            if (body.getId().equals(id)) {
                toRemove = body;
                break;
            }
        }
        
        if (toRemove != null) {
            // 1. Pause the simulation
            boolean wasPaused = isPaused;
            pause();
            
            // 2. Remove the body from the physics world and our array
            System.out.println("Removing body: " + toRemove.getId());
            simulationWorld.destroyBody(toRemove.getBody());
            bodies.removeValue(toRemove, true);
            
            // 3. Make the current state with remaining bodies the new simulation state
            WorldState newCurrentState = new WorldState();
            for (PhysicsBody body : bodies) {
                newCurrentState.addBodyState(new BodyState(
                    body.getPosition(),
                    body.getVelocity(),
                    body.getRadius(),
                    body.getMass(),
                    body.getColor(),
                    body.getId()
                ));
            }
            
            // 4. Clear the WorldStateManager's queue
            worldStateManager.clearHistory();
            
            // 5. Start recalculating with the missing body
            // First make sure we're in a stable state by using the current state
            // No need to reset to state since we've just removed the body directly
            // Generate trajectory from this point forward
            // The fast-forward will populate the state queue with new trajectory data
            fastForwardToPresent();
            
            // 6. Notify WorldStateManager about the removal
            worldStateManager.bodyRemoved();
            
            // 7. Resume if it wasn't paused before
            if (!wasPaused) {
                resume();
            }
        }
    }

    public Array<PhysicsBody> captureWorldState() {
        Array<PhysicsBody> snapshot = new Array<>();
        for (PhysicsBody body : bodies) {
            snapshot.add(new PhysicsBody(body)); // Use copy constructor
        }
        return snapshot;
    }

    public void resume() {
        if (isPaused) {
            isPaused = false;
            accumulatedTime = 0f; // Reset physics timing
            Gdx.app.log("Simulation Manager Resume", isPaused ? "Game Paused" : "Game Unpaused");
        }
    }

    public void pause() {
        isPaused = true;
        Gdx.app.log("Simulation Manager Resume", isPaused ? "Game Paused" : "Game Unpaused");
    }

    private float calculateMass(float radius) {
        return (float) (Math.PI * radius * radius);
    }

    // Getters
    public World getWorld() { return this.simulationWorld; }
    public Array<PhysicsBody> getBodies() { return bodies; }
    public boolean isPaused() { return isPaused; }
    public void togglePause() {
        isPaused = !isPaused;
        Gdx.app.log("Simulation Manager", isPaused ? "Game Paused" : "Game Unpaused");
    }

    public void dispose() { simulationWorld.dispose(); }
}
