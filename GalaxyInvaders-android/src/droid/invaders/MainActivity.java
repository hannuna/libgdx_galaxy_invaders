package droid.invaders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Privacy;
import com.sromku.simple.fb.entities.Privacy.PrivacySettings;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.StartAppAd;

import droid.invaders.DroidInvadersMain.Tienda;
import droid.invaders.game.GameScreen;
import droid.invaders.handlers.RequestHandler;

public class MainActivity extends AndroidApplication implements RequestHandler {

	String admobIdBanner = "52bfdeb5ac394aa4";
	String admobIdInterstitial = "c88fea01edbc4079";

	String StartppDeveloperId = "104087430";
	String StartappAppId = "207835163";

	String facebookAppID = "1446232218929405";

	public Tienda tienda;
	protected DroidInvadersMain game;

	InterstitialAd interAdmob;
	AdView bannerAdmob;
	AdRequest adRequest;
	StartAppAd interStartApp;

	protected SimpleFacebook oFacebook;
	Privacy privacy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StartAppAd.init(this, StartppDeveloperId, StartappAppId);
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		// Create the libgdx View
		View gameView = initializeForView(game, cfg);
		bannerAdmob = new AdView(this);
		bannerAdmob.setAdSize(AdSize.SMART_BANNER);
		bannerAdmob.setAdUnitId(admobIdBanner);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);

		layout.addView(bannerAdmob, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.addView(gameView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

		setContentView(layout);

		adRequest = new AdRequest.Builder()//
				// .addTestDevice("1854CA4BA5218E72358728EB28DC2CED")//
				.build();

		bannerAdmob.loadAd(adRequest);
		interAdmob = new InterstitialAd(this);
		interAdmob.setAdUnitId(admobIdInterstitial);
		interAdmob.loadAd(adRequest);

		interStartApp = new StartAppAd(this);
	}

	@Override
	protected void onResume() {
		Permission[] permissions = new Permission[] { Permission.PUBLISH_ACTION };
		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder().setAppId(facebookAppID)
		// .setNamespace("")
				.setPermissions(permissions).build();
		privacy = new Privacy.Builder().setPrivacySettings(PrivacySettings.EVERYONE).build();

		SimpleFacebook.setConfiguration(configuration);
		oFacebook = SimpleFacebook.getInstance(this);
		com.facebook.AppEventsLogger.activateApp(this, facebookAppID);
		super.onResume();
		interStartApp.onResume();
		interStartApp.loadAd();
		bannerAdmob.resume();
	}

	@Override
	protected void onPause() {
		bannerAdmob.pause();
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		bannerAdmob.destroy();
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
		oFacebook.onActivityResult(this, request, response, data);
	}

	@Override
	public void onBackPressed() {

	}

	// ------
	public void showInterstitial() {
		runOnUiThread(new Runnable() {
			public void run() {
				showStartAppInterstitial();
			}

		});
	}

	private void showGoogleInterstitial() {
		AdListener adListener = new AdListener() {
			@Override
			public void onAdOpened() {
				if (GameScreen.state == GameScreen.GAME_RUNNING)
					GameScreen.state = GameScreen.GAME_PAUSE;
				super.onAdOpened();
			}
		};
		interAdmob.setAdListener(adListener);
		interAdmob.show();
		interAdmob.loadAd(adRequest);

	}

	private void showStartAppInterstitial() {
		if (interStartApp.isReady()) {
			interStartApp.showAd(new AdDisplayListener() {

				@Override
				public void adHidden(com.startapp.android.publish.Ad arg0) {

				}

				@Override
				public void adDisplayed(com.startapp.android.publish.Ad arg0) {
					if (GameScreen.state == GameScreen.GAME_RUNNING)
						GameScreen.state = GameScreen.GAME_PAUSE;
				}
			});

		}
		else {

			showGoogleInterstitial();
		}
		interStartApp.loadAd();

	}

	public void showFacebook() {
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/216392968461240"));
					startActivity(intent);
				}
				catch (Exception e) {
					Gdx.net.openURI("https://www.facebook.com/Tiarsoft");
				}
			}
		});

	}

	ProgressDialog progress;

	@Override
	public void shareOnFacebook(final String mensaje, final String caption, final String descripcion) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (oFacebook.isLogin()) {

					// build feed
					Feed feed =
							new Feed.Builder()
									.setPrivacy(privacy)
									.setMessage(mensaje)
									.setName("Galaxy invasion")
									.setCaption("Get it now!")
									.setDescription(
											"Galaxy invasion is a shooting game, the point of the game is to defeat waves of UFOs to earn as many points as possible.")
									.setPicture(
											"https://dl.dropboxusercontent.com/u/78073642/IconosWeb/droidInvaders.png")
									.addProperty("Also available for iOS", "Download",
											"https://itunes.apple.com/us/app/droid-invaders/id769496537?ls=1&mt=8")
									.setLink("https://play.google.com/store/apps/details?id=com.tiar.galaxyinvasion")
									.addAction("Tiarsoft", "https://www.facebook.com/Tiarsoft").build();

					// publish the feed
					oFacebook.publish(feed, new OnPublishListener() {

						@Override
						public void onFail(String reason) {
							progress.hide();

						}

						@Override
						public void onException(Throwable throwable) {
							progress.hide();

						}

						@Override
						public void onThinking() {
							progress = ProgressDialog.show(MainActivity.this, "Wait..", "Sharing Score");

						}

						@Override
						public void onComplete(String id) {

							progress.hide();

						}

					});

				}
				else {
					oFacebook.login(new OnLoginListener() {
						@Override
						public void onFail(String reason) {
							Log.e("Facebook", reason);
						}

						@Override
						public void onException(Throwable throwable) {
							Log.e("Facebook", throwable.toString());
						}

						@Override
						public void onThinking() {
						}

						@Override
						public void onLogin() {
							shareOnFacebook(mensaje, caption, descripcion);
						}

						@Override
						public void onNotAcceptingPermissions(Type type) {
							Log.w("Facebook", "User didn't accept read permissions");

						}
					});

				}

			}
		});

	}

	@Override
	public void showMoreGames() {
		Gdx.net.openURI("http://ad.leadboltads.net/show_app_wall?section_id=157481307");
	}

	public void showRater() {
		runOnUiThread(new Runnable() {
			public void run() {
				String nombrePaquete = MainActivity.this.getPackageName();
				String linkTienda;
				if (tienda == Tienda.amazon) {
					linkTienda = "amzn://apps/android?p=";
				}
				else if (tienda == Tienda.samsung) {
					linkTienda = "samsungapps://ProductDetail/";
				}
				else if (tienda == Tienda.slideMe) {
					linkTienda = "sam://details?id=";
				}
				else {// GooglePlay, Yandex, Otros
					linkTienda = "market://details?id=";
				}

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(linkTienda + nombrePaquete));
				// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

				Settings.seCalifico = true;
				Settings.guardar();
				MainActivity.this.startActivity(intent);

			}
		});

	}

	@Override
	public void showAdBanner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideAdBanner() {
		// TODO Auto-generated method stub

	}
}