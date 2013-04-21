package il.ac.shenkar.mapmarker;

import android.os.Handler;

public class AutoUpdate extends Handler implements Runnable
{
	private MarkerHandler markerHandler;
	private static long interval;
	
	public AutoUpdate()
	{
		super();
	}
	
	public AutoUpdate(MarkerHandler markerHandler)
	{
		this.markerHandler = markerHandler;
		interval = 60000; // Default time is 1 minute
	}
	
	public void startAutoUpdate()
	{
		postDelayed(this, interval); // Start the repeating process for checking new markers in 1 minute
	}
	
	public void removeRunnable()
	{
		removeCallbacks(this); // Stop the repeating
	}

	@Override
	public void run()
	{	
		markerHandler.clearAllMarkersFromMap();
		markerHandler.getMarkers(); // Checking for new markers
		
		postDelayed(this, interval); // Check for new markers again in 1 minute
	}

	public static long getInterval()
	{
		return interval;
	}

	public static void setInterval(long interval)
	{
		AutoUpdate.interval = interval;
	}
}
