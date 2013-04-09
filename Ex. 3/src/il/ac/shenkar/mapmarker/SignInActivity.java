package il.ac.shenkar.mapmarker;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends Activity
{
	private EditText usernameField;
	private EditText passwordField;
	private EditText emailField;
	
	private Button signin;
	private Button signup;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Initializing the parse
		Parse.initialize(this, "7NTkZQ7iCulhrCgT6h6Iux8q0qnr8L1oJDE2yByJ", "aAwkOZKPGi8AUtVn9VSAeat0eQMKJD0pXiGNTuHe");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null)
		{
			 Intent intent = new Intent(getApplicationContext(),MainActivity.class);
			 startActivity(intent);
			 finish();
		}
		
		signin = (Button) findViewById(R.id.signin_btn);
		signup = (Button) findViewById(R.id.signup_btn);
		
		signin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				usernameField = (EditText) findViewById(R.id.username_login_field);
				passwordField = (EditText) findViewById(R.id.password_login_field);
				
				progressDialog = ProgressDialog.show(SignInActivity.this, "Loging In", "Loading...", false);
				
				ParseUser.logInInBackground(usernameField.getText().toString(), passwordField.getText().toString(), new LogInCallback()
				{
					@Override
					public void done(ParseUser user, ParseException e)
					{
						 if (user != null)
						 {
							 progressDialog.dismiss();
							 Intent intent = new Intent(getApplicationContext(),MainActivity.class);
							 startActivity(intent);
							 finish();
						 }
						 else
						 {
							 progressDialog.dismiss();
							 Toast.makeText(getApplicationContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
						 }
					}
				});
			}
		});
		
		signup.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				usernameField = (EditText) findViewById(R.id.username_signup_field);
				passwordField = (EditText) findViewById(R.id.password_signup_field);
				emailField = (EditText) findViewById(R.id.email_signup_field);
				
				progressDialog = ProgressDialog.show(SignInActivity.this, "Signing Up", "Loading...", false);
				
				if (usernameField.getText().toString().equals("") || passwordField.getText().toString().equals("") || emailField.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "You must fill all fields to signup.", Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					return;
				}
				
				ParseUser user = new ParseUser();
				user.setUsername(usernameField.getText().toString());
				user.setPassword(passwordField.getText().toString());
				user.setEmail(emailField.getText().toString());
				
				user.signUpInBackground(new SignUpCallback()
				{
					@Override
					public void done(ParseException e)
					{
						if (e == null)
						{
							progressDialog.dismiss();
							Intent intent = new Intent(getApplicationContext(),MainActivity.class);
							startActivity(intent);
							finish();
						}							
						else
						{
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "Signup Failed.", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}
}
