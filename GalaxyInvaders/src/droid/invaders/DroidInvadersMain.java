package droid.invaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import droid.invaders.handlers.GameServicesHandler;
import droid.invaders.handlers.RequestHandler;
import droid.invaders.idiomas.IdiomasManager;
import droid.invaders.screens.MainMenuScreen;
import droid.invaders.screens.Screens;

public class DroidInvadersMain extends Game {

	public enum Tienda {
		googlePlay,
		amazon,
		slideMe,
		otros,
		samsung,
		appleStore
	}

	final public RequestHandler reqHandler;
	final public GameServicesHandler gameServiceHandler;

	final public Tienda tiendaActual;

	public DroidInvadersMain(Tienda tienda, RequestHandler handler, GameServicesHandler gameServiceHandler) {
		this.reqHandler = handler;
		this.gameServiceHandler = gameServiceHandler;
		tiendaActual = tienda;
	}

	public Stage stage;
	public SpriteBatch batcher;
	public Assets oAssets;
	public DialogSingInGGS dialogs;
	public IdiomasManager idiomas;

	@Override
	public void create() {
		idiomas = new IdiomasManager();
		Assets.load();
		stage = new Stage(Screens.SCREEN_WIDTH, Screens.SCREEN_HEIGHT, false);
		batcher = new SpriteBatch();
		dialogs = new DialogSingInGGS(this, stage);
		setScreen(new MainMenuScreen(this));// aqui tengo que poner lo principal
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
	}

}
