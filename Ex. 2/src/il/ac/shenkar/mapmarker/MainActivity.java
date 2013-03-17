package il.ac.shenkar.mapmarker;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private ImageView map;
	private RelativeLayout relativeLayout;
	private Dialog descriptionDialog;
	
	private Activity thisActivity;
	private TextView infoBox;
	private MarkerArray markerArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		thisActivity = this;
		markerArray = MarkerArray.getInstance();

		// Creating a gesture detector for the map listener
		final GestureDetector mapGD = new GestureDetector(getApplicationContext(), mapGestureListener);
		
		map = (ImageView) findViewById(R.id.map);
		infoBox = (TextView) findViewById(R.id.info_box);
		relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);
		
		map.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				mapGD.onTouchEvent(event);
				return true;
			}
		});
	}
	
	// Gesture listener for the map to be active when the image is been touched
	GestureDetector.OnGestureListener mapGestureListener = new GestureDetector.SimpleOnGestureListener()
	{
		private float x;
		private float y;
		private Marker newMarker;
		
		@Override
		public void onLongPress(MotionEvent e)
		{	
			descriptionDialog = new Dialog(thisActivity);
			descriptionDialog.setContentView(R.layout.get_description_dialog);
			descriptionDialog.setTitle("Enter Point Description");
			
			descriptionDialog.show();
			
			x = e.getX()-40;
			y = e.getY()-60;
			
			// ClickListener for the create code button in the dialog
			Button setButton = (Button) descriptionDialog.findViewById(R.id.set_description);
			setButton.setOnClickListener(new View.OnClickListener()
			{	
				@Override
				public void onClick(View v)
				{
					EditText descriptionField = (EditText) descriptionDialog.findViewById(R.id.description_field);
					LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
					
					// Creating new marker and adding it to the array
					newMarker = new Marker(x, y, descriptionField.getText().toString());
					markerArray.addMarker(newMarker);
					
					// Creating new image view and setting the position
					ImageView markerImage = (ImageView) inflater.inflate(R.layout.marker, null);
					markerImage.setX(x);
					markerImage.setY(y);
					
					// Creating new custom gesture detector
					MarkerGestureDetector markerGestureDetector = new MarkerGestureDetector(newMarker, infoBox, thisActivity, relativeLayout, markerImage);
					
					// Creating a gesture detector for the marker listener
					final GestureDetector markerGD = new GestureDetector(getApplicationContext(), markerGestureDetector);
					
					markerImage.setOnTouchListener(new View.OnTouchListener()
					{
						@Override
						public boolean onTouch(View v, MotionEvent event)
						{
							markerGD.onTouchEvent(event);
							return true;
						}
					});
					
					// Adding the marker to the layout
					relativeLayout.addView(markerImage);
					
					descriptionDialog.cancel();
				}
			});
		}
	};
}
