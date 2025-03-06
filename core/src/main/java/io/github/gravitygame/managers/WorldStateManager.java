package io.github.gravitygame.managers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.utils.WorldState;

public class WorldStateManager {
    private static final int MAX_STATES = 60; // 3 seconds of history at 20 updates per second
    private final Queue<WorldState> stateQueue = new LinkedList<>();

    /**
     * Creates a snapshot of the current world state by converting each PhysicsBody
     * into an immutable BodyState.
     *
     * @param bodies The current simulation bodies.
     * @return A WorldState containing a snapshot of each body's state.
     */
    public WorldState createWorldState(Array<PhysicsBody> bodies) {
        WorldState state = new WorldState();
        for (PhysicsBody body : bodies) {
            state.addBodyState(new BodyState(
                body.getPosition(),
                body.getVelocity(),
                body.getRadius(),
                body.getMass(),
                body.getColor(),
                body.getId()
            ));
        }
        return state;
    }

    /**
     * Saves a new WorldState snapshot, maintaining only the last 60 states.
     * @param state The WorldState snapshot to store.
     */
    public void saveState(WorldState state) {
        if (stateQueue.size() >= MAX_STATES) {
            stateQueue.poll(); // Remove the oldest state
        }
        stateQueue.offer(state);
    }

    public WorldState getOldestState() {
        return stateQueue.peek();
    }

    public void clearHistory() {
        stateQueue.clear();
    }

    public int getHistorySize() {
        return stateQueue.size();
    }
    
    public List<WorldState> getHistory() {
        return new ArrayList<>(stateQueue);
    }
}