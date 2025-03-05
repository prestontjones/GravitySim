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

    public StarsManager(float width, float height, int numStars, OrthographicCamera camera) {
        this.width = width;
        this.height = height;
        this.numStars = numStars;
        this.stars = new ArrayList<>();
        this.cameraOffsetX = camera.position.x - width / 2;
        this.cameraOffsetY = camera.position.y - height / 2;
        generateStars(camera);
    }

    private void generateStars(OrthographicCamera camera) {
        stars.clear();
        for (int i = 0; i < numStars; i++) {
            // Generate random star position
            float x = MathUtils.random(0, width) + cameraOffsetX;
            float y = MathUtils.random(0, height) + cameraOffsetY;

            // Assign a distance value to simulate depth (farther stars should be smaller and dimmer)
            float distance = MathUtils.random(50, 200); // Star distance from camera
            stars.add(new Star(x, y, distance));
        }
    }

    public void render(ShapeRenderer renderer, OrthographicCamera camera, CameraController cameraController) {
        // Get the parallax offset from the cameraController
        Vector2 parallaxOffset = cameraController.getParallaxOffset();
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Adjust each star's position based on the parallax effect
        for (Star star : stars) {
            // Apply parallax by shifting the star's position based on the camera's position
            float xOffset = star.getPosition().x - parallaxOffset.x;
            float yOffset = star.getPosition().y - parallaxOffset.y;

            // Update the star's twinkle effect (this makes them shimmer randomly)
            star.updateTwinkle();

            // Render the star at the adjusted position with twinkling effect
            star.render(renderer, xOffset, yOffset);
        }

        renderer.end();
    }
}