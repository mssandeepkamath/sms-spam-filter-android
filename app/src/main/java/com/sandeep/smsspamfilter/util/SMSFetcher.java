package com.sandeep.smsspamfilter.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sandeep.smsspamfilter.model.Message;

import java.util.ArrayList;
import java.util.List;

public class SMSFetcher {
    public static List<Message> fetchSMS(Context context) {
        List<Message> smsList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String senderName = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                smsList.add(new Message(message,senderName,-1));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return smsList;
    }
}
