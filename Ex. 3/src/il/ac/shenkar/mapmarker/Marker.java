package il.ac.shenkar.mapmarker;

import android.widget.ImageView;

public class Marker
{
	private float x;
	private float y;
	private String description;
	private String QRcode;
	private String owner;
	private ImageView markerImage;
	
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

	public String getId()
	{
		return QRcode;
	}

	public void setId(String id)
	{
		this.QRcode = id;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public ImageView getMarkerImage()
	{
		return markerImage;
	}

	public void setMarkerImage(ImageView markerImage)
	{
		this.markerImage = markerImage;
	}
}
