package droid.invaders.handlers;

public interface GoogleGameServicesHandler extends GameServicesHandler {

	public boolean isSignedInGPGS();

	public void signInGPGS();

	public void signOutGPGS();

}
