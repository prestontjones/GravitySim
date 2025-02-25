package io.github.gravitygame.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsWorld {
    private final World world;

    public PhysicsWorld() {
        world = new World(new Vector2(0, -9.8f), true); // Gravity: (0, -9.8 m/s²)
    }

    public void update(float deltaTime) {
        world.step(deltaTime, 6, 2); // Step the physics simulation
    }

    public World getWorld() {
        return world;
    }

    public void dispose() {
        world.dispose();
    }
}