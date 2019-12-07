package com.barebrains.gyanith20.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.barebrains.gyanith20.Activities.SplashActivity;
import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.Statics.PostManager;
import com.barebrains.gyanith20.gyanith19;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostUploadService extends Service {

    private Map<String,Integer> ImgProgress;
    private int PROGRESS_MAX;
    private int NOTIFICATION_ID = 19;
    private String[] imgPaths;
    private String captions;
    private ArrayList<Pair<String, UploadTask>> imgUploads;
    private int success = 0;

    public PostUploadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        imgPaths = intent.getExtras().getStringArray("EXTRA_IMG_PATHS");
        captions = intent.getStringExtra("EXTRA_CAPTIONS");

        imgUploads = PostManager.uploadImages(this,imgPaths);
        HandleProgress();
        return Service.START_REDELIVER_INTENT;
    }

    private void HandleProgress(){
        PROGRESS_MAX = getTotalSize(imgPaths);
        ImgProgress = new HashMap<>();
        for (Pair<String,UploadTask> imgUpload : imgUploads)
            ImgProgress.put(imgUpload.first,0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, gyanith19.PROGRESS_CHANNEL)
                .setSmallIcon(R.drawable.l2)
                .setContentTitle("Posting")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(false)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        final NotificationManagerCompat notManager = NotificationManagerCompat.from(this);

        builder.setProgress(PROGRESS_MAX, 0, false);
        notManager.notify(NOTIFICATION_ID, builder.build());

        for (int i = 0;i<imgUploads.size();i++)
        {
            final UploadTask uploadTask = imgUploads.get(i).second;
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) taskSnapshot.getBytesTransferred();

                    String id = taskSnapshot.getStorage().getName();
                    ImgProgress.remove(id);
                    ImgProgress.put(id,progress);

                    builder.setProgress(PROGRESS_MAX, getProgress(), false);
                    notManager.notify(NOTIFICATION_ID, builder.build());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("asd", "error : " + e);
                    builder.setProgress(0, 0, false);
                    builder.setContentTitle("Posting Interrupted");
                    notManager.notify(NOTIFICATION_ID, builder.build());
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    success++;
                    if (success == imgPaths.length)
                    {
                        OnUploadComplete(builder,notManager);
                    }
                }
            });
        }

    }

    private void OnUploadComplete(NotificationCompat.Builder builder,NotificationManagerCompat notManager){


        PostManager.CommitPostToDB(getImgIds(),captions,System.currentTimeMillis());
        builder.setAutoCancel(true);

        builder.setContentTitle("Posted")
                .setProgress(0,0,false);
        notManager.notify(NOTIFICATION_ID, builder.build());
    }
    private int getProgress()
    {
        int totalProgress = 0;
        for (Pair<String,UploadTask> imgUpload : imgUploads)
            totalProgress += ImgProgress.get(imgUpload.first);
        Log.d("asd","progress : " + totalProgress);
        return totalProgress;
    }

    private String[] getImgIds(){
        String[] imgIds = new String[imgUploads.size()];
        for (int i =0;i<imgIds.length;i++)
            imgIds[i] = imgUploads.get(i).first;
        return imgIds;
    }

    private int getTotalSize(String[] filePaths){
        int totalSize = 0;
        for (String path : filePaths)
        {
            File file = new File(path);
            totalSize += file.length();
        }
        return totalSize;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
