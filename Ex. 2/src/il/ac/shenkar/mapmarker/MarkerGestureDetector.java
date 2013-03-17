package il.ac.shenkar.mapmarker;

import android.app.Activity;
import android.app.Dialog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MarkerGestureDetector extends GestureDetector.SimpleOnGestureListener
{
	private Marker marker;
	private TextView infoBox;
	private Dialog descriptionDialog;
	private Activity thisActivity;
	private RelativeLayout relativeLayout;
	private ImageView markerImageView;
	private MarkerArray markerArray;

	public MarkerGestureDetector()
	{
		markerArray = MarkerArray.getInstance();
	}
	
	public MarkerGestureDetector(Marker marker, TextView infoBox, Activity thisActivity, RelativeLayout relativeLayout, ImageView markerImageView)
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
		markerArray.removemarker(marker);
		relativeLayout.removeView(markerImageView);
		infoBox.setText("");
	}
	
	// Double tap gesture for setting a new marker description
	@Override
	public boolean onDoubleTap(MotionEvent e)
	{
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
