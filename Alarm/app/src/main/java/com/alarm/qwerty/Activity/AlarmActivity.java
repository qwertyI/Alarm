package com.alarm.qwerty.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alarm.qwerty.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmActivity extends Activity implements OnClickListener{

    private List<Alarm> Alarms = new ArrayList<>();

    private Button select_music;
    private Button create_alarm;
    private TextView music_name;
    private EditText music_name_et;

    public SharedPreferences gpf;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePickerDialog tpd = null;

    private int hours;
    private int minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alarm);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hours = hourOfDay;
                minutes = minute;
                tpd.dismiss();
                AlarmSend(hours, minutes);
            }
        };

        Calendar calendar = Calendar.getInstance();

        gpf = getSharedPreferences("Alarm_Music", MODE_PRIVATE);
        tpd = new TimePickerDialog(this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        select_music = (Button) findViewById(R.id.select_music_btn);
        create_alarm = (Button) findViewById(R.id.create_alarm_btn);
        music_name = (TextView) findViewById(R.id.music_name_tv);
        music_name_et = (EditText) findViewById(R.id.music_name_et);
//判断是否已经选择过音乐，如果选择过了就显示已选择的音乐，如果没有则显示hint
        String name = gpf.getString("name", "");
        Log.i("gpf", name);
        if (!name.equals("")){
            music_name_et.setText(name);
        }



        select_music.setOnClickListener(this);
        create_alarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.select_music_btn:
                Intent select_music_intent = new Intent(AlarmActivity.this, MusicActivity.class);
                startActivityForResult(select_music_intent, 1);
                break;
            case R.id.create_alarm_btn:
                tpd.show();
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int RequestCode, int ResultCode, Intent data){
        if (data == null){
            music_name_et.setText(gpf.getString("name", ""));
        }else {
            music_name_et.setText(data.getStringExtra("music"));
        }
    }

    public void AlarmSend(int hour, int minute){
        Intent intent = new Intent();
        intent.setAction("com.alarm.start");
        intent.putExtra("msg", "Time to wake up!");
        intent.putExtra("path", gpf.getString("path", ""));
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
        private int aHour;
        private int aMinute;
        private boolean isOpen;

        public Alarm(int hour, int minute, boolean isOpen){
            this.aHour = hour;
            this.aMinute = minute;
            this.isOpen = isOpen;
        }

        public int GetHour(){
            return aHour;
        }
        public int GetMinute(){
            return aMinute;
        }
        public boolean GetIsOpen(){
            return isOpen;
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
