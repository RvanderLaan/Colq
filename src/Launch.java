import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.UIManager;


public class Launch extends JFrame {
	private boolean fullScreen = false;
	private Level lvl;
	
	public static void main(String[] args) {
		new Launch();
	}
	
	public Launch() {
		lvl = new Level();
		this.addKeyListener(lvl);
		this.addMouseListener(lvl);
		this.addMouseMotionListener(lvl);
		this.add(lvl);
		
		
		this.setTitle("Colq");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1280, 800);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH );
		this.setMinimumSize(new Dimension(640, 480));
		//this.addKeyListener(this);
		//this.setResizable(false);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native LAF: " + e);
			}
		
		lvl.gameLoop();
		lvl.setVisible(true);
		this.pack();
	}

//	@Override
//	public void keyPressed(KeyEvent e) {
//		switch (e.getKeyCode()) {
//		case (KeyEvent.VK_F11):
//			System.out.println("test");
//			if (!fullScreen) {
//				this.setUndecorated(true);
//			}
//			else {
//				this.setUndecorated(false);
//			}
//			fullScreen = !fullScreen;
//			break;
//		}
//	}
//
//	@Override
//	public void keyReleased(KeyEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void keyTyped(KeyEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//	
	
}
