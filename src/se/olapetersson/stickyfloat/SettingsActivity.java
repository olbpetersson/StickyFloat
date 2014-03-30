package se.olapetersson.stickyfloat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity{
Context c;
SharedPreferences sharedPreferences;
EditTextPreference noteEditText;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle("Settings");
		
		c = this;
		sharedPreferences = getSharedPreferences("se.olapetersson.stickyfloat", 0);//PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.settings_ui);
		noteEditText = (EditTextPreference) findPreference(c.getString(R.string.settings_add_note_key));
		noteEditText.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				String noteText = (String) newValue;//sharedPreferences.getString(c.getResources().getString(R.string.settings_add_note_key), "Note1");
				noteEditText.setTitle(noteText);
				noteEditText.setText(noteText);
				sharedPreferences.edit().putString(c.getResources().getString(R.string.settings_add_note_key), noteEditText.getText()).apply();
				Intent i = new Intent(c, MainActivity.class);
				i.putExtra(Intent.EXTRA_TEXT, noteText);
				startActivity(i);
				Log.d("Changed1", "changed");
				return false;
			}
		});
//		sharedPreferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
//			
//			@Override
//			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//					String key) {
//				if (key.equals(c.getResources().getString(R.string.settings_add_note_key))){
//					String noteText = sharedPreferences.getString(c.getResources().getString(R.string.settings_add_note_key), "Note2");
//					Intent i = new Intent(c, MainActivity.class);
//					noteEditText.setTitle(sharedPreferences.getString(c.getResources().getString(R.string.settings_add_note_key), "Note3"));
//					sharedPreferences.edit().putString(c.getResources().getString(R.string.settings_add_note_key), noteEditText.getText()).apply();
//					i.putExtra(Intent.EXTRA_TEXT, noteText);
//					startActivity(i);
//					Log.d("Changed2", "changed2");
//				}
//			}
//		});
		
	}

//	@Override
//	public void onResume(){
//		super.onResume();
//		
//		noteEditText.setTitle(sharedPreferences.getString(c.getResources().getString(R.string.settings_add_note_key), "Note"));
//	
//	}
}
