
public class Character {
	private String name;
	private float posX;
	private float posY;
	
	int dirX;
	int dirY;
	
	public float velocity;
	
	public Character(float x, float y, float vel, String nm) {
		posX = x;
		posY = y;
		velocity = vel;
		
		dirX = 0;
		dirY = 0;
		
		name = nm;
	}
	
	public float[] updatePosition(double delta) {
		int slower = 20;
		if (dirX != 0 && dirY != 0) {
			slower = 30;
		}
		
		if (dirX == -1) {
			posX -= delta * velocity / slower;
			posY -= delta * velocity / slower;
		}
		else if (dirX == 1) {
			posX += delta * velocity / slower;
			posY += delta * velocity / slower;
		}
		if (dirY == -1) {
			posX -= delta * velocity / slower;
			posY += delta * velocity / slower;
		}
		else if (dirY == 1) {
			posX += delta * velocity / slower;
			posY -= delta * velocity / slower;
		}
		return new float[] {posX, posY};
	}
	
	public float[] updateAbsPosition(double delta) {
		int slower = 15;
		if (dirX != 0 && dirY != 0) {
			slower = 20;
		}
		
		if (dirX == -1) {
			posX -= delta * velocity / slower;
		}
		else if (dirX == 1) {
			posX += delta * velocity / slower;
		}
		if (dirY == -1) {
			posY += delta * velocity / slower;
		}
		else if (dirY == 1) {
			posY -= delta * velocity / slower;
		}
		return new float[] {posX, posY};
	}
	
	public void dirX(int dir) {
		dirX = dir;
	}
	
	public void dirY(int dir) {
		dirY = dir;
	}
	
	public float getX() {
		return posX;
	}
	public float getY() {
		return posY;
	}
	public void setX(float x) {
		posX = x;
	}
	public void setY(float y) {
		posY = y;
	}
	public String getName() {
		return name;
	}
	public void flipDirection() {
		if (dirX == 1)
			dirX = -1;
		else if (dirX == -1)
			dirX = 1;
		if (dirY == 1)
			dirY =-1;
		else if (dirY == -1) 
			dirY = 1;
	}
}
