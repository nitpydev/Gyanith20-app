package com.barebrains.gyanith20.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.FileUtils;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.Post;
import com.barebrains.gyanith20.statics.GyanithUserManager;
import com.barebrains.gyanith20.statics.AppNotiManager;
import com.barebrains.gyanith20.statics.PostManager;
import com.barebrains.gyanith20.statics.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static com.barebrains.gyanith20.statics.Util.incrementer;

public class PostUploadService extends Service {

    private ArrayList<PostUploadWork> works;

    private ArrayList<CompletionListener> boundListeners;

    public PostUploadService() {}

   @Override
    public void onCreate() {
        super.onCreate();
        works = new ArrayList<>();
        boundListeners = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            works.add(new PostUploadWork(works.size()
                    ,this
                    ,intent.getExtras().getStringArray("EXTRA_IMG_PATHS")
                    ,intent.getStringExtra("EXTRA_CAPTIONS")
            ,new CompletionListener(){
                @Override
                public void OnComplete() {
                    for (CompletionListener listener : boundListeners)
                        if (listener != null)listener.OnComplete();
                }

                @Override
                public void OnError(String error) {
                    for (CompletionListener listener : boundListeners)
                        if (listener != null)listener.OnError("Posting Interrupted");
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
            for (CompletionListener listener : boundListeners)
                if (listener != null)listener.OnError("Error reading images");
        }

        return Service.START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PostBinder();
    }

    public class PostBinder extends Binder{
        public void addListener(CompletionListener listener){
            boundListeners.add(listener);
        }

        public void removeListener(CompletionListener listener){boundListeners.remove(listener);
        }
    }




    private class PostUploadWork{

        private int workId;
        private Service service;
        private String caption;

        private CompletionListener completionListener;
        private ArrayList<File> compressedFiles = new ArrayList<>();
        private Map<String,Long> currProgress = new HashMap<>();
        private int tasksCompleted = 0;

        private String[] duplicateImgPaths;

        public PostUploadWork(int workId,Service service,String[] imgPaths,String captions,CompletionListener completionListener) throws IOException {
            this.workId = workId;
            this.service = service;
            this.caption = captions;
            this.completionListener = completionListener;
            this.duplicateImgPaths = imgPaths;
            compressToFiles(imgPaths);
            startUpload();
        }

        private void compressToFiles(String[] imgPaths) throws IOException {
            for (String imgPath : imgPaths) {
                File file = new Compressor(service)
                        .setMaxWidth(640)
                        .setMaxHeight(640)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(service.getCacheDir().getAbsolutePath() + "/uploadPosts")
                        .compressToFile(new File(imgPath));

                compressedFiles.add(file);
            }
        }

        private void startUpload() throws FileNotFoundException {
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference().child("PostImages");

            AppNotiManager.Create(service,workId,getTotalBytes());
            AppNotiManager.setProgress(workId,0);

            for (File file : compressedFiles) {
                final String id = Util.generateUniqueId();
                InputStream stream = new FileInputStream(file);
                UploadTask uploadTask = storageRef.child(id).putStream(stream);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        currProgress.put(id,taskSnapshot.getBytesTransferred());
                        updateProgress();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                            updateUploadTaskComplete();
                        else
                            updateError(task.getException());
                    }
                });
            }
        }

        private void commitPostToFirebase(final CompletionListener listener){
            GyanithUserManager.getCurrentUser().observeForever(new Observer<Resource<GyanithUser>>() {
                @Override
                public void onChanged(Resource<GyanithUser> res) {
                    if (res.value == null)
                    {
                        Toast.makeText(service, "User not signed In", Toast.LENGTH_SHORT).show();
                        GyanithUserManager.getCurrentUser().removeObserver(this);
                        return;
                    }

                    Post post = new Post(""
                            ,res.value.userName
                            ,res.value.gyanithId
                            ,System.currentTimeMillis()
                            ,caption
                            ,new ArrayList<>(currProgress.keySet()));

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference postRef = rootRef.child("posts").push();
                    post.time = -post.time;
                    post.postId = postRef.getKey();
                    postRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {



                            listener.OnComplete();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    deleteDuplicates();
                                    listener.OnError(e.getMessage());
                                }
                            });

                    rootRef.child("users").child(post.gyanithId).child("postCount").runTransaction(incrementer);
                    rootRef.child("postCount").runTransaction(incrementer);
                    rootRef.child("users").child(GyanithUserManager.loggedUser_value.value.gyanithId)
                            .child("posts").child(post.postId).setValue(post);

                    GyanithUserManager.getCurrentUser().removeObserver(this);
                }
            });

        }

        private void updateProgress(){
            AppNotiManager.setProgress(workId,getCurrentBytes());
        }

        private void updateUploadTaskComplete(){
            tasksCompleted++;
            if (tasksCompleted == compressedFiles.size()) {
                commitPostToFirebase(new CompletionListener(){
                    @Override
                    public void OnComplete() {
                        AppNotiManager.setProgressTitle(workId,"Posted !");
                        AppNotiManager.finishNotification(service,workId);
                        completionListener.OnComplete();
                    }

                    @Override
                    public void OnError(String error) {
                        updateError(new Exception("While commit to db , :" + error));
                    }
                });

            }
        }

        private void updateError(Exception e){
            Log.d("asd","Posts Upload Error : " + e.getMessage());
            AppNotiManager.setProgressTitle(workId,"Posting Interrupted !");
            AppNotiManager.finishNotification(service,workId);
            completionListener.OnError(e.getMessage());
        }


        private void deleteDuplicates(){
            for (String path : duplicateImgPaths){
                File file = new File(path);
                deleteFile(path);
                Log.d("asd","File : " + file.isFile());
            }
        }

        private long getCurrentBytes(){
            long total = 0;
            for (Long val : currProgress.values())
                total += val;
            return total;
        }

        private long getTotalBytes(){
            long total = 0;
            for (File file : compressedFiles)
                total += file.length();
            return total;
        }
    }
}
