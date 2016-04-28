package danielsandovalutrgv.brains;

import android.annotation.TargetApi;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjection.Callback;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.SurfaceView;
import android.widget.ToggleButton;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context = this;
    private DrawerLayout drawer;
    private boolean recording = false;
    private boolean vrecording = false;
    private static final int REQUEST_CODE = 1000;
    private MediaProjectionManager videoPM;
    private MediaRecorder videoRecorder, audioRecorder;
    private int screenDensity;
    private MediaProjection videoP;
    private MediaProjectionCallback videoPC;
    private VirtualDisplay videoVD;
    final private int DISPLAY_WIDTH = 720;
    final private int DISPLAY_HEIGHT = 1280;
    final private int REQUEST_STORAGE_VIDEO = 2;
    final private int REQUEST_RECORD_AUDIO = 3;
    final private int REQUEST_STORAGE_AUDIO = 1;
    final private int REQUEST_VIDEO = 4;
    private boolean continueRecordS;
    private boolean continueRecordA;
    private boolean recgranted, vidgranted;
    private View rec;
    private RelativeLayout editLayout;
    private int xd, yd;
    private ViewGroup el;
    private String br = "Brains";
    final private File videoDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Video"+ "/" );
    final private File audioDirectory = new File(Environment.getExternalStorageDirectory()+ "/" +br, "Audio"+ "/" );
    private MediaProjection mp;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private Surface thesurf;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private Surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        switchOnOff(false);
        buttonDescriptions();
        buttonActions();
        fab3.setTag("@string/vidle");
        fab2.setTag("@string/aIdle");
        fab1.setColorFilter(Color.RED);

        //Screen Capture variables
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        videoRecorder = new MediaRecorder();
        audioRecorder = new MediaRecorder();
        videoPM = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        //For Drawing
        CoordinatorLayout relativeLayout;
        CoordinatorLayout.LayoutParams params;
        relativeLayout = (CoordinatorLayout) findViewById(R.id.root);
        params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        MyTouchEventView touch = new MyTouchEventView(this, fab1);
        touch.setLayoutParams(params);
        relativeLayout.addView(touch);



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

    /**
     * This method toggles the buttons' visibility.
     * @param status
     */
    private void switchOnOff(boolean status){
        if(!status){
            fab1.hide();
            fab2.hide();
            fab3.hide();
            fab4.hide();
        }
        else{
            fab1.show();
            fab2.show();
            fab3.show();
            fab4.show();
        }
    }

    /**
     * This method describes the functionality of the buttons.
     */
    private void buttonDescriptions(){
        fab1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Clear Screen", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fab2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Record Audio", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fab3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Record Screen", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fab4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Add Comment", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /**
     * This method defines the action performed by button clicks.
     */
    private void buttonActions(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fab1.isShown()){
                    switchOnOff(true);
                    fab.setImageResource(R.drawable.ic_compact);
                }
                else{
                    switchOnOff(false);
                    fab.setImageResource(R.drawable.ic_add);
                }
            }
        });
        /*
         * Record Audio Button
         */
        fab2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                    if(vrecording){
                        Toast.makeText(context, "Please stop screen capture before starting to record audio", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] perms = new String[2];
                    if(ContextCompat.checkSelfPermission(menu.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        perms[0] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    }
                    if(ContextCompat.checkSelfPermission(menu.this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        perms[1] = android.Manifest.permission.RECORD_AUDIO;
                    }
                    if(perms[0] != null || perms[1] != null){
                        if(perms[0] == null){
                            ActivityCompat.requestPermissions(menu.this, new String[]{perms[1]}, REQUEST_RECORD_AUDIO);
                            if(!continueRecordA){
                                return;
                            }
                        }
                        else if(perms[1] == null){
                            ActivityCompat.requestPermissions(menu.this, new String[]{perms[0]}, REQUEST_STORAGE_VIDEO);
                            if(!continueRecordS){
                                return;
                            }
                        }
                        else{
                            ActivityCompat.requestPermissions(menu.this, perms, REQUEST_VIDEO);
                            if (!continueRecordS || !continueRecordA){
                                return;
                            }
                        }
                    }

                    ///////////////////////////////////////////////////////

                    if(fab2.getTag().toString().equals("@string/aIdle")){
                        recording = false;
                        fab2.setTag("@string/startArec");
                        fab2.setColorFilter(Color.GREEN);
                        Toast.makeText(context, "Press again to start recording audio", Toast.LENGTH_SHORT).show();
                    }
                    else if(fab2.getTag().toString().equals("@string/startArec")){
                        startRecordingAudio();
                        recording = true;
                        fab2.setImageResource(R.drawable.ic_stop);
                        fab2.setColorFilter(Color.RED);
                        fab2.setTag("@string/stopArec");
                        Toast.makeText(context, "Press Stop to stop recording audio", Toast.LENGTH_SHORT).show();
                    }
                    else if(fab2.getTag().toString().equals("@string/stopArec")){
                        stopRecordingAudio();
                        recording = false;
                        fab2.setImageResource(R.drawable.ic_mic);
                        fab2.clearColorFilter();
                        fab2.setTag("@string/aIdle");
                        Toast.makeText(context, "Stopped Recording", Toast.LENGTH_SHORT).show();
                    }
            }
        });
        /*
         * Record Screen Button
         */
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recording){//if audio recording, stop it
                    Toast.makeText(context, "Please stop recording audio before starting screen capture", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if We have permission to write to external storage
                String[] perms = new String[2];
                if(ContextCompat.checkSelfPermission(menu.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    perms[0] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                if(ContextCompat.checkSelfPermission(menu.this,android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                    perms[1] = android.Manifest.permission.RECORD_AUDIO;
                }
                if(perms[0] != null || perms[1] != null){
                    if(perms[0] == null){
                        ActivityCompat.requestPermissions(menu.this, new String[]{perms[1]}, REQUEST_RECORD_AUDIO);
                        if(!continueRecordA){
                            return;
                        }
                    }
                    else if(perms[1] == null){
                        ActivityCompat.requestPermissions(menu.this, new String[]{perms[0]}, REQUEST_STORAGE_VIDEO);
                        if(!continueRecordS){
                            return;
                        }
                    }
                    else{
                        ActivityCompat.requestPermissions(menu.this, perms, REQUEST_VIDEO);
                        if (!continueRecordS || !continueRecordA){
                            return;
                        }
                    }
                }


                if (fab3.getTag().equals("@string/vidle")) {
                    vrecording = false;
                    fab3.setImageResource(R.drawable.ic_play);
                    fab3.setColorFilter(Color.GREEN);
                    fab3.setTag("@string/vRecord");
                    Toast.makeText(context, "Press Play to Record Screen", Toast.LENGTH_SHORT).show();
                } else if (fab3.getTag().equals("@string/vRecord")) {

                    initiateScreenCapture();
                    startScreenCapture();
                    fab3.setImageResource(R.drawable.ic_stop);
                    fab3.setColorFilter(Color.RED);
                    fab3.setTag("@string/vStop");
                    vrecording = true;
                    Toast.makeText(context, "Press Stop to stop Recording Screen", Toast.LENGTH_SHORT).show();
                } else if (fab3.getTag().equals("@string/vStop")) {
                    fab3.setImageResource(R.drawable.ic_play);
                    fab3.setTag("@string/vidle");
                    fab3.clearColorFilter();

                    stopScreenCapture();
                    vrecording = false;
                    Toast.makeText(context, "Stopped Recording", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    /**
     * This method handles permissions.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
                    continueRecordS = false;
                }
                else{
                    continueRecordS = true;
                }
                break;
            case REQUEST_RECORD_AUDIO:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog ration = new AlertDialog.Builder(context).create();
                    ration.setTitle("Storage Permission Denied");
                    ration.setMessage("Need permission to record audio for video file.");
                    ration.show();
                    continueRecordA = false;
                }
                else{
                    continueRecordA = true;
                }
                break;
            case REQUEST_VIDEO:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog ration = new AlertDialog.Builder(context).create();
                    ration.setTitle("Storage and Recording Permission Denied");
                    ration.setMessage("Need permission to record audio for video file and to store video. ");
                    ration.show();
                    continueRecordS = false;
                    continueRecordA = false;
                }
                else if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog ration = new AlertDialog.Builder(context).create();
                    ration.setTitle("Storage Permission Denied");
                    ration.setMessage("Need permission to store video.");
                    ration.show();
                    continueRecordS = false;
                }
                else if(grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog ration = new AlertDialog.Builder(context).create();
                    ration.setTitle(" Recording Permission Denied");
                    ration.setMessage("Need permission to record audio for video file.");
                    ration.show();
                    continueRecordA = false;
                }
                else{
                    continueRecordA = true;
                    continueRecordS = true;
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

        } else if (id == R.id.filesList) {
            intent = new Intent(context, micControls.class);
            startActivity(intent);
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
        destroyMediaProjection();
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

    /**
     * This will setup the Screen recording.
     */
    private void initiateScreenCapture(){

        videoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        videoRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        videoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        String file_path = videoDirectory + "/" + nextFile();
        videoRecorder.setOutputFile(file_path);
        videoRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        videoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        videoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        videoRecorder.setVideoEncodingBitRate(512*1000);
        videoRecorder.setVideoFrameRate(30);
        try{

            videoRecorder.prepare();
            Log.v("Prepare", "prepare successful");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This method Initializes the Audio Recording
     */
    private void startRecordingAudio(){
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String audio_path = audioDirectory.toString() + "/" + nextFileaudio();
        audioRecorder.setOutputFile(audio_path);
        try {
            audioRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioRecorder.start();
        recording = true;
    }

    private void stopRecordingAudio(){
        audioRecorder.stop();
        recording = false;
        audioRecorder.reset();
    }

    /**
     * This method will start the screen recording.
     */
    private void startScreenCapture(){

        if (videoP == null) {
            startActivityForResult(videoPM.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        videoRecorder.start();
        vrecording = true;
    }

    /**
     *
     */
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (fab3.getTag().equals("@string/vRecord")) {
                videoRecorder.stop();
                vrecording = false;
                videoRecorder.reset();
            }
            videoP = null;
            stopScreenCapture();
        }
    }

    /**
     * This method will stop video recording.     *
     */
    private void stopScreenCapture(){

        if(videoVD == null){
            return;
        }
        videoRecorder.stop();
        vrecording = false;
        videoRecorder.reset();
        videoVD.release();
        destroyMediaProjection();

    }

    /**
     * This will help initiate the screen recording.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        videoPC = new MediaProjectionCallback();
        videoP = videoPM.getMediaProjection(resultCode, data);
        videoP.registerCallback(videoPC, null);
        videoVD = videoP.createVirtualDisplay("Main", DISPLAY_WIDTH, DISPLAY_HEIGHT, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, videoRecorder.getSurface(), null, null);
        videoRecorder.start();
        vrecording = true;
    }

    /**
     * This method will help start a new Video recording session.
     */
    private void destroyMediaProjection() {
        if (videoP != null) {
            videoP.unregisterCallback(videoPC);
            videoP.stop();
            videoP = null;
        }
    }

    /**
     * This method return the file name for the next audio/video file we store.
     */
    private String nextFile(){
        String fileName = "video0.mp4";
        ArrayList<String> files = getFiles(videoDirectory.toString());
        if(files == null){
            //there are no files in the directory
            return fileName;
        }
        for(int i=0; i<files.size(); i++){
            if(i == files.size()-1 && !fileName.equals(files.get(i))){
                return fileName;
            }
            else{
                if(fileName.equals(files.get(i))){//if filename exists,
                    fileName = "video" + (i+1) + ".mp4";
                }
                else{
                    continue;
                }
            }
        }
        return fileName;
    }

    private String nextFileaudio(){
        String fileName = "audio0.mp4";
        ArrayList<String> files = getFiles(audioDirectory.toString());
        if(files == null){
            //there are no files in the directory
            return fileName;
        }
        for(int i=0; i<files.size(); i++){
            if(i == files.size()-1 && !fileName.equals(files.get(i))){
                return fileName;
            }
            else{
                if(fileName.equals(files.get(i))){//if filename exists,
                    fileName = "audio" + (i+1) + ".mp4";
                }
                else{
                    continue;
                }
            }
        }
        return fileName;
    }

    /**
     * This method returns a list of the files in the path given.
     * @param path
     * @return
     */
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

}
