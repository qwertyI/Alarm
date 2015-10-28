package com.alarm.qwerty.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alarm.qwerty.Adapter.MusicAdapter;
import com.alarm.qwerty.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends Activity {

    private static final String MUSIC_FILE_NAME = "music.txt";
    private static final String MUSIC_FILE_PATH = "musicPath.txt";
    private static final int LOAD_MUSIC = 1;

    private List<MusicName> Musics = new ArrayList<>();
    private List<MusicPath> Music_path = new ArrayList<>();
    private SharedPreferences.Editor editor;
    private File file;

    private ListView music_lv;

    private Handler handler = new Handler() {
        public void handleMessage(Message message){
            switch (message.what){
                case LOAD_MUSIC:
                    getName(file);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music);
        editor = getSharedPreferences("Alarm_Music", MODE_PRIVATE).edit();
        file = Environment.getExternalStorageDirectory();
        if (fileIsExists(MUSIC_FILE_NAME) && fileIsExists(MUSIC_FILE_PATH)){
            try {
                WriteToMusics();
                WriteToMusicPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Message message = new Message();
//                    message.what = LOAD_MUSIC;
//                    handler.sendMessage(message);
//                }
//            }).start();
            getName(file);
            for(MusicName musicName: Musics){
                Save(MUSIC_FILE_NAME, musicName.getMusicName());
            }
            for (MusicPath music_path : Music_path){
                Save(MUSIC_FILE_PATH, music_path.getMusicPath());
            }
        }
        MusicAdapter musicAdapter = new MusicAdapter(this, R.layout.music_lv_item, Musics);
        music_lv = (ListView) findViewById(R.id.music_lv);
        music_lv.setAdapter(musicAdapter);
        music_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("name", Musics.get(position).getMusicName());
                editor.putString("path", Music_path.get(position).getMusicPath());
                editor.commit();
//                发送一个广播，将音乐路径传递过去，播放音乐
                Intent intent = new Intent("com.alarm.start");
                intent.putExtra("music", Musics.get(position).getMusicName());
                intent.putExtra("path", Music_path.get(position).getMusicPath());
//                sendBroadcast(intent);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

/*
* by.qwerty
* 使用递归的方法遍历SD卡中的所有文件夹和文件
* 如果是文件夹，则将文件夹中的文件取出后，进一步判断
* 如果是文件则判断是否以.mp3结尾
***********************************************************************************s
* 使用中碰到的问题：File[] files = file.listFiles();这一步需要有相应的权限才可以，
* 比如我的代码中读SD卡，没有权限，则files一直为null，每次都闪退
* */
    public void getName(File file){
        File[] files = file.listFiles();
        if(files.length > 0){
            for(int i = 0; i < files.length; ++i){
                if (files[i].isDirectory()){
                    getName(files[i]);
                }else if (files[i].isFile()){
                    if (files[i].getName().endsWith(".mp3")){
                        Musics.add(new MusicName(files[i].getName()));
                        Music_path.add(new MusicPath(files[i].getPath()));
                    }
                }
            }
        }else {
            return;
        }
    }

    public class MusicName{
        private String name;

        public MusicName(String name){
            this.name = name;
        }
        public String getMusicName(){
            return name;
        }
    }

    public class MusicPath{
        private String path;

        public MusicPath(String path){
            this.path = path;
        }
        public String getMusicPath(){
            return path;
        }
    }

    /*
    * by.qwerty
    * 向文件中写入数据
    * */
    public void Save(String MusicName, String nameData){
        FileOutputStream outName = null;
        FileOutputStream outPath = null;
        BufferedWriter writer = null;
        try{
            outName = openFileOutput(MusicName, Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(outName));
            writer.write(nameData);
            writer.write("\r\n");
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
/*
* by.qwerty
* 判断文件是否存在
*
* 遇到的问题，使用file1 == MUSIC_FILE_NAME时，每次都报不相等，使用equals后才正常
* */
    public boolean fileIsExists(String filename){
        try{
            File file = this.getFilesDir();
            String[] files = file.list();
            for (String file1:files){
                if (file1.equals(filename)){
                    return true;
                }
            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }
/*
* by.qwerty
* 取出文件中的数据，逐行读出并放入Musics中，这样读起来速度非常快，比递归快很多倍
*
* 碰到的问题：中文乱码问题。之前的方法是使用DataInputStream方法读出数据，不能更正编码。需要注意
* */
    public void WriteToMusics() throws IOException{
        FileInputStream fileInputStream = openFileInput(MUSIC_FILE_NAME);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
//        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            MusicName musicName = new MusicName(line);
            Musics.add(musicName);
        }
        bufferedReader.close();
        fileInputStream.close();
    }

    public void WriteToMusicPath() throws IOException{
        FileInputStream fileInputStream = openFileInput(MUSIC_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
//        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            MusicPath musicPath = new MusicPath(line);
            Music_path.add(musicPath);
        }
        bufferedReader.close();
        fileInputStream.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music, menu);
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
