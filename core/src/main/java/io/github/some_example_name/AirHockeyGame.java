package io.github.some_example_name;

import com.badlogic.gdx.Game;

import io.github.some_example_name.screens.GameScreen;

public class AirHockeyGame extends Game {
    @Override
    public void create() { //Equivalente al OnCreate de android
        setScreen(new GameScreen(this));
    }
}
