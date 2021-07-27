package com.nopalsoft.invaders.frame;

public class AlienBullet extends DynamicGameObject {

    public static final float WIDTH = 0.3f;
    public static final float HEIGHT = 0.9f;

    public final float VELOCIDADALIEN = -30;
    public final static int STATE_DISPARADO = 0;
    public final static int STATE_EXPLOTANDO = 1;

    public float stateTime;
    public int state;

    /**
     * X y Y son la posicion de la punta de nave
     *
     * @param x La misma que Bob.x
     * @param y La misma que Bob.y
     */
    public AlienBullet(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
        state = STATE_DISPARADO;
        stateTime = 0;
        velocity.set(0, VELOCIDADALIEN);
    }

    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        boundsRectangle.x = position.x - WIDTH / 2;
        boundsRectangle.y = position.y - HEIGHT / 2;
        stateTime += deltaTime;
    }

    public void hitTarget() {
        velocity.set(0, 0);
        stateTime = 0;
        state = STATE_EXPLOTANDO;

    }

}
