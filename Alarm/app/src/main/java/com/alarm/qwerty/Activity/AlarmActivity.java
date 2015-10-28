package com.alarm.qwerty.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import com.alarm.qwerty.R;
import com.alarm.qwerty.Activity.MusicActivity;
import java.util.Calendar;

public class AlarmActivity extends Activity implements OnClickListener{

    private final String ALARM_RECODE = "alarm";
    private final String ALARM_MUSIC = "Alarm_Music";
    private final String ALARM_ACTION = "com.alarm.start";
    private final int ALARM_TIME_UPDATE = 1;

    private ImageButton select_music;
    private TextView alarm_time;
    private EditText music_name_et;
    private Switch mSwitch;

    private static SharedPreferences music_gpf;
    private SharedPreferences alarm_time_gpf;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePickerDialog tpd = null;
    private Calendar calendar;
    private SharedPreferences.Editor editor;

    private int hours;
    private int minutes;
    private String sHours;
    private String sMinutes;

    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case ALARM_TIME_UPDATE:
                    alarm_time.setText(sHours+":"+sMinutes);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alarm);

        editor = getSharedPreferences(ALARM_RECODE, MODE_PRIVATE).edit();
        alarm_time_gpf = getSharedPreferences(ALARM_RECODE, MODE_PRIVATE);

        music_gpf = getSharedPreferences(ALARM_MUSIC, MODE_PRIVATE);
        select_music = (ImageButton) findViewById(R.id.select_music_btn);
        alarm_time = (TextView) findViewById(R.id.alarm_time_tv);
        music_name_et = (EditText) findViewById(R.id.music_name_et);
        mSwitch = (Switch) findViewById(R.id.switch_alarm_sth);
        mSwitch.setChecked(true);
//判断是否已经选择过音乐，如果选择过了就显示已选择的音乐，如果没有则显示hint
        String name = music_gpf.getString("name", "");
        String alarm_time_gpfString = alarm_time_gpf.getString("time", "");
        if (!name.equals("")){
            music_name_et.setText(name);
        }
        if (!alarm_time_gpfString.equals("")){
            alarm_time.setText(alarm_time_gpfString);
        }
        mSwitch.setChecked(alarm_time_gpf.getBoolean("isChecked", false));
        alarm_time.setOnClickListener(this);
        select_music.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!alarm_time_gpf.getString("time", "").equals("")){
                        AlarmSend(hours, minutes, ALARM_ACTION);
                    }
                    editor.putBoolean("isChecked", true);
                }else {
                    if (alarmManager == null && pendingIntent == null){
                        editor.putBoolean("isChecked", false);
                    }else {
                        alarmManager.cancel(pendingIntent);
                    }
                }
                editor.commit();
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.select_music_btn:
                Intent select_music_intent = new Intent(AlarmActivity.this, MusicActivity.class);
                startActivityForResult(select_music_intent, 1);
                break;
            case R.id.alarm_time_tv:
                calendar = Calendar.getInstance();
                tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hours = hourOfDay;
                        minutes = minute;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = ALARM_TIME_UPDATE;
                                handler.sendMessage(message);
                            }
                        }).start();
                        if (mSwitch.isChecked()){
                            AlarmSend(hourOfDay, minute, ALARM_ACTION);
                            editor.putBoolean("isChecked", true);
                        }else {
                            editor.putBoolean("isChecked", false);
                        }
                        if (hours < 10){
                            sHours = "0" + hours;
                        }else {
                            sHours = hours + "";
                        }
                        if (minutes < 10){
                            sMinutes = "0" + minutes;
                        }else {
                            sMinutes = "" + minutes;
                        }
                        editor.putString("time",sHours+":"+sMinutes);
                        editor.commit();
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                tpd.show();
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int RequestCode, int ResultCode, Intent data){
        if (data == null){
            music_name_et.setText(music_gpf.getString("name", ""));
        }else {
            music_name_et.setText(data.getStringExtra("music"));
        }
    }

    public static SharedPreferences getMusic_gpf(){
        return music_gpf;
    }

    public void AlarmSend(int hour, int minute, String Action){
        Intent intent = new Intent();
        intent.setAction(Action);
        intent.putExtra("msg", "Time to wake up!");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public class Alarm{
        private String aTime;

        public Alarm(String Time){
            this.aTime = Time;
        }

        public String getTime(){
            return aTime;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
