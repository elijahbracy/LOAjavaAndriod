package com.example.loa;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

/**
 * The coin flip activity of the application.
 */
public class FlipActivity extends AppCompatActivity {

    /**
     * Sets up the activity when it is created, including enabling edge-to-edge display and initializing UI elements.
     * @param savedInstanceState The saved instance state of the activity.
     */
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


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose heads or tails to decide who goes first.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog when OK is clicked
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();




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

    /**
     * Handles the user's selection of heads or tails and initiates the coin flip process.
     * @param v The view that triggered the click event.
     * @param userInput A boolean indicating whether the user chose heads (true) or tails (false).
     */
    public void handleFlip(View v, boolean userInput) {
        Log.d("flip", String.valueOf(userInput));

        // Simulate the coin flip
        // heads == true, tails == false
        boolean flipResult = Math.random() < 0.5;

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

    /**
     * Builds and displays an AlertDialog to show the result of the coin toss.
     * @param result A boolean indicating whether the user won (true) or lost (false) the coin toss.
     * @return The AlertDialog containing the result message and an OK button.
     */
    public AlertDialog buildResult(boolean result) {
        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog properties
        builder.setTitle("Coin Toss Result");
                if (result) {
                    builder.setMessage("You won the coin toss!\nYou will play first.\nYou will play black.") ;
                }
                else {
                    builder.setMessage("You lost the coin toss!\nComputer will play first.\nYou will play white.") ;
                }
                builder.setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        dialog.dismiss(); // Close the dialog
                        Intent intent = new Intent(FlipActivity.this, GameActivity.class);
                        String resultExtra = result ? "win" : "lose";
                        String gameData = getIntent().getStringExtra("fileContent");
                        if (gameData != null) {
                            Log.d("flipTest", gameData);
                        }
                        intent.putExtra("fileContent", gameData);
                        intent.putExtra("flipResult", resultExtra);
                        startActivity(intent);
                    }
                });
        // Create AlertDialog
        return builder.create();
    }
}