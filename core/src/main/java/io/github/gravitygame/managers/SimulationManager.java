package io.github.gravitygame.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.physics.GravityManager;

public class SimulationManager {
    private final World world;
    private final List<PhysicsBody> bodies;
    private boolean isPaused = false;
    private float timestepScale = 1f;

    public SimulationManager() {
        this.world = new World(new Vector2(0, 0), true); // No global gravity
        this.bodies = new ArrayList<>();

        // Example: Add initial bodies
        bodies.add(new PhysicsBody(world, 400, 300, 50, 1000, Color.BLUE)); // Planet
        for (int i = 0; i < 3; i++) {
            float angle = (float) (i * 2 * Math.PI / 3);
            float x = (float) (400 + 150 * Math.cos(angle));
            float y = (float) (300 + 150 * Math.sin(angle));
            bodies.add(new PhysicsBody(world, x, y, 20, 100, Color.RED)); // Moons
        }
    }

    public void update(float delta) {
        if (!isPaused) {
            // Update gravity
            GravityManager.updateGravity(bodies);

            // Step the physics simulation
            world.step(delta * timestepScale, 6, 2);
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setTimestepScale(float scale) {
        this.timestepScale = scale;
    }

    public List<PhysicsBody> getBodies() {
        return bodies;
    }

    public void dispose() {
        world.dispose();
    }
}