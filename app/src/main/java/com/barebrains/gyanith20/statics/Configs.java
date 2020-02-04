package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.gyanith20;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class Configs {

    private static final String VERSION_NAME = "version_code";
    private static final String UPDATE_NOTE = "update_note";
    private static final String FORCE_UPDATE = "force_update";

    private static final String REG_LOCK = "reg_lock";
    private static final String REG_LOCK_NOTE = "reg_lock_note";

    private static final String ADMIN_TOKEN = "admin_token";

    public static void init(){
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        remoteConfig.setDefaultsAsync(R.xml.defaults_remote_config);

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();

        remoteConfig.setConfigSettingsAsync(configSettings);

        //Activate already fetched config
        remoteConfig.activate();

        //Try to fetch new config
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                remoteConfig.fetch();
            }
        }, 0);
    }


    public static void checkUpdate(final Context context, final FragmentManager fragmentManager){
        if (!Configs.isUpdateRequired())
            return;

        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        final botSheet bs = botSheet.makeBotSheet(fragmentManager);
        bs.setTitle("NEW VERSION AVAILABLE!")
                .setAction("PLAY STORE")
                .setBody(remoteConfig.getString(UPDATE_NOTE))
                .setActionListener(new CompletionListener(){
                    @Override
                    public void OnComplete() {
                        goToPlayStore(context);
                    }

                    @Override
                    public void OnError(String error) {
                        if (remoteConfig.getBoolean(FORCE_UPDATE)) {
                            Toast.makeText(context, "Please Update to Continue", Toast.LENGTH_SHORT).show();
                            checkUpdate(context,fragmentManager);
                        }
                    }
                }).show();
    }


    private static boolean isUpdateRequired(){
        String v = getAppVersionName();
        if (v == null)
            return false;

        String rv = FirebaseRemoteConfig.getInstance().getString(VERSION_NAME);

        return !rv.equals(v);
    }

    private static String getAppVersionName(){
        try {
            Context context = gyanith20.appContext;
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void goToPlayStore(Context context){
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    public static boolean isRegLocked(){
        return  FirebaseRemoteConfig.getInstance().getBoolean(REG_LOCK);
    }

    public static String getRegLockNote(){
        return FirebaseRemoteConfig.getInstance().getString(REG_LOCK_NOTE);
    }

    public static boolean isValidAdmin(){
        return gyanith20.ADMIN_TOKEN.equals(FirebaseRemoteConfig.getInstance().getString(ADMIN_TOKEN));
    }

}
