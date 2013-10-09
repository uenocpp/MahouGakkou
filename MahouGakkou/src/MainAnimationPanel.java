import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;


public class MainAnimationPanel extends JPanel implements Runnable {

	/**
	 * 定数
	 */
	private static final long serialVersionUID = 1L;
	private static final long FRAME_PER_SECOND = 30;
	
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int GOLD = 3;
	
	public static final int MAX_MANA = 3;
	
	public static final int OP_USE = 0;
	public static final int OP_RESTORE = 1;
	
	public static final String strCylinderFileName = "cylinder.png";
	public static final String strBrokenFileName = "cylinder_broken_l.png";
	
	public static final String strLiquidFileName[] = { "liquid_red.png", "liquid_green.png", "liquid_blue.png", "liquid_gold.png" };
	public static final int CYLINDERS_NUMBER = strLiquidFileName.length;
	
	public static final long FLASH_TIME = 1000;
	
	
	/**
	 * レイアウト系
	 *　左上を(0.0,0.0),右下を(1.0,1.0)とした比率で指定
	 */
	public static final double CYLINDER_WIDTH = 0.1;
	public static final double CYLINDER_HEIGHT = 0.5;
	
	public static final double CYLINDER_TOP = 0.2;
	public static final double CYLINDER_LEFT = 0.1;
	
	public static final double CYLINDER_MARGIN = 0.1;
	
	public static final double CYLINDER_BOTTOM = CYLINDER_HEIGHT + CYLINDER_TOP - 0.04;
	
	public static final double BROKEN_LEFT = CYLINDER_LEFT - 0.1;
	public static final double BROKEN_WIDTH = CYLINDER_WIDTH + 0.2;
	
	public static final double PARTICLE_MARGIN = 0.12;
	public static final double PARTICLE_DEVIATION = 0.01;
	public static final double PARTICLE_SPEED = 0.2;
	//下げる比率
	public static final double LIQUID_HEIGHT_RATE[] = { 0.64, 0.4, 0.2, 0.0 };
	/**
	 * 変数
	 */
	
	Image imageCylinder = Toolkit.getDefaultToolkit().createImage( strCylinderFileName );
	Image imageBroken = Toolkit.getDefaultToolkit().createImage( strBrokenFileName );
	
	private long timeFlash=0;
	
	/**
	 * 内部クラス
	 */
	class Cylinder{
		
		public static final int STATE_STAY = 0;
		public static final int STATE_INC = 1;
		public static final int STATE_DEC = 2;
		
		public static final int MAX_MANA = 3;
		
		public static final long MS_ANIMATION_TIME = 1500;
		
		private int storedMana;
		private Image imageLiquid = null;
		
		private int nextMana;
		private long beginningTime = 0;
		
		Cylinder( int storedMana, String fileName ){
			this.storedMana = storedMana;
			this.nextMana = storedMana;
			imageLiquid = Toolkit.getDefaultToolkit().createImage( fileName );
		}
		
		public void setMana( int mana ){
			storedMana = mana;
			nextMana = mana;
		}
		
		public int getState(){
			if( nextMana == storedMana ){
				return STATE_STAY;
			}
			else if( nextMana < storedMana ){
				return STATE_DEC;
			}
			assert(nextMana > storedMana);
			return STATE_INC;
		}
		
		public void beginAnime( int next ){
			beginningTime = getTime();
			nextMana = next;
		}
		
		public double getAnimeProcRate(){
			double ret = (double)(getTime()-beginningTime)/MS_ANIMATION_TIME;
			return Math.max(Math.min( 1.0, ret ),0.0);
		}
		
		public boolean doesAnimeFinished(){
			return getAnimeProcRate() == 1.0;
		}
		
		public boolean isProcAnime(){
			return !doesAnimeFinished();
		}
		
		public void finishAnime(){
			storedMana = nextMana;
		}
		
		public void update(){
			if( doesAnimeFinished() ){
				finishAnime();
			}
		}
		
		public void restoreMana(){
			System.out.println("restore: " + storedMana + nextMana );
			if( !isProcAnime() && storedMana < MAX_MANA ){
				finishAnime();
				beginAnime( storedMana + 1 );
			}
		}
		
		public void consumeMana(){
			System.out.println("consume: " + storedMana + nextMana );
			if( !isProcAnime() && storedMana > 0 ){
				finishAnime();
				beginAnime( storedMana - 1 );
			}
		}
		
		public void draw( Graphics g, Dimension d, double dx, double dy ){
			if( getState() == STATE_STAY ){
				g.drawImage( imageLiquid, (int)(d.width*(CYLINDER_LEFT + dx)), (int)(d.height*(CYLINDER_TOP +dy +CYLINDER_HEIGHT*LIQUID_HEIGHT_RATE[storedMana])), (int)(d.width*CYLINDER_WIDTH), (int)(d.height*CYLINDER_HEIGHT), null);
				g.setColor(Color.black);
				g.fillRect( (int)(d.width*(CYLINDER_LEFT + dx)), (int)(d.height*CYLINDER_BOTTOM), (int)(d.width*CYLINDER_WIDTH), d.height );
				g.drawImage( imageCylinder, (int)(d.width*(CYLINDER_LEFT + dx)), (int)(d.height*(CYLINDER_TOP +dy)), (int)(d.width*CYLINDER_WIDTH), (int)(d.height*CYLINDER_HEIGHT), null);
			}
			else{
				int border = (int)(d.height*(CYLINDER_TOP +dy +(CYLINDER_HEIGHT*(LIQUID_HEIGHT_RATE[storedMana]*(1.0-getAnimeProcRate())+LIQUID_HEIGHT_RATE[nextMana]*getAnimeProcRate()))));
				g.drawImage( imageLiquid, (int)(d.width*(CYLINDER_LEFT + dx)), border, (int)(d.width*CYLINDER_WIDTH), (int)(d.height*CYLINDER_HEIGHT), null);
				g.setColor(Color.black);
				g.fillRect( (int)(d.width*(CYLINDER_LEFT + dx)), (int)(d.height*CYLINDER_BOTTOM), (int)(d.width*CYLINDER_WIDTH), d.height );
				g.drawImage( imageCylinder, (int)(d.width*(CYLINDER_LEFT + dx)), (int)(d.height*(CYLINDER_TOP +dy)), (int)(d.width*CYLINDER_WIDTH), (int)(d.height*CYLINDER_HEIGHT), null);
				addParticle( new Particle( (int)(d.width*(CYLINDER_LEFT + CYLINDER_WIDTH / 2.0 + dx + PARTICLE_DEVIATION * random.nextGaussian())), border + (int)(d.height*PARTICLE_MARGIN), PARTICLE_SPEED, 10 ) );
			}
			update();
		}
	}
	
	class BrokenCylinder extends Cylinder{

		BrokenCylinder(int storedMana, String fileName) {
			super(storedMana, fileName);
		}
		
		@Override
		public void draw( Graphics g, Dimension d, double dx, double dy ){
			g.drawImage( imageBroken, (int)(d.width*(BROKEN_LEFT + dx)), (int)(d.height*(CYLINDER_TOP +dy)), (int)(d.width*BROKEN_WIDTH), (int)(d.height*CYLINDER_HEIGHT), null);
		}
		
	}
	
	List< Cylinder > cylinders= new ArrayList< Cylinder >();
	
	class Particle{
		private long birthTime;
		public static final long LIFE_SPAN = 5000;
		public int x;
		public int y;
		public double speed;
		public int r;
		
		Particle( int x, int y, double speed, int r ){
			this.x = x;
			this.y = y;
			this.r = r;
			this.speed = speed;
			birthTime = getTime();
		}
		
		public void draw( Graphics g ){
			if( !isExceeded() ){
				g.setColor( Color.white );
				g.fillOval( x, y-(int)(getAge()*speed), r, r );
			}
		}
		
		public long getAge(){
			return getTime() - birthTime;
		}
		
		public boolean isExceeded(){
			return getAge() > LIFE_SPAN;
		}
	}
	
	Particle particles[] = new Particle[ 300 ];
	private int particleIndex = 0;
	private Random random;
	
	public void addParticle( Particle p ){
		particles[ particleIndex++ ] = p;
		particleIndex %= particles.length;
	}
	
	public void drawParticles( Graphics g ){
		for( int i = 0; i < particles.length; ++i ){
			if( particles[ i ] != null ) particles[ i ].draw(g);
		}
	}
	
	class Subtitles{
		private long beginTime = getTime();
		private String message[];
		private static final long interval = 100;
		private int y;
		private static final int FONT_HEIGHT = 50;
		Subtitles( String msg[], int y ){
			message = msg;
			this.y = y;
		}
		
		public void drawString( Graphics g ){
			int resource = (int)((getTime() - beginTime)/interval);
			for( int i = 0; i < message.length; ++i ){
				int touse = Math.min(resource, message[i].length());
				resource -= touse;
				g.drawString( message[i].substring( 0,touse ), 10, y + i * FONT_HEIGHT );
			}
		}
	}
	
	String message[] = {
			"      ",
			"こんにちは。私は魔王だ。",
			"今日は君たちをすてきな魔法学校に招待しよう。"
	};
	
	private Subtitles subtitles = null;
	
	public void startSubtitle(){
		subtitles = new Subtitles(message,700);
	}
	
	public void deleteSubtitle(){
		subtitles = null;
	}
	
	MainAnimationPanel(){
		
		for( int i = 0; i < CYLINDERS_NUMBER; ++i ){
			cylinders.add( new Cylinder( MAX_MANA, strLiquidFileName[i] ) );
		}

		//cylinders.get(GOLD).setMana(2);
		
		random = new Random();
		new Thread(this).start();
	}
	
	public static long getTime(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
	
	public void consumeMana( int color ){
		cylinders.get(color).consumeMana();
	}
	
	public void restoreMana( int color ){
		cylinders.get(color).restoreMana();
	}
	
	public void breakCylinder(){
		beginFlash();
		cylinders.remove(GOLD);
		cylinders.add( new BrokenCylinder(0,strLiquidFileName[GOLD]) );
	}
	
	private void beginFlash() {
		timeFlash  = getTime();
	}
	
	private double flashRate(){
		double ret = (double)(getTime()-timeFlash)/FLASH_TIME;
		return Math.max(0.0,Math.min(1.0,ret));
	}

	@Override
	public void paintComponent( Graphics g ){
		super.paintComponent(g);
		Dimension d = getSize();
		//System.out.println("repaint: " + d);
		this.setBackground( Color.black );
		//g.setColor( Color.red );
		//g.fillRect( (int)(d.width*CYLINDER_LEFT), (int)(d.height*CYLINDER_TOP), (int)(d.width*CYLINDER_WIDTH), (int)(d.height*CYLINDER_HEIGHT) );
		for( int i = 0; i < cylinders.size(); ++i ){
			cylinders.get( i ).draw( g, d, i*(CYLINDER_MARGIN+CYLINDER_WIDTH), 0 );
		}
		
		//drawParticles(g);
		
		g.setColor( new Color(255,255,255,(int)(255*(1.0-flashRate()))));
		g.fillRect(0, 0, d.width, d.height);
		
		g.setColor(Color.white);
		g.setFont(new Font(null,Font.BOLD,40));
		//g.drawString("こんにちは。私は魔王です。",d.width-(int)(getTime()/2%(d.width+100)),40);
		
		if( subtitles != null )subtitles.drawString(g);
		
	}

	@Override
	public void run() {
		while(true){
			repaint();
			try {
				Thread.sleep( 1000L / FRAME_PER_SECOND );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
