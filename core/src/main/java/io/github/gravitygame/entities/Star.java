package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Star {
    public Vector2 position;
    public float size;
    public Color color;

    public Star(float x, float y) {
        this.position = new Vector2(x, y);
        this.size = MathUtils.random(1f, 3f); // Varying star size
        float brightness = MathUtils.random(0.5f, 1f); // Random brightness
        this.color = new Color(brightness, brightness, brightness, 1f); // White to light gray
    }

    public void render(ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.circle(position.x, position.y, size);
    }
}
