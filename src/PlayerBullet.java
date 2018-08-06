import java.awt.Image;

public class PlayerBullet extends Sprite2D 
{

	
	public PlayerBullet(Image i) 
	{
		super(i, i);
	}
	
	public boolean move()
	{
		y-=10;
		return (y<0);
	}
}