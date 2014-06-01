
public class Player extends Character {
	boolean sprint;
	
	public Player(float x, float y, float vel, String nm) {
		super(x, y, vel, nm);
	}
	
	public void speedUp() {
		if (sprint)
			velocity = 2;
		else
			velocity = 1;
		sprint = !sprint;
	}
}
