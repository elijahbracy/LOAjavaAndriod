package com.example.loa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loa.Model.Board;
import com.example.loa.Model.Computer;
import com.example.loa.Model.Human;
import com.example.loa.Model.Round;
import com.example.loa.Model.Tournament;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements BoardView.OnMoveListener{

    private BoardView boardView;
    private Tournament tournament;

    private Human human;
    private Computer computer;
    private Board board;

    private Round round;

    public boolean isHumanTurn;

    private TextView playerTurnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        boardView = findViewById(R.id.board_view);
        boardView.setOnMoveListener(this);

        playerTurnText = findViewById(R.id.textView);




        int humanRoundsWon = getIntent().getIntExtra("humanRoundsWon", 0);
        int computerRoundsWon = getIntent().getIntExtra("computerRoundsWon", 0);


        boolean enterFlip = getIntent().getBooleanExtra("enterFlip", false);

        if (enterFlip) {
            Intent intent = new Intent(this, FlipActivity.class);
            startActivity(intent);
        }

        //initializeGame();

        human = new Human();
        computer = new Computer();
        round = new Round();

        Board board = new Board();

        isHumanTurn = getIntent().getBooleanExtra("flipResult", true);

        if (isHumanTurn) {
            round.setCurPlayer(human);
            round.setNextPlayer(computer);
        }
        else {
            round.setCurPlayer(computer);
            round.setNextPlayer(human);
        }

        Log.d("gameBoard", Arrays.toString(board.getBoard()));

        boardView.setBoard(board);
        boardView.setRound(round);

        updateTurn();
        Log.d("test", round.getCurPlayer().toString());
        Log.d("test", human.toString());
        Log.d("test", computer.toString());
    }

    public void updateTurn() {
        if (round.getCurPlayer() == human) {
            playerTurnText.setText("Human Turn");
        }
        else {
            playerTurnText.setText("Computer Turn");
        }
        Log.d("gameTurn", round.getCurPlayer().toString());
        playerTurnText.invalidate();
    }

    @Override
    public void onMoveMade(boolean isHumanTurn) {
        // Update the player turn text or any other data in the GameActivity
        if (isHumanTurn) {
            playerTurnText.setText("Human Turn");
        } else {
            playerTurnText.setText("Computer Turn");
        }
    }



    public AlertDialog buildFlip() {
        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog properties
        builder.setTitle("AlertDialog Title")
                .setMessage("This is a message for the user.")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        dialog.dismiss(); // Close the dialog
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle negative button click or dismiss the dialog
                        dialog.dismiss();
                    }
                });

        // Create AlertDialog
        return builder.create();
    }

    public void initializeGame() {

    }
}