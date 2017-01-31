package com.example.siddhant.popularmovies;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

import java.util.ArrayList;

/**
 * Created by siddhant on 1/30/17.
 */

public class MoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(new SampleDumperPluginProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    private static class SampleDumperPluginProvider implements DumperPluginsProvider {

        private final Context mContext;

        public  SampleDumperPluginProvider(Context context) {
            mContext = context;
        }

        @Override
        public Iterable<DumperPlugin> get() {
            ArrayList<DumperPlugin> plugins = new ArrayList<>();
            for (DumperPlugin defaultPlugin: Stetho.defaultDumperPluginsProvider(mContext).get()) {
                plugins.add(defaultPlugin);
            }
            return plugins;
        }
    }
}
