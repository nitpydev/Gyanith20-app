package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.models.eventitem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Util {
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

    public static String sha1(String input)
    { try {
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
       /* return new GyanithUser(json.gyanithId
        ,json.name
        ,json.username
        ,json.email
        ,json.phoneNumber
        ,json.clg
        ,json.token);*/
       //FOR NOW AS USER INFO FIELDS IN THE BACKEND ARE NOT YET IMPLEMENTED,WE CREATE A DUMMY USER
        json.registeredEvents = new ArrayList<>();
        eventItemJson a = new eventItemJson();
        a.name = "nameed";
        a.id = "tageg";
        a.timestamp = "6a4446464";
        a.type = "te";
        json.registeredEvents.add(a);
        eventItemJson b = new eventItemJson();
        b.name = "nameed_te";
        b.id = "tageg_te";
        b.timestamp = "6a4446464_te";
        b.type = "w";
        json.registeredEvents.add(b);
        Pair<ArrayList<eventitem>,ArrayList<eventitem>> events = segregateEvents(jsonToEventItem(json.registeredEvents));
        return new GyanithUser("GYsd59",
               "Pushpavel",
               "Pixel54","jpushpavel@gmail.com",
               "4897854541",
               "NITPY",
               events.first,
               events.second,
               true,
               token);
    }

    public static ArrayList<eventitem> jsonToEventItem(ArrayList<eventItemJson> jsons){
        ArrayList<eventitem> eventitems = new ArrayList<>(jsons.size());
        for (int i = 0; i < jsons.size();i++){
            eventItemJson itemJson = jsons.get(i);

            eventitem item = new eventitem(itemJson.name,itemJson.timestamp,itemJson.id);
            item.setType(itemJson.type);
            eventitems.add(item);
        }

        return eventitems;
    }

    public static Pair<ArrayList<eventitem>,ArrayList<eventitem>> segregateEvents(ArrayList<eventitem> unsorted){
        ArrayList<eventitem> te,w;
        te = new ArrayList<>();
        w = new ArrayList<>();

        for (eventitem eventitem : unsorted){
            if (eventitem.type == "w")
                w.add(eventitem);
            else
                te.add(eventitem);
        }

        return new Pair<>(te,w);
    }
}

class GyanithUserJsonResponse{
    public String username;
    public String name;
    public String email;
    public String gyanithId;
    public String phoneNumber;
    public String clg;
    public String token;
    public ArrayList<eventItemJson> registeredEvents;

    public GyanithUserJsonResponse(){}
    //Other Fields will be updated following the backend
}


class eventItemJson{
    public String id;
    public String name;
    public String des;
    public String rules;
    public String contact;
    public String img1;
    public String img2;
    public String cost;
    public String max_ptps;
    public String type;
    public String timestamp;

    public eventItemJson(){}
}
