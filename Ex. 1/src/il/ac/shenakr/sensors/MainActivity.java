package il.ac.shenakr.sensors;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private Activity thisActivity;
	private Context context;
	
	private Button scanQrButton;
	private Button generateQrButton;
	private Button linearAccButton;
	private Button azimuthButton;
	private Button lightButton;
	private Button pressureButton;
	
	private IntentIntegrator integrator;
	
	private Dialog getStringDialog;
	private Dialog showLinearAccResultsDialog;
	private Dialog showAzimuthDialog;
	private Dialog showLightDialog;
	private Dialog showPressureDiaolg;
	
	private TextView Xaxis;
	private TextView Yaxis;
	private TextView Zaxis;
	
	private SensorManager sensorManager;
	private Sensor linearAccSensor;
	private Sensor magneticSensor;
	private Sensor lightSensor;
	private Sensor pressureSensor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		thisActivity = this;
		context = getApplicationContext();
		
		scanQrButton = (Button) findViewById(R.id.scan_qr);
		generateQrButton = (Button) findViewById(R.id.generate_qr);
		linearAccButton = (Button) findViewById(R.id.linear_acceleration);
		azimuthButton = (Button) findViewById(R.id.azimuth);
		lightButton = (Button) findViewById(R.id.light);
		pressureButton = (Button) findViewById(R.id.pressure);
		
		// Creating a QR code integrator
		integrator = new IntentIntegrator(thisActivity);
		
		// Getting the sensor manager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		linearAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		
		// ClickListener to activate the QR scanning mode
		scanQrButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{	
				// Start the QR scanning
				integrator.initiateScan();
			}
		});

		// ClickListener to activate the QR generating mode
		generateQrButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				getStringDialog = new Dialog(thisActivity);
				getStringDialog.setContentView(R.layout.get_string_dialog);
				getStringDialog.setTitle("Enter a Phrase");
				
				getStringDialog.show(); // Showing the dialog to the user
				
				// ClickListener for the create code button in the dialog
				Button createButton = (Button) getStringDialog.findViewById(R.id.create_code);
				createButton.setOnClickListener(new View.OnClickListener()
				{	
					@Override
					public void onClick(View v)
					{
						EditText phraseField = (EditText) getStringDialog.findViewById(R.id.phrase_field);
						integrator.shareText(phraseField.getText().toString());
					}
				});
			}
		});
		
		// ClickListener to get and present the Linear Acceleration sensor data
		linearAccButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{	
				showLinearAccResultsDialog = new Dialog(thisActivity);
				showLinearAccResultsDialog.setContentView(R.layout.linear_acc_dialog);
				showLinearAccResultsDialog.setTitle("Linear Accelerometer Results:");
				
				showLinearAccResultsDialog.show();
				
				// Getting the text views in which the linear acceleration results will be printed
				Xaxis = (TextView) showLinearAccResultsDialog.findViewById(R.id.linear_acc_x_output);
				Yaxis = (TextView) showLinearAccResultsDialog.findViewById(R.id.linear_acc_y_output);
				Zaxis = (TextView) showLinearAccResultsDialog.findViewById(R.id.linear_acc_z_output);
				
				// Registering for the sensor for getting his results
				sensorManager.registerListener(linearAccListener, linearAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
				
				// ClickListener to stop listening the sensor
				Button doneButton = (Button) showLinearAccResultsDialog.findViewById(R.id.linear_acc_done);
				doneButton.setOnClickListener(new View.OnClickListener()
				{	
					@Override
					public void onClick(View v)
					{
						// Stop listening to the sensor
						sensorManager.unregisterListener(linearAccListener);
						showLinearAccResultsDialog.cancel();
					}
				});
			}
		});
		
		// ClickListener to get and present the Azimuth sensor data
		azimuthButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showAzimuthDialog = new Dialog(thisActivity);
				showAzimuthDialog.setContentView(R.layout.azimuth_dialog);
				showAzimuthDialog.setTitle("Azimuth Results:");
				
				showAzimuthDialog.show();
				
				// Getting the text views in which the linear acceleration results will be printed
				Xaxis = (TextView) showAzimuthDialog.findViewById(R.id.azimuth_x_output);
				Yaxis = (TextView) showAzimuthDialog.findViewById(R.id.azimuth_y_output);
				Zaxis = (TextView) showAzimuthDialog.findViewById(R.id.azimuth_z_output);
				
				// Registering for the sensor for getting his results
				sensorManager.registerListener(azimuthListener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
				
				// ClickListener to stop listening the sensor
				Button doneButton = (Button) showAzimuthDialog.findViewById(R.id.azimuth_done);
				doneButton.setOnClickListener(new View.OnClickListener()
				{	
					@Override
					public void onClick(View v)
					{
						// Stop listening to the sensor
						sensorManager.unregisterListener(azimuthListener);
						showAzimuthDialog.cancel();
					}
				});
			}
		});
		
		// ClickListener to get and present the Light sensor data
		lightButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showLightDialog = new Dialog(thisActivity);
				showLightDialog.setContentView(R.layout.light_dialog);
				showLightDialog.setTitle("Light Results:");
				
				showLightDialog.show();
				
				// Getting the text views in which the linear acceleration results will be printed
				Xaxis = (TextView) showLightDialog.findViewById(R.id.light_output);
				
				// Registering for the sensor for getting his results
				sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
				
				// ClickListener to stop listening the sensor
				Button doneButton = (Button) showLightDialog.findViewById(R.id.light_done);
				doneButton.setOnClickListener(new View.OnClickListener()
				{	
					@Override
					public void onClick(View v)
					{
						// Stop listening to the sensor
						sensorManager.unregisterListener(lightListener);
						showLightDialog.cancel();
					}
				});		
			}
		});
		
		// ClickListener to get and present the Pressure sensor data
		pressureButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Checking if the pressure sensor is available on the device
				if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) == null)
				{
					Toast.makeText(context, "Pressure sensor is not available on your device.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				showPressureDiaolg = new Dialog(thisActivity);
				showPressureDiaolg.setContentView(R.layout.pressure_dialog);
				showPressureDiaolg.setTitle("Pressure Results:");
				
				showPressureDiaolg.show();
				
				// Getting the text views in which the linear acceleration results will be printed
				Xaxis = (TextView) showPressureDiaolg.findViewById(R.id.pressure_output);
				
				// Registering for the sensor for getting his results
				sensorManager.registerListener(pressureListener, pressureSensor, SensorManager.SENSOR_DELAY_FASTEST);
				
				// ClickListener to stop listening the sensor
				Button doneButton = (Button) showPressureDiaolg.findViewById(R.id.pressure_done);
				doneButton.setOnClickListener(new View.OnClickListener()
				{	
					@Override
					public void onClick(View v)
					{
						// Stop listening to the sensor
						sensorManager.unregisterListener(pressureListener);
						showPressureDiaolg.cancel();
					}
				});	
			}
		});	
	}
	
	// handle result
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		String result;
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanResult != null)
		{
			result = scanResult.getContents();
			if (result == null)
				return;
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
		}
	}
	
	// SensorEventListener for the Accelerometer sensor
	SensorEventListener linearAccListener = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			float[] values = event.values;
			
			Xaxis.setText(String.valueOf(values[0]));
			Yaxis.setText(String.valueOf(values[1]));
			Zaxis.setText(String.valueOf(values[2]));
		}
	};
	
	// SensorEventListener for the azimuth sensor (MagneticField)
	SensorEventListener azimuthListener = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			float[] values = event.values;
			
			Xaxis.setText(String.valueOf(values[0]));
			Yaxis.setText(String.valueOf(values[1]));
			Zaxis.setText(String.valueOf(values[2]));
		}
	};

	// SensorEventListener for the light sensor
	SensorEventListener lightListener = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			float[] values = event.values;
			
			Xaxis.setText(String.valueOf(values[0]));
		}
	};

	// SensorEventListener for the pressure sensor
	SensorEventListener pressureListener = new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			float[] values = event.values;
			
			Xaxis.setText(String.valueOf(values[0]));
		}
	};
}
