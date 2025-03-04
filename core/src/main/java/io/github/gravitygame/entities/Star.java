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

        // Generate a base brightness level (close to white)
        float brightness = MathUtils.random(0.85f, 1f);

        // Add slight color variations
        float r = brightness + MathUtils.random(-0.05f, 0.05f); // Slight red shift
        float g = brightness + MathUtils.random(-0.05f, 0.05f); // Slight green shift
        float b = brightness + MathUtils.random(-0.05f, 0.05f); // Slight blue shift

        // Ensure values stay within valid color range [0,1]
        r = MathUtils.clamp(r, 0.85f, 1f);
        g = MathUtils.clamp(g, 0.85f, 1f);
        b = MathUtils.clamp(b, 0.85f, 1f);

        this.color = new Color(r, g, b, 1f); // Slightly tinted white
    }

    public void render(ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.circle(position.x, position.y, size);
    }
}
