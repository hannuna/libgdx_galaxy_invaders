package droid.invaders.handlers;

public interface GameServicesHandler {

	/**
	 * Este metodo abstrae a GPGS o a AGC
	 * 
	 * @param score
	 */
	public void submitScore(int score);

	/**
	 * Este metodo abstrae a GPGS o a AGC
	 * 
	 * @param score
	 */
	public void unlockAchievement(String achievementId);

	/**
	 * Este metodo abstrae a GPGS o a AGC
	 * 
	 * @param score
	 */
	public void getLeaderboard();

	/**
	 * Este metodo abstrae a GPGS o a AGC
	 * 
	 * @param score
	 */
	public void getAchievements();

}
