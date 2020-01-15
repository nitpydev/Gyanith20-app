package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.EventItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Util {

    public static StorageReference[] getStorageRefs(List<String> ids,StorageReference parent){
        StorageReference[] storageReferences = new StorageReference[ids.size()];
        for (int i=0;i<ids.size();i++)
            storageReferences[i] = parent.child(ids.get(i));

        return storageReferences;
    }

    public static Transaction.Handler incrementer = new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            Long p = mutableData.getValue(Long.class);
            if (p == null) {
                return Transaction.success(mutableData);
            }
            p++;

            if (p < 0)
                p = 0L;
            // Set value and report transaction success
            mutableData.setValue(p);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

        }
    };

    public static Transaction.Handler decrementer = new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
            Long p = mutableData.getValue(Long.class);
            if (p == null) {
                return Transaction.success(mutableData);
            }
            p--;
            if (p > 0)
                p = 0L;
            // Set value and report transaction success
            mutableData.setValue(p);
            return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

        }
    };

    public static String generateUniqueId(){
        return FirebaseDatabase.getInstance().getReference().push().getKey();
    }

    public static String timeToDate(String time)
    {
        String date;
        try {
            long timeInt = Long.parseLong(time);
            SimpleDateFormat s = new SimpleDateFormat("MMM dd");

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timeInt);
            if (c.getTime().getDate() == Calendar.getInstance().getTime().getDate())
                return "Today";
            else if (c.getTime().getDate() == Calendar.getInstance().getTime().getDate() + 1)
                return "Tommorow";

            Date d = new Date(timeInt);
            date = s.format(d);
        }catch (NumberFormatException n)
        {
            date = "Feb 26 10 am";
        }
        return date;
    }

    public static String BuildDateString(long time){
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(time);

        return cl.get(Calendar.DAY_OF_MONTH)
                + " , "
                + weekDayToDay(cl.get(Calendar.DAY_OF_WEEK))
                + " " + cl.get(Calendar.HOUR)
                + ":" + cl.get(Calendar.MINUTE)
                + " " + amPm(cl.get(Calendar.AM_PM));
    }

    private static String weekDayToDay(int day){
        switch (day){
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thur";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
                default:
                    return "";
        }
    }

public static String amPm(int i){
        if (i == 0)
            return "AM";
        else
            return "PM";
}

    public static String UriAbsPath(Context context,Uri imgUri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        // Get the cursor
        Cursor cursor =  context.getContentResolver().query(imgUri, filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();
        //Get the column index of MediaStore.Images.Media.DATA
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        //Gets the String value in the column
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }

    public static Bitmap[] getImgBitmaps(String[] imgPaths){
        Bitmap[] bitmaps = new Bitmap[imgPaths.length];
        for (int i =0;i<imgPaths.length;i++) {
            bitmaps[i] = BitmapFactory.decodeFile(imgPaths[i]);
        }

        return bitmaps;
    }

    public static Pair<Integer,String>[] arrayToPair(List<String> array){
        Pair<Integer,String>[] pairs = new Pair[array.size()];
        for (int i=0;i<array.size();i++)
            pairs[i] = new Pair<>(i,array.get(i));

        return pairs;
    }

    public static String BuildTimeAgoString(long postTime){
        final int minute = 60000;
        final int hrs = 60*minute;
        final int days = 24*hrs;
        long currentTime = System.currentTimeMillis();
        long diff = currentTime + postTime;

        if (diff < minute)
            return "just now";
        else if (diff < 2*minute)
            return "a min ago";
        else if (diff < hrs)
            return diff/minute + " mins ago";
        else if (diff < 2*hrs)
            return "an hour ago";
        else if (diff < days)
            return diff/hrs + " hrs ago";
        else if (diff < 2*days)
            return "yesterday";
        else
            return diff/days + " days ago";
    }

    public static void putBitmaptoFile(Bitmap bitmap,File file) throws IOException {
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(Util.class.getSimpleName(), "Error writing bitmap", e);
        }

    }

    public static Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

    public static String sha1(String input){
        try {
        // getInstance() method is called with algorithm SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        // digest() method is called
        // to calculate message digest of the input string
        // returned as array of byte
        byte[] messageDigest = md.digest(input.getBytes());

        // Convert byte array into signum representation
        BigInteger no = new BigInteger(1, messageDigest);

        // Convert message digest into hex value
        String hashtext = no.toString(16);

        // Add preceding 0s to make it 32 bit
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        // return the HashText
        return hashtext;
    }

    // For specifying wrong message digest algorithms
    catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
    }

    public static GyanithUser jsonToGyanithUser(String s,String token){
        Gson gson = new Gson();
        GyanithUserJsonResponse json = gson.fromJson(s,GyanithUserJsonResponse.class);
        return new GyanithUser(json.gyid
        ,json.name
        ,json.usr
        ,json.email
        ,json.phno
        ,json.clg
        ,json.reg
        ,token);
    }
}

class GyanithUserJsonResponse{

    public String gyid;
    public String usr;
    public String name;
    public String clg;
    public String email;
    public String phno;
    public String gender;
    public ArrayList<String> reg;

    public GyanithUserJsonResponse(){}
}