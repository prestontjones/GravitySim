package io.github.gravitygame.physics;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

public class PhysicsRenderer {
    private final WorldStateManager stateQueue;
    private boolean debugRenderCurrentState = false;

    public PhysicsRenderer(WorldStateManager stateQueue) {
        this.stateQueue = stateQueue;
    }

    public void setDebugRender(boolean debug) {
        this.debugRenderCurrentState = debug;
    }

    public void renderBodies(ShapeRenderer renderer, Array<PhysicsBody> currentBodies) {
        renderer.begin(ShapeRenderer.ShapeType.Line);

        // Get the WorldState from 3 seconds ago
        WorldState pastState = stateQueue.getOldestState();
        if (pastState != null) {
            for (BodyState pastBody : pastState.getBodyStates()) {
                renderer.setColor(pastBody.getColor());
                renderer.circle(
                    pastBody.getPosition().x,
                    pastBody.getPosition().y,
                    pastBody.getRadius()
                );
            }
        }

        // Debug mode: Draw current physics bodies
        if (debugRenderCurrentState) {
            for (PhysicsBody body : currentBodies) {
                renderer.setColor(1, 0, 0, 0.5f); // Semi-transparent red
                renderer.circle(
                    body.getPosition().x,
                    body.getPosition().y,
                    body.getRadius()
                );
            }
        }

        renderer.end();
    }
}