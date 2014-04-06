package droid.invaders.free;

import android.os.Bundle;
import droid.invaders.DroidInvadersMain.Tienda;

public class DroidInvadersActivity extends GalaxyInvadersAGC {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		tienda = Tienda.otros;
		super.onCreate(savedInstanceState);
	}

}