package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Star {
    public Vector2 position;
    public float size;
    public Color color;
    private final float brightness;
    private final float distance;
    private float twinkleFactor;

    // Constructor now includes distance and random brightness
    public Star(float x, float y, float distance) {
        this.position = new Vector2(x, y);
        this.distance = distance;

        // Adjust size based on distance (near stars are bigger, far stars are smaller)
        this.size = MathUtils.random(50f, 150f) * (1 / distance); // Smaller stars for distant
        this.brightness = MathUtils.random(10f, 200f) * (1 / distance); // Dimmer for distant stars

        // Add slight color variations based on distance (simulating temperature)
        float temp = MathUtils.random(); // Random temperature for star color
        if (temp < 0.33f) {
            this.color = new Color(1f, 0.6f, 0.1f, 1f); // Red-orange (closer stars)
        } else if (temp < 0.66f) {
            this.color = new Color(1f, 1f, 1f, 1f); // White (mid-range distance)
        } else {
            this.color = new Color(0.2f, 0.5f, 1f, 1f); // Blue (distant stars)
        }

        // Randomize twinkle factor for shimmering effect
        this.twinkleFactor = MathUtils.random(0.85f, 1f);
    }

    // Update brightness for twinkling effect (flickering)
    public void updateTwinkle() {
        this.twinkleFactor = MathUtils.random(0.85f, 1.0f); // Slight variation for twinkling
    }

    // Render star with twinkle effect and size adjustment
    public void render(ShapeRenderer renderer, float xOffset, float yOffset) {
        // Adjust brightness based on twinkling effect
        float currentBrightness = this.brightness * twinkleFactor;
        renderer.setColor(color.r * currentBrightness, color.g * currentBrightness, color.b * currentBrightness, 1f);
        renderer.circle(position.x + xOffset, position.y + yOffset, size);
    }

    public Vector2 getPosition() {
        return position;
    }
}
