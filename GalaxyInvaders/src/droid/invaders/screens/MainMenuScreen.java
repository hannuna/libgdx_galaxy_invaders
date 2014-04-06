package droid.invaders.screens;

import java.text.MessageFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import droid.invaders.Assets;
import droid.invaders.DroidInvadersMain;
import droid.invaders.DroidInvadersMain.Tienda;
import droid.invaders.Settings;
import droid.invaders.game.GameScreen;
import droid.invaders.handlers.AmazonGameServicesHandler;
import droid.invaders.handlers.GoogleGameServicesHandler;

public class MainMenuScreen extends Screens {

	TextButton btPlay, btSettings, btLeaderBoard, btMore, btFacebook;

	Label lbHighestScore;

	ImageButton btSonido, btMusica;
	Image elipseIzq;

	public MainMenuScreen(final DroidInvadersMain game) {
		super(game);

		Table tituloTable = new Table();
		tituloTable.setBackground(Assets.tituloMenuRecuadro);
		Label titulo = new Label(idiomas.getTraduccion("titulo_app"), new LabelStyle(Assets.font60, Color.GREEN));
		titulo.setAlignment(Align.center);
		tituloTable.setSize(265, 100);
		tituloTable.setPosition((SCREEN_WIDTH - 265) / 2, SCREEN_HEIGHT - 110);
		tituloTable.add(titulo).expand().center();

		// El texto se lo pongo en el update
		lbHighestScore = new Label("", new LabelStyle(Assets.font10, Color.GREEN));
		lbHighestScore.setWidth(SCREEN_WIDTH);
		lbHighestScore.setAlignment(Align.center);
		lbHighestScore.setPosition(0, SCREEN_HEIGHT - 120);

		btPlay = new TextButton(idiomas.getTraduccion("play"), Assets.styleTextButtonMenu);
		btPlay.setSize(250, 50);
		btPlay.setPosition(0, 280);
		btPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new GameScreen(game));

			}
		});

		btSettings = new TextButton(idiomas.getTraduccion("settings"), Assets.styleTextButtonMenu);
		btSettings.setSize(300, 50);
		btSettings.setPosition(0, 210);
		btSettings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new SettingsScreen(game));
			}
		});

		btLeaderBoard = new TextButton(idiomas.getTraduccion("leaderboard"), Assets.styleTextButtonMenu);
		btLeaderBoard.setSize(310, 50);
		btLeaderBoard.setPosition(0, 140);
		btLeaderBoard.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);

				if (game.gameServiceHandler instanceof AmazonGameServicesHandler) {
					game.setScreen(new LeaderboardScreen(game));
				}
				else {
					if (!((GoogleGameServicesHandler) game.reqHandler).isSignedInGPGS()) {
						game.dialogs.showDialogSignIn();
						game.reqHandler.showAdBanner();
					}
					else {
						game.setScreen(new LeaderboardScreen(game));
					}
				}
			}
		});

		btMore = new TextButton(idiomas.getTraduccion("more"), Assets.styleTextButtonMenu);
		btMore.setSize(250, 50);
		btMore.setPosition(0, 70);
		btMore.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				game.reqHandler.showMoreGames();

			}
		});

		btFacebook = new TextButton(idiomas.getTraduccion("like_us_to_get_lastest_news"), Assets.styleTextButtonFacebook);
		btFacebook.getLabel().setWrap(true);
		btFacebook.setWidth(170);
		btFacebook.setPosition(SCREEN_WIDTH - btFacebook.getWidth() - 2, 2);
		btFacebook.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Settings.seDioLike = true;
				Assets.playSound(Assets.clickSound);
				game.reqHandler.showFacebook();

			}
		});

		btSonido = new ImageButton(Assets.botonSonidoOn, Assets.botonSonidoOff, Assets.botonSonidoOff);
		btSonido.setSize(40, 40);
		btSonido.setPosition(2, 2);
		if (!Settings.soundEnabled)
			btSonido.setChecked(true);
		btSonido.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Settings.soundEnabled = !Settings.soundEnabled;
				Assets.playSound(Assets.clickSound);
				if (!Settings.soundEnabled)
					btSonido.setChecked(true);
				else
					btSonido.setChecked(false);
			}
		});

		btMusica = new ImageButton(Assets.botonMusicaOn, Assets.botonMusicaOff, Assets.botonMusicaOff);
		btMusica.setSize(40, 40);
		btMusica.setPosition(44, 2);
		if (!Settings.musicEnabled)
			btMusica.setChecked(true);
		btMusica.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Settings.musicEnabled = !Settings.musicEnabled;
				Assets.playSound(Assets.clickSound);
				if (!Settings.musicEnabled) {
					btMusica.setChecked(true);
					Assets.music.pause();
				}
				else {
					btMusica.setChecked(false);
					Assets.music.play();
				}
			}
		});

		// Las medidas se sacaron con una formual de 3 si 480 / 960 x 585 donde 585 es el tamano,
		// 960 es el tamano para lo que se hicieron y 480 es el tamano de la camara
		elipseIzq = new Image(Assets.elipseMenuIzq);
		elipseIzq.setSize(18.5f, 292.5f);
		elipseIzq.setPosition(0, 60);

		stage.addActor(tituloTable);
		stage.addActor(lbHighestScore);

		stage.addActor(btPlay);
		stage.addActor(btSettings);
		stage.addActor(btLeaderBoard);
		stage.addActor(btMore);
		stage.addActor(elipseIzq);
		stage.addActor(btSonido);
		stage.addActor(btMusica);

		if (game.tiendaActual != Tienda.amazon) {
			if (!Settings.seDioLike)
				stage.addActor(btFacebook);
		}

		if (game.gameServiceHandler instanceof GoogleGameServicesHandler) {
			if (Settings.numeroDeVecesQueSeHaJugado == 0 && !((GoogleGameServicesHandler) game.gameServiceHandler).isSignedInGPGS()) {
				game.dialogs.showDialogSignIn();
				game.reqHandler.showAdBanner();
			}
		}

	}

	@Override
	public void update(float delta) {
		// Lo tengo que poner aqui porque cuando se sale del gameScreen los highScores se actualizas en el hide() que eso puede ser
		// despues de que se llama el oncreate en la clase MainMenuScreen
		// Todo este pex para formater que el la puntuacin quede bien traducida http://docs.oracle.com/javase/tutorial/i18n/format/messageFormat.html
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(idiomas.getTraduccion("local_highest_score"));
		Object[] messageArguments = { new Integer(Settings.highScores[0]) };
		String salida = formatter.format(messageArguments);
		lbHighestScore.setText(salida);

	}

	@Override
	public void draw(float delta) {
		guiCam.update();
		batcher.setProjectionMatrix(guiCam.combined);

		batcher.disableBlending();
		Assets.parallaxFondo.render(delta);
	}

	@Override
	public boolean keyDown(int tecleada) {
		if (tecleada == Keys.BACK || tecleada == Keys.ESCAPE) {
			Assets.playSound(Assets.clickSound);
			if (game.dialogs.isDialogShown()) {
				game.dialogs.dismissAll();
			}
			else {

				Gdx.app.exit();
			}
			return true;
		}
		return false;
	}
}
