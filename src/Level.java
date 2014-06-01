import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Level extends JPanel implements KeyListener, MouseListener, ActionListener, MouseMotionListener {
	private Map map;
	private Player player;
	private float lastPlayX;
	private float lastPlayY;
	
	private boolean running;

	private final int tileSize = 64;

	private long lastFpsTime;
	private int fps;
	private int currentFPS;

	private ArrayList<Character> characters;
	private ArrayList<Character> followers;
	private ArrayList<Image> tiles;
	private ArrayList<ArrayList<Image>> animTiles;
	
	private Image miniMap;
	private Image bg;
	int bgX;
	int bgY;
	
	long lastLoopTime = System.nanoTime();
	final int TARGET_FPS = 60;
	final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	
	int mouseX, mouseY;
	int shoot;
	
	double rotate;
	int rotCount;
	

	public Level() {
		map = new Map("map01.png", 128, 128);
		
		tiles = new ArrayList<Image>();
		animTiles = new ArrayList<ArrayList<Image>>();
		ArrayList<Image> water = new ArrayList<Image>();
		animTiles.add(water);
		
		characters = new ArrayList<Character>();
		followers = new ArrayList<Character>();
		
		player = new Player(32, 32, 1,"You");
		lastPlayX = 0;
		lastPlayY = 0;
		
		for (int i = 0; i < 20; i++) {
			float x = (float) (Math.random() * map.getSizeX());
			float y = (float) (Math.random() * map.getSizeY());
			characters.add(new Enemy(x, y, 1, "Enemy " + i));
		}

		running = true;
		lastFpsTime = 0;
		fps = 0;
		
		mouseX = 0;
		mouseY = 0;
		shoot = 0;
		
		bgX = 0;
		bgY = 0;
		
		rotCount = 90;
		rotate = 0;
		
		this.setBackground(Color.black);
		
		//Load images
		try {
			//Tiles
			tiles.add(ImageIO.read(this.getClass().getResource("air.png")));
			tiles.add(ImageIO.read(this.getClass().getResource("grassCube.png")));
			tiles.add(ImageIO.read(this.getClass().getResource("brickPillar.png")));
			tiles.add(ImageIO.read(this.getClass().getResource("waterCube.png")));
			tiles.add(ImageIO.read(this.getClass().getResource("bushCube.png")));
			//BG
			bg = ImageIO.read(this.getClass().getResource("cloudysky.jpg"));
			//Minimap
			miniMap = ImageIO.read(this.getClass().getResource("map01.png"));
			
			for (int i = 0; i < 5; i++) {
				water.add(ImageIO.read(this.getClass().getResource("waterCube"+i+".png")));
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.requestFocusInWindow();
		this.requestFocus();
		this.setVisible(true);
		
		this.revalidate();
		
	}

	public void gameLoop() {
		// keep looping round til the game ends
		while (running) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			// update the frame counter
			lastFpsTime += updateLength;
			fps++;

			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000000000) {
				currentFPS = fps;
				System.out.println("(FPS: " + fps + ")");
				lastFpsTime = 0;
				fps = 0;
				
			}

			// update the game logic
			doGameUpdates(delta);

			// draw everyting
			repaint();

			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give
			// us our final value to wait for
			// remember this is in ms, whereas our lastLoopTime etc. vars are in
			// ns.
			try {
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doGameUpdates(double delta) {
		//Update players location + collision:
		
		//Player:
		//Map border collision:
		float pos[] = player.updatePosition(delta);
		if (pos[0] < 0) 
			player.setX(0);
		else if (pos[0] > map.getSizeX() )
			player.setX((float) (map.getSizeX() - 0.01)); 
		
		if (pos[1] < 0) 
			player.setY(0);
		else if (pos[1] > map.getSizeY())
			player.setY((float) (map.getSizeY() - 0.01));
		
		//Tile collision
		int playX = (int) Math.round(pos[0] - 0.5);
		int playY =(int) Math.round(pos[1] - 0.5);
		//If it collides, restore last position
		if (playX >= 0 && playY >= 0 && playX <= map.getSizeX()-1 && playY <= map.getSizeY()-1 && map.get(playX, playY).collides()) {
			player.setX(lastPlayX);
			player.setY(lastPlayY);
		}
		
		//Store last player position
		lastPlayX = player.getX();
		lastPlayY = player.getY();
		
		if (shoot > 0) {
			shoot -= 5;
		}
			
		//-----------------------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------------------
		//Enemies
		//Update enemies location
		for (int i = 0; i < characters.size(); i++) {
			Character c = (Character) characters.get(i);
			
			//Check if shot
			if (shoot >= 240) {
				double x = (mouseX / 64 * 2) + (mouseX / 64 * 2);
				double y = (mouseY / 32 * 2) - (mouseY / 32 * 2);
				
				if (c.getX() - 5 > x && c.getX() + 5 < x && c.getY() -5 > y && c.getX() + 5 < y)
					characters.remove(i);
			}
			
			
			//Set direction of enemies
			if (characters.get(i) instanceof Enemy) {
				/*
				if (c.getX() -8 < player.getX() && c.getX() + 8 > player.getX() && c.getY()-8 < player.getY() && c.getY()+8 > player.getY()) {
					if (c.getX() < player.getX())
						c.dirX = 1;
					else if (c.getX() > player.getX())
						c.dirX = -1;
					
					if (c.getY() < player.getY())
						c.dirY = -1;
					else if (c.getY() > player.getY())
						c.dirY = 1;
					
					if (!followers.contains(c))
						followers.add(c);
				}
				else 
				*/ 
				if (Math.random() > 0.95) {
					if (followers.contains(c))
						followers.remove(c);
					Enemy e = (Enemy) characters.get(i);
					e.autoDir();
				}
			}
			
			
			//Position
//			if (followers.contains(c)) {
//				if (pos[0] + followers.indexOf(c) < player.getX()) 
//					c.setX(player.getX() + followers.indexOf(c));
//				else if (pos[0] - followers.indexOf(c) > player.getX())
//					c.setX(player.getX() - followers.indexOf(c));
//				
//				if (pos[1] + followers.indexOf(c) < player.getY()) 
//					c.setY(player.getY() + followers.indexOf(c));
//				else if (pos[1] - followers.indexOf(c) > player.getY()) 
//					c.setY(player.getY() - followers.indexOf(c));
//			}
			
			pos = c.updateAbsPosition(delta);
			if (pos[0] < 0) 
				c.setX(0);
			else if (pos[0] > map.getSizeX())
				c.setX((float) (map.getSizeX() - 0.01));
			
			if (pos[1] < 0) 
				c.setY(0);
			else if (pos[1] > map.getSizeY())
				c.setY((float) (map.getSizeY() - 0.01));
			
			

		}
		

		bgX -= 1;
		bgY -= 1;
		
		if (bgX <= -2048)
			bgX = 0;
		if (bgY <= -3*1024)
			bgY = 0;
	}

	public void paint(Graphics g) {
		
		super.paint(g);

		// Smooth setup
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		//Draw background
		AffineTransform t = new AffineTransform();
		t.translate(bgX / 2, bgY / 3);
		for (int i=0; i < 2; i++) {
			for (int j=0; j < 3; j++) {
				g2d.drawImage(bg, t, this);
				t.translate(1024, 0);
			}
			t.translate(0, 1024);
			t.translate(-3 * 1024, 0);
		}
		
		//Draw GUI
		g2d.setColor(Color.blue);
		g2d.drawString("FPS: " + currentFPS + " - Pos.: " + player.getX() + ", " + player.getY(), 32, 32);
		g2d.drawString("Mouse: " + mouseX + ", " + mouseY + ", ", 32, 48);
		//Minimap
		g2d.drawImage(miniMap, this.getWidth() - 150, 20, this);
		g2d.setColor(Color.red);
		g2d.fillOval(this.getWidth() - 150 + Math.round(player.getY()) - 2, 20 + Math.round(player.getX()) - 2, 4, 4);
		
		//Zoom
		if (rotCount > 2) {
			g2d.translate(this.getWidth()/2, this.getHeight()/2);
			rotate += 0.01;
			g2d.rotate((Math.random() - 0.5)/4);
			g2d.scale(rotate / rotCount * 10, rotate / rotCount* 10);
			rotCount--;
			g2d.translate(-this.getWidth()/2, -this.getHeight()/2);
		}
		else if (rotCount > 1) {
			rotCount = 0;
			rotate = 0;
		}
		
		//draw level + characters
		drawLevel(g2d);
	}
	
	public void drawTile(Graphics2D g2d, int i, int j) {
		
		
		int x, y;
		x = (j * 64 / 2) + (i * 64 / 2);
		y = (i * 32 / 2) - (j * 32 / 2);
		if (!map.get(i, j).isHigh()) {
			y -= 16;
		}
		else{
			y -= 52;
		}
		y += map.get(i,j).fall;
		
		
		if (!map.get(i, j).isDrawn) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f-map.get(i, j).fall / 60f);
			g2d.setComposite(ac);
		}
		else if (map.get(i, j).isDrawn && map.get(i, j).fall > 1) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f-map.get(i, j).fall / 61f);
			g2d.setComposite(ac);
		}
		if (map.get(i, j).tileID() != 3)
			g2d.drawImage(tiles.get(map.get(i, j).tileID()), x, y, this);
		else {
			int z = (int) System.nanoTime()% 10;
			Image im = animTiles.get(0).get((int) Math.round(Math.random() * 4)); 
			
			//System.out.println(z);
			g2d.drawImage(im, x, y, this);
		}
			
		
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(ac);
	}
	
	public void drawLevel(Graphics2D g2d) {		
		//Follow player
		float transX = ( this.getWidth() /2 + (this.getWidth() /2 - mouseX)/2 + (-player.getX() - player.getY()) * 32);
		float transY = ( this.getHeight()/2 + (this.getHeight()/2 - mouseY)/2 + (player.getY() - player.getX()) * 16);
		g2d.translate(transX, transY);
		
		//Get render borders
		int minX = Math.round(player.getX() - (this.getWidth() / 2 / 64) - 1);
		int minY = Math.round(player.getY() - (this.getHeight() / 2 / 32) - 1);
		int maxX = Math.round(player.getX() + (this.getWidth() / 2 / 64) + 1);
		int maxY = Math.round(player.getY() + (this.getHeight() / 2 / 32) + 1);
		
		// Paint level
		for (int i = 0; i < map.getSizeX(); i++) {
			for (int j = map.getSizeY()-1; j >= 0; j--) {
				
				//Check if it has to be drawn
				if (i >= minX && i <= maxX && j >= minY && j <= maxY) {
					
					//Fade in
					if (!map.get(i, j).isDrawn) {
						map.get(i,j).fall = map.get(i, j).fall - 1 - (int) Math.round(Math.random());
						if (map.get(i,j).fall < 1) {
							map.get(i, j).isDrawn = true;
							map.get(i,j).fall = 0;
						}
					}
					//If it has to fade in right after fading out, reset the fall height
					else {
						if (map.get(i,j).fall > 1) 
							map.get(i,j).fall = map.get(i, j).fall - 1 - (int) Math.round(Math.random());
						else 
							map.get(i,j).fall = 0;
					}
					drawTile(g2d, i, j);
				}
				
				//Else fade out
				else {
					if (map.get(i,j).isDrawn) {
						if (map.get(i,j).fall < 60) {
							map.get(i,j).fall = map.get(i, j).fall + 1 + (int) Math.round(Math.random());
							drawTile(g2d, i, j);
						} 
						else {
							map.get(i,j).isDrawn = false;
							map.get(i,j).fall = 60;
						}
					}
				}
			}
		}
		
		//draw player
		//In-game coordinates of player
		int dX = (int) Math.round(player.getX() - 0.5);
		int dY = (int) Math.round(player.getY() - 0.5);
		
		//Coordinates on screen
		double x = (player.getY() * 64 / 2) + (player.getX() * 64 / 2);
		double y = (player.getX() * 32 / 2) - (player.getY() * 32 / 2);
		
		Rectangle2D rect;
		Ellipse2D shad;
		boolean drawPlayer = true;
		
		//Check if player is behind something
		//Tile itself
		if (map.get(dX, dY).isHigh())
			drawPlayer = false;
		//Tile below it
		if (dX + 1 < map.getSizeX() && dY -1 >= 0 && map.get(dX + 1, dY - 1).isHigh())
			drawPlayer = false;
		//Below and left
		if (dX + 1 < map.getSizeX() && map.get(dX + 1, dY).isHigh() && x % 1 >= 0.5)
			drawPlayer = false;
		//Below and right
		if (dY -1 >= 0  && map.get(dX, dY - 1).isHigh() && x % 1 < 0.5)
			drawPlayer = false;
		
		if (drawPlayer) {
			//shadow
			g2d.setColor(new Color(0,0,0,150));
			shad = new Ellipse2D.Double(x-3, y+10, 22, 12);
			g2d.fill(shad);
			//character
			rect = new Rectangle2D.Double(x, y, 16, 16);
			g2d.setColor(Color.green);
			g2d.fill(rect);
		}
		//Draw name tag
		g2d.setColor(new Color(255,255,255,150));
		rect = new Rectangle2D.Double(x-2, y-15, 24, 14);
		g2d.fill(rect);
		g2d.setColor(Color.black);
		g2d.drawString(player.getName(), (float) x - 1, (float) y -4);
		
		//Draw shot trail
		if (shoot > 0) {
			g2d.setColor(new Color(255,255,255,shoot));
			Line2D line = new Line2D.Double(x,y, -this.getWidth()/2 + mouseX + x, -this.getHeight()/2 + mouseY + y);
			g2d.setStroke(new BasicStroke(3));
			g2d.draw(line);
			//shoot = false;
		}
		
		//Draw cube
		if (shoot > 100) {
		//	g2d.setXORMode(Color.pink);
			int mx = (int) Math.round((-this.getWidth()/2 + mouseX + x)/64 );
			int my = (int) Math.round((-this.getHeight()/2 + mouseY + y)/32);
		//	g2d.drawImage(blankCube, mx*64, my*32 + 16, this);
		}
		//-----------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------
		//Draw Enemies
		
		for (int i = 0; i < characters.size(); i++) {
			Character c = characters.get(i);
			dX = (int) Math.round(c.getX() - 0.5);
			dY = (int) Math.round(c.getY() - 0.5);
			x = (c.getY() * 64 / 2) + (c.getX() * 64 / 2);
			y = (c.getX() * 32 / 2) - (c.getY() * 32 / 2);
			boolean drawEnemy = true;
			
			//Check if enemy is behind something
			//Tile itself
			if (map.get(dX, dY).isHigh())
				drawEnemy = false;
			//Tile below it
			if (dX + 1 < map.getSizeX() && dY -1 >= 0 && map.get(dX + 1, dY - 1).isHigh())
				drawEnemy = false;
			//Below and left
			if (dX + 1 < map.getSizeX() && map.get(dX + 1, dY).isHigh() && x % 1 >= 0.5)
				drawEnemy = false;
			//Below and right
			if (dY -1 >= 0  && map.get(dX, dY - 1).isHigh() && x % 1 < 0.5)
				drawEnemy = false;
			
			if (drawEnemy) {
				//shadow
				g2d.setColor(new Color(0,0,0,150));
				shad = new Ellipse2D.Double(x-3, y+10, 22, 12);
				g2d.fill(shad);
				//character
				rect = new Rectangle2D.Double(x, y, 16, 16);
				g2d.setColor(Color.red);
				g2d.fill(rect);
			}
			
			//Name tags
			g2d.setColor(new Color(255,255,255,150));
			rect = new Rectangle2D.Double(x-2, y-15, 64, 14);
			g2d.fill(rect);
			g2d.setColor(Color.black);
			g2d.drawString(c.getName(), (float) x - 1, (float) y -4);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		shoot = 255;
		rotCount = 90;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case (KeyEvent.VK_UP):
		case (KeyEvent.VK_W):
			player.dirY(-1);
			break;
		case (KeyEvent.VK_A):
			player.dirX(-1);
			break;
		case (KeyEvent.VK_S):
			player.dirY(1);
			break;
		case (KeyEvent.VK_D):
			player.dirX(1);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case (KeyEvent.VK_UP):
		case (KeyEvent.VK_W):
			player.dirY(0);
			break;
		case (KeyEvent.VK_A):
			player.dirX(0);
			break;
		case (KeyEvent.VK_S):
			player.dirY(0);
			break;
		case (KeyEvent.VK_D):
			player.dirX(0);
			break;
		case (KeyEvent.VK_SHIFT):
			player.speedUp();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		
	}

}
