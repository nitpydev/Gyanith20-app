package com.barebrains.gyanith20.Statics;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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

