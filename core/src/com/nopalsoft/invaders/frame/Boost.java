package com.nopalsoft.invaders.frame;

public class Boost extends DynamicGameObject {

	public static final float DRAW_SIZE = 5;
	public static final float RADIO = 1f;
	public static final float VELOCIDAD = -10f;

	public static final int VIDA_EXTRA = 0;
	public static final int UPGRADE_NIVEL_ARMAS = 1;
	public static final int MISSIL_EXTRA = 2;
	public static final int SHIELD = 3;
	public final int tipo;
	int stateTime;

	public Boost(int tipo, float x, float y) {
		super(x, y, RADIO);
		this.tipo = tipo;
		velocity.add(0, VELOCIDAD);
		stateTime = 0;

	}

	public void update(float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		boundsCircle.x = position.x;
		boundsCircle.y = position.y;
		stateTime += deltaTime;
	}

}
