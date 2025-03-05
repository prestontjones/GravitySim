package io.github.gravitygame.managers;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyFactory;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.physics.GravityManager;

public class SimulationManager {
    private final World simulationWorld;
    private final Array<PhysicsBody> bodies = new Array<>();
    private boolean isPaused = false;

    public SimulationManager() {
        this.simulationWorld = new World(Vector2.Zero, true);
    }

   public void update(float delta) {
        if (isPaused) {
            Gdx.app.log("SimManager", "GamePaused"); 
            return;
        }
        
        GravityManager.updateGravity(bodies);
        
        simulationWorld.step(delta, 6, 2);
    }


     public void addBody(float x, float y, float radius, Vector2 initialVelocity) {
        PhysicsBody body = BodyFactory.createBody(
            simulationWorld,
            initialVelocity,
            x, y,
            radius,
            calculateMass(radius)
        );
        body.getBody().setLinearVelocity(initialVelocity);
        bodies.add(body);
        Gdx.app.log("Entities", "Body Created with velocity: " + initialVelocity);
    }

    public void removeBody(UUID id) {
        PhysicsBody toRemove = null;
        for (PhysicsBody body : bodies) {
            if (body.getId().equals(id)) {
                simulationWorld.destroyBody(body.getBody());
                toRemove = body;
                break;
            }
        }
        if (toRemove != null) bodies.removeValue(toRemove, true);
    }

    private float calculateMass(float radius) {
        return (float) (Math.PI * radius * radius);
    }
    // Getters
    public World getWorld() {return this.simulationWorld;}
    public Array<PhysicsBody> getBodies() { return bodies; }
    public boolean isPaused() { return isPaused; }
    public void togglePause() { isPaused = !isPaused; }
    public void dispose() { simulationWorld.dispose(); }
}