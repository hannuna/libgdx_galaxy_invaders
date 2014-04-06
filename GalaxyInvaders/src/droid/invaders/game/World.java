package droid.invaders.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;

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

public class World {
	public interface WorldListener {
		public void coin();

		public void explosion();

		public void missilFire();
	}

	public static float WIDTH = Screens.WORLD_SCREEN_WIDTH;
	public static final float HEIGHT = Screens.WORLD_SCREEN_HEIGHT;

	public static final int STATE_RUNNING = 0;
	public static final int STATE_GAME_OVER = 1;
	public static final int STATE_PAUSED = 2;
	WorldListener listener;
	Nave oNave;
	Array<AlienShip> alienShips;
	Array<BalaNormal> balaNormalList;
	Array<BalaNormal> balasAlien;
	Array<Boost> boosts;
	Array<BalaNivel1> arrBalaNivel1;
	Array<BalaNivel2> arrBalaNivel2;
	Array<BalaNivel3> arrBalaNivel3;
	Array<BalaNivel4> arrBalaNivel4;
	Array<Missil> missiles;

	Random oRan;
	int missilesDisponibles;
	int puntuacion;
	int nivel;// el nivel del juego, cada vez que se termina una ronda se aumenta un nivel
	int state;
	int extraChanceDrop;
	int maxMissilesRonda, maxBalasRonda;
	int nivelBala;// Es el nivel en que se encuentra la bala actual cada vez que se agarra un boost aumenta
	float tiempoCargando;
	float probs;// Esta variable ira aumentando cada nivel para dar mas dificultad al juego
	float aumentoVel;
	float menosTiempoCargando;

	public World(WorldListener listener) {
		this.listener = listener;
		oNave = new Nave(WIDTH / 2f, 9.5f);
		alienShips = new Array<AlienShip>();
		balaNormalList = new Array<BalaNormal>();
		balasAlien = new Array<BalaNormal>();
		boosts = new Array<Boost>();
		arrBalaNivel1 = new Array<BalaNivel1>();
		arrBalaNivel2 = new Array<BalaNivel2>();
		arrBalaNivel3 = new Array<BalaNivel3>();
		arrBalaNivel4 = new Array<BalaNivel4>();
		missiles = new Array<Missil>();
		// if (Settings.seCalifico) {
		missilesDisponibles = 5;
		oNave.vidas = 5;
		extraChanceDrop = 5;
		maxMissilesRonda = 5;
		maxBalasRonda = 5;
		// }
		// else {
		// missilesDisponibles = 3;
		// extraChanceDrop = 0;
		// maxMissilesRonda = 1;
		// maxBalasRonda = 3;
		// }
		puntuacion = 0;
		nivel = 0;
		tiempoCargando = 0;
		menosTiempoCargando = 0;
		probs = aumentoVel = 0;
		oRan = new Random((long) Gdx.app.getGraphics().getDeltaTime() * 10000);
		state = STATE_RUNNING;
		agregarAliens();
	}

	private void agregarAliens() {
		nivel++;

		// Cada 2 niveles aumento los missiles que se pueden disparar
		if (nivel % 2f == 0) {
			maxMissilesRonda++;
			maxBalasRonda++;
		}
		float x;
		float y = 21f;
		int vida = 1;

		boolean vidaAlterable = false;
		if (nivel > 2) {
			vidaAlterable = true;
			probs += 0.2f;
			aumentoVel += .02f;
		}

		// agregare 25 aliens 5x5 columnas de 5 filas de 5
		for (int col = 0; col < 6; col++) {
			y += 3.8;
			x = 1.5f;
			for (int ren = 0; ren < 6; ren++) {
				if (vidaAlterable)
					vida = oRan.nextInt(3) + 1 + (int) probs;//
				alienShips.add(new AlienShip(vida, aumentoVel, x, y));
				x += 4.5f;
			}
		}

	}

	public void update(float deltaTime, float accelX, boolean seDisparo, boolean seDisparoMissil) {
		updateNave(deltaTime, accelX);
		updateAlienShip(deltaTime);// <-- aqui mismo se agregan las balas de los aliens. Se updatean en otro metodo

		updateBalaNormalYConNivel(deltaTime, seDisparo);
		updateMissil(deltaTime, seDisparoMissil);
		updateBalaAlien(deltaTime);
		/* Los boost se agregan cada vez que se da hit a un alienShip. Aqui solo se actualizan */
		updateBoost(deltaTime);

		if (oNave.state != Nave.NAVE_STATE_EXPLODE) {
			checkCollision();
		}
		checkGameOver();
		checkLevelEnd();// Cuando ya mate a todos los aliens
	}

	private void updateNave(float deltaTime, float accelX) {
		if (oNave.state != Nave.NAVE_STATE_EXPLODE) {
			oNave.velocity.x = -accelX / Settings.aceletometerSensitive * Nave.NAVE_MOVE_SPEED;
		}
		oNave.update(deltaTime);
	}

	private void updateAlienShip(float deltaTime) {

		Iterator<AlienShip> it = alienShips.iterator();
		while (it.hasNext()) {
			AlienShip oAlienShip = it.next();
			oAlienShip.update(deltaTime);

			/* AgregoBalas a los Aliens */
			if (oRan.nextInt(5000) < (1 + probs) && oAlienShip.state != AlienShip.EXPLOTING) {
				float x = oAlienShip.position.x;
				float y = oAlienShip.position.y;
				balasAlien.add(new BalaNormal(x, y, true));
			}

			/* Elimino si ya explotaron */
			if (oAlienShip.state == AlienShip.EXPLOTING && oAlienShip.stateTime > AlienShip.TIEMPO_EXPLODE) {
				it.remove();
			}

			/* Si los alien llegan hacia abajo pierdes automaticamente */
			if (oAlienShip.position.y < 9.5f) {
				state = STATE_GAME_OVER;
			}

		}

	}

	private void updateBalaAlien(float deltaTime) {
		/* Ahora Actualizo //Recalculo len por si se disparo una bala nueva */

		Iterator<BalaNormal> it = balasAlien.iterator();
		while (it.hasNext()) {
			BalaNormal oBalaNormal = it.next();
			if (oBalaNormal.position.y < -2)
				oBalaNormal.hitTarget();// para que no llegue tan lejos el misil
			oBalaNormal.update(deltaTime);
			if (oBalaNormal.state == BalaNormal.STATE_EXPLOTANDO) {
				it.remove();
			}

		}

	}

	private void updateBalaNormalYConNivel(float deltaTime, boolean seDisparo) {
		int len;
		float x = oNave.position.x;
		float y = oNave.position.y + 1;
		switch (nivelBala) {
			case 0:
				len = balaNormalList.size;
				if (seDisparo && len < maxBalasRonda) {
					balaNormalList.add(new BalaNormal(x, y));
				}
				break;
			case 1:
				len = arrBalaNivel1.size;
				if (seDisparo && len < maxBalasRonda) {
					arrBalaNivel1.add(new BalaNivel1(x, y));
				}
				break;
			case 2:
				len = arrBalaNivel2.size;
				if (seDisparo && len < maxBalasRonda) {
					arrBalaNivel2.add(new BalaNivel2(x, y));
				}
				break;
			case 3:
				len = arrBalaNivel3.size;
				if (seDisparo && len < maxBalasRonda) {
					arrBalaNivel3.add(new BalaNivel3(x, y));
				}
				break;
			default:
			case 4:
				len = arrBalaNivel4.size;
				if (seDisparo && len < maxBalasRonda) {
					arrBalaNivel4.add(new BalaNivel4(x, y, nivelBala));
				}
				break;
		}

		/* Ahora Actualizo. Recalculo len por si se disparo una bala nueva */
		Iterator<BalaNormal> it = balaNormalList.iterator();
		while (it.hasNext()) {
			BalaNormal oBalaNormal = it.next();
			if (oBalaNormal.position.y > HEIGHT + 2)
				oBalaNormal.hitTarget();// para que no llegue tan lejos el misil
			oBalaNormal.update(deltaTime);
			if (oBalaNormal.state == BalaNormal.STATE_EXPLOTANDO) {
				it.remove();

			}
		}

		// Bala nivel 1
		Iterator<BalaNivel1> it1 = arrBalaNivel1.iterator();
		while (it1.hasNext()) {
			BalaNivel1 oBalaNivel1 = it1.next();
			if (oBalaNivel1.position.y > HEIGHT + 2)
				oBalaNivel1.destruirBala();// para que no llegue tan lejos el misil
			oBalaNivel1.update(deltaTime);
			if (oBalaNivel1.state == BalaNivel1.STATE_EXPLOTANDO) {
				it1.remove();
			}
		}

		// Bala nivel 2
		Iterator<BalaNivel2> it2 = arrBalaNivel2.iterator();
		while (it2.hasNext()) {
			BalaNivel2 oBalaNivel2 = it2.next();
			if (oBalaNivel2.position.y > HEIGHT + 2)
				oBalaNivel2.destruirBala();// para que no llegue tan lejos el misil
			oBalaNivel2.update(deltaTime);
			if (oBalaNivel2.state == BalaNivel2.STATE_EXPLOTANDO) {
				it2.remove();

			}

		}

		// Bala nivel 3
		Iterator<BalaNivel3> it3 = arrBalaNivel3.iterator();
		while (it3.hasNext()) {
			BalaNivel3 oBalaNivel3 = it3.next();
			if (oBalaNivel3.position.y > HEIGHT + 2)
				oBalaNivel3.destruirBala();// para que no llegue tan lejos el misil
			oBalaNivel3.update(deltaTime);
			if (oBalaNivel3.state == BalaNivel3.STATE_EXPLOTANDO) {
				it3.remove();
			}

		}

		// Bala nivel 4
		Iterator<BalaNivel4> it4 = arrBalaNivel4.iterator();
		while (it4.hasNext()) {
			BalaNivel4 oBalaNivel4 = it4.next();
			if (oBalaNivel4.position.y > HEIGHT + 2)
				oBalaNivel4.destruirBala();// para que no llegue tan lejos el misil
			oBalaNivel4.update(deltaTime);
			if (oBalaNivel4.state == BalaNivel4.STATE_EXPLOTANDO) {
				it4.remove();

			}

		}

	}

	private void updateMissil(float deltaTime, boolean seDisparoMissil) {
		/* Limite de maxMissilesRonda Missiles en una ronda */
		int len = missiles.size;
		if (seDisparoMissil && missilesDisponibles > 0 && len < maxMissilesRonda) {
			float x = oNave.position.x;
			float y = oNave.position.y + 1;
			missiles.add(new Missil(x, y));
			missilesDisponibles--;
			listener.missilFire();
		}

		/* Ahora Actualizo. Recalculo len por si se disparo una missil nueva */
		Iterator<Missil> it = missiles.iterator();
		while (it.hasNext()) {
			Missil oMissil = it.next();
			if (oMissil.position.y > HEIGHT + 2 && oMissil.state != Missil.STATE_EXPLOTANDO)
				oMissil.hitTarget();
			oMissil.update(deltaTime);
			// oMissil.updatePerseguidor(deltaTime,alienShips);
			if (oMissil.state == Missil.STATE_EXPLOTANDO && oMissil.stateTime > Missil.TIEMPO_EXPLODE) {
				it.remove();

			}

		}
	}

	private void updateBoost(float deltaTime) {
		Iterator<Boost> it = boosts.iterator();
		while (it.hasNext()) {
			Boost oBoost = it.next();
			oBoost.update(deltaTime);
			if (oBoost.position.y < -2) {
				it.remove();
			}

		}
	}

	/**
	 * ##################################### # Se Checan todo tipo de colisiones # #####################################
	 */
	private void checkCollision() {
		checkColisionNaveBalaAliens();// Primero reviso si le dieron a mi nave =(
		checkColisionAliensBala();// Checo si mis balas les dio a esos weas (Reviso BalaNormal, BalaNivel1, BalaNivel2, BalaNivel3.... etc
		checkColisionAlienBalaDoble();
		checkColisionAlienMissil();
		checkColisionBoostNave();
	}

	private void checkColisionNaveBalaAliens() {

		Iterator<BalaNormal> it = balasAlien.iterator();
		while (it.hasNext()) {
			BalaNormal oBalaNormal = it.next();
			if (Intersector.overlaps(oNave.boundsRectangle, oBalaNormal.boundsRectangle) && oNave.state != Nave.NAVE_STATE_EXPLODE && oNave.state != Nave.NAVE_STATE_BEING_HIT) {
				oNave.beingHit();
				oBalaNormal.hitTarget();
				Gdx.app.log("Choco", "Si choco");
			}

		}
	}

	private void checkColisionAliensBala() {

		// Bala Normal
		Iterator<BalaNormal> it = balaNormalList.iterator();
		while (it.hasNext()) {
			BalaNormal oBala = it.next();
			Iterator<AlienShip> it2 = alienShips.iterator();
			while (it2.hasNext()) {
				AlienShip oAlien = it2.next();
				if (Intersector.overlaps(oAlien.boundsCircle, oBala.boundsRectangle) && (oAlien.state != AlienShip.EXPLOTING)) {
					oBala.hitTarget();
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}

			}
		}

		// Bala nivel 1
		Iterator<BalaNivel1> it1 = arrBalaNivel1.iterator();
		while (it1.hasNext()) {
			BalaNivel1 oBala = it1.next();
			Iterator<AlienShip> it2 = alienShips.iterator();
			while (it2.hasNext()) {
				AlienShip oAlien = it2.next();
				if (Intersector.overlaps(oAlien.boundsCircle, oBala.boundsRectangle) && (oAlien.state != AlienShip.EXPLOTING)) {
					oBala.hitTarget(oAlien.vidasLeft);
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}

			}
		}

		// Bala nivel 2
		Iterator<BalaNivel2> itl2 = arrBalaNivel2.iterator();
		while (itl2.hasNext()) {
			BalaNivel2 oBala = itl2.next();
			Iterator<AlienShip> it2 = alienShips.iterator();
			while (it2.hasNext()) {
				AlienShip oAlien = it2.next();
				if (Intersector.overlaps(oAlien.boundsCircle, oBala.boundsRectangle) && (oAlien.state != AlienShip.EXPLOTING)) {
					oBala.hitTarget(oAlien.vidasLeft);
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}

			}
		}

		// Bala nivel 3
		Iterator<BalaNivel3> it3 = arrBalaNivel3.iterator();
		while (it3.hasNext()) {
			BalaNivel3 oBala = it3.next();
			Iterator<AlienShip> it2 = alienShips.iterator();
			while (it2.hasNext()) {
				AlienShip oAlien = it2.next();
				if (Intersector.overlaps(oAlien.boundsCircle, oBala.boundsRectangle) && (oAlien.state != AlienShip.EXPLOTING)) {
					oBala.hitTarget(oAlien.vidasLeft);
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}

			}
		}

		// Bala nivel 4
		Iterator<BalaNivel4> it4 = arrBalaNivel4.iterator();
		while (it4.hasNext()) {
			BalaNivel4 oBala = it4.next();
			Iterator<AlienShip> it2 = alienShips.iterator();
			while (it2.hasNext()) {
				AlienShip oAlien = it2.next();
				if (Intersector.overlaps(oAlien.boundsCircle, oBala.boundsRectangle) && (oAlien.state != AlienShip.EXPLOTING)) {
					oBala.hitTarget(oAlien.vidasLeft);
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}

			}
		}
	}

	private void checkColisionAlienBalaDoble() {
		// int len=balasDobles.size();
		// for (int i=0;i<len;i++){
		// BalaDoble oBala=balasDobles.get(i);
		// int lenAlien=alienShips.size();
		// for (int j=0;j<lenAlien;j++){
		// AlienShip oAlien=alienShips.get(j);
		// if(Intersector.overlapCircleRectangle(oAlien.boundsCircle,oBala.boundsRectangle) && (oAlien.state!=AlienShip.EXPLOTING)){
		// oBala.hitTarget(oAlien.vidasLeft);
		// oAlien.beingHit();
		// if(oAlien.state==AlienShip.EXPLOTING){//Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
		// puntuacion+=oAlien.puntuacion;//Actualizo la puntuacion
		// agregarBoost(oAlien.position.x,oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
		// listener.explosion();
		// }
		// }
		// }
		// }

	}

	private void checkColisionAlienMissil() {
		// if(oMissil!=null){
		// int len=alienShips.size();
		// for(int i=0;i<len;i++){
		// AlienShip oAlien=alienShips.get(i);
		// if(oMissil.state==Missil.STATE_DISPARADO && Intersector.overlapCircleRectangle(oAlien.boundsCircle,oMissil.boundsRectangle) && oAlien.state!=AlienShip.EXPLOTING){
		// oMissil.hitTarget();
		// oAlien.beingHit();
		// if(oAlien.state==AlienShip.EXPLOTING){//Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
		// puntuacion+=oAlien.puntuacion;//Actualizo la puntuacion
		// agregarBoost(oAlien.position.x,oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
		// listener.explosion();
		// }
		// }
		// //Checo con el radio de la explosion
		// if(oMissil.state==Missil.STATE_EXPLOTANDO && Intersector.overlapCircles(oAlien.boundsCircle,oMissil.boundsCircle) && oAlien.state!=AlienShip.EXPLOTING){
		// oAlien.beingHit();
		// if(oAlien.state==AlienShip.EXPLOTING){//Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
		// puntuacion+=oAlien.puntuacion;//Actualizo la puntuacion
		// agregarBoost(oAlien.position.x,oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
		// listener.explosion();
		// }
		// }
		//
		// }
		// }

		// ++

		Iterator<Missil> it = missiles.iterator();
		while (it.hasNext()) {
			Missil oMissil = it.next();

			Iterator<AlienShip> it2 = alienShips.iterator();
			while (it2.hasNext()) {
				AlienShip oAlien = it2.next();
				if (oMissil.state == Missil.STATE_DISPARADO && Intersector.overlaps(oAlien.boundsCircle, oMissil.boundsRectangle) && oAlien.state != AlienShip.EXPLOTING) {
					oMissil.hitTarget();
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}
				// Checo con el radio de la explosion
				if (oMissil.state == Missil.STATE_EXPLOTANDO && Intersector.overlaps(oAlien.boundsCircle, oMissil.boundsCircle) && oAlien.state != AlienShip.EXPLOTING) {
					oAlien.beingHit();
					if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
						puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
						agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
						listener.explosion();
					}
				}

			}

		}

		// ++
	}

	private void checkColisionBoostNave() {

		Iterator<Boost> it = boosts.iterator();
		while (it.hasNext()) {
			Boost oBoost = it.next();
			if (Intersector.overlaps(oBoost.boundsCircle, oNave.boundsRectangle) && oNave.state != Nave.NAVE_STATE_EXPLODE) {
				switch (oBoost.tipo) {
					case Boost.VIDA_EXTRA:
						oNave.hitVidaExtra();
						break;
					case Boost.UPGRADE_NIVEL_ARMAS: // MENOS_TIEMPO_CARGANDO: <-- Antes se llamaba asi
						// menosTiempoCargando+=.055; <-- antes hacia esta accion
						// TODO Tengo que revisar el que el menosTimpoCargando no supere los .5 porke si no se va a disparar como loco
						nivelBala++;
						break;
					case Boost.MISSIL_EXTRA:
						missilesDisponibles++;
						break;
					default:
					case Boost.SHIELD:
						oNave.hitEscudo();
						break;

				}
				it.remove();
				listener.coin();
			}

		}

	}

	/**
	 * Recibe las coordenadas en x,y de la nave que acaba de ser destruida El Boost puede ser una vida, armas, escudo, etc
	 * 
	 * @param x
	 *            posicion donde va aparecer el boost
	 * @param y
	 *            posicion donde va aparecer el boost
	 */
	private void agregarBoost(float x, float y) {
		if (oRan.nextInt(100) < 5 + extraChanceDrop) {// Probabilidades de que aparezca un boost
			switch (oRan.nextInt(4)) {
				case Boost.VIDA_EXTRA:
					boosts.add(new Boost(Boost.VIDA_EXTRA, x, y));
					break;
				case 1:
					boosts.add(new Boost(Boost.UPGRADE_NIVEL_ARMAS, x, y));
					break;
				case Boost.MISSIL_EXTRA:
					boosts.add(new Boost(Boost.MISSIL_EXTRA, x, y));
					break;
				default:// Boost.SHIELD
					boosts.add(new Boost(Boost.SHIELD, x, y));
					break;
			}
		}
	}

	private void checkGameOver() {
		if (oNave.state == Nave.NAVE_STATE_EXPLODE && oNave.stateTime > Nave.TIEMPO_EXPLODE) {
			oNave.position.x = 200;
			state = STATE_GAME_OVER;
		}
	}

	private void checkLevelEnd() {
		if (alienShips.size == 0) {
			balaNormalList.clear();
			balasAlien.clear();
			agregarAliens();
		}

	}

}
