package il.ac.shenkar.mapmarker;

import il.ac.shenkar.mapmarker.IntentResult;
import il.ac.shenkar.mapmarker.IntentIntegrator;
import com.parse.Parse;
import com.parse.ParseUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private IntentIntegrator integrator;
	private MarkerHandler markerHandler;
	private ImageView map;
	private RelativeLayout relativeLayout;
	private Dialog qrDialog;
	private Dialog mapDialog;
	private GestureDetector mapGD;
	private String QRcode;
	
	private Activity thisActivity;
	private TextView infoBox;
	private MarkerArray markerArray;
	
	private Button scanCodeLocation;
	
	// Variable for scanning mode, 1 = when creating new marker, 2 = when scanning QR to retrieve location
	private int scanCode;
	
	// Variables for the gesture
	private float x;
	private float y;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Initializing the parse
		Parse.initialize(this, "7NTkZQ7iCulhrCgT6h6Iux8q0qnr8L1oJDE2yByJ", "aAwkOZKPGi8AUtVn9VSAeat0eQMKJD0pXiGNTuHe");
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		thisActivity = this;
		markerArray = MarkerArray.getInstance();
		
		// Creating a gesture detector for the map listener
		mapGD = new GestureDetector(getApplicationContext(), mapGestureListener);
		
		// Creating a QR code integrator
		integrator = new IntentIntegrator(thisActivity);
		
		map = (ImageView) findViewById(R.id.map);
		infoBox = (TextView) findViewById(R.id.info_box);
		relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
		scanCodeLocation = (Button) findViewById(R.id.scan_for_location_btn);
		
		markerHandler = new MarkerHandler(thisActivity, infoBox, relativeLayout);
		
		markerHandler.getMarkers();

		map.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				mapGD.onTouchEvent(event);
				return true;
			}
		});
		
		scanCodeLocation.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Setting the scan code to let know that the scan is for retrieving location from QR code
				scanCode = 2;
				
				// Opening the scan mode
				integrator.initiateScan();
			}
		});
	}

	// Gesture listener for the map to be active when the image is been touched
	GestureDetector.OnGestureListener mapGestureListener = new GestureDetector.SimpleOnGestureListener()
	{
		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			Matrix zoommatrix = new Matrix();
			float[] centerpoint = {map.getWidth()/2.0f, map.getHeight()/2.0f};
			
			zoommatrix.postScale(1.1f, 1.1f, centerpoint[0], centerpoint[1]);
			map.setImageMatrix(zoommatrix);
			
			map.invalidate();
			
			return super.onDoubleTap(e);
		}

		@Override
		public void onLongPress(MotionEvent e)
		{	
			// If the map dialog is not null there is no need to rescan the code (It means the user already scan some code but it was not found)
			if (mapDialog != null)
			{
				x = e.getX()+30;
				y = e.getY()-60;
				
				mapDialog.cancel();
				mapDialog = null;
				markerHandler.createMarker(QRcode, x, y);
				return;
			}
			
			x = e.getX()-35;
			y = e.getY()-65;
			
			// Opening a dialog to explain the user he needs to scan a QR code to attach to the new marker
			qrDialog = new Dialog(thisActivity);
			qrDialog.setContentView(R.layout.scan_qr_dialog);
			qrDialog.setTitle("Scan QR");
			
			qrDialog.show();
			
			// ClickListener for the create code button in the dialog
			Button scanButton = (Button) qrDialog.findViewById(R.id.scan_qr_btn);
			scanButton.setOnClickListener(new View.OnClickListener()
			{	
				@Override
				public void onClick(View v)
				{
					// Setting the scan code to let know that the scan is for creating new marker
					scanCode = 1;
					
					// Opening the scan mode
					integrator.initiateScan();
					
					// Closing the dialog
					qrDialog.cancel();
				}
			});
		}
	};
	
	// Activity result for handling the returning result from the QR scan
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		boolean markerFound = false;
		String result;
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

		if (scanResult != null)
		{
			result = scanResult.getContents();
			if (result == null)
				return;
			
			switch (scanCode)
			{
			case 1:
				markerHandler.createMarker(result, x, y);
				break;
				
			case 2:
				for (int i=0 ; markerArray.getArraySize() > i ; ++i)
				{
					if (markerArray.getMarker(i).getId().equals(result))
					{
						markerArray.getMarker(i).getMarkerImage().setImageResource(R.drawable.marker_current);
						infoBox.setText(markerArray.getMarker(i).getDescription());
						markerFound = true;
					}
					else
					{
						markerArray.getMarker(i).getMarkerImage().setImageResource(R.drawable.marker);
					}
				}
				
				// If the marker was not found suggest the user to add it
				if (!markerFound)
					markerNotFound(result);
				
				break;
			}
		}
	}
	
	// In case the marker was scanned and not found suggest the user to add it
	public void markerNotFound(String result)
	{
		QRcode = result;
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    	{
    	    public void onClick(DialogInterface dialog, int which)
    	    {    	    	
    	        switch (which)
    	        {
    	        case DialogInterface.BUTTON_POSITIVE: // add new marker if "Yes" is selected
    				// Opening a dialog to explain the user he needs to point the marker place on the map
    				mapDialog = new Dialog(thisActivity);
    				mapDialog.setContentView(R.layout.get_point_on_map_dialog);
    				mapDialog.setTitle("Pick a point on the map.");
    				
    				mapDialog.show();
    				
    				map = (ImageView) mapDialog.findViewById(R.id.dialog_map);
    				
    				map.setOnTouchListener(new View.OnTouchListener()
    				{
    					@Override
    					public boolean onTouch(View v, MotionEvent event)
    					{
    						mapGD.onTouchEvent(event);
    						return true;
    					}
    				});
    				
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE: // Do nothing if "No" is selected
    	            break;
    	        }
    	    }
    	};
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
    	builder.setMessage("The marker was not found :(\n\nWould you like to add it?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();		
	}
	
	public float convertPixelsToDp(float px)
	{
	    Resources resources = thisActivity.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	getMenuInflater().inflate(R.menu.action_bar, menu);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    		case R.id.logout:
    			ParseUser.logOut();
    			finish();
    			break;
    	}
    	return true;
    }
}
