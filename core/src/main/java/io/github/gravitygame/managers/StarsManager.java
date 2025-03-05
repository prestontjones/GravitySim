package io.github.gravitygame.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.gravitygame.entities.Star;

public class StarsManager {
    private final List<Star> stars;
    private final int numStars;
    private final float width, height;
    private final float cameraOffsetX, cameraOffsetY;
    
    // A separate camera for the stars
    private final OrthographicCamera starCamera;
    // Factor to scale the zoom effect for the stars (0 means stars never zoom, 1 means same as simulation)
    private final float starZoomFactor = 0.01f; 
    // Factor to scale the panning (parallax) effect for the stars (0 means stars never move, 1 means same as simulation)
    private final float starPanFactor = 0.1f; 

    // Store the initial simulation camera position (for parallax calculations)
    private final Vector2 initialCameraPosition;
    
    public StarsManager(float width, float height, int numStars, OrthographicCamera simulationCamera) {
        this.width = width;
        this.height = height;
        this.numStars = numStars;
        this.stars = new ArrayList<>();
        // Save the initial simulation camera position.
        this.initialCameraPosition = new Vector2(simulationCamera.position.x, simulationCamera.position.y);
        this.cameraOffsetX = initialCameraPosition.x - width / 2;
        this.cameraOffsetY = initialCameraPosition.y - height / 2;
        
        // Initialize the star camera with the same viewport dimensions as the simulation camera.
        starCamera = new OrthographicCamera(simulationCamera.viewportWidth, simulationCamera.viewportHeight);
        // Start with the initial position.
        starCamera.position.set(initialCameraPosition.x, initialCameraPosition.y, 0);
        // Set the star camera zoom to be less affected by the simulation zoom.
        starCamera.zoom = 1 + (simulationCamera.zoom - 1) * starZoomFactor;
        starCamera.update();
        
        generateStars();
    }
    
    private void generateStars() {
        stars.clear();
        for (int i = 0; i < numStars; i++) {
            // Generate random star position within the defined area plus the camera offset.
            float x = MathUtils.random(0, width) + cameraOffsetX;
            float y = MathUtils.random(0, height) + cameraOffsetY;
            // Use distance to simulate depth
            float distance = MathUtils.random(50, 200);
            stars.add(new Star(x, y, distance));
        }
    }
    
    /**
     * Renders the stars using the dedicated star camera.
     *
     * @param renderer the ShapeRenderer used for drawing
     * @param simulationCamera the simulation camera to follow (for position and zoom)
     */
    public void render(ShapeRenderer renderer, OrthographicCamera simulationCamera) {
        // Calculate a parallax position: start from the initial position and add a fraction of the simulation camera's movement.
        float newX = initialCameraPosition.x + (simulationCamera.position.x - initialCameraPosition.x) * starPanFactor;
        float newY = initialCameraPosition.y + (simulationCamera.position.y - initialCameraPosition.y) * starPanFactor;
        starCamera.position.set(newX, newY, simulationCamera.position.z);
        
        // Apply a reduced zoom effect for the star field.
        starCamera.zoom = 1 + (simulationCamera.zoom - 1) * starZoomFactor;
        starCamera.viewportWidth = simulationCamera.viewportWidth;
        starCamera.viewportHeight = simulationCamera.viewportHeight;
        starCamera.update();
        
        renderer.setProjectionMatrix(starCamera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Star star : stars) {
            star.render(renderer, star.getPosition().x, star.getPosition().y);
        }
        renderer.end();
    }
}
