package droid.invaders;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import droid.invaders.DroidInvadersMain.Tienda;
import droid.invaders.handlers.GoogleGameServicesHandler;
import droid.invaders.handlers.RequestHandler;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "DroidInvaders";
		cfg.useGL20 = false;
		cfg.width = 540;
		cfg.height = 960;
		// //
		// TexturePacker.Settings set = new TexturePacker.Settings();
		// set.combineSubdirectories = true;
		// set.flattenPaths = true;
		// set.ignoreBlankImages = false;
		// set.filterMag = TextureFilter.Linear;
		// set.filterMin = TextureFilter.Linear;

		// //
		// TexturePacker.process(set,
		// "/Users/Yayo/Pictures/Games/Galaxy Invasion/Imagenes",
		// "/Users/Yayo/Documents/eclipseAndroid/GalaxyInvaders-P/GalaxyInvaders-android/assets/data/",
		// "atlasMap.txt");

		new LwjglApplication(new DroidInvadersMain(Tienda.otros, handler,
				gameHander), cfg);
	}

	static RequestHandler handler = new RequestHandler() {

		@Override
		public void showRater() {
			// TODO Auto-generated method stub

		}

		@Override
		public void showMoreGames() {
			// TODO Auto-generated method stub

		}

		@Override
		public void showInterstitial() {
			// TODO Auto-generated method stub

		}

		@Override
		public void showFacebook() {
			// TODO Auto-generated method stub

		}

		@Override
		public void showAdBanner() {
			// TODO Auto-generated method stub

		}

		@Override
		public void shareOnFacebook(String mensaje, String caption,
				String descripcion) {
			// TODO Auto-generated method stub

		}

		@Override
		public void hideAdBanner() {
			// TODO Auto-generated method stub

		}
	};

	static GoogleGameServicesHandler gameHander = new GoogleGameServicesHandler() {

		@Override
		public void unlockAchievement(String achievementId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void submitScore(int score) {
			// TODO Auto-generated method stub

		}

		@Override
		public void getLeaderboard() {
			// TODO Auto-generated method stub

		}

		@Override
		public void getAchievements() {
			// TODO Auto-generated method stub

		}

		@Override
		public void signOutGPGS() {
			// TODO Auto-generated method stub

		}

		@Override
		public void signInGPGS() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isSignedInGPGS() {
			// TODO Auto-generated method stub
			return false;
		}
	};
}