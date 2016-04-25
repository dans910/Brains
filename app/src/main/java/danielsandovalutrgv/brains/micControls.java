package danielsandovalutrgv.brains;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class micControls extends AppCompatActivity {

    private Context context = this;
    private MediaPlayer playMedia;
    private ArrayList<String> files;
    private ArrayAdapter<String> adapter;
    private ListView audioList;
    private boolean playedmedia = false;
    final private int REQUEST_MIC=1;
    private boolean requsetmic;         //true if permission granted
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_controls);


        final String br = "Brains";
        final File audioDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Audio"+ "/" );
        if(!audioDirectory.exists())
        {
            audioDirectory.mkdirs();
        }
        final MediaRecorder recorder = new MediaRecorder();

        final TextView status = (TextView) findViewById(R.id.recordStatus);
        final Drawable ob = status.getBackground();

        final ImageButton start_record = (ImageButton) findViewById(R.id.startRecord);
        start_record.setTag("rec");


        TextView a = new TextView(this);
        a.setText("Audio Files");
        audioList = (ListView) findViewById(R.id.audioList);
        assert audioList != null;
        audioList.addHeaderView(a);

        files = getFiles(audioDirectory.toString());

        if(files==null) {
            files = new ArrayList<>();
            files.add("No Audio Files...");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        audioList.setAdapter(adapter);


        assert start_record != null;
        start_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowR(v);
                try {
                    if (ContextCompat.checkSelfPermission(micControls.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(micControls.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MIC);
                        if(!requsetmic){
                            return;
                        }
                    }
                    if(start_record.getTag().toString().equals("rec")){
                        int numfiles;
                        final String audio_path;//
                        File audiofiles[] = audioDirectory.listFiles();
                        if(audioDirectory.listFiles()!=null)
                        {
                            numfiles = Array.getLength(audiofiles);
                            audio_path =  audioDirectory.getAbsolutePath() + "/" + "audio" + (numfiles+1)+ ".mp4";
                        }else
                        {
                            audio_path =  audioDirectory.getAbsolutePath() + "/" + "audio1.mp4";
                        }
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(audio_path);
                        start_record.setImageResource(R.drawable.ic_stop);
                        start_record.setTag("stop");
                        start_record.setColorFilter(Color.RED);
                        recorder.prepare();
                        recorder.start();

                    }
                    else if(start_record.getTag().toString().equals("stop")){
                        recorder.stop();
                        status.setText(R.string.press_mic_button_to_start_recording);
                        status.setBackground(ob);

                        files = getFiles(audioDirectory.toString());
                        adapter.clear();
                        adapter.addAll(files);
                        adapter.notifyDataSetChanged();

                        start_record.setImageResource(R.drawable.ic_mic);
                        start_record.setTag("rec");
                        start_record.clearColorFilter();
                        recorder.reset();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



        audioList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String deleteFile = (String) audioList.getItemAtPosition(position);
                AlertDialog askdelete = new AlertDialog.Builder(context).create();
                askdelete.setTitle("Delete Audio");
                askdelete.setMessage("Do You Want To Delete " +deleteFile);

                askdelete.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File file = new File(audioDirectory + "/" + deleteFile);
                        if(file.exists()){
                            file.delete();
                        }

                        files = getFiles(audioDirectory.toString());
                        if(files==null) {
                            files = new ArrayList<>();
                            files.add("No Audio Files...");
                        }
                        adapter.clear();
                        adapter.addAll(files);
                        adapter.notifyDataSetChanged();


                    }
                });
                askdelete.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                askdelete.show();
                return false;
            }
        });

        audioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String deleteFile = (String) audioList.getItemAtPosition(position);
                String file = audioDirectory + "/" + deleteFile;

                playAudio(deleteFile,file);
            }
        });


    }

    public  void nowR(View view)
    {
        TextView status = (TextView) findViewById(R.id.recordStatus);
        status.setText("Recording... Press Button Stop Recording");
        status.setBackgroundColor(Color.RED);
    }

    public void playAudio(String aD, String f){
        if(!aD.equals("No Audio Files...")){
            playMedia = MediaPlayer.create(this, Uri.parse(f));
            playedmedia = true;
            playMedia.setScreenOnWhilePlaying(true);
            playMedia.setLooping(false);
            playMedia.start();
        }

    }

    public ArrayList<String> getFiles(String path){

        ArrayList files = new ArrayList<String>();
        File f = new File(path);
        f.mkdirs();
        File[] file = f.listFiles();
        if(file.length == 0){
            return null;
        }
        else{
            for(int i=0; i<file.length; i++){
                files.add(file[i].getName());
            }
        }

        return files;
    }

    @Override
    protected void onDestroy() {
        if(playedmedia){
            playMedia.release();
        }

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_MIC:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog perm = new AlertDialog.Builder(micControls.this).create();
                    perm.setTitle("Record Audio Permission Denied");
                    perm.setMessage("Need permission to record and store new audio files.");
                    perm.show();
                    requsetmic = false;
                }
                else{
                    requsetmic = true;
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}





