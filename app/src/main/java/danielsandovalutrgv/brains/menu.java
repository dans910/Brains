package danielsandovalutrgv.brains;

import android.annotation.TargetApi;
import android.media.projection.MediaProjection;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.DragEvent;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

public class menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context = this;
    private DrawerLayout drawer;
    private boolean recording;
    final private MediaRecorder videoRecorder = new MediaRecorder();
    final private int REQUEST_STORAGE_VIDEO = 2;
    final private int REQUEST_STORAGE_AUDIO = 1;
    private boolean continueRecord;
    private boolean recgranted, vidgranted;
    private View rec;
    private RelativeLayout editLayout;
    private int xd, yd;
    private ViewGroup el;
    private String br = "Brains";
    final private File videoDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Video"+ "/" );
    private MediaProjection mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab1.hide();
        fab2.hide();
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        final FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab3.hide();
        fab3.setTag("@string/vidle");
        fab4.hide();
        assert fab!=null;
        final EditText newText  = (EditText) findViewById(R.id.editText);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fab1.isShown()){
                    fab1.show();
                    fab2.show();
                    fab3.show();
                    fab4.show();
                    fab.setImageResource(R.drawable.ic_compact);
                }
                else{
                    fab1.hide();
                    fab2.hide();
                    fab3.hide();
                    fab4.hide();
                    fab.setImageResource(R.drawable.ic_add);
                }
            }
        });
        /**
         * Clear Button
         */
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fab1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Clear Screen", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        /**
         * Record Audio Button
         */
        fab2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                rec = v;
                if(ActivityCompat.checkSelfPermission(menu.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(menu.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_AUDIO);

                    return;
                }
                recordAudio(v);

                fab.callOnClick();
            }
        });
        fab2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Record Audio", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        /**
         * Record Screen Button
         */
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if We have permission to write to external storage

                if(checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(menu.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_VIDEO);
                    if(!continueRecord){
                        return;
                    }
                }
                if (fab3.getTag().equals("@string/vidle")) {
                    initiateScreenCapture();
                    fab3.setImageResource(R.drawable.ic_play);
                    fab3.setTag("@string/vRecord");
                    Toast.makeText(context, "Press Play to Record Screen", Toast.LENGTH_SHORT).show();
                } else if (fab3.getTag().equals("@string/vRecord")) {
                    fab3.setImageResource(R.drawable.ic_stop);
                    fab3.setColorFilter(Color.RED);
                    fab3.setTag("@string/vStop");
                    try {
                        videoRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    videoRecorder.start();
                    recording = true;
                    Toast.makeText(context, "Press Stop to stop Recording Screen", Toast.LENGTH_SHORT).show();
                } else if (fab3.getTag().equals("@string/vStop")) {
                    fab3.setImageResource(R.drawable.ic_gallery);
                    fab3.setTag("@string/vidle");
                    fab3.clearColorFilter();
                    if(recording){
                        videoRecorder.stop();
                        videoRecorder.reset();
                    }
                }
            }
        });
        fab3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Record Screen", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        /**
         * Add Comment Button
         */
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newText.setVisibility(View.VISIBLE);
                fab.callOnClick();
            }
        });
        fab4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Add Comment", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        el = (ViewGroup) findViewById(R.id.editroot);
        newText.setText("type here");
        newText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();
                if(event.getPointerCount()>1){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            RelativeLayout.LayoutParams plp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                            xd = plp.leftMargin;
                            yd = plp.topMargin;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) v.getLayoutParams();
                            p.leftMargin = x - xd;
                            p.topMargin = y - yd;
                            p.bottomMargin = y;
                            p.rightMargin = x;

                            v.setLayoutParams(p);
                            break;
                        default:
                            break;
                    }
                }
                else{
                    v.requestFocus();
                }
                el.invalidate();
                return true;
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * This method is used to open the micControls activity.
     * This method creates an intent and will be called when a button is pressed.
     * @param view
     */
    public void recordAudio(View view){

        Intent intent = new Intent(context, micControls.class);
        startActivity(intent);
    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            EditText ed = (EditText) findViewById(R.id.editText);
            if(ed.hasFocus()){
                ed.clearFocus();
            }
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_STORAGE_AUDIO:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog rationale=new AlertDialog.Builder(context).create();
                    rationale.setTitle("Storage Permission Denied");
                    rationale.setMessage("Need permission to access and store audio files.");
                    rationale.show();
                }
                else{
                    recordAudio(rec);
                }
                break;
            case REQUEST_STORAGE_VIDEO:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog ration = new AlertDialog.Builder(context).create();
                    ration.setTitle("Storage Permission Denied");
                    ration.setMessage("Need permission to store video file.");
                    ration.show();
                }
                else{
                    continueRecord = true;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.privateC) {
            intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.publicC) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initiateScreenCapture(){
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        videoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        videoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

        videoDirectory.mkdirs();
        String video_path = videoDirectory.getAbsolutePath() + "/" + "video.mp4";

        videoRecorder.setOutputFile(video_path);
        videoRecorder.setMaxDuration(60000);
        videoRecorder.setMaxFileSize(10000000);

    }

}
