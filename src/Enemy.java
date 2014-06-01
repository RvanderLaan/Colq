
public class Enemy extends Character {

	public Enemy(float x, float y, float vel, String nm) {
		super(x, y, vel, nm);
		// TODO Auto-generated constructor stub
	}
	
	public void autoDir() {
		double r = Math.random();
		if (r < 0.333) 
			dirX = 1;
		else if (r > 0.333 && r < 0.666)
			dirX = -1;
		else
			dirX = 0;
		
		r = Math.random();
		if (r < 0.333) 
			dirY = 1;
		else if (r > 0.333 && r < 0.666)
			dirY = -1;
		else
			dirY = 0;
	}


}
