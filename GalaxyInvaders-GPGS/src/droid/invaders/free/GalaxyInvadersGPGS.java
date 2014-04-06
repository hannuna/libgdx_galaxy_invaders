package droid.invaders.free;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

import droid.invaders.DroidInvadersMain;
import droid.invaders.MainActivity;
import droid.invaders.handlers.GoogleGameServicesHandler;

public class GalaxyInvadersGPGS extends MainActivity implements
		GoogleGameServicesHandler, GameHelperListener {

	private GameHelper gameHelper;

	public void onCreate(Bundle savedInstanceState) {
		game = new DroidInvadersMain(tienda, this, this);
		super.onCreate(savedInstanceState);
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.setup(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		gameHelper.onActivityResult(request, response, data);
	}

	@Override
	public void submitScore(int score) {
		if (!isSignedInGPGS())// por si no esta logeado
			return;
		Games.Leaderboards.submitScore(gameHelper.getApiClient(),
				"CgkI4NW_mfYCEAIQBg", score);

	}

	@Override
	public void unlockAchievement(String achievementId) {
		if (!isSignedInGPGS())// por si no esta logeado
			return;
		Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);

	}

	@Override
	public void getLeaderboard() {
		startActivityForResult(
				Games.Leaderboards.getLeaderboardIntent(
						gameHelper.getApiClient(), "CgkI4NW_mfYCEAIQBg"), 100);

	}

	@Override
	public void getAchievements() {
		startActivityForResult(
				Games.Achievements.getAchievementsIntent(gameHelper
						.getApiClient()), 101);

	}

	@Override
	public boolean isSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void signInGPGS() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (final Exception ex) {
		}

	}

	public void signOutGPGS() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.signOut();
				}
			});
		} catch (final Exception ex) {
		}

	}

	@Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub

	}

}