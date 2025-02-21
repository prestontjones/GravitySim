package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import io.github.some_example_name.Entities.PhysicsBody;
import io.github.some_example_name.Main;
import io.github.some_example_name.Physics.PhysicsWorld;

public class GameScreen implements Screen {
    private final Main main;
    private SpriteBatch batch;
    private PhysicsWorld physicsWorld;
    private PhysicsBody planet;
    private PhysicsBody[] moons;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        physicsWorld = new PhysicsWorld();

        // Load textures
        Texture planetTexture = new Texture(Gdx.files.internal("planet.png")); // Replace with your texture
        Texture moonTexture = new Texture(Gdx.files.internal("moon.png")); // Replace with your texture

        // Create the planet
        planet = new PhysicsBody(physicsWorld.getWorld(), 400, 300, 50, planetTexture);

        // Create the moons
        moons = new PhysicsBody[3];
        for (int i = 0; i < moons.length; i++) {
            float angle = (float) (i * 2 * Math.PI / moons.length);
            float x = (float) (400 + 150 * Math.cos(angle));
            float y = (float) (300 + 150 * Math.sin(angle));
            moons[i] = new PhysicsBody(physicsWorld.getWorld(), x, y, 20, moonTexture);

            // Set initial velocity for orbiting
            Vector2 velocity = new Vector2((float) Math.cos(angle + Math.PI / 2), (float) Math.sin(angle + Math.PI / 2)).scl(5);
            moons[i].getBody().setLinearVelocity(velocity);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update physics
        physicsWorld.update(delta);

        // Update and render bodies
        batch.begin();
        planet.update();
        planet.render(batch);
        for (PhysicsBody moon : moons) {
            moon.update();
            moon.render(batch);
        }
        batch.end();
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
        batch.dispose();
        physicsWorld.dispose();
    }
}
