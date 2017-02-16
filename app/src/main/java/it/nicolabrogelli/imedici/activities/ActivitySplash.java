package it.nicolabrogelli.imedici.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import it.nicolabrogelli.imedici.R;

/**
 * Design and developed by Nicola Brogelli
 *
 * ActivitySplash is created to display welcome screen.
 * Created using AppCompatActivity.
 */
public class ActivitySplash extends AppCompatActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configuration in Android API below 21 to set window to full screen.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Create loading to wait for few second before displaying ActivityHome
        new Loading().execute();
    }

    /**
     * Loading, Crea un Loading di attesa prima di richiamare l'Activity di avvio
     *
     */
    public class Loading extends AsyncTask<Void, Void, Void>{

        /**
         * doInBackground
         *
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(2000);
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
            return null;
        }

        /**
         * onPostExecute
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent homeIntent = new Intent(getApplicationContext(), ActivityHome.class);
            startActivity(homeIntent);
            overridePendingTransition(R.anim.open_next, R.anim.close_main);
        }
    }

    // Configuration in Android API 21 to set window to full screen.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }


}
