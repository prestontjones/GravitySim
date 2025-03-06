package io.github.gravitygame.managers;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.utils.WorldState;

public class WorldStateManager {
    private final Queue<WorldState> stateQueue = new LinkedList<>();
    private SimulationManager simulationManager;
    private float timeSinceLastCapture = 0;
    public static final float CAPTURE_INTERVAL = 0.05f;
    public static final int MAX_STATES = 200;

    // Pointers to the most recent and the oldest states
    private WorldState mostRecentState;
    private WorldState oldestState;
    
    // Add a processing flag to ensure we rotate through states
    private boolean processStates = true;
    
    // Add a flag to pause cycling after adding a new body
    private float stabilizationDelay = 0f;
    private static final float STABILIZATION_TIME = 0.5f; // Half second delay

    public void setSimulationManager(SimulationManager manager) {
        this.simulationManager = manager;
    }

    /**
     * Creates a snapshot of the current world state by converting each PhysicsBody
     * into an immutable BodyState.
     *
     * @param bodies The current simulation bodies.
     * @return A WorldState containing a snapshot of each body's state.
     */
    private WorldState createWorldState(Array<PhysicsBody> bodies) {
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

    public void update(float delta) {
        if (simulationManager == null) return;
        
        // Update stabilization delay if active
        if (stabilizationDelay > 0) {
            stabilizationDelay -= delta;
        }

        timeSinceLastCapture += delta;

        // Always capture states at regular intervals, even when paused
        if (timeSinceLastCapture >= CAPTURE_INTERVAL) {
            WorldState newState = createWorldState(simulationManager.getBodies());
            saveState(newState);
            timeSinceLastCapture = 0; // Reset timer
        }
    }

    /**
     * Saves a new WorldState snapshot, maintaining only the last 'MAX_STATES' states.
     * @param state The WorldState snapshot to store.
     */
    private void saveState(WorldState state) {
        // Add the new state
        stateQueue.offer(state);
        mostRecentState = state; // Update most recent state pointer

        // Update the oldest state if the queue was empty
        if (stateQueue.size() == 1) {
            oldestState = state;
        }
        
        // Remove oldest state if queue is too large
        if (stateQueue.size() > MAX_STATES) {
            stateQueue.poll();
            if (!stateQueue.isEmpty()) {
                oldestState = stateQueue.peek();
            }
        }
    }

    public float getTimeToFillQueue() {
        return MAX_STATES * CAPTURE_INTERVAL;
    }

    public WorldState getOldestState() {
        return oldestState; // Return the pointer to the oldest state
    }

    public WorldState getMostRecentState() {
        return mostRecentState; // Return the pointer to the most recent state
    }

    /**
     * Cycles through states for animation purposes.
     * @return true if a cycle occurred, false otherwise
     */
    public boolean cycleStates() {
        // Don't cycle during stabilization period
        if (stabilizationDelay > 0 || !processStates) {
            return false;
        }
        
        if (!stateQueue.isEmpty() && stateQueue.size() > 1) {
            // Move the oldest state to the back of the queue
            WorldState oldest = stateQueue.poll();
            // Only do this if we have enough states to maintain a good visual
            if (stateQueue.size() >= 5) {
                oldestState = stateQueue.peek(); // Update pointer to the new oldest state
                return true;
            } else {
                // Put it back if we don't have enough states
                stateQueue.offer(oldest);
            }
        }
        return false;
    }

    /**
     * Call this when a new body is added to prevent immediate cycling
     */
    public void bodyAdded() {
        stabilizationDelay = STABILIZATION_TIME;
        
        // Clear existing states if necessary
        // clearHistory();
    }

    public void setProcessingEnabled(boolean enabled) {
        this.processStates = enabled;
    }

    /**
     * Makes a copy of the current state.
     */
    public WorldState copyCurrentState() {
        return mostRecentState != null ? new WorldState(mostRecentState) : null;
    }

    /**
     * Clears the history of states.
     */
    public void clearHistory() {
        stateQueue.clear();
        mostRecentState = null;
        oldestState = null;
        stabilizationDelay = 0f;
    }

    public Queue<WorldState> getHistoryQueue() {
        return stateQueue; 
    }

    public int getHistorySize() {
        return stateQueue.size();
    }
    
    public boolean isStabilizing() {
        return stabilizationDelay > 0;
    }
}