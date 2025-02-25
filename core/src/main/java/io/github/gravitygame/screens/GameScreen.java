package io.github.gravitygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.physics.PhysicsWorld;
import io.github.gravitygame.Main;

public class GameScreen implements Screen {
    private final Main main;
    private ShapeRenderer shapeRenderer;
    private PhysicsWorld physicsWorld;
    private PhysicsBody planet;
    private PhysicsBody[] moons;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        physicsWorld = new PhysicsWorld();

        // Create the planet
        planet = new PhysicsBody(physicsWorld.getWorld(), 400, 300, 50, Color.BLUE);

        // Create the moons
        moons = new PhysicsBody[3];
        for (int i = 0; i < moons.length; i++) {
            float angle = (float) (i * 2 * Math.PI / moons.length);
            float x = (float) (400 + 150 * Math.cos(angle));
            float y = (float) (300 + 150 * Math.sin(angle));
            moons[i] = new PhysicsBody(physicsWorld.getWorld(), x, y, 20, Color.RED);

            // Set initial velocity for orbiting
            float speed = 5;
            float vx = (float) (speed * Math.cos(angle + Math.PI / 2));
            float vy = (float) (speed * Math.sin(angle + Math.PI / 2));
            moons[i].getBody().setLinearVelocity(vx, vy);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update physics
        physicsWorld.update(delta);

        // Draw bodies
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        planet.render(shapeRenderer);
        for (PhysicsBody moon : moons) {
            moon.render(shapeRenderer);
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        physicsWorld.dispose();
    }
}