package danielsandovalutrgv.brains;

import android.content.Context;
import android.media.MediaTimestamp;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;


public class Add extends AppCompatActivity {

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.exitButton);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton edit = (ImageButton) findViewById(R.id.editButton);
        assert edit != null;
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout editlay = new RelativeLayout(context);
                EditText newText = new EditText(context);
                newText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                newText.setMinHeight(10);
                newText.setMinWidth(10);
                newText.setGravity(Gravity.TOP | Gravity.CENTER);
                newText.setText("Type Here", TextView.BufferType.EDITABLE);
                newText.setVisibility(View.VISIBLE);
                newText.setTextSize(getResources().getDimension(android.support.design.R.dimen.abc_text_size_medium_material));
                editlay.addView(newText);


            }
        });


        /**
         * The MIC buttton will call activity that will allow users to use the microphone and record some audio.
         * They might use it to walk through their thought process on a certain actions.
         */
        ImageButton mic = (ImageButton) findViewById(R.id.micButton);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        ImageButton image = (ImageButton) findViewById(R.id.imageButton);
        assert image != null;
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageButton video = (ImageButton) findViewById(R.id.videoButton);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void sendMessage(View view){

        Intent intent = new Intent(this, micControls.class);
        startActivity(intent);
    }
}
