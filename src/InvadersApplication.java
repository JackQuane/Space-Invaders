import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Graphics;

public class InvadersApplication extends JFrame implements Runnable, KeyListener 
{

	public static final Dimension WindowSize = new Dimension(800, 600);
	private static final int NUMALIENS = 30;
	private Alien[] AliensArray = new Alien[NUMALIENS];
	private Spaceship PlayerShip;
	private static String workingDirectory;
	private boolean isInitialised = false;
	private BufferStrategy strategy;
	private Graphics offscreenGraphics;
	private Image bulletImage;
	private boolean isGameInProgress = false;
	private ArrayList bulletsList = new ArrayList();
	private int score;
	private int best;
	
	
	
	public InvadersApplication()
	{
		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = screensize.width/2 - WindowSize.width/2;
		int y = screensize.height/2 - WindowSize.height/2;
		setBounds(x, y, WindowSize.width, WindowSize.height);
		setVisible(true);
		this.setTitle("Space Invaders");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		ImageIcon icon = new ImageIcon(workingDirectory + "/AlienImage.png");
		Image alienImage = icon.getImage();
		icon = new ImageIcon(workingDirectory + "/AlienImage2.png");
		Image alienImage2 = icon.getImage();
		icon = new ImageIcon(workingDirectory + "/bullet.png");
		bulletImage = icon.getImage();
		
		
		for(int i=0; i<NUMALIENS; i++)
		{
			AliensArray[i] = new Alien(alienImage, alienImage2);
			double xx = (i%5)*80 + 70;
			double yy = (i/5)*40 + 50;
			AliensArray[i].setPosition(xx, yy);
			AliensArray[i].setXSpeed(2);
		}
		
		
		icon = new ImageIcon(workingDirectory + "/Space-invaders.jpg");
		Image shipImage = icon.getImage();
		PlayerShip = new Spaceship(shipImage, bulletImage);
		PlayerShip.setPosition(300, 530);
		
		
		Thread t = new Thread(this);
		t.start();
		
		addKeyListener(this);
		
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		offscreenGraphics = strategy.getDrawGraphics();
		
		isInitialised = true;
	}
	
	
	
	public void run()
	{
		while(1==1)
		{
			try
			{
				Thread.sleep(20);
			} catch (InterruptedException e) { }
			
			boolean alienDirectionReversalNeeded = false;
			for (int i=0; i<NUMALIENS; i++)
			{
				 if(AliensArray[i].isAlive)
				 { 
					if(AliensArray[i].move())
					  alienDirectionReversalNeeded=true;
				 }
			}
		
		  if(alienDirectionReversalNeeded)
		  {
			for(int i=0; i<NUMALIENS; i++)
			{
			  if(AliensArray[i].isAlive)
			  {
			  AliensArray[i].reverseDirection();
			  }
			}
		  }	
			
			
			PlayerShip.move();
			
			
			Iterator iterator = bulletsList.iterator();
			while(iterator.hasNext())
			{
				PlayerBullet b = (PlayerBullet) iterator.next();
				if(b.move())
				{
					iterator.remove();
				}
				else
				{
					double x2 = b.x, y2 = b.y;
					double w1 = 50, h1 = 32;
					double w2 = 6, h2 = 16;
					for(int i=0; i<NUMALIENS; i++)
					{
						if(AliensArray[i].isAlive)
						{
						double x1 = AliensArray[i].x;
						double y1 = AliensArray[i].y;
							if(  ((x1<x2 && x1+w1>x2) || (x2<x1 && x2+w2>x1)) && ((y1<y2 && y1+h1>y2) || (y2<y1 && y2+h2>y1))   )
							{
								AliensArray[i].isAlive=false;
								iterator.remove();
								score+=5;
								
								if(score>best)
								best = score;
								
								
								
								
								break;
							}
					    }	
					}
				}
			}
			
			
			
			
			this.repaint();
			
			
			
			
			
			
		}
	}
	
	
	
	
	
	public void keyPressed (KeyEvent e)
	{
		isGameInProgress = true;
		
		if(e.getKeyCode()==KeyEvent.VK_LEFT)
			PlayerShip.setXSpeed(-4);
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
			PlayerShip.setXSpeed(4);
		else if(e.getKeyCode()==KeyEvent.VK_SPACE)
			bulletsList.add(PlayerShip.shootBullet());
	}
	
	public void keyReleased (KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT)
			PlayerShip.setXSpeed(0);
	}
	
	public void keyTyped (KeyEvent e) { }
	
	
	
	public void paint(Graphics g)
	{
		if (!isInitialised)
			return;
			
		
			g = offscreenGraphics;
			g.setColor(Color.black);
			
			g.fillRect(0, 0, WindowSize.width, WindowSize.height);
			
			
			if(isGameInProgress==false)
			{
				g.setColor(Color.white);
				g.setFont(new Font("TimesRoman", Font.PLAIN, 50)); 
				g.drawString("GAME OVER", 270, 200);
				
				g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				g.drawString("Press any key to play", 290, 250);
				
				g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				g.drawString("[Arrow keys to move, space to fire]", 275, 280);
				
				strategy.show();
				return;
			}
			
			g.setColor(Color.white);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 23));
			g.drawString("Score: " + getScore() + "  Best: " + getBest(), 15, 50);
			
			for(int i=0; i<NUMALIENS; i++)
				AliensArray[i].paint(g);
			
			
			PlayerShip.paint(g);
		
			Iterator iterator = bulletsList.iterator();
			while(iterator.hasNext())
			{
				PlayerBullet b = (PlayerBullet) iterator.next();
				b.paint(g);
			}
			
			
			
			strategy.show();
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int getBest()
	{
		return best;
	}
	
	
	public static void main(String[] args) 
	{
		workingDirectory = System.getProperty("user.dir");
		System.out.println("Working Directory = " + workingDirectory);
		InvadersApplication w = new InvadersApplication();
	}

}

