package com.barebrains.gyanith20.Statics;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import java.security.PublicKey;
import java.util.List;

import javax.crypto.interfaces.PBEKey;

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
        long diff = currentTime - postTime;

        if (diff < minute)
            return "just now";
        else if (diff < hrs)
            return diff/minute + " mins ago";
        else if (diff < days)
            return diff/hrs + " hrs ago";
        else if (diff < 2*days)
            return "yesterday";
        else
            return diff/days + " days ago";
    }

}

