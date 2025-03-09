package io.github.gravitygame.entities;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

public class BodyDeletionController extends InputAdapter {
    private final SimulationManager simulationManager;
    private final WorldStateManager worldStateManager;
    private final OrthographicCamera camera;
    
    public BodyDeletionController(SimulationManager simulationManager, WorldStateManager worldStateManager, OrthographicCamera camera) {
        this.simulationManager = simulationManager;
        this.worldStateManager = worldStateManager;
        this.camera = camera;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Only process right clicks (button 1) for body deletion
        if (button == 1) {
            Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
            Vector2 mouseWorldPos = new Vector2(worldPos.x, worldPos.y);
            
            // Get the UUID of the body to delete from the displayed world state
            BodyState bodyState = findBodyStateAtPosition(mouseWorldPos);
            
            if (bodyState != null) {
                System.out.println("Deleting body: " + bodyState.getId() + " at position: " + bodyState.getPosition());
                simulationManager.removeBody(bodyState.getId());
                return true;
            }
        }
        return false;
    }
    
    private BodyState findBodyStateAtPosition(Vector2 position) {
        System.out.println("Searching for body at position: " + position);
        
        // Get the currently displayed world state
        WorldState displayedState = worldStateManager.getOldestState();
        if (displayedState == null) return null;
        
        BodyState closestBody = null;
        float closestDistance = Float.MAX_VALUE;
        
        for (BodyState bodyState : displayedState.getBodyStates()) {
            Vector2 bodyPosition = bodyState.getPosition();
            float radius = bodyState.getRadius();
            float distance = position.dst(bodyPosition);
            
            System.out.println("Body ID: " + bodyState.getId() +
                             ", position: " + bodyPosition + 
                             ", radius: " + radius + 
                             ", distance: " + distance);
            
            // Check if click is within the body's exact radius
            if (distance <= radius) {
                // If multiple bodies overlap, select the closest one
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestBody = bodyState;
                }
            }
        }
        
        if (closestBody != null) {
            System.out.println("Body found: " + closestBody.getId());
            return closestBody;
        }
        
        System.out.println("No body found within radius");
        return null;
    }
}