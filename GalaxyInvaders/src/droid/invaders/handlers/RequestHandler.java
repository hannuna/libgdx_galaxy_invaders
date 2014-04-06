package droid.invaders.handlers;

public interface RequestHandler {
	public void showRater();

	public void showInterstitial();

	public void showFacebook();

	public void showMoreGames();

	public void shareOnFacebook(final String mensaje, final String caption, final String descripcion);

	public void showAdBanner();

	public void hideAdBanner();
}
