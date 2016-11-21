package danielsandovalutrgv.brains;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.session.PlaybackState;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.os.Bundle;
import android.os.Handler;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import static android.R.attr.dial;
import static android.R.attr.numColumns;
import static android.R.attr.onClick;
import static android.graphics.Color.rgb;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "danielsandovalutrgv.brains.MESSAGE";
    private Paint paint = new Paint();
    private Path path = new Path();
    Context context = this;
    private DrawerLayout mDrawerLayout;
    private WebView initial_web;
    private String inviteLink;
    private int refreshCount=0;
    private int[] colors;
    private int selectedColor;
    private String tag;
    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RLENGTH = 10;
    private String UserEmail;
    final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
    private int penSize = 3;
    private CastContext mCC;
    private MenuItem mRI;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private String RoomUrl;
    private messageChannel mmC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.webview);
        // Enable Javascript

        UiChangeListener();




        initial_web = (WebView) findViewById(R.id.collaborate);
        initial_web.setWebViewClient(new MyWeb());
        initial_web.setWebChromeClient(new WebChromeClient());
        initial_web.getSettings().setAllowContentAccess(true);
        initial_web.getSettings().setAllowFileAccess(true);
        initial_web.getSettings().setDatabaseEnabled(true);
        initial_web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        initial_web.getSettings().setDomStorageEnabled(true);
        initial_web.getSettings().setJavaScriptEnabled(true);
        initial_web.addJavascriptInterface(new webAppComm(), "webApp");

        //color dialog color array for dialog

        colors = new int[6];
        initializeColor();
        selectedColor = colors[4];


        colorPickerDialog.initialize(
                R.string.color_picker_default_title, colors, selectedColor, 3, colors.length);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ColorPickerPalette colorPickerPalette = (ColorPickerPalette) layoutInflater
            .inflate(R.layout.colorpicker, null);
        colorPickerPalette.init(colors.length, 3, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                if (color == Color.parseColor("#5bc0de")) {
                    initial_web.loadUrl("javascript: setColor('#5bc0de');" +
                            "changeMouse();");
                    Toast.makeText(context, "BLUE", LENGTH_SHORT).show();
                }
                else if(color == Color.parseColor("#5cb85c")){
                    initial_web.loadUrl("javascript: setColor('#5cb85c');" +
                            "changeMouse();");
                    Toast.makeText(context, "GREEN", LENGTH_SHORT).show();
                }
                else if(color == Color.parseColor("#f0ad4e")) {
                    initial_web.loadUrl("javascript: setColor('#f0ad4e');" +
                            "changeMouse();");
                    Toast.makeText(context, "ORANGE", LENGTH_SHORT).show();
                }
                else if(color == Color.parseColor("#d9534f")) {
                    initial_web.loadUrl("javascript: setColor('#d9534f');" +
                            "changeMouse();");
                    Toast.makeText(context, "RED", LENGTH_SHORT).show();
                }
                else if(color == Color.parseColor("#202020")) {
                    initial_web.loadUrl("javascript: setColor('#202020');" +
                            "changeMouse();");
                    Toast.makeText(context, "BLACK", LENGTH_SHORT).show();
                }
                else if(color == Color.WHITE){
                    initial_web.loadUrl("javascript: eraser();" +
                            "changeMouse();");
                    Toast.makeText(context, "ERASER", LENGTH_SHORT).show();
                }
                else{}
                selectedColor = color;
            }
        });
        colorPickerPalette.drawPalette(colors, selectedColor);

        //Open link
        Intent intent = getIntent();
        String action = intent.getAction();
        String togetherLink = intent.getDataString();
        if(Intent.ACTION_VIEW.equals(action) && togetherLink !=null){

            initial_web.loadUrl("http://mindcollab.me/app/" + togetherLink.substring(togetherLink.length()-10));
            RoomUrl = togetherLink.substring(togetherLink.length()-10);
        }
        else{
            Bundle b = getIntent().getExtras();
            UserEmail = b.getString("email");
            initial_web.loadUrl("http://mindcollab.me/app/" + b.getString("room"));
            RoomUrl = b.getString("room");
        }
        Toast.makeText(context, RoomUrl, LENGTH_SHORT).show();

        setupCastListener();

        //cast stuff

        mCC = CastContext.getSharedInstance(this);
        mCC.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
        mCastSession = mCC.getSessionManager().getCurrentCastSession();
    }


    //this object class will be able to be used in android browsers from the javascript in the webview
    class webAppComm{

        @android.webkit.JavascriptInterface
        public void sendLinkToApp(String invite){
            inviteLink = invite;
        }

        @android.webkit.JavascriptInterface
        public boolean inApp(){return true;}

        @android.webkit.JavascriptInterface
        public void sendLink(){

            sendInvite(inviteLink);
        }

        @android.webkit.JavascriptInterface
        public String SetEmail(){
            return UserEmail;
        }

        @android.webkit.JavascriptInterface
        public int retrefreshCount(){
            return refreshCount;
        }
        @android.webkit.JavascriptInterface
        public void updaterefreshCount(){
            refreshCount++;
        }

        @android.webkit.JavascriptInterface
        public void isInApp(){
            Toast.makeText(context, "is in app", LENGTH_SHORT).show();

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        initial_web.saveState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        initial_web.restoreState(savedInstanceState);
    }



    /**
     * generate random string to use with new room
     */
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

    public void initializeColor() {
        colors[0] = Color.parseColor("#5bc0de");
        colors[1] = Color.parseColor("#5cb85c");
        colors[2] = Color.parseColor("#f0ad4e");
        colors[3] = Color.parseColor("#d9534f");
        colors[4] = Color.parseColor("#202020");
        colors[5] = Color.WHITE;
    }

    private class MyWeb extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            view.loadUrl("javascript: enterUsername('"+UserEmail+"');");

        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            initial_web.setSystemUiVisibility(
                    /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            |*/ View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
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
        initial_web.loadUrl("about:blank");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mRI = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.cast);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.collab) {
            initial_web.loadUrl("javascript: (function(){toggleDialog();})()");
            return true;
        }
        if (id == R.id.action_pen) {
            penOptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendInvite( String newLink ){

        Intent inviteIntent = new Intent();
        inviteIntent.setAction(Intent.ACTION_SEND);
        inviteIntent.putExtra(Intent.EXTRA_TEXT, "Let's Collaborate!!!!\n" + newLink);
        inviteIntent.setType("text/plain");
        startActivity(inviteIntent);
    }

    public void UiChangeListener()
    {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    |*/ View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }

    private void penOptions(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.pen_dialog, null);
        AlertDialog.Builder penalert = new AlertDialog.Builder(context);
        penalert.setTitle("Pen Options");
        penalert.setView(alertLayout);
        AlertDialog dialog = penalert.create();
        dialog.show();

        final TextView pen_size = (TextView) dialog.findViewById(R.id.pen_width);
        pen_size.setText(penSize + " px");

        Button cp = (Button) dialog.findViewById(R.id.colorpicker);
        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmd = "";
                colorPickerDialog.show(getFragmentManager(), tag);
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        if (color == Color.parseColor("#5bc0de")) {
                            initial_web.loadUrl("javascript: setColor('#5bc0de');" +
                                    "changeMouse();");
                            Toast.makeText(context, "BLUE", LENGTH_SHORT).show();
                        }
                        else if(color == Color.parseColor("#5cb85c")){
                            initial_web.loadUrl("javascript: setColor('#5cb85c');" +
                                    "changeMouse();");
                            Toast.makeText(context, "GREEN", LENGTH_SHORT).show();
                        }
                        else if(color == Color.parseColor("#f0ad4e")) {
                            initial_web.loadUrl("javascript: setColor('#f0ad4e');" +
                                    "changeMouse();");
                            Toast.makeText(context, "ORANGE", LENGTH_SHORT).show();
                        }
                        else if(color == Color.parseColor("#d9534f")) {
                            initial_web.loadUrl("javascript: setColor('#d9534f');" +
                                    "changeMouse();");
                            Toast.makeText(context, "RED", LENGTH_SHORT).show();
                        }
                        else if(color == Color.parseColor("#202020")) {
                            initial_web.loadUrl("javascript: setColor('#202020');" +
                                    "changeMouse();");
                            Toast.makeText(context, "BLACK", LENGTH_SHORT).show();
                        }
                        else if(color == Color.WHITE){
                            initial_web.loadUrl("javascript: eraser();" +
                                    "changeMouse();");
                            Toast.makeText(context, "ERASER", LENGTH_SHORT).show();
                        }
                        else{}

                    }
                });
            }
        });

        Button inc = (Button) dialog.findViewById(R.id.button5);
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                penSize = penSize + 3;
                pen_size.setText(penSize + " px");
                initial_web.loadUrl("javascript: setSize(context.lineWidth+3);" +
                        "changeMouse();");
            }
        });

        Button dec = (Button) dialog.findViewById(R.id.button6);
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(penSize > 3){
                    penSize = penSize - 3;
                    pen_size.setText(penSize + " px");
                    initial_web.loadUrl("javascript: setSize(context.lineWidth-3);" +
                            "changeMouse();");
                }

            }
        });

        Button clear = (Button) dialog.findViewById(R.id.clear_canvas);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initial_web.loadUrl("javascript: clear(true);");
            }
        });

        Button erase = (Button) dialog.findViewById(R.id.erase);
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initial_web.loadUrl("javascript: eraser(); changeMouse();");
            }
        });

    }


    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
                mCastSession = session;

                mmC = new messageChannel(getString(R.string.namespace));
                try {
                    mCastSession.setMessageReceivedCallbacks(mmC.getNamespace(), mmC);
                    sendMessage("http://mindcollab.me/cast/" + RoomUrl);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
                session.sendMessage(getString(R.string.namespace), "http://mindcollab.me/cast/" + RoomUrl);
            }

            @Override
            public void onSessionEnding(CastSession session) {}

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {}

            @Override
            public void onSessionSuspended(CastSession session, int reason) {}

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                mCastSession.sendMessage(getString(R.string.namespace), "http://mindcollab.me/cast/" + RoomUrl);

                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {

                invalidateOptionsMenu();
            }


        };
    }

    /*
     * Send a message to the receiver app
     */
    private void sendMessage(String message) {
        if (mmC != null) {
            mCastSession.sendMessage(mmC.getNamespace(), message);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Custom message channel
     */
    static class messageChannel implements Cast.MessageReceivedCallback {

        private final String mNamespace;

        /**
         * @param namespace the namespace used for sending messages
         */
        messageChannel(String namespace) {
            mNamespace = namespace;
        }

        /**
         * @return the namespace used for sending messages
         */
        public String getNamespace() {
            return mNamespace;
        }

        /*
         * Receive message from the receiver app
         */
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Log.d("lol", "onMessageReceived: " + message);
        }

    }

}

/*        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial_web.loadUrl("javascript: clear(true);");
            }
        });

        collabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initial_web.loadUrl("javascript: (function(){toggleDialog();})()");
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });

        penBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                *//*

*//*
            }
        });*/
/*        final AlertDialog alert = new AlertDialog.Builder(this, R.style.MyDialogTheme).setTitle(R.string.color_picker_default_title)
                .setPositiveButton("+", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initial_web.loadUrl("javascript: setSize(context.lineWidth+3);" +
                                "changeMouse();");
                    }
                })
                .setNeutralButton("-", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initial_web.loadUrl("javascript: if (context.lineWidth > 3) {" +
                                "      setSize(context.lineWidth-3);" +
                                "    }" +
                                "changeMouse();");
                    }
                })
                .setView(colorPickerPalette)
                .create();*/
//Buttons
/*        clearBtn = (Button) findViewById(R.id.button8);
        recordBtn = (Button) findViewById(R.id.button9);
        penBtn = (Button) findViewById(R.id.button10);
        collabBtn = (Button) findViewById(R.id.button7);*/
/*    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
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
    }*/