package com.alarm.qwerty.LoadTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.alarm.qwerty.Activity.MusicActivity.MusicName;
import com.alarm.qwerty.Activity.MusicActivity.MusicPath;
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

/**
 * Created by wei on 2015/11/25.
 */
public class LoadAsyncTask extends AsyncTask<Void, String, Boolean> {

    private static final String MUSIC_FILE_NAME = "music.txt";
    private static final String MUSIC_FILE_PATH = "musicPath.txt";
    private static final String TAG = "ASYNC_TASK";
    private static final int WAIT_MUSIC_TIME = 300;

    private ProgressDialog progressDialog;
    private ListView music_lv;
    private Context mContext;
    private File file;
    private List<MusicName> Musics = new ArrayList<>();
    private List<MusicPath> Music_Path = new ArrayList<>();
    private MusicAdapter mAdapter;
    private MusicName musicName;
    private MusicPath musicPath;

    private int i = 0;

    public LoadAsyncTask(Context context, ListView listView, MusicAdapter musicAdapter,
                         List<MusicName> Musics, MusicName musicName,
                         List<MusicPath> Music_path, MusicPath musicPath){
        progressDialog = new ProgressDialog(context, 0);
        mContext = context;
        this.Musics = Musics;
        this.musicName = musicName;
        this.Music_Path = Music_path;
        this.musicPath = musicPath;
        mAdapter = musicAdapter;
        music_lv = listView;
        mAdapter = new MusicAdapter(mContext, R.layout.music_lv_item, this.Musics);
        music_lv.setAdapter(mAdapter);
        file = Environment.getExternalStorageDirectory();
    }

    public int doDownload(){
        return i++;
    }

    //onPreExecute方法用于在执行后台任务前做一些UI操作
    @Override
    protected void onPreExecute() {
        Log.i(TAG, "" + mContext);
        progressDialog.show();
    }

    //doInBackground方法内部执行后台任务,不可在此方法内修改UI
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.i(TAG, "doInBackground(Params... params) called");
        try {
            if (fileIsExists(MUSIC_FILE_NAME) && fileIsExists(MUSIC_FILE_PATH)){
                WriteToMusics();
                WriteToMusicPath();
                for (MusicName musicName : Musics){
                    publishProgress(musicName.getMusicName());
                }
            }else {
                getName(file);
                for(MusicName musicName: Musics){
                    Save(MUSIC_FILE_NAME, musicName.getMusicName());
                }
                for (MusicPath music_path : Music_Path){
                    Save(MUSIC_FILE_PATH, music_path.getMusicPath());
                }
            }
//            while (true){
//                int downloadPercent = doDownload();
//                Thread.sleep(1000);
//                publishProgress(downloadPercent);
//                if (downloadPercent >= 100){
//                    break;
//                }
//            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    //onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(String ... progresses) {
        for (String progress : progresses){
            progressDialog.setMessage(progress + "");
        }
        mAdapter.notifyDataSetChanged();
    }

    //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    @Override
    protected void onPostExecute(Boolean result) {
        Log.i(TAG, "onPostExecute(Result result) called");
        progressDialog.dismiss();

        if (result){
            Toast.makeText(mContext, "Check Success", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "Check failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void getName(File file){
        File[] files = file.listFiles();
        if(files.length > 0){
            for(int i = 0; i < files.length; ++i){
                if (files[i].isDirectory()){
                    getName(files[i]);
                }else if (files[i].isFile()){
                    try{
                        if (files[i].getName().endsWith(".mp3")){
                            Thread.sleep(WAIT_MUSIC_TIME);
                            publishProgress(files[i].getName());
                            musicName = new MusicName(files[i].getName());
                            Musics.add(musicName);
                            musicPath = new MusicPath(files[i].getPath());
                            Music_Path.add(musicPath);
                        }
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
//                        Musics.add(new MusicName(files[i].getName()));
//                        Music_path.add(new MusicPath(files[i].getPath()));
                }
            }
        }else {
            return;
        }
    }

    public void Save(String MusicName, String nameData){
        FileOutputStream outName = null;
        FileOutputStream outPath = null;
        BufferedWriter writer = null;
        try{
            outName = mContext.getApplicationContext().openFileOutput(MusicName, mContext.MODE_APPEND);
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

    public boolean fileIsExists(String filename){
        try{
            File file = mContext.getFilesDir();
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

    public void WriteToMusics() throws IOException{
        FileInputStream fileInputStream = mContext.getApplicationContext().openFileInput(MUSIC_FILE_NAME);
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
        FileInputStream fileInputStream = mContext.getApplicationContext().openFileInput(MUSIC_FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
//        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            MusicPath musicPath = new MusicPath(line);
            Music_Path.add(musicPath);
        }
        bufferedReader.close();
        fileInputStream.close();
    }

//    //onCancelled方法用于在取消执行中的任务时更改UI
//    @Override
//    protected void onCancelled() {
//        Log.i(TAG, "onCancelled() called");
//        textView.setText("cancelled");
//        progressBar.setProgress(0);
//
//        execute.setEnabled(true);
//        cancel.setEnabled(false);
//    }
}
