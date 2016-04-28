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
import android.view.Surface;
import android.view.SurfaceView;
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
    private ArrayList<String> files, filesv;
    private ArrayAdapter<String> adapter, adapterv;
    private ListView audioList, videoList;
    private boolean playedmedia = false;
    final private int REQUEST_MIC=1;
    private boolean requsetmic;         //true if permission granted
    final String br = "Brains";
    final private File audioDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Audio"+ "/" );
    final private File videoDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Video" + "/");;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_controls);

        selectMedia();
    }

    private void selectMedia(){
        TextView a = new TextView(this);
        a.setText("Audio: Press to Play, Long Press to Delete");
        TextView b = new TextView(this);
        b.setText("Video: Press to Play, Long Press to Delete");
        audioList = (ListView) findViewById(R.id.audioList);
        assert audioList != null;
        videoList = (ListView) findViewById(R.id.videoList);
        assert videoList != null;
        audioList.addHeaderView(a);
        videoList.addHeaderView(b);

        files = getFiles(audioDirectory.toString());
        filesv = getFiles(videoDirectory.toString());

        if(files==null) {
            files = new ArrayList<>();
            files.add("No Audio Files...");
        }
        if(filesv==null){
            filesv = new ArrayList<>();
            filesv.add("No Videos...");
        }
        adapterv = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filesv);
        videoList.setAdapter(adapterv);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        audioList.setAdapter(adapter);

        audioList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String deleteFile = (String) audioList.getItemAtPosition(position);
                AlertDialog askdelete = new AlertDialog.Builder(context).create();
                askdelete.setTitle("Delete Audio");
                if(audioList.getItemAtPosition(position).equals("No Audio Files...")){
                    askdelete.setMessage("No Audio files to delete");
                }
                else {
                    askdelete.setMessage("Do You Want To Delete " + deleteFile);


                    askdelete.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File file = new File(audioDirectory + "/" + deleteFile);
                            if (file.exists()) {
                                file.delete();
                            }

                            files = getFiles(audioDirectory.toString());
                            if (files == null) {
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
                }
                askdelete.show();
                return false;
            }
        });
        videoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                final String deleteFilev = (String) videoList.getItemAtPosition(position);
                AlertDialog askdelete = new AlertDialog.Builder(context).create();
                askdelete.setTitle("Delete Video");
                if(videoList.getItemAtPosition(position).equals("No Videos...")){
                    askdelete.setMessage("No Videos to delete");
                }else {
                    askdelete.setMessage("Do You Want To Delete " + deleteFilev);


                    askdelete.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File file = new File(videoDirectory + "/" + deleteFilev);
                            if (file.exists()) {
                                file.delete();
                            }

                            filesv = getFiles(videoDirectory.toString());
                            if (filesv == null) {
                                filesv = new ArrayList<>();
                                filesv.add("No Videos...");
                            }
                            adapterv.clear();
                            adapterv.addAll(filesv);
                            adapterv.notifyDataSetChanged();


                        }
                    });
                    askdelete.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
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

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String deleteFilev = (String) videoList.getItemAtPosition(position);
                String filev = videoDirectory + "/" + deleteFilev;

                playVideo(deleteFilev,filev);
            }
        });
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

    public void playVideo(String aD, String f){
        if(!aD.equals("No Videos...")){
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


}





