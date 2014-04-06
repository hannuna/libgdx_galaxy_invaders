package droid.invaders;

import org.robovm.bindings.gpgs.GPGToastPlacement;
import org.robovm.bindings.gpp.GPPURLHandler;
import org.robovm.bindings.mopub.MPAdView;
import org.robovm.bindings.mopub.MPAdViewDelegate;
import org.robovm.bindings.mopub.MPConstants;
import org.robovm.bindings.mopub.MPInterstitialAdController;
import org.robovm.bindings.mopub.sample.MPAdViewController;
import org.robovm.bindings.playservices.PlayServicesManager;
import org.robovm.bindings.playservices.PlayServicesManager.LoginCallback;
import org.robovm.cocoatouch.coregraphics.CGRect;
import org.robovm.cocoatouch.foundation.NSAutoreleasePool;
import org.robovm.cocoatouch.foundation.NSDictionary;
import org.robovm.cocoatouch.foundation.NSError;
import org.robovm.cocoatouch.foundation.NSObject;
import org.robovm.cocoatouch.foundation.NSURL;
import org.robovm.cocoatouch.uikit.UIApplication;
import org.robovm.cocoatouch.uikit.UIScreen;
import org.robovm.cocoatouch.uikit.UIViewController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import droid.invaders.DroidInvadersMain.Tienda;
import droid.invaders.handlers.GoogleGameServicesHandler;
import droid.invaders.handlers.RequestHandler;

public class RobovmLauncher extends IOSApplication.Delegate implements RequestHandler, GoogleGameServicesHandler {

	private IOSApplication gdxApp;
	private PlayServicesManager manager;
	private DroidInvadersMain game;
	final static Tienda tienda = Tienda.appleStore;

	private final String IdBanner = "a520a2f8d6a244888c9e1ba47fafd290";
	private final String IdInterstitial = "bf6b02f71cb7490c998ca8d367a27350";

	private final String clientID = "100448332512-6n372j6lgqccnva2km18k097ck74le5r.apps.googleusercontent.com";

	MPAdViewController bannerController;
	MPInterstitialAdController interstitialController;
	private UIViewController rootViewController;

	@Override
	protected IOSApplication createApplication() {
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		game = new DroidInvadersMain(tienda, this, this);
		gdxApp = new IOSApplication(game, config);
		return gdxApp;

	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, RobovmLauncher.class);
		pool.drain();
	}

	private LoginCallback loginCallback = new LoginCallback() {

		@Override
		public void success() {

		}

		@Override
		public void error(NSError error) {

		}
	};

	@SuppressWarnings("rawtypes")
	@Override
	public boolean didFinishLaunching(UIApplication application, NSDictionary launchOptions) {
		final boolean result = super.didFinishLaunching(application, launchOptions);

		// // // Banner
		MPAdView banner = new MPAdView(IdBanner, MPConstants.MOPUB_BANNER_SIZE);
		float bannerWidth = UIScreen.getMainScreen().getApplicationFrame().size().width();
		float bannerHeight =
				bannerWidth / MPConstants.MOPUB_BANNER_SIZE.width() * MPConstants.MOPUB_BANNER_SIZE.height();
		banner.setFrame(new CGRect(0, 0, bannerWidth, bannerHeight));
		// banner.setBackgroundColor(new UIColor(1, 0, 0, 1)); // Remove this after testing.
		bannerController = new MPAdViewController(banner);
		MPAdViewDelegate bannerDelegate = new MPAdViewDelegate.Adapter() {
			@Override
			public UIViewController getViewController() {
				return bannerController;
			}
		};
		banner.setDelegate(bannerDelegate);
		bannerController.getView().addSubview(banner);
		banner.loadAd();

		// UIViewController root = gdxApp.getUIViewController();
		// UIView rootView = root.getView().getSuperview();
		// UIView libgdx = root.getView();
		// libgdx.setFrame(new CGRect(0, bannerHeight + 1, UIScreen.getMainScreen().getApplicationFrame().size().width(), UIScreen.getMainScreen().getApplicationFrame().size().height() - 1 - bannerHeight));
		// rootView.addSubview(banner);

		interstitialController = MPInterstitialAdController.getAdController(IdInterstitial);
		interstitialController.loadAd();

		rootViewController = new UIViewController();
		rootViewController.getView().setUserInteractionEnabled(false);
		application.getKeyWindow().addSubview(rootViewController.getView());

		manager = new PlayServicesManager();
		manager.setClientId(clientID);
		manager.setViewController(rootViewController);
		manager.setToastLocation(PlayServicesManager.TOAST_BOTH, GPGToastPlacement.GPGToastPlacementTop);
		manager.setUserDataToRetrieve(true, false);
		manager.setLoginCallback(loginCallback);
		manager.didFinishLaunching();
		return result;
	}

	@Override
	public boolean openURL(UIApplication application, NSURL url, String sourceApplication, NSObject annotation) {
		return GPPURLHandler.handleURL(url, sourceApplication, annotation);
	}

	public IOSApplication getApplication() {
		return gdxApp;
	}

	// Game services google

	@Override
	public void showInterstitial() {
		interstitialController.show(rootViewController);
		interstitialController.loadAd();

	}

	@Override
	public void showAdBanner() {
		((IOSApplication) Gdx.app).getUIViewController().getView().addSubview(bannerController.getView());

	}

	@Override
	public void hideAdBanner() {
		bannerController.getView().removeFromSuperview();

	}

	@Override
	public void showFacebook() {
		Gdx.net.openURI("https://www.facebook.com/Tiarsoft");
	}

	@Override
	public void showRater() {
		Settings.seCalifico = true;
		Settings.guardar();
		Gdx.net.openURI("https://itunes.apple.com/us/app/droid-invaders/id769496537?ls=1&mt=8");

	}

	@Override
	public void showMoreGames() {
		Gdx.net.openURI("http://ad.leadboltads.net/show_app_wall?section_id=166127026");
	}

	@Override
	public void shareOnFacebook(String mensaje, String caption, String descripcion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void submitScore(int score) {
		if (isSignedInGPGS())
			manager.postScore("CgkI4NW_mfYCEAIQBg", score);

	}

	@Override
	public void unlockAchievement(String achievementId) {
		manager.unlockAchievement(achievementId);

	}

	@Override
	public void getLeaderboard() {
		manager.showLeaderboardsPicker();

	}

	@Override
	public void getAchievements() {
		manager.showAchievements();

	}

	@Override
	public boolean isSignedInGPGS() {
		return manager.isLoggedIn();
	}

	@Override
	public void signInGPGS() {
		manager.login();

	}

	@Override
	public void signOutGPGS() {
		manager.logout();

	}

}
