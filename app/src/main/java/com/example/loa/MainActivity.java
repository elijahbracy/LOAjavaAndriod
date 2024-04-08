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

    public void newGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        // just handle load flag with intent, create or load objects in gameActivity
        intent.putExtra("enterFlip", "true");
        startActivity(intent);
    }

    public void loadGame(View v) {
        // Call method to prompt the user to choose a file
        promptForFileName();
    }

    private void promptForFileName() {
        // Inflate the layout containing the Spinner view
        View view = LayoutInflater.from(this).inflate(R.layout.spinner_dropdown, null);
        fileNameSpinner = view.findViewById(R.id.fileSpinner);

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a File");
        builder.setView(view);

        // Set positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the selected file URI from the Spinner
                Uri selectedFileUri = getFileUriFromFileName(fileNameSpinner.getSelectedItem().toString());
                // Perform further actions, such as loading the game
                readFile(selectedFileUri);
            }
        });

        // Set negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel dialog
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Populate the Spinner with file names
        List<String> fileNames = getFileNamesFromDirectory();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fileNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileNameSpinner.setAdapter(adapter);
    }

    private List<String> getFileNamesFromDirectory() {
        List<String> fileNames = new ArrayList<>();
        // Get the directory path for downloads
        File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        //File downloadsDir = getExternalFilesDir(Environment.getExternalStorageDirectory().getPath());

        if (downloadsDir != null && downloadsDir.isDirectory()) {
            // List files in the downloads directory
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Add file names to the list
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    private Uri getFileUriFromFileName(String fileName) {
        // Get the directory path for downloads
        File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir != null && downloadsDir.isDirectory()) {
            // List files in the downloads directory
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Check if the filename matches the selected filename
                    if (file.getName().equals(fileName)) {
                        // Return the URI of the matching file
                        return Uri.fromFile(file);
                    }
                }
            }
        }
        return null; // Return null if file not found
    }



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





