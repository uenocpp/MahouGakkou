import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import javazoom.jl.decoder.JavaLayerException;


public class MainFrame extends JFrame implements KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainAnimationPanel mpanel;
	
	public static final String strMagicSound = "magic.mp3";
	public static final String strMaouVoice1 = "maou1.mp3";
	public static final String strMaouVoice2 = "maou2.mp3";
	public static final String strMaouVoice3 = "maou3.mp3";
	public static final String strMaouVoice4 = "maou4.mp3";
	
	MainFrame(){
		getContentPane().setLayout(new BorderLayout());
		ToggleFullScreen(true);
		mpanel = new MainAnimationPanel();
		getContentPane().add( mpanel );
		
		setVisible(true);
		addKeyListener(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainFrame();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Press: " + KeyEvent.getKeyText(e.getKeyCode()));
		switch(e.getKeyCode()){
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		case KeyEvent.VK_A:
			mpanel.consumeMana(0);
			break;
		case KeyEvent.VK_S:
			mpanel.restoreMana(0);
			break;
		case KeyEvent.VK_D:
			mpanel.consumeMana(1);
			break;
		case KeyEvent.VK_F:
			mpanel.restoreMana(1);
			break;
		case KeyEvent.VK_G:
			mpanel.consumeMana(2);
			break;
		case KeyEvent.VK_H:
			mpanel.restoreMana(2);
			break;
		case KeyEvent.VK_J:
			mpanel.consumeMana(3);
			break;
		case KeyEvent.VK_K:
			mpanel.restoreMana(3);
			break;
		case KeyEvent.VK_ENTER:
			breakCylinder();
			break;
		case KeyEvent.VK_SPACE:
			playMusic( strMagicSound );
			break;
		case KeyEvent.VK_Z:
			playMusic( strMaouVoice1 );
			break;
		case KeyEvent.VK_X:
			playMusic( strMaouVoice2 );
			break;
		case KeyEvent.VK_C:
			playMusic( strMaouVoice3 );
			break;
		case KeyEvent.VK_V:
			playMusic( strMaouVoice4 );
			break;
		case KeyEvent.VK_Q:
			mpanel.startSubtitle();
			break;
		case KeyEvent.VK_W:
			mpanel.deleteSubtitle();
			break;
		}
	}

	private void breakCylinder() {
		new Thread(new Runnable(){
			@Override
			public void run(){
		        MyPlayer player = new MyPlayer();
		        try {
		            player.play("sei_ge_wareru04.mp3");
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (JavaLayerException e) {
		            e.printStackTrace();
		        } finally {
		            if (player != null) {
		                try {
		                    player.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
	        	}
			}
		}).start();
		mpanel.breakCylinder();
	}

	private void playMusic( final String str ) {
		new Thread(new Runnable(){
			@Override
			public void run(){
		        MyPlayer player = new MyPlayer();
		        try {
		            player.play(str);
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (JavaLayerException e) {
		            e.printStackTrace();
		        } finally {
		            if (player != null) {
		                try {
		                    player.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
	        	}
			}
		}).start();
	}
	
	private void ToggleFullScreen(boolean b) {
		this.setUndecorated(b);
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		device.setFullScreenWindow(b?this:null);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}


}
