package il.ac.shenkar.mapmarker;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerGestureListener extends GestureDetector.SimpleOnGestureListener
{
	private Marker marker;
	private TextView infoBox;
	private Dialog descriptionDialog;
	private Activity thisActivity;
	private RelativeLayout relativeLayout;
	private ImageView markerImageView;
	private MarkerArray markerArray;
	
	private ProgressDialog progressDialog;

	public MarkerGestureListener()
	{
		markerArray = MarkerArray.getInstance();
	}
	
	public MarkerGestureListener(Marker marker, TextView infoBox, Activity thisActivity, RelativeLayout relativeLayout, ImageView markerImageView)
	{
		this.marker = marker;
		this.infoBox = infoBox;
		this.thisActivity = thisActivity;
		this.relativeLayout = relativeLayout;
		this.markerImageView = markerImageView;
		markerArray = MarkerArray.getInstance();
	}

	// Long press gesture for deleting a marker
	@Override
	public void onLongPress (MotionEvent e)
	{
    	if (!isTheOwner(marker))
    	{
    		Toast.makeText(thisActivity, "Marker can only be deleted by his owner.", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    	{
    	    public void onClick(DialogInterface dialog, int which)
    	    {    	    	
    	        switch (which)
    	        {
    	        case DialogInterface.BUTTON_POSITIVE: // Delete the marker if "Yes" is selected
    	        	progressDialog = ProgressDialog.show(thisActivity, "Deleting Marker", "Loading...", false);
    	    		markerArray.removemarker(marker);
    	    		relativeLayout.removeView(markerImageView);
    	    		infoBox.setText("");
    	    		
    				ParseQuery query  = new ParseQuery("Marker");
    				query.whereEqualTo("ID", marker.getId());
    				query.getFirstInBackground(new GetCallback()
    				{
						@Override
						public void done(ParseObject markerToDel, ParseException e)
						{
							try
							{
								markerToDel.delete();
								Toast.makeText(thisActivity, "Marker: " + marker.getDescription() + " was deleted.", Toast.LENGTH_SHORT).show();
							}
							catch (ParseException e1)
							{
								Toast.makeText(thisActivity, "Could not delete the marker form server", Toast.LENGTH_SHORT).show();
							}
							
							progressDialog.dismiss();
						}
    				});
    				
    	            break;

    	        case DialogInterface.BUTTON_NEGATIVE: // Do nothing if "No" is selected
    	            break;
    	        }
    	    }
    	};
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
    	builder.setMessage("Remove Marker? Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
	}
	
	// Double tap gesture for setting a new description for the marker
	@Override
	public boolean onDoubleTap(MotionEvent e)
	{
    	if (!isTheOwner(marker))
    	{
    		Toast.makeText(thisActivity, "Marker can only be edited by his owner.", Toast.LENGTH_SHORT).show();
    		return false;
    	}
		
		descriptionDialog = new Dialog(thisActivity);
		descriptionDialog.setContentView(R.layout.get_description_dialog);
		descriptionDialog.setTitle("Set New Description");

		descriptionDialog.show();

		final EditText descriptionField = (EditText) descriptionDialog.findViewById(R.id.description_field);
		descriptionField.setText(marker.getDescription());

		// ClickListener for the create code button in the dialog
		Button setButton = (Button) descriptionDialog.findViewById(R.id.set_description);
		setButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Deleting the old marker
				markerArray.removemarker(marker);
				
				// Updating the marker object and the the info box
				marker.setDescription(descriptionField.getText().toString());
				infoBox.setText(descriptionField.getText().toString());
				
				// Getting the object from the server and updating the description
				ParseQuery query  = new ParseQuery("Marker");
				query.whereEqualTo("ID", marker.getId());
				
				// Getting the first object that match the ID in background and updating it in background
				query.getFirstInBackground(new GetCallback()
				{
					public void done(ParseObject markerToUpdate, ParseException e)
					{
						markerToUpdate.put("Description", marker.getDescription());
						markerToUpdate.saveInBackground();
					}
				});
				
				// Re-adding the updated marker to the array and closing the dialog
				markerArray.addMarker(marker);
				descriptionDialog.cancel();
			}
		});

		return true;
	}

	// Single tap gesture for showing the marker description in the info box 
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		infoBox.setText(marker.getDescription());
		return true;
	}
	
	// If the marker is owned by the current user return true, else false
	public boolean isTheOwner(Marker marker)
	{
		if (ParseUser.getCurrentUser().getObjectId().equals(marker.getOwner()))
				return true;
		else
			return false;
	}

	public Marker getMarker()
	{
		return marker;
	}

	public void setMarker(Marker marker)
	{
		this.marker = marker;
	}

	public TextView getInfoBox()
	{
		return infoBox;
	}

	public void setInfoBox(TextView infoBox)
	{
		this.infoBox = infoBox;
	}

	public Activity getThisActivity()
	{
		return thisActivity;
	}

	public void setThisActivity(Activity thisActivity)
	{
		this.thisActivity = thisActivity;
	}

	public RelativeLayout getRelativeLayout()
	{
		return relativeLayout;
	}

	public void setRelativeLayout(RelativeLayout relativeLayout)
	{
		this.relativeLayout = relativeLayout;
	}
}
