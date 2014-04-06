package droid.invaders.free;

import android.os.Bundle;
import droid.invaders.DroidInvadersMain.Tienda;

public class DroidInvadersActivity extends GalaxyInvadersAGC {

	public void onCreate(Bundle savedInstanceState) {
		tienda = Tienda.amazon;
		super.onCreate(savedInstanceState);
	}

}