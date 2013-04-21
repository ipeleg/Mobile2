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
	
	public boolean isMarkerWithSameCodeExist(String QRcode)
	{
		for (int i=0; i<markersArray.size() ; ++i)
		{
			if (markersArray.get(i).getId().equals(QRcode))
				return true;
		}
		
		return false;
	}
	
	public void addMarker(Marker marker)
	{
		markersArray.add(marker);
	}
	
	public void removemarker(Marker marker)
	{
		markersArray.remove(marker);
	}
	
	public void removeAllMarkers()
	{
		markersArray.clear();
	}
	
	public Marker getMarker(int index)
	{
		return markersArray.get(index);
	}
	
	public int getArraySize()
	{
		return markersArray.size();
	}
}
