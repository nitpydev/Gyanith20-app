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
import com.barebrains.gyanith20.models.EventItem;
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

        //Dummy till server updates
        json.regEventIds = new String[]{"5","4","6","23","21","22","20"};

        return new GyanithUser(json.gyid
        ,json.name
        ,json.usr
        ,json.email
        ,json.phno
        ,json.clg
        ,json.regEventIds
        ,token);
    }

    public static ArrayList<EventItem> jsonToEventItem(ArrayList<eventItemJson> jsons){
        ArrayList<EventItem> EventItems = new ArrayList<>(jsons.size());
        for (int i = 0; i < jsons.size();i++){
            eventItemJson itemJson = jsons.get(i);

           // EventItem item = new EventItem(itemJson.name,itemJson.timestamp,itemJson.id);
            //item.setType(itemJson.type);
            //item.setIconImgUrl(itemJson.img2);
            //EventItems.add(item);
        }

        return EventItems;
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
    public String[] regEventIds;

    public GyanithUserJsonResponse(){}
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
