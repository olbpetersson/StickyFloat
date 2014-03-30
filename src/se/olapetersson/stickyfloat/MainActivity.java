package se.olapetersson.stickyfloat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;


public class MainActivity extends PreferenceActivity {
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String sharedText = null;

        Intent startIntent = getIntent();
      
        if(startIntent != null && startIntent.getStringExtra(Intent.EXTRA_TEXT) != null){
                sharedText = startIntent.getStringExtra(Intent.EXTRA_TEXT);
       
            c = this;
            Intent serviceIntent = new Intent(c, FloatReminderService.class);
            serviceIntent.putExtra(getString(R.string.string_extra), sharedText.replace("Shared from Google Keep", "").trim()); 
            startService(serviceIntent);
 
            finish();
        }
        else{
            Toast.makeText(this, getString(R.string.toast_message_main_activity), Toast.LENGTH_LONG).show();
            finish();
        }
       
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
 
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

}
