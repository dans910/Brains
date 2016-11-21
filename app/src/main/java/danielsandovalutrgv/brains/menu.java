package danielsandovalutrgv.brains;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Random;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserOptions.OnFragmentInteractionListener {
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
    private boolean vidgranted;
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
    public FloatingActionButton fab2;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private Surface thesurf;
    private CoordinatorLayout coordinatorLayout;
    private CoordinatorLayout.LayoutParams params;
    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams params1;
    private Button textButton;
    private EditText editText;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RLENGTH = 10;
    private String RoomUrl;
    private String UserEmail;

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


        RoomUrl = generateRandomString();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user!=null){

                }else{
                    //user is signed out
                }
            }
        };

        params = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        textButton = (Button) findViewById(R.id.textButton);

        switchOnOff(false);
        buttonDescriptions();
        buttonActions();
        fab3.setTag(R.string.vidle);
        fab2.setTag(R.string.aIdle);
        fab1.setColorFilter(Color.RED);

        //Screen Capture variables
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenDensity = metrics.densityDpi;
        videoRecorder = new MediaRecorder();
        audioRecorder = new MediaRecorder();
        videoPM = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        //For Drawing
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);
        relativeLayout  = (RelativeLayout) findViewById(R.id.editRoot);


        MyTouchEventView touch = new MyTouchEventView(this, fab1);
        touch.setLayoutParams(params);
        assert coordinatorLayout != null;
        coordinatorLayout.addView(touch);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);



            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                UserEmail = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            Toast.makeText(menu.this, email, Toast.LENGTH_SHORT).show();

            View hView =  navigationView.getHeaderView(0);
            TextView nav_user = (TextView)hView.findViewById(R.id.profile_name);
            nav_user.setText(email);
            TextView status = (TextView)hView.findViewById(R.id.log_status);
            status.setText("Signed In");/*
            ImageView profile_pic = (ImageView) hView.findViewById(R.id.profilePic);
            profile_pic.setImageBitmap(getBitmapFromURL(photoUrl.toString()));*/
        }

    }


    /**
     * This method open the Media files activity
     * This method creates an intent and will be called when a button is pressed.
     * @param view ,View containing file
     */
    public void recordAudio(View view){

        Intent intent = new Intent(context, micControls.class);
        startActivity(intent);
    }

    /**
     * This method toggles the buttons' visibility.
     * @param status, boolean to check whether the button has been clicked
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
                Toast.makeText(context, "Please stop screen capture before starting to record audio", Toast.LENGTH_SHORT).show();
                    if(fab2.getTag().equals(R.string.aIdle)){
                        recording = false;
                        fab2.setTag(R.string.startArec);
                        fab2.setColorFilter(Color.GREEN);
                        Toast.makeText(context, "Press again to start recording audio", Toast.LENGTH_SHORT).show();
                    }
                    else if(fab2.getTag().equals(R.string.startArec)){
                        startRecordingAudio();
                        recording = true;
                        fab2.setImageResource(R.drawable.ic_stop);
                        fab2.setColorFilter(Color.RED);
                        fab2.setTag(R.string.stopArec);
                        Toast.makeText(context, "Press Stop to stop recording audio", Toast.LENGTH_SHORT).show();
                    }
                    else if(fab2.getTag().equals(R.string.stopArec)){
                        stopRecordingAudio();
                        recording = false;
                        fab2.setImageResource(R.drawable.ic_mic);
                        fab2.clearColorFilter();
                        fab2.setTag(R.string.aIdle);
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
                //if(recording){//if audio recording, stop it
                //    Toast.makeText(context, "Please stop recording audio before starting screen capture", Toast.LENGTH_SHORT).show();
                //    return;
                //}
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


                if (fab3.getTag().equals(R.string.vidle)) {
                    vrecording = false;
                    fab3.setImageResource(R.drawable.ic_play);
                    fab3.setColorFilter(Color.GREEN);
                    fab3.setTag(R.string.vRecord);
                    Toast.makeText(context, "Press Play to Record Screen", Toast.LENGTH_SHORT).show();
                } else if (fab3.getTag().equals(R.string.vRecord)) {

                    initiateScreenCapture();
                    startScreenCapture();
                    fab3.setImageResource(R.drawable.ic_stop);
                    fab3.setColorFilter(Color.RED);
                    fab3.setTag(R.string.vStop);
                    vrecording = true;
                    Toast.makeText(context, "Press Stop to stop Recording Screen", Toast.LENGTH_SHORT).show();


                } else if (fab3.getTag().equals(R.string.vStop)) {
                    fab3.setImageResource(R.drawable.ic_play);
                    fab3.setTag(R.string.vidle);
                    fab3.clearColorFilter();

                    stopScreenCapture();
                    vrecording = false;
                    Toast.makeText(context, "Stopped Recording", Toast.LENGTH_SHORT).show();
                }
            }
        });


        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textButton.callOnClick();
                fab.callOnClick();

            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("Edit Text");

                editText = new EditText(context);
                editText.setText(textButton.getText());
                editText.setWidth(CoordinatorLayout.LayoutParams.WRAP_CONTENT);
                dialog.setView(editText);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textButton.setText(editText.getText());
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textButton.setText("");
                    }
                });

                dialog.show();
            }
        });

        textButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);

                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.topMargin = (int) event.getRawY() - v.getHeight();
                        layoutParams.leftMargin = (int) event.getRawX() - (v.getWidth() / 2);
                        v.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        layoutParams.topMargin = (int) event.getRawY() - v.getHeight();
                        layoutParams.leftMargin = (int) event.getRawX() - (v.getWidth() / 2);
                        v.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        v.setLayoutParams(layoutParams);
                    default:
                        break;
                }
                return false;
            }
        });
    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
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

        Fragment fragment = null;

        if (id == R.id.privateC) {

        } else if (id == R.id.publicC) {


            intent = new Intent(context, MainActivity.class);
            intent.putExtra("room", RoomUrl);
            intent.putExtra("email", UserEmail);
            startActivity(intent);
        } else if (id == R.id.filesList) {
            intent = new Intent(context, micControls.class);

            startActivity(intent);
        } else if (id == R.id.nav_manage) {

            Class fc = UserOptions.class;
            try {
                fragment = (Fragment) fc.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.mainFrame, fragment).commit();

        } else if (id == R.id.nav_share) {
            mAuth.signOut();
            startActivity(new Intent(menu.this, Login.class));
            finish();

        } else if (id == R.id.nav_send) {
            sendInvite();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
            vidgranted = false;
            return;
        }
        if (resultCode != RESULT_OK) {
            vidgranted = false;
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
        vidgranted = true;
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
     * @param path, Path to directory of files to be played.
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

    public void sendInvite(){

        Intent inviteIntent = new Intent();
        inviteIntent.setAction(Intent.ACTION_SEND);
        inviteIntent.putExtra(Intent.EXTRA_TEXT, "Let's Collaborate\nhttp://mindcollab.me/"+ RoomUrl);
        inviteIntent.setType("text/plain");
        startActivity(inviteIntent);
    }

    public String generateRandomString(){
        StringBuffer str = new StringBuffer();
        for(int i=0; i<RLENGTH; i++){
            int rnd = getRandomNumber();
            char c = CHARACTERS.charAt(rnd);
            str.append(c);
        }
        return str.toString();
    }

    /**
     * get random int
     */
    public int getRandomNumber(){
        int randy=0;
        Random rndgen = new Random();
        randy = rndgen.nextInt(CHARACTERS.length());
        if(randy -1 ==-1){
            return  randy;
        }
        else{
            return  randy -1;
        }
    }

}
