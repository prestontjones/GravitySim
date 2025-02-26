package io.github.gravitygame.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import io.github.gravitygame.entities.Star;

public class StarsManager {
    private final List<Star> stars;
    private final int numStars;
    private final float width, height;
    
    public StarsManager(float width, float height, int numStars) {
        this.width = width;
        this.height = height;
        this.numStars = numStars;
        this.stars = new ArrayList<>();
        generateStars();
    }

    private void generateStars() {
        stars.clear();
        for (int i = 0; i < numStars; i++) {
            float x = MathUtils.random(0, width);
            float y = MathUtils.random(0, height);
            stars.add(new Star(x, y));
        }
    }

    public void render(ShapeRenderer renderer, OrthographicCamera camera) {
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Star star : stars) {
            star.render(renderer);
        }
        renderer.end();
    }
}