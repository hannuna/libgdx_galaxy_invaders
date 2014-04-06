package droid.invaders.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import droid.invaders.Assets;
import droid.invaders.DroidInvadersMain;
import droid.invaders.handlers.GoogleGameServicesHandler;

public class LeaderboardScreen extends Screens {

	TextButton btLeaderBoard, btAchievements, btBack, btSignOut;
	Image elipseIzq;

	public LeaderboardScreen(final DroidInvadersMain game) {
		super(game);

		btBack = new TextButton(idiomas.getTraduccion("back"), Assets.styleTextButtonBack);
		btBack.pad(0, 15, 35, 0);
		btBack.setSize(63, 63);
		btBack.setPosition(SCREEN_WIDTH - 63, SCREEN_HEIGHT - 63);
		btBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				game.setScreen(new MainMenuScreen(game));
			}
		});

		btLeaderBoard = new TextButton(idiomas.getTraduccion("leaderboard"), Assets.styleTextButtonMenu);
		btLeaderBoard.setHeight(50);// Altura 50
		btLeaderBoard.size(50, 0);// Al ancho actual le agregamos 50
		btLeaderBoard.setPosition(0, 245);
		btLeaderBoard.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				game.gameServiceHandler.getLeaderboard();

			}
		});

		btAchievements = new TextButton(idiomas.getTraduccion("achievements"), Assets.styleTextButtonMenu);
		btAchievements.setHeight(50);// Altura 50
		btAchievements.size(50, 0);// Al ancho actual le agregamos 50
		btAchievements.setPosition(0, 150);
		btAchievements.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				game.gameServiceHandler.getAchievements();
			}
		});

		btSignOut = new TextButton(idiomas.getTraduccion("sign_out"), new TextButtonStyle(Assets.btSignInUp, Assets.btSignInDown, null, Assets.font15));
		btSignOut.getLabel().setWrap(true);
		btSignOut.setWidth(140);
		btSignOut.setPosition(2, 2);
		btSignOut.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.playSound(Assets.clickSound);
				((GoogleGameServicesHandler) game.gameServiceHandler).signOutGPGS();
				game.setScreen(new MainMenuScreen(game));
			}
		});

		elipseIzq = new Image(Assets.elipseMenuIzq);
		elipseIzq.setSize(18.5f, 250.5f);
		elipseIzq.setPosition(0, 105);

		if (game.gameServiceHandler instanceof GoogleGameServicesHandler)
			stage.addActor(btSignOut);
		stage.addActor(btAchievements);
		stage.addActor(btLeaderBoard);
		stage.addActor(btBack);
		stage.addActor(elipseIzq);

	}

	@Override
	public void draw(float delta) {
		guiCam.update();
		batcher.setProjectionMatrix(guiCam.combined);

		batcher.disableBlending();
		Assets.parallaxFondo.render(delta);

	}

	@Override
	public void update(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int tecleada) {
		if (tecleada == Keys.BACK || tecleada == Keys.ESCAPE) {
			Assets.playSound(Assets.clickSound);
			game.setScreen(new MainMenuScreen(game));
			return true;
		}
		return false;
	}
}
