package io.github.gravitygame.physics;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.entities.PhysicsBody;

public class PhysicsWorld {
    private final World world;
    private final List<PhysicsBody> bodies;

    public PhysicsWorld(List<PhysicsBody> bodies) {
        this.world = new World(new Vector2(0, 0), true); // No global gravity
        this.bodies = bodies;
    }

    public void update(float deltaTime) {
        // Update gravity
        GravityManager.updateGravity(bodies);

        // Step the physics simulation
        world.step(deltaTime, 6, 2);
    }

    public World getWorld() {
        return world;
    }

    public void dispose() {
        world.dispose();
    }
}