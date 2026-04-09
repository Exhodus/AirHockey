package io.github.some_example_name;

import com.badlogic.gdx.Game;

import io.github.some_example_name.screens.GameScreen;
import io.github.some_example_name.screens.MenuScreen;

public class AirHockeyGame extends Game {
    @Override
    public void create() { //Equivalente al OnCreate de android
        setScreen(new MenuScreen(this));
    }
}
