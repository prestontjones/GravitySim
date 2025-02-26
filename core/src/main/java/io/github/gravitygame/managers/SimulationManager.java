package io.github.gravitygame.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.physics.GravityManager;
import io.github.gravitygame.utils.CameraController;

public class SimulationManager {
    private final World world;
    private final List<PhysicsBody> bodies;
    private boolean isPaused = false;
    private float timestepScale = 1f;
    private boolean predictionsEnabled = false;

    public SimulationManager() {
        this.world = new World(new Vector2(0, 0), true); // No global gravity
        this.bodies = new ArrayList<>();
    }

    public void update(float delta) {
        if (!isPaused) {
            // Update gravity
            GravityManager.updateGravity(bodies);

            // Step the physics simulation
            world.step(delta * timestepScale, 6, 2);
        }
    }

    public void addBody(float x, float y, float radius, Vector2 velocity) {
        System.out.println("[DEBUG] Adding body at (" + x + "," + y + ")");
        PhysicsBody newBody = new PhysicsBody(world, x, y, radius, calculateMass(radius), Color.GRAY);
        newBody.getBody().setLinearVelocity(velocity);
        bodies.add(newBody);
    }

    public Vector2 getCenterOfMass() {
        Vector2 com = new Vector2();
        float totalMass = 0f;
        
        for (PhysicsBody body : bodies) {
            float mass = body.getMass();
            com.x += body.getBody().getPosition().x * mass;
            com.y += body.getBody().getPosition().y * mass;
            totalMass += mass;
        }
        
        if (totalMass > 0) {
            com.scl(1f / totalMass);
        }
        return com;
    }

    public void togglePause() {
        isPaused = !isPaused;
    }
    public boolean isPaused() {
        return isPaused;
    }
    public void setPredictionsEnabled(boolean enabled) {
        this.predictionsEnabled = enabled;
    }
    public boolean isPredictionsEnabled() {
        return predictionsEnabled;
    }
    public void setTimestepScale(float scale) {
        this.timestepScale = scale;
    }

    public List<PhysicsBody> getBodies() {
        return bodies;
    }

    public World getWorld() {
        return world;
    }

    private float calculateMass(float radius) {
        // Example: Mass proportional to area
        return (float) (Math.PI * radius * radius);
    }

    public List<PhysicsBody> getBodiesSnapshot() {
        return new ArrayList<>(bodies); // Return defensive copy
    }

    public void dispose() {
        world.dispose();
    }

    public CameraController getCameraController() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCameraController'");
    }
}