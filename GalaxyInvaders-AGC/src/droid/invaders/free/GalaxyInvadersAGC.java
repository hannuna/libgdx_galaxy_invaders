package droid.invaders.free;

import java.util.EnumSet;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.AmazonGamesStatus;

import droid.invaders.DroidInvadersMain;
import droid.invaders.MainActivity;
import droid.invaders.handlers.AmazonGameServicesHandler;

public class GalaxyInvadersAGC extends MainActivity implements AmazonGameServicesHandler {

	AmazonGamesClient amazonClient;

	ProgressDialog dialogWait;
	final String pleaseWait = "Please Wait...";

	AmazonGamesCallback callback = new AmazonGamesCallback() {
		@Override
		public void onServiceNotReady(AmazonGamesStatus status) {
			if (dialogWait != null)
				dialogWait.dismiss();
		}

		@Override
		public void onServiceReady(AmazonGamesClient amazonGamesClient) {
			amazonClient = amazonGamesClient;
			if (dialogWait != null)
				dialogWait.dismiss();
			// ready to use GameCircle
		}
	};

	EnumSet<AmazonGamesFeature> myGameFeatures = EnumSet.of(AmazonGamesFeature.Achievements,
			AmazonGamesFeature.Leaderboards);

	public void onCreate(Bundle savedInstanceState) {
		game = new DroidInvadersMain(tienda, this, this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		AmazonGamesClient.initialize(this, callback, myGameFeatures);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (amazonClient != null) {
			AmazonGamesClient.release();
		}
	}

	@Override
	public void onBackPressed() {
		if (dialogWait != null) {
			dialogWait.dismiss();
		}
	}

	@Override
	public void submitScore(int score) {
		if (amazonClient == null)
			return;
		amazonClient.getLeaderboardsClient().submitScore("HighScoreID", score);

	}

	@Override
	public void unlockAchievement(String achievementId) {
		if (amazonClient == null)
			return;
		amazonClient.getAchievementsClient().updateProgress(achievementId, 100.0f);

	}

	@Override
	public void getLeaderboard() {
		if (amazonClient == null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					dialogWait = ProgressDialog.show(GalaxyInvadersAGC.this, "", pleaseWait, true, true);
				}
			});
			AmazonGamesClient.initialize(this, callback, myGameFeatures);

		}
		else
			amazonClient.getLeaderboardsClient().showLeaderboardOverlay("HighScoreID");

	}

	@Override
	public void getAchievements() {
		if (amazonClient == null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					dialogWait = ProgressDialog.show(GalaxyInvadersAGC.this, "", pleaseWait, true, true);
				}
			});
			AmazonGamesClient.initialize(this, callback, myGameFeatures);
		}
		else
			amazonClient.getAchievementsClient().showAchievementsOverlay();

	}

}