package io.github.gravitygame.entities;

import com.badlogic.gdx.math.Vector2;

public class BodyCreationData {
    private Vector2 position;
    private Vector2 radiusEnd;
    private Vector2 currentMouse;

    public void reset() {
        position = null;
        radiusEnd = null;
        currentMouse = null;
    }

    // Getters and setters with null checks
    public Vector2 getPosition() { return position != null ? position.cpy() : null; }
    public void setPosition(Vector2 position) { this.position = position.cpy(); }
    
    public Vector2 getRadiusEnd() { return radiusEnd != null ? radiusEnd.cpy() : null; }
    public void setRadiusEnd(Vector2 radiusEnd) { this.radiusEnd = radiusEnd.cpy(); }
    
    public Vector2 getCurrentMouse() { return currentMouse != null ? currentMouse.cpy() : null; }
    public void setCurrentMouse(Vector2 currentMouse) { this.currentMouse = currentMouse.cpy(); }
}