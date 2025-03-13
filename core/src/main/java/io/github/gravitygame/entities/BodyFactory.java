package io.github.gravitygame.entities;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public final class BodyFactory {
    private BodyFactory() {} // Static utility

    public static PhysicsBody createBody(World world, Vector2 velocity, float x, float y, float radius, float mass, Color color) {
        BodyState initialState = new BodyState(
            new Vector2(x, y),
            velocity,
            radius, 
            mass,
            color,
            UUID.randomUUID()
        );
        Body body = createBox2DBody(world, initialState);
        return new PhysicsBody(body, initialState);
    }

    private static Body createBox2DBody(World world, BodyState state) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(state.getPosition());
        bodyDef.linearVelocity.set(state.getVelocity());

        Body body = world.createBody(bodyDef);
        body.setUserData(state.getId());

        CircleShape circle = new CircleShape();
        circle.setRadius(state.getRadius());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = calculateDensity(state.getMass(), state.getRadius());
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.5f;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        circle.dispose();
        body.setAwake(true);
    
        
        return body;
    }

    private static float calculateDensity(float mass, float radius) {
        return mass / (radius *radius * radius * (float) Math.PI);
    }
}