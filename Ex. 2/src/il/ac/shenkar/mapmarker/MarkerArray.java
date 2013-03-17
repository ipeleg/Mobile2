package il.ac.shenkar.mapmarker;

import java.util.ArrayList;

public class MarkerArray
{
	private static MarkerArray array;
	private ArrayList<Marker> markersArray;
	
	private MarkerArray()
	{
		markersArray = new ArrayList<Marker>();
	}
	
	public static MarkerArray getInstance()
	{
		if (array == null)
			array = new MarkerArray();
		return array;
	}
	
	public void addMarker(Marker marker)
	{
		markersArray.add(marker);
	}
	
	public void removemarker(Marker marker)
	{
		markersArray.remove(marker);
	}
	
	public int getArraySize()
	{
		return markersArray.size();
	}
}
