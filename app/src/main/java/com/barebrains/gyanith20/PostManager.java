package com.barebrains.gyanith20;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.net.Uri;
import android.util.Pair;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class  PostManager{
    public static ArrayList<Pair<String,UploadTask>>  uploadImages(Context context, String[] imgPaths){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("PostImages");
        ArrayList<Pair<String,UploadTask>> pics = new ArrayList<>();
        for (int i =0;i<imgPaths.length;i++)
        {
            String id = generateUniqueId();
            try {
                InputStream stream = new FileInputStream(new File(imgPaths[i]));
                UploadTask uploadTask = storageRef.child(id).putStream(stream);
                pics.add(new Pair<>(id, uploadTask));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pics;
    }

    public  static void CommitPostToDB(String[] imgIds, String caption, long time){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("posts").push().setValue(jsonPost("Test",
                time,
                caption
        ,imgIds));
    }

    private static String generateUniqueId(){
        return FirebaseDatabase.getInstance().getReference().push().getKey();
    }

    private static String jsonPost(String userName,long time,String caption,String[] imgIds){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userName", userName);
            jsonObject.put("time",time);
            jsonObject.put("caption",caption);
            jsonObject.put("imgIds", imgIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
