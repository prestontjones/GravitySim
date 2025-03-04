package io.github.gravitygame.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.managers.SimulationManager;

public class BodyCreationLogic {
    private final SimulationManager simulationManager;
    private final float velocityScale;

    public BodyCreationLogic(SimulationManager simulationManager,
                           World physicsWorld,
                           float velocityScale) {
        this.simulationManager = simulationManager;
        this.velocityScale = velocityScale;
    }

    public void finalizeCreation(BodyCreationData data) {
        // Calculate final body properties
        float radius = data.getPosition().dst(data.getRadiusEnd());
        Vector2 velocity = data.getCurrentMouse().cpy()
                            .sub(data.getPosition())
                            .scl(velocityScale);

        // Create the body through simulation manager
        simulationManager.addBody(
            data.getPosition().x, 
            data.getPosition().y,
            radius,
            velocity
        );
    }

    // private float calculateRadius(BodyCreationData data) {
    //     return data.getPosition().dst(data.getRadiusEnd());
    // }

    // private Vector2 calculateVelocity(BodyCreationData data) {
    //     return data.getCurrentMouse().cpy()
    //         .sub(data.getPosition())
    //         .scl(velocityScale);
    // }
}