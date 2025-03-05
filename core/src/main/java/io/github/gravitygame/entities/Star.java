package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Star {
    public Vector2 position;
    public float size;
    public Color color;

    // Constructor now includes distance and random brightness
    public Star(float x, float y, float distance) {
        this.position = new Vector2(x, y);

        // Adjust size based on distance (near stars are bigger, far stars are smaller)
        this.size = MathUtils.random(50f, 150f) * (1 / distance); // Smaller stars for distant

        // Add slight color variations based on distance (simulating temperature)
        float temp = MathUtils.random(); // Random temperature for star color
        if (temp < 0.33f) {
            this.color = new Color(1f, 0.75f, 0.4f, 1f); // Red-orange (closer stars)
        } else if (temp < 0.66f) {
            this.color = new Color(.1f, 1f, 1f, 1f); // White (mid-range distance)
        } else {
            this.color = new Color(0.4f, 0.6f, .9f, .9f); // Blue (distant stars)
        }
    }

    // Render star with twinkle effect and size adjustment
    public void render(ShapeRenderer renderer, float xOffset, float yOffset) {
        // Adjust brightness based on twinkling effect
        renderer.setColor(color.r, color.g , color.b, 1f);
        renderer.circle(position.x + xOffset, position.y + yOffset, size);
    }

    public Vector2 getPosition() {
        return position;
    }
}
