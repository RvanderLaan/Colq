import java.awt.Image;

/**
 * A tile with an ID which represents its properties and image.<br>
 * 0:	Air (nothing) -		Collides (can't pass it)	<br>
 * 1:	Grass -				Doesn't Collide				<br>
 * 2:	Pillar -			Collides					<br>
 * 3:	Water -				Collides					<br>
 * 
 * @author Remi
 *
 */
public class Tile {
	private final int posX;
	private final int posY;
	private int tileID;
	public boolean isDrawn;
	public int fall;
	
	public Tile(int x, int y, int id) {
		posX = x;
		posY = y;
		tileID = id;
		isDrawn = false;
		fall = 60;
	}
	
	public int tileID() {
		return tileID;
	}
	
	public void changeImage(int ID) {
		tileID = ID;
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}
	
	public boolean collides() {
		boolean collides = false;
		switch (tileID) {
		case 0: 
		case 2:
		case 3:
		case 4:
			collides = true;
			break;
		}
		
		return collides;
	}
	public boolean isHigh() {
		boolean isHigh = false;
		switch (tileID) {
		case 2:
		case 4:
			isHigh = true;
			break;
		}
		
		return isHigh;
	}
	
	
	
	
	
}
