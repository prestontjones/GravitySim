package io.github.gravitygame.physics;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;

public class PhysicsRenderer {
    public void renderBodies(ShapeRenderer renderer, Array<PhysicsBody> bodies) {
        for(PhysicsBody body : bodies) {
            renderer.setColor(body.getColor());
            renderer.circle(
                body.getPosition().x,
                body.getPosition().y,
                body.getRadius()
            );
        }
    }
}