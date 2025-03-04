package io.github.gravitygame.managers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyFactory;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.physics.GravityManager;
import io.github.gravitygame.physics.WorldState;

public class SimulationManager {
    private final World box2dWorld;
    private final Array<PhysicsBody> bodies = new Array<>();
    private final ArrayDeque<WorldState> stateBuffer = new ArrayDeque<>();
    private final float bufferDuration = 3f;
    private boolean isPaused = false;

    public SimulationManager() {
        this.box2dWorld = new World(Vector2.Zero, true);
    }

   public void update(float delta) {
        if (isPaused) return;
        
        // Convert Array to List properly
        List<PhysicsBody> bodyList = new ArrayList<>();
        for(PhysicsBody body : bodies) bodyList.add(body);
        GravityManager.updateGravity(bodies);
        
        box2dWorld.step(delta, 6, 2);
        storeCurrentState();
        trimStateBuffer(delta);
    }

    private void storeCurrentState() {
        WorldState state = new WorldState();
        for (PhysicsBody body : bodies) {
            state.addBodyState(
                body.getId(),
                body.getPosition(),
                body.getVelocity()
            );
        }
        stateBuffer.addLast(state);
    }

    private void trimStateBuffer(float delta) {
        int maxStates = (int) (bufferDuration / delta);
        while (stateBuffer.size() > maxStates) {
            stateBuffer.removeFirst();
        }
    }

     public void addBody(float x, float y, float radius, Vector2 initialVelocity) {
        PhysicsBody body = BodyFactory.createBody(
            box2dWorld,
            x, y,
            radius,
            calculateMass(radius)
        );
        body.getBody().setLinearVelocity(initialVelocity);
        bodies.add(body);
    }

    public void removeBody(UUID id) {
        PhysicsBody toRemove = null;
        for (PhysicsBody body : bodies) {
            if (body.getId().equals(id)) {
                box2dWorld.destroyBody(body.getBody());
                toRemove = body;
                break;
            }
        }
        if (toRemove != null) bodies.removeValue(toRemove, true);
    }

    private float calculateMass(float radius) {
        return (float) (Math.PI * radius * radius);
    }

    public WorldState createPredictionStartState() {
        WorldState state = new WorldState();
        for (PhysicsBody body : bodies) {
            state.addBodyState(
                body.getId(),
                body.getPosition().cpy(),
                body.getVelocity().cpy()
            );
        }
        return state;
    }

    // Getters
    public World getWorld() {return this.box2dWorld;}
    public Array<PhysicsBody> getBodies() { return bodies; }
    public boolean isPaused() { return isPaused; }
    public void togglePause() { isPaused = !isPaused; }
    public void dispose() { box2dWorld.dispose(); }
}