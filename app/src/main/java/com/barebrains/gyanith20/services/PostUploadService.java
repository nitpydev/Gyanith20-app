package com.barebrains.gyanith20.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PostUploadService extends Service {

    private ArrayList<Pair<String, UploadTask>> imgUploads;
    private Map<String,Integer> ImgProgress;
    private String[] imgPaths;
    private String captions;
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
        AppNotiManager.Create(this,getTotalSize(imgPaths));//CREATE PROGRESS NOTIFICATION

        ImgProgress = new HashMap<>();
        for (Pair<String,UploadTask> imgUpload : imgUploads)
            ImgProgress.put(imgUpload.first,0);

        AppNotiManager.setProgress(0);  //Show Notification

        for (int i = 0;i<imgUploads.size();i++)
        {
            final UploadTask uploadTask = imgUploads.get(i).second;

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    String id = taskSnapshot.getStorage().getName();//Get ID

                    ImgProgress.remove(id);//Remove object with ID
                    ImgProgress.put(id,(int)taskSnapshot.getBytesTransferred());//Put new object with ID

                    AppNotiManager.setProgress(getProgress());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("asd", "error : " + e);
                    AppNotiManager.setProgressTitle("Posting Interrupted");
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    success++;
                    if (success == imgPaths.length)
                    {
                        Post post = new Post(""
                                ,GyanithUserManager.getCurrentUser().userName
                                ,GyanithUserManager.getCurrentUser().gyanithId
                                ,System.currentTimeMillis()
                                ,captions
                                ,Arrays.asList(getImgIds()));


                        PostManager.CommitPostToDB(post, new CompletionListener() {
                            @Override
                            public void OnComplete() {
                                stopSelf();
                            }
                        });
                        AppNotiManager.CompleteProgress();
                    }
                }
            });
        }

    }


    private int getProgress()
    {
        int totalProgress = 0;
        for (Pair<String,UploadTask> imgUpload : imgUploads)
            totalProgress += ImgProgress.get(imgUpload.first);
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
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
