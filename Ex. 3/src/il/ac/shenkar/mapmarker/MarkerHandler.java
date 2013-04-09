package il.ac.shenkar.mapmarker;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerHandler
{
	private Activity thisActivity;
	private TextView infoBox;
	private RelativeLayout relativeLayout;
	private MarkerArray markerArray;
	
	// Variables for the gesture
	private Marker newMarker;
	private Dialog descriptionDialog;
	private ProgressDialog progressDialog;
	
	public MarkerHandler()
	{
		markerArray = MarkerArray.getInstance();
	}
	
	public MarkerHandler(Activity thisActivity, TextView infoBox, RelativeLayout relativeLayout)
	{
		markerArray = MarkerArray.getInstance();
		this.thisActivity = thisActivity;
		this.infoBox = infoBox;
		this.relativeLayout = relativeLayout;
	}

	// Function for creating the marker and the gesture listener
	public void addMarkerToMap(Marker markerToAdd)
	{
		LayoutInflater inflater = LayoutInflater.from(thisActivity);
		
		// Creating new image view and setting the position
		final ImageView markerImage = (ImageView) inflater.inflate(R.layout.marker, null);
		markerImage.setX(markerToAdd.getX());
		markerImage.setY(markerToAdd.getY());
		
		// Creating new custom gesture detector
		MarkerGestureListener markerGestureListener = new MarkerGestureListener(markerToAdd, infoBox, thisActivity, relativeLayout, markerImage);
		
		// Creating a gesture detector for the marker listener
		final GestureDetector markerGD = new GestureDetector(thisActivity, markerGestureListener);
		
		markerImage.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				markerGD.onTouchEvent(event);
				return true;
			}
		});
		
		// Setting the marker ImageView
		markerToAdd.setMarkerImage(markerImage);
		
		// Adding the marker to the layout
		relativeLayout.addView(markerImage);
		
		// Setting the animation for the marker and showing it
		ValueAnimator animation = ObjectAnimator.ofFloat(markerImage, "y" , 0f, markerToAdd.getY());
		Interpolator pol = AnimationUtils.loadInterpolator(thisActivity, android.R.anim.bounce_interpolator);
		animation.setInterpolator(pol);
		animation.setDuration(800);
		animation.start();
	}
	
	// Function for asking the user for marker description and creating it
	public void createMarker(final String qrStringCode, final float x, final float y)
	{
		// Checking if there is already a marker with this code
		if (markerArray.isMarkerWithSameCodeExist(qrStringCode))
		{
			Toast.makeText(thisActivity, "Sorry, The marker already exist", Toast.LENGTH_SHORT).show();
			return;
		}
		
		descriptionDialog = new Dialog(thisActivity);
		descriptionDialog.setContentView(R.layout.get_description_dialog);
		descriptionDialog.setTitle("Enter Point Description");
		
		descriptionDialog.show();
		
		// ClickListener for the create code button in the dialog
		Button setButton = (Button) descriptionDialog.findViewById(R.id.set_description);
		setButton.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				EditText descriptionField = (EditText) descriptionDialog.findViewById(R.id.description_field);
				
				// Creating new marker and adding it to the array
				newMarker = new Marker(x, y, descriptionField.getText().toString());
				newMarker.setId(qrStringCode);
				newMarker.setOwner(ParseUser.getCurrentUser().getObjectId());
				
				addMarkerToMap(newMarker);
				
				markerArray.addMarker(newMarker);
				
				// Closing the dialog
				descriptionDialog.cancel();
				
				// Creating new parseMarker object to be saved in the server
				ParseObject parseMarker = new ParseObject("Marker");
				parseMarker.put("ID", qrStringCode);
				parseMarker.put("Xaxis", x);
				parseMarker.put("Yaxis", y);
				parseMarker.put("Owner", newMarker.getOwner());
				parseMarker.put("Description", newMarker.getDescription());
				
				// Saving the new marker in the server
				parseMarker.saveInBackground();
			}
		});		
	}
	
	// Function for downloading all the markers from the server
	public void getMarkers()
	{
		ParseQuery query  = new ParseQuery("Marker");
		
		progressDialog = ProgressDialog.show(thisActivity, "Downloading Markers", "Loading...", false);
		
		query.findInBackground(new FindCallback()
		{
			@Override
			public void done(List<ParseObject> markers, ParseException e)
			{
				for (int i=0 ; i<markers.size() ; ++i)
				{	
					Marker newMarker = new Marker(markers.get(i).getInt("Xaxis"), markers.get(i).getInt("Yaxis") ,markers.get(i).getString("Description"));
					newMarker.setId(markers.get(i).getString("ID"));
					newMarker.setOwner(markers.get(i).getString("Owner"));
					
					markerArray.addMarker(newMarker);
					addMarkerToMap(newMarker);
				}
				
				progressDialog.dismiss();
			}
		});
	}
}
