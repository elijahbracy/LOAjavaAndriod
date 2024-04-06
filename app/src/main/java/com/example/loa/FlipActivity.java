package com.example.loa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flip);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button heads = findViewById(R.id.headsBtn);
        Button tails = findViewById(R.id.tailsBtn);



        heads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFlip(v, true);
            }
        });

        tails.setOnClickListener(new View.OnClickListener() {
            @ Override
            public void onClick(View v) {
                handleFlip(v, false);
            }
        });
    }


    public void handleFlip(View v, boolean userInput) {
        Log.d("flip", String.valueOf(userInput));

        // Simulate the coin flip
        boolean flipResult = Math.random() < 0.5; // heads == true, tails == false

        if (flipResult == userInput) {
            Log.d("coinToss", "win");
            AlertDialog winDialog = buildResult(true);
            winDialog.show();
        } else {
            Log.d("coinToss", "lose");
            AlertDialog loseDialog = buildResult(false);
            loseDialog.show();
        }
    }

    public AlertDialog buildResult(boolean result) {
        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog properties
        builder.setTitle("Coin Toss Result");
                if (result) {
                    builder.setMessage("You won the coin toss!") ;
                }
                else {
                    builder.setMessage("You lost the coin toss!") ;
                }
                builder.setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        dialog.dismiss(); // Close the dialog
                        Intent intent = new Intent(FlipActivity.this, GameActivity.class);
                        intent.putExtra("flipResult", result);
                        intent.putExtra("enterFlip", false );
                        startActivity(intent);
                    }
                });
        // Create AlertDialog
        return builder.create();
    }
}