package com.example.loa;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loa.Model.Tournament;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The main activity of the application.
 */
public class MainActivity extends AppCompatActivity {

    private Spinner fileNameSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Starts a new game.
     * @param v The view that was clicked.
     */
    public void newGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        // just handle load flag with intent, create or load objects in gameActivity
        intent.putExtra("enterFlip", "true");
        startActivity(intent);
    }

    /**
     * Loads a game from a file.
     * @param v The view that was clicked.
     */
    public void loadGame(View v) {
        // Call method to prompt the user to choose a file
        promptForFileName();
    }

    /**
     * Prompts the user to choose a file for loading a game.
     */
    private void promptForFileName() {
        // Create an intent to open the file picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only text files
        intent.setType("*/*");

        // Start the activity to select a file
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            if (data != null) {
                // Get the URI of the selected file
                Uri uri = data.getData();
                Log.d("uri", String.valueOf(uri));
                readFile(uri);
            }
        }
    }

    /**
     * Reads a file from the provided URI.
     * @param uri The URI of the file to read.
     */
    public void readFile(Uri uri) {
        // Access the file content using the provided URI
        try {
            // Get the InputStream from the ContentResolver
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            // Read data from the InputStream
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                inputStream.close();
            }

            // Pass the file content to the GameActivity
            String fileContent = stringBuilder.toString();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("fileContent", fileContent);
            startActivity(intent);
        } catch (IOException e) {
            // Error occurred while accessing the file
            e.printStackTrace();
            Toast.makeText(this, "Error accessing file", Toast.LENGTH_SHORT).show();
        }
    }
}
