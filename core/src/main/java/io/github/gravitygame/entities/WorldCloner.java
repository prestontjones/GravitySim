package io.github.gravitygame.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class WorldCloner {
    public static World cloneWorld(World original) {
        World clone = new World(original.getGravity(), true);
        Array<Body> bodies = new Array<>();
        original.getBodies(bodies);
        
        for(Body body : bodies) {
            // Clone body definition
            
            BodyDef bd = new BodyDef();
            bd.type = body.getType();
            bd.position.set(body.getPosition());
            bd.angle = body.getAngle();
            bd.linearVelocity.set(body.getLinearVelocity());
            bd.angularVelocity = body.getAngularVelocity();
            
            // Create cloned body
            Body newBody = clone.createBody(bd);
            newBody.setUserData(body.getUserData());
            
            // Clone fixtures (circles only)
            Array<Fixture> fixtures = body.getFixtureList();
            for(Fixture fixture : fixtures) {
                FixtureDef fd = new FixtureDef();
                CircleShape originalShape = (CircleShape) fixture.getShape(); // Safe cast
                
                // Create new circle shape
                CircleShape clonedShape = new CircleShape();
                clonedShape.setRadius(originalShape.getRadius());
                clonedShape.setPosition(originalShape.getPosition());
                
                // Configure fixture
                fd.shape = clonedShape;
                fd.density = fixture.getDensity();
                fd.friction = fixture.getFriction();
                fd.restitution = fixture.getRestitution();
                
                newBody.createFixture(fd);
                clonedShape.dispose(); // Cleanup cloned shape
            }
        }
        return clone;
    }
}