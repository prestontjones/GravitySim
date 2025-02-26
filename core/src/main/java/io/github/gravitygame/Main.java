package io.github.gravitygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.gravitygame.screens.MainMenuScreen;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch; // Shared SpriteBatch for the entire game

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this)); // Start with the main menu screen
        System.out.println("[DEBUG] Loading Main Menu Screen");
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}