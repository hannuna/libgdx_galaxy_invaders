package droid.invaders;

import droid.invaders.handlers.AmazonGameServicesHandler;
import droid.invaders.handlers.GameServicesHandler;

public class Achievements {

	GameServicesHandler gameServiceHandler;

	final String THE_BEST_DEFENSE;
	final String YOU_SHALL_NOT_PASS;
	final String MASTER_OF_THE_UNIVERSE;
	final String STAYING_ALIVE;
	final String SURVIVOR;
	final String UNBREAKABLE;

	public Achievements(DroidInvadersMain game) {
		gameServiceHandler = game.gameServiceHandler;

		if (gameServiceHandler instanceof AmazonGameServicesHandler) {
			THE_BEST_DEFENSE = "theBestDefense";
			YOU_SHALL_NOT_PASS = "youShallNotPass";
			MASTER_OF_THE_UNIVERSE = "masterOfTheUniverse";
			STAYING_ALIVE = "stayingAlive";
			SURVIVOR = "survivor";
			UNBREAKABLE = "unbreakable";
		}
		else {// Google game services
			THE_BEST_DEFENSE = "CgkI4NW_mfYCEAIQAw";
			YOU_SHALL_NOT_PASS = "CgkI4NW_mfYCEAIQBQ";
			MASTER_OF_THE_UNIVERSE = "CgkI4NW_mfYCEAIQAA";
			STAYING_ALIVE = "CgkI4NW_mfYCEAIQAQ";
			SURVIVOR = "CgkI4NW_mfYCEAIQAg";
			UNBREAKABLE = "CgkI4NW_mfYCEAIQBA";
		}
	}

	public void checkLevel(int nivel, boolean didDie) {

		if (nivel == 25) {
			gameServiceHandler.unlockAchievement(THE_BEST_DEFENSE);// The best defense

		}
		else if (nivel == 50) {
			gameServiceHandler.unlockAchievement(YOU_SHALL_NOT_PASS);// You shall not pass
		}
		else if (nivel == 100) {
			gameServiceHandler.unlockAchievement(MASTER_OF_THE_UNIVERSE);// Master of the universe
		}

		if (!didDie) {
			if (nivel == 25) {
				gameServiceHandler.unlockAchievement(STAYING_ALIVE);// staying alive

			}
			else if (nivel == 40) {
				gameServiceHandler.unlockAchievement(SURVIVOR);// Survivor
			}
			else if (nivel == 60) {
				gameServiceHandler.unlockAchievement(UNBREAKABLE);// Unbreakable
			}
		}
	}

}
