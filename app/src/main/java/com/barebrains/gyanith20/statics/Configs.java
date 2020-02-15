package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.fragments.botSheet;
import com.barebrains.gyanith20.gyanith20;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import static com.barebrains.gyanith20.gyanith20.sp;

public class Configs {

    private static final String RC_STALE = "stale_config";


    private static final String VERSION_NAME = "version_code";
    private static final String UPDATE_NOTE = "update_note";
    private static final String FORCE_UPDATE = "force_update";

    private static final String REG_LOCK = "reg_lock";
    private static final String REG_LOCK_NOTE = "reg_lock_note";
    private static final String REG_LOCK_EXCLUDES = "reg_lock_excludes";

    private static final String ADMIN_TOKEN = "admin_token";

    public static void init(){
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        remoteConfig.setDefaultsAsync(R.xml.defaults_remote_config);

        FirebaseDatabase.getInstance().getReference().child("config_token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                String token = dataSnapshot.getValue(String.class);

                if (!sp.getString(RC_STALE,"").equals(token))
                    refresh(remoteConfig,token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void refresh(FirebaseRemoteConfig remoteConfig, final String token){
        remoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                sp.edit().putString(RC_STALE,token).apply();
            }
        });
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

        boolean update;
        try {
            update = Float.parseFloat(rv) > Float.parseFloat(v);
        }catch (NumberFormatException e){
            update = !rv.equals(v);
        }
        return update;
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

    public static boolean isRegLockExcluded(String id){
        String[] ar = FirebaseRemoteConfig.getInstance().getString(REG_LOCK_EXCLUDES).split("/");

        for (int i = 0;i < ar.length;i++){
            if (ar[i].equals(id))
                return true;
        }

        return false;
    }

    public static boolean isValidAdmin(){
        return gyanith20.ADMIN_TOKEN.equals(FirebaseRemoteConfig.getInstance().getString(ADMIN_TOKEN));
    }

    public static Long getDayMillis(int day){
        return FirebaseRemoteConfig.getInstance().getLong("Day" + day);
    }
}
