package il.ac.shenkar.mapmarker;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class Settinges extends PreferenceFragment
{
	private ListPreference listPreference;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settinges);
		
		listPreference = (ListPreference) findPreference("update_interval");
		listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				AutoUpdate.setInterval(Long.parseLong((String) newValue));
				String minutes = String.valueOf(Long.parseLong((String) newValue)/60000);
				Toast.makeText(getActivity(), "Markers will new update every " + minutes + " Minutes.", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}
}
