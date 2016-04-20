package danielsandovalutrgv.brains;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

public class micControls extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_controls);

        final String br = "Brains";
        File audioDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Audio"+ "/" );
        if(!audioDirectory.exists())
        {
            audioDirectory.mkdirs();
        }
        int numfiles;
        final String audio_path;//
        File audiofiles[] = audioDirectory.listFiles();
        if(audioDirectory.listFiles()!=null)
        {
            numfiles = Array.getLength(audiofiles);
            audio_path =  audioDirectory.getAbsolutePath() + "/" + "audio" + (numfiles+1)+ ".3gp";
        }else
        {
            audio_path =  audioDirectory.getAbsolutePath() + "/" + "audio1.3gp";
        }


        final MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audio_path);

        final TextView status = (TextView) findViewById(R.id.recordStatus);
        final Drawable ob = status.getBackground();

        final ImageButton start_record = (ImageButton) findViewById(R.id.startRecord);
        final ImageButton stop_record = (ImageButton) findViewById(R.id.stopRecord);
        start_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowR(v);
                try {
                    stop_record.setColorFilter(Color.RED);
                    recorder.prepare();
                    recorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        assert stop_record != null;
        stop_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.release();
                status.setText(R.string.press_mic_button_to_start_recording);
                status.setBackground(ob);
                finish();
            }
        });
    }
    public  void nowR(View view)
    {
        TextView status = (TextView) findViewById(R.id.recordStatus);
        status.setText("Recording... Press Stop TO Stop Recording");
        status.setBackgroundColor(Color.RED);
    }

}




