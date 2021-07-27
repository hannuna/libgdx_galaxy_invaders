package com.nopalsoft.invaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.nopalsoft.invaders.Assets;
import com.nopalsoft.invaders.Settings;
import com.nopalsoft.invaders.frame.*;
import com.nopalsoft.invaders.screens.Screens;

import java.util.Iterator;
import java.util.Random;

public class World {

    public static float WIDTH = Screens.WORLD_SCREEN_WIDTH;
    public static final float HEIGHT = Screens.WORLD_SCREEN_HEIGHT;

    public static final int STATE_RUNNING = 0;
    public static final int STATE_GAME_OVER = 1;
    public static final int STATE_PAUSED = 2;
    Nave oNave;
    Array<AlienShip> alienShips;
    Array<AlienBullet> balasAlien;
    Array<Boost> boosts;
    Array<Bullet> arrBullets;
    Array<Missile> missiles;

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

    public World() {
        oNave = new Nave(WIDTH / 2f, 9.5f);
        alienShips = new Array<AlienShip>();
        balasAlien = new Array<AlienBullet>();
        boosts = new Array<Boost>();
        arrBullets = new Array<Bullet>();
        missiles = new Array<Missile>();

        missilesDisponibles = 5;
        oNave.vidas = 5;
        extraChanceDrop = 5;
        maxMissilesRonda = 5;
        maxBalasRonda = 5;

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
                balasAlien.add(new AlienBullet(x, y));
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

        Iterator<AlienBullet> it = balasAlien.iterator();
        while (it.hasNext()) {
            AlienBullet oAlienBullet = it.next();
            if (oAlienBullet.position.y < -2)
                oAlienBullet.hitTarget();// para que no llegue tan lejos el misil
            oAlienBullet.update(deltaTime);
            if (oAlienBullet.state == AlienBullet.STATE_EXPLOTANDO) {
                it.remove();
            }

        }

    }

    private void updateBalaNormalYConNivel(float deltaTime, boolean seDisparo) {
        float x = oNave.position.x;
        float y = oNave.position.y + 1;

        if (seDisparo && arrBullets.size < maxBalasRonda) {
            arrBullets.add(new Bullet(x, y, nivelBala));
        }

        Iterator<Bullet> it1 = arrBullets.iterator();
        while (it1.hasNext()) {
            Bullet oBullet = it1.next();
            if (oBullet.position.y > HEIGHT + 2)
                oBullet.destruirBala();// para que no llegue tan lejos el misil
            oBullet.update(deltaTime);
            if (oBullet.state == Bullet.STATE_EXPLOTANDO) {
                it1.remove();
            }
        }
    }

    private void updateMissil(float deltaTime, boolean seDisparoMissil) {
        /* Limite de maxMissilesRonda Missiles en una ronda */
        int len = missiles.size;
        if (seDisparoMissil && missilesDisponibles > 0 && len < maxMissilesRonda) {
            float x = oNave.position.x;
            float y = oNave.position.y + 1;
            missiles.add(new Missile(x, y));
            missilesDisponibles--;
            Assets.playSound(Assets.missilFire, 0.15f);
        }

        /* Ahora Actualizo. Recalculo len por si se disparo una missil nueva */
        Iterator<Missile> it = missiles.iterator();
        while (it.hasNext()) {
            Missile oMissile = it.next();
            if (oMissile.position.y > HEIGHT + 2 && oMissile.state != Missile.STATE_EXPLOTANDO)
                oMissile.hitTarget();
            oMissile.update(deltaTime);
            // oMissil.updatePerseguidor(deltaTime,alienShips);
            if (oMissile.state == Missile.STATE_EXPLOTANDO && oMissile.stateTime > Missile.TIEMPO_EXPLODE) {
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
        checkColisionAlienMissil();
        checkColisionBoostNave();
    }

    private void checkColisionNaveBalaAliens() {

        Iterator<AlienBullet> it = balasAlien.iterator();
        while (it.hasNext()) {
            AlienBullet oAlienBullet = it.next();
            if (Intersector.overlaps(oNave.boundsRectangle, oAlienBullet.boundsRectangle) && oNave.state != Nave.NAVE_STATE_EXPLODE && oNave.state != Nave.NAVE_STATE_BEING_HIT) {
                oNave.beingHit();
                oAlienBullet.hitTarget();
                Gdx.app.log("Choco", "Si choco");
            }

        }
    }

    private void checkColisionAliensBala() {

        // Bala nivel 1
        Iterator<Bullet> it1 = arrBullets.iterator();
        while (it1.hasNext()) {
            Bullet oBala = it1.next();
            Iterator<AlienShip> it2 = alienShips.iterator();
            while (it2.hasNext()) {
                AlienShip oAlien = it2.next();
                if (Intersector.overlaps(oAlien.boundsCircle, oBala.boundsRectangle) && (oAlien.state != AlienShip.EXPLOTING)) {
                    oBala.hitTarget(oAlien.vidasLeft);
                    oAlien.beingHit();
                    if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
                        puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
                        agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
                        Assets.playSound(Assets.explosionSound, 0.6f);
                    }
                }

            }
        }
    }

    private void checkColisionAlienMissil() {
        Iterator<Missile> it = missiles.iterator();
        while (it.hasNext()) {
            Missile oMissile = it.next();

            Iterator<AlienShip> it2 = alienShips.iterator();
            while (it2.hasNext()) {
                AlienShip oAlien = it2.next();
                if (oMissile.state == Missile.STATE_DISPARADO && Intersector.overlaps(oAlien.boundsCircle, oMissile.boundsRectangle) && oAlien.state != AlienShip.EXPLOTING) {
                    oMissile.hitTarget();
                    oAlien.beingHit();
                    if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
                        puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
                        agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
                        Assets.playSound(Assets.explosionSound, 0.6f);
                    }
                }
                // Checo con el radio de la explosion
                if (oMissile.state == Missile.STATE_EXPLOTANDO && Intersector.overlaps(oAlien.boundsCircle, oMissile.boundsCircle) && oAlien.state != AlienShip.EXPLOTING) {
                    oAlien.beingHit();
                    if (oAlien.state == AlienShip.EXPLOTING) {// Solo aumenta la puntuacion y agrego boost si ya esta exlotando, no si disminuyo su vida
                        puntuacion += oAlien.puntuacion;// Actualizo la puntuacion
                        agregarBoost(oAlien.position.x, oAlien.position.y);/* Aqui voy a ver si me da algun boost o no */
                        Assets.playSound(Assets.explosionSound, 0.6f);
                    }
                }
            }
        }
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
                Assets.playSound(Assets.coinSound);
            }

        }

    }

    /**
     * Recibe las coordenadas en x,y de la nave que acaba de ser destruida El Boost puede ser una vida, armas, escudo, etc
     *
     * @param x posicion donde va aparecer el boost
     * @param y posicion donde va aparecer el boost
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
            arrBullets.clear();
            balasAlien.clear();
            agregarAliens();
        }

    }

}
