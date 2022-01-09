package com.iflytek.elpmobile.smartlearning;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dataDir = this.getExternalFilesDir(null);
        Log.d(TAG, "Data dir: " + dataDir.toString());

        File userDir = new File(dataDir, "iflytek/com.iflytek.elpmobile.smartlearning/user/");
        Log.d(TAG, "User dir: " + userDir.toString());
        if (!userDir.exists()) userDir.mkdirs();

        try {
            File userFile = new File(userDir, "user");
            Log.d(TAG, "User file: " + userFile.toString());
            if (!userFile.exists()) {
                userFile.createNewFile();

                PrintWriter writer = new PrintWriter(userFile);
                writer.write("{\"token\": \"token\"}");
                writer.close();
            }
        } catch (IOException e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            Log.e(TAG, stringWriter.toString());
        }
    }
}
