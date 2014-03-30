package se.olapetersson.stickyfloat;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Ola on 2013-06-23.
 */
public class FloatReminderService extends Service {
    private WindowManager wManager;
    private TextView stickyTextView, cancelTextView;
    private WindowManager.LayoutParams stickyParams, cancelParams;
    private String noteText;
    private Display mDisplay;
    private Context c;
//    private Vibrator mVibrator; 

 //   WakeLock mWakeLock;
    private final String colorMask = "#5A000000";
    private final int SF_NOTI_ID = 1337;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        noteText = intent.getStringExtra(getString(R.string.string_extra));
        stickyTextView.setText(noteText);       
        
        Notification noti = new Notification.Builder(this)
        .setContentTitle("Sticky Float")
        .setContentText(noteText)
        .setSmallIcon(R.drawable.stickyfloatlogo).getNotification();
        
        startForeground(SF_NOTI_ID, noti);
        
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        c = this;
        
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themeChoice = sharedPreferences.getString(c.getResources().getString(R.string.settings_theme_list_key), "1");
		String textSizeChoice = sharedPreferences.getString(c.getResources().getString(R.string.settings_text_size_list_key), "14");
		sharedPreferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
				
				@Override
				public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
						String key) {
					String themeChoice = sharedPreferences.getString(c.getResources().getString(R.string.settings_theme_list_key), "1");
					String textSizeChoice = sharedPreferences.getString(c.getResources().getString(R.string.settings_text_size_list_key), "14");
					updateTextViewAttributes(themeChoice, textSizeChoice);
				}
			});
		
        
        PowerManager mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
//        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getString(R.string.wake_lock_label));
//        mWakeLock.acquire();
        wManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = wManager.getDefaultDisplay();
        Point size = new Point();
        mDisplay.getSize(size);      
 //       mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//          c = this.getApplicationContext();
        
        stickyTextView = new TextView(this);
        stickyTextView.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				// TODO Auto-generated method stub
				Log.d("STRING_VISIBILITY", "-> "+visibility);
			}
		});
        updateTextViewAttributes(themeChoice, textSizeChoice);
      
        
        stickyTextView.setTypeface(null, Typeface.ITALIC);
        

        cancelParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        
        cancelParams.x = size.x /2;
        cancelParams.y = -10000;
        
        cancelTextView = new TextView(this);
        cancelTextView.setText("DELETE");
        cancelTextView.setTypeface(null, Typeface.BOLD);
        cancelTextView.setWidth(size.x);
        cancelTextView.setBackgroundColor(Color.parseColor(colorMask));       
        cancelTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        cancelTextView.setTextSize(35);
        cancelTextView.setTextColor(Color.WHITE);
        
        
        stickyParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        stickyParams.gravity = Gravity.TOP | Gravity.LEFT;
        stickyParams.x = 0;
        stickyParams.y = size.y/2;

        stickyTextView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = stickyParams.x;
                        initialY = stickyParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        wManager.addView(cancelTextView, cancelParams);
                        return true;
                    case MotionEvent.ACTION_UP:
                    	if(event.getRawY()-stickyTextView.getHeight()/2 < cancelTextView.getHeight()){
                            stopSelf();
                        }
                    	//cancelTextView.setBackgroundColor(Color.parseColor(colorMask));
                    	wManager.removeView(cancelTextView);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        stickyParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        stickyParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        
                        wManager.updateViewLayout(stickyTextView, stickyParams);
                       
                        if(event.getRawY()-stickyTextView.getHeight()/2 < cancelTextView.getHeight()){
                        	cancelTextView.setBackgroundColor(Color.RED);
            //        		mVibrator.vibrate(5);                        	                       	
                        } else {
                        	cancelTextView.setBackgroundColor(Color.parseColor(colorMask));    
                        }
                        
                        
                        wManager.updateViewLayout(cancelTextView, cancelParams);
                        return true;
                }
                return false;
            }
        });
        wManager.addView(stickyTextView, stickyParams);        

    }
    
  
    public void updateTextViewAttributes(String themeChoice, String textSize){
    	stickyTextView.setTextSize(Integer.parseInt(textSize));
    	Log.d("TEXTSIZE: " , textSize +", ::" +themeChoice);
    	 if(themeChoice.equals("1")){
         	stickyTextView.setBackgroundResource(R.drawable.stickynote9patch);
         	stickyTextView.setTextColor(Color.BLACK);
         	Log.d("FIRST: " , textSize +", ::" +themeChoice);
         }
         else if(themeChoice.equals("2")){
         	 stickyTextView.setBackgroundColor(Color.parseColor("#ff000000"));
         	 stickyTextView.setTextColor(Color.parseColor("#bebebe"));
         	Log.d("SECOND: " , textSize +", ::" +themeChoice);
         }
         else{
         	 stickyTextView.setBackgroundColor(Color.parseColor("#fff3f3f3"));
         	 stickyTextView.setTextColor(Color.parseColor("#323232"));
         	 Log.d("THIRD: " , textSize +", ::" +themeChoice);
         }
    	stickyTextView.invalidate();
    
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stickyTextView != null) wManager.removeView(stickyTextView);
     //   mWakeLock.release();
    }
}
