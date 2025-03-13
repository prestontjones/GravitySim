package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Planet {
    // Encapsulated fields
    private final float x;
    private final float y;
    private final float radius;
    private final Color color;
    public final boolean hasMoon;
    private float rotationAngle;
    private final float rotationSpeed;
    private float moonAngle;
    private final float moonOrbitRadius;
    private final float moonRotationSpeed;

    public Planet(float x, float y, float radius, Color color, boolean hasMoon) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.hasMoon = hasMoon;

        // Rotation properties
        this.rotationAngle = MathUtils.random(0, 360);
        this.rotationSpeed = MathUtils.random(5f, 15f) / radius;

        // Moon properties
        if (hasMoon) {
            this.moonAngle = MathUtils.random(0, 360);
            this.moonOrbitRadius = radius * MathUtils.random(1.8f, 3.5f);
            this.moonRotationSpeed = MathUtils.random(200f, 300f) / radius;
        } else {
            this.moonOrbitRadius = 0;
            this.moonRotationSpeed = 0;
        }
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getRadius() { return radius; }
    public Color getColor() { return color; }
    public boolean hasMoon() { return hasMoon; }
    public float getRotationAngle() { return rotationAngle; }
    public float getMoonAngle() { return moonAngle; }
    public float getMoonOrbitRadius() { return moonOrbitRadius; }

    public void update(float delta) {
        // Update planet rotation
        rotationAngle += rotationSpeed * delta;
        rotationAngle %= 360;

        // Update moon position
        if (hasMoon) {
            moonAngle += moonRotationSpeed * delta;
            moonAngle %= 360;
        }
    }
}