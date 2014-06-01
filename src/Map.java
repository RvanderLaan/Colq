import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Map {
	private Tile[][] map;
	private int sizeX;
	private int sizeY;

	public Map(String path, int x, int y) {
		sizeX = x;
		sizeY = y;

		map = new Tile[sizeX][sizeY];

		if (path == null)
			randomMap();
		else
			read(path);
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public Tile get(int x, int y) {
		return map[x][y];
	}

	public void randomMap() {

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				map[i][j] = new Tile(i, j, 1);
				if (Math.random() > 0.95)
					map[i][j].changeImage(2);
				else if (Math.random() > 0.9)
					map[i][j].changeImage(0);
			}
		}
	}

	public void read(String path) {
		try {
			// Load image
			BufferedImage img = ImageIO.read(this.getClass().getResource(path));
			int width = img.getWidth();
			int height = img.getHeight();
			map = new Tile[width][height];

			byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer())
					.getData();

			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				// int argb = 0;
				// argb += ; // 255 alpha
				int red = (((int) pixels[pixel + 2] & 0xff) << 16 / 256); // red
				int green = (((int) pixels[pixel + 1] & 0xff) << 8) / 256; // green
				int blue = ((int) pixels[pixel] & 0xff); // blue
					
				if (red == 0 && green == 255 && blue == 0) 
					map[row][col] = new Tile(row, col, 1);
				else if (red == 150 && green == 150 && blue == 150) 
					map[row][col] = new Tile(row, col, 2);
				else if (red == 0 && green == 0 && blue == 255) 
					map[row][col] = new Tile(row, col, 3);
				else if (red == 0 && green == 150 && blue == 0) 
					map[row][col] = new Tile(row, col, 4);
				else
					map[row][col] = new Tile(row, col, 0);
				
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}

		} catch (IOException e) {
			randomMap();
			System.out.println("Can't find map, creating random one...");
		}
	}
}
