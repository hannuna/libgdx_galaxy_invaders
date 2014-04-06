package droid.invaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import droid.invaders.Assets;
import droid.invaders.DroidInvadersMain;
import droid.invaders.Settings;
import droid.invaders.game.GameScreen;
import droid.invaders.idiomas.IdiomasManager;

public abstract class Screens extends InputAdapter implements Screen {
	public static final int SCREEN_WIDTH = 320;
	public static final int SCREEN_HEIGHT = 480;

	public static final int WORLD_SCREEN_WIDTH = 32;
	public static final int WORLD_SCREEN_HEIGHT = 48;

	public DroidInvadersMain game;
	public IdiomasManager idiomas;

	public OrthographicCamera guiCam;
	public SpriteBatch batcher;
	public Stage stage;
	public Assets oAssets;
	public TextBounds bounds;

	public Screens(DroidInvadersMain game) {
		stage = game.stage;
		stage.clear();
		idiomas = game.idiomas;
		this.game = game;
		oAssets = game.oAssets;

		guiCam = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		guiCam.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
		batcher = game.batcher;

		InputMultiplexer input = new InputMultiplexer(this, stage);
		Gdx.input.setInputProcessor(input);

		bounds = new TextBounds();

		if (this instanceof MainMenuScreen) {
			Assets.font10.setScale(.65f);
			Assets.font15.setScale(1f);
			Assets.font45.setScale(.85f);
			Assets.font60.setScale(1.2f);
		}
		else if (this instanceof GameScreen) {
			Assets.font15.setScale(1);
			Assets.font45.setScale(.7f);
			Assets.font10.setScale(.65f);
		}
		else if (this instanceof SettingsScreen) {
			Assets.font10.setScale(1);
			Assets.font15.setScale(1f);
			Assets.font45.setScale(.65f);
			Assets.font60.setScale(1);
		}

	}

	@Override
	public void render(float delta) {
		if (delta > .1f)
			delta = .1f;
		update(delta);

		GLCommon gl = Gdx.gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		draw(delta);

		stage.act(delta);
		stage.draw();

	}

	public abstract void draw(float delta);

	public abstract void update(float delta);

	@Override
	public void resize(int width, int height) {
		stage.setViewport(Screens.SCREEN_WIDTH, Screens.SCREEN_HEIGHT, false);

	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {
		Settings.guardar();
	}

	@Override
	public void pause() {
		Assets.music.pause();
	}

	@Override
	public void resume() {
		if (Settings.musicEnabled && !Assets.music.isPlaying())
			Assets.music.play();
	}

	@Override
	public void dispose() {
		stage.dispose();
		batcher.dispose();
	}

}
