package it.nicolabrogelli.imedici;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MainApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
