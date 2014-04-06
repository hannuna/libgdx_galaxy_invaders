package droid.invaders.free;

import android.os.Bundle;
import droid.invaders.DroidInvadersMain.Tienda;

public class GalaxyInvadersActivity extends GalaxyInvadersGPGS {

	public void onCreate(Bundle savedInstanceState) {
		tienda = Tienda.googlePlay;
		super.onCreate(savedInstanceState);
	}

}