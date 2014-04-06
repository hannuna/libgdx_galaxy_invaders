package droid.invaders.game;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import droid.invaders.Assets;
import droid.invaders.Settings;
import droid.invaders.frame.AlienShip;
import droid.invaders.frame.BalaNivel1;
import droid.invaders.frame.BalaNivel2;
import droid.invaders.frame.BalaNivel3;
import droid.invaders.frame.BalaNivel4;
import droid.invaders.frame.BalaNormal;
import droid.invaders.frame.Boost;
import droid.invaders.frame.Missil;
import droid.invaders.frame.Nave;
import droid.invaders.screens.Screens;

public class WorldRenderer {

	static final float FRUSTUM_WIDTH = Screens.WORLD_SCREEN_WIDTH;
	static final float FRUSTUM_HEIGHT = Screens.WORLD_SCREEN_HEIGHT;

	World oWorld;
	OrthographicCamera cam;
	SpriteBatch batch;

	public WorldRenderer(SpriteBatch batch, World oWorld) {
		this.oWorld = oWorld;
		this.cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
		this.batch = batch;
	}

	public void render(float deltaTime) {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		renderBackground(deltaTime);
		renderObjects();
	}

	private void renderBackground(float deltaTime) {
		batch.disableBlending();
		batch.begin();
		// batch.draw(Assets.fondo1, cam.position.x - FRUSTUM_WIDTH / 2, cam.position.y - FRUSTUM_HEIGHT / 2, FRUSTUM_WIDTH,FRUSTUM_HEIGHT);
		batch.end();
		if (oWorld.state == World.STATE_RUNNING) {
			Assets.parallaxFondo.render(deltaTime);
		}
		else {// GAMEOVER, PAUSA, READY, ETC
			Assets.parallaxFondo.render(0);
		}

	}

	private void renderObjects() {
		batch.enableBlending();
		batch.begin();
		renderNave();
		renderAliens();
		renderBalaNormalYBalaNiveles();
		renderBalaDoble();
		renderBalaAliens();
		renderMissil();
		renderBoost();
		batch.end();
		if (Settings.drawDebugLines) {
			renderFigurasBounds();
		}

	}

	private void renderNave() {
		TextureRegion keyFrame;
		switch (oWorld.oNave.state) {
			case Nave.NAVE_STATE_EXPLODE:
				keyFrame = Assets.explosionFuego.getKeyFrame(oWorld.oNave.stateTime, false);
				break;
			case Nave.NAVE_STATE_NORMAL:
				if (oWorld.oNave.velocity.x < -3)
					keyFrame = Assets.naveLeft;

				else if (oWorld.oNave.velocity.x > 3)
					keyFrame = Assets.naveRight;

				else
					keyFrame = Assets.nave;
				break;
			default:
			case Nave.NAVE_STATE_BEING_HIT:
				keyFrame = Assets.explosionFuego.getKeyFrame(oWorld.oNave.stateTime, false);
		}
		batch.draw(keyFrame, oWorld.oNave.position.x - Nave.DRAW_WIDTH / 2f, oWorld.oNave.position.y - Nave.DRAW_HEIGHT
				/ 2f, Nave.DRAW_WIDTH, Nave.DRAW_HEIGHT);

		/* Dibuja el escudo de la nave */
		if (oWorld.oNave.vidasEscudo > 0) {
			batch.draw(Assets.shield.getKeyFrame(oWorld.oNave.shieldStateTime, true), oWorld.oNave.position.x - 5.5f,
					oWorld.oNave.position.y - 5.5f, 11, 11);
		}

	}

	private void renderAliens() {
		int len = oWorld.alienShips.size;
		for (int i = 0; i < len; i++) {
			AlienShip oAlienShip = oWorld.alienShips.get(i);
			TextureRegion keyFrame;
			switch (oAlienShip.state) {
				case AlienShip.EXPLOTING:
					keyFrame = Assets.explosionFuego.getKeyFrame(oAlienShip.stateTime, false);
					break;
				default:
					if (oAlienShip.vidasLeft >= 10)
						keyFrame = Assets.alien4;
					else if (oAlienShip.vidasLeft >= 5)
						keyFrame = Assets.alien3;
					else if (oAlienShip.vidasLeft >= 2)
						keyFrame = Assets.alien2;
					else
						// keyFrame = Assets.ufo.getKeyFrame(oAlienShip.stateTime, true);
						// keyFrame=Assets.ufoVerde;
						keyFrame = Assets.alien1;

			}

			batch.draw(keyFrame, oAlienShip.position.x - AlienShip.DRAW_WIDTH / 2f, oAlienShip.position.y
					- AlienShip.DRAW_HEIGHT / 2f, AlienShip.DRAW_WIDTH, AlienShip.DRAW_HEIGHT);
		}

	}

	private void renderBalaNormalYBalaNiveles() {
		int len = oWorld.balaNormalList.size;
		for (int i = 0; i < len; i++) {
			BalaNormal oBalaNormal = oWorld.balaNormalList.get(i);
			batch.draw(Assets.balaNormal, oBalaNormal.position.x - 0.15f, oBalaNormal.position.y - 0.45f, 0.3f, 0.9f);
		}

		// Todas las balas tienen el mismo tama�o porquee antes era una animacion y los sprites todos eran del mismo tama�o
		// Bala nivel 1
		len = oWorld.arrBalaNivel1.size;
		for (int i = 0; i < len; i++) {
			BalaNivel1 oBala = oWorld.arrBalaNivel1.get(i);
			batch.draw(Assets.balaNivel1, oBala.position.x - 1.05f, oBala.position.y - 0.75f, 2.1f, 1.5f);
		}

		// Bala nivel 2
		len = oWorld.arrBalaNivel2.size;
		for (int i = 0; i < len; i++) {
			BalaNivel2 oBala = oWorld.arrBalaNivel2.get(i);
			batch.draw(Assets.balaNivel2, oBala.position.x - 1.05f, oBala.position.y - 0.75f, 2.1f, 1.5f);
		}

		// Bala nivel 3
		len = oWorld.arrBalaNivel3.size;
		for (int i = 0; i < len; i++) {
			BalaNivel3 oBala = oWorld.arrBalaNivel3.get(i);
			batch.draw(Assets.balaNivel3, oBala.position.x - 1.05f, oBala.position.y - 0.75f, 2.1f, 1.5f);
		}

		// Bala nivel 4
		len = oWorld.arrBalaNivel4.size;
		for (int i = 0; i < len; i++) {
			BalaNivel4 oBala = oWorld.arrBalaNivel4.get(i);
			batch.draw(Assets.balaNivel4, oBala.position.x - 1.05f, oBala.position.y - 0.75f, 2.1f, 1.5f);
		}
	}

	private void renderBalaDoble() {
		// int len=oWorld.balasDobles.size();
		// for(int i=0;i<len;i++){
		// BalaDoble oBalaDoble=oWorld.balasDobles.get(i);
		// batch.draw(Assets.balaDoble.getKeyFrame(oBalaDoble.stateTime,false), oBalaDoble.position.x-1.05f, oBalaDoble.position.y-0.75f,2.1f,1.5f);
		// }
	}

	private void renderBalaAliens() {
		int len = oWorld.balasAlien.size;
		for (int i = 0; i < len; i++) {
			BalaNormal oBalaNormal = oWorld.balasAlien.get(i);
			batch.draw(Assets.balaNormalEnemigo, oBalaNormal.position.x - 0.15f, oBalaNormal.position.y - 0.45f, 0.3f,
					0.9f);
		}
	}

	private void renderMissil() {
		int len = oWorld.missiles.size;
		for (int i = 0; i < len; i++) {
			Missil oMissil = oWorld.missiles.get(i);
			float widht, heigth;
			TextureRegion keyFrame;
			switch (oMissil.state) {
				case Missil.STATE_DISPARADO:
					keyFrame = Assets.misil.getKeyFrame(oMissil.stateTime, true);
					widht = .8f;
					heigth = 2.5f;
					break;
				default:
				case Missil.STATE_EXPLOTANDO:
					keyFrame = Assets.explosionFuego.getKeyFrame(oMissil.stateTime, false);
					widht = heigth = 15.0f;
					break;

			}

			// Pa cuando era perseguidor
			// batch.draw(keyFrame, oMissil.position.x-widht/2f, oMissil.position.y-heigth/2f,.5f,.5f,widht,heigth,1,1,oMissil.getVelocity().rotate(-90).angle());
			batch.draw(keyFrame, oMissil.position.x - widht / 2f, oMissil.position.y - heigth / 2f, widht, heigth);

		}
	}

	private void renderBoost() {
		int len = oWorld.boosts.size;
		for (int i = 0; i < len; i++) {
			Boost oBoost = oWorld.boosts.get(i);
			TextureRegion keyFrame;

			switch (oBoost.tipo) {
				case Boost.VIDA_EXTRA:
					keyFrame = Assets.upgLife;

					break;
				case Boost.UPGRADE_NIVEL_ARMAS:
					keyFrame = Assets.boost1;
					break;
				case Boost.MISSIL_EXTRA:
					keyFrame = Assets.boost2;
					break;
				default:// Boost.SHIELD
					keyFrame = Assets.boost3;

			}

			batch.draw(keyFrame, oBoost.position.x - Boost.DRAW_SIZE / 2f, oBoost.position.y - Boost.DRAW_SIZE / 2f,
					Boost.DRAW_SIZE, Boost.DRAW_SIZE);
		}
	}

	private void renderFigurasBounds() {
		ShapeRenderer render = new ShapeRenderer();
		render.setProjectionMatrix(cam.combined);// testing propuses
		render.begin(ShapeType.Line);

		Rectangle naveBounds = oWorld.oNave.boundsRectangle;
		render.rect(naveBounds.x, naveBounds.y, naveBounds.width, naveBounds.height);

		Iterator<AlienShip> it = oWorld.alienShips.iterator();
		while (it.hasNext()) {
			AlienShip obj = it.next();
			Circle objBounds = obj.boundsCircle;
			render.circle(objBounds.x, objBounds.y, objBounds.radius);
		}

		Iterator<Boost> itBoost = oWorld.boosts.iterator();
		while (itBoost.hasNext()) {
			Boost obj = itBoost.next();
			Circle objBounds = obj.boundsCircle;
			render.circle(objBounds.x, objBounds.y, objBounds.radius);
		}

		render.end();

	}

}
