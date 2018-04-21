package ru.deadsoftware.cavecraft;

import com.badlogic.gdx.Game;

public class CaveGame extends Game {

	public static final String VERSION = "alpha 0.2";

	public static GameState STATE;

	public static boolean TOUCH;

	public CaveGame() {
		this(false);
	}

	public CaveGame(boolean touch) {
		TOUCH = touch;
		STATE = GameState.GAME_PLAY;
	}

	@Override
	public void create () {
		setScreen(new GameScreen());
	}

}
