package droid.invaders.frame;

public class BalaNivel3 extends DynamicGameObject {
	
	public static final float WIDTH=2.1f;
	public static final float HEIGHT=1.5f;
	
	public final float VELOCIDAD=30;
	public final float VELOCIDADALIEN=-30;
	public final static int STATE_DISPARADO=0;
	public final static int STATE_EXPLOTANDO=1;
	
	public final static int VIDA=5;

	public int vida;
	public float stateTime;
	public int state;
	/**
	 * X y Y son la posicion de la punta de nave
	 * 
	 * @param x La misma que Bob.x 
	 * @param y La misma que Bob.y
	 */
	public BalaNivel3(float x, float y) {
		super(x, y, WIDTH, HEIGHT);
		state=STATE_DISPARADO;
		stateTime=0;
		velocity.set(0,VELOCIDAD);
		vida=VIDA;
	}
	
	public BalaNivel3(float x,float y,boolean disparoAlien){
		super(x, y, WIDTH, HEIGHT);
		state=STATE_DISPARADO;
		stateTime=0;
		velocity.set(0,VELOCIDADALIEN);
		
	}
	
	public void update(float deltaTime){
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		boundsRectangle.x = position.x - WIDTH/ 2;
		boundsRectangle.y = position.y - HEIGHT / 2;
		stateTime += deltaTime;
	}
	
	public void hitTarget(int vidaTarget){
		vida-=vidaTarget;
		if(vida<=0){
			velocity.set(0,0);
			stateTime=0;
			state=STATE_EXPLOTANDO;
		}
	}
	
	/**
	 * En caso de que la bala se salga de la pantalla World.Height pues mando llamar este metodo para que se remueva del arreglo
	 */
	public void destruirBala(){
		velocity.set(0,0);
		stateTime=0;
		state=STATE_EXPLOTANDO;
	}

}
