package il.ac.shenkar.mapmarker;

public class Marker
{
	private float x;
	private float y;
	private String description;
	private int id;
	
	public Marker()
	{

	}
	
	public Marker(float x, float y, String description)
	{
		super();
		this.x = x;
		this.y = y;
		this.description = description;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
