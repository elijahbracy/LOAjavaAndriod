package com.example.loa;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.Manifest;



import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loa.Model.Board;
import com.example.loa.Model.Computer;
import com.example.loa.Model.Human;
import com.example.loa.Model.Move;
import com.example.loa.Model.Round;
import com.example.loa.Model.Tournament;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GameActivity extends AppCompatActivity /*implements BoardView.OnMoveListener*/{

    private BoardView boardView;
    private Tournament tournament;

    private Human human;
    private Computer computer;
    private Board board;

    private Round round;

    private boolean enterFlip;

    private Stack<Move> moveStack;

    private Move currentMove;

    public boolean isHumanTurn;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1001;



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
        //boardView.setOnMoveListener(this);


        moveStack = new Stack<>();

        initializeGame();


        enterFlip = getIntent().getBooleanExtra("enterFlip", false);

        if (enterFlip) {
            Intent intent = new Intent(this, FlipActivity.class);
            startActivity(intent);
        }

        isHumanTurn = getIntent().getBooleanExtra("flipResult", true);

        if (isHumanTurn) {
            round.setCurPlayer(human);
            human.setColor('b');
            round.setNextPlayer(computer);
            computer.setColor('w');
        }
        else {
            round.setCurPlayer(computer);
            computer.setColor('b');
            round.setNextPlayer(human);
            human.setColor('w');
            computerMove();
        }

        Log.d("gameBoard", Arrays.toString(board.getBoard()));

        boardView.setBoard(board);
        boardView.setRound(round);
        boardView.setMoveStack(moveStack);

        List<Move> possibleMoves = board.getPossibleMoves('b');
        // Log the coordinates of each possible move
        for (Move move : possibleMoves) {
            Log.d("moves", "Move: " + move.toRankFileNotation());
        }

        //updateTurn();
    }

    public void help(View v) {
        // Get the list of possible moves from your game logic
        List<Move> possibleMoves = board.getPossibleMoves(human.getColor());

        // get best move and reason
        Pair<Move, String>bestMoveReason = human.strategize(board);

        // Build the help dialog with the list of possible moves
        AlertDialog helpDialog = buildHelp(possibleMoves, bestMoveReason);

        // ask boardView to highlight suggestion
        boardView.highlightBestMove(bestMoveReason.first);

        // Show the dialog
        helpDialog.show();
    }

    public void confirm(View v) {
        if ((human.getColor() == 'b' && moveStack.size() % 2 != 0) ||
                (human.getColor() == 'w' && moveStack.size() % 2 == 0)) {
            round.SwitchPlayers();
            computerMove();
        }
        else {
            // Create a dialog box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You must first make a move")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Dismiss the dialog box
                            dialog.dismiss();
                        }
                    });
            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public void showLog(View v) {
        // Create a StringBuilder to build the message content
        StringBuilder message = new StringBuilder("Move Log:\n");

        int turnNum = 1;
        for (int i = 0; i < moveStack.size(); i += 2) {
            // Get the moves for the current turn
            Move move1 = moveStack.get(i);
            Move move2 = null;
            if (i + 1 < moveStack.size()) {
                move2 = moveStack.get(i + 1);
            }

            // Append the moves to the message
            message.append(turnNum).append(". ");
            message.append(move1.toRankFileNotation());
            if (move2 != null) {
                message.append("        ").append(move2.toRankFileNotation());
            }
            message.append("\n");
            turnNum++;
        }


        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog properties
        builder.setTitle("Move Log")
                .setMessage(message.toString())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        dialog.dismiss(); // Close the dialog
                    }
                });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void undo(View v) {
        if ((human.getColor() == 'b' && moveStack.size() % 2 != 0) ||
                (human.getColor() == 'w' && moveStack.size() % 2 == 0)) {
            // Undo the last move by popping it from the stack
            Move lastMove = moveStack.pop();

            // Undo the move by making the inverse move
            board.makeMove(lastMove.getDestinationRow(),
                    lastMove.getDestinationCol(),
                    lastMove.getOriginRow(),
                    lastMove.getOriginCol(),
                    human.getColor());

            //reset the prevMove
            if (!moveStack.empty()) {
                boardView.setPrevMove(moveStack.peek());
            }
            else {
                boardView.setPrevMove(null);
            }


            // Update the view to reflect the undone move
            boardView.setBoard(board);
            boardView.resetMadeMoveHighlight();;
            boardView.invalidate();

        } else {
            // Show a message indicating that there are no moves to undo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Moves to Undo")
                    .setMessage("There are no moves to undo.")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public void quit(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit Game")
                .setMessage("Would you like to save the current game?")
                .setIcon(android.R.drawable.ic_media_pause)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Prompt user to enter a filename
                        promptForFileName();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Go back to the main activity without saving
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                    }
                })
                .show();
    }



    private void promptForFileName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edittext, null);
        builder.setView(dialogView);
        final EditText editText = dialogView.findViewById(R.id.editText);

        builder.setTitle("Save Game")
                .setMessage("Enter a filename:")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = editText.getText().toString().trim();

                        // Perform validation
                        if (isValidFileName(fileName)) {
                            saveGameToFile("filename.txt"); // ADD confimation

                            Intent intent = new Intent(GameActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Display an error message indicating invalid filename
                            Toast.makeText(getApplicationContext(), "Invalid filename. Please use alphanumeric characters, underscores, and periods.", Toast.LENGTH_SHORT).show();
                            promptForFileName();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Request focus on the EditText and show the soft keyboard
        editText.requestFocus();
    }

    // Function to validate filename
    private boolean isValidFileName(String fileName) {
        // Regular expression to match valid filename characters
        String regex = "^[a-zA-Z0-9_.]+$";
        // Return true if the filename matches the regex
        return fileName.matches(regex);
    }

    public void saveGameToFile(String filename) {
        // Create a StringBuilder to construct the content of the file
        StringBuilder content = new StringBuilder();

        // Append board state to content
        content.append("Board:\n");
        char[][] boardState = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                content.append(boardState[i][j]).append(" ");
            }
            content.append("\n");
        }
        content.append("\n");

        // Append human player state to content
        content.append("Human:\n");
        content.append("Rounds won: ").append(human.getRoundsWon()).append("\n");
        content.append("Score: ").append(human.getScore()).append("\n\n");

        // Append computer player state to content
        content.append("Computer:\n");
        content.append("Rounds won: ").append(computer.getRoundsWon()).append("\n");
        content.append("Score: ").append(computer.getScore()).append("\n\n");

        // Append next player and color to content
        content.append("Next player: ").append(round.getCurPlayer() instanceof Human ? "Human" : "Computer").append("\n");
        content.append("Color: ").append(round.getCurPlayer().getColor() == 'w' ? "White" : "Black").append("\n");

        // Check if the filename already ends with .txt
        if (!filename.toLowerCase().endsWith(".txt")) {
            // If not, append .txt extension to the filename
            filename += ".txt";
        }

        // Get application context using a ContextWrapper
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        // Specify the directory path to your Downloads folder
        File directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        // Create a File object with the specified filename in the Downloads directory
        File file = new File(directory, filename);

        // Write content to the file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(content.toString());
            osw.flush();
            osw.close();
            fos.close();

            new AlertDialog.Builder(this)
                    .setTitle("Success")
                    .setMessage("File write successful..")
                    .setPositiveButton("OK", null)
                    .show();

            Log.d("FilePath", "Saved file path: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadGameFromFile(String filename) {
        // Create a File object representing the file to be read
        File file = new File(this.getExternalFilesDir(null), filename);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read the content of the file line by line
            String line;
            while ((line = reader.readLine()) != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void computerMove() {
        Pair<Move, String> bestMoveReason = computer.strategize(board);

        Move computerMove = bestMoveReason.first;
        board.makeMove(computerMove.getOriginRow(),
                        computerMove.getOriginCol(),
                        computerMove.getDestinationRow(),
                        computerMove.getDestinationCol(),
                        computer.getColor());

        boardView.setBoard(board);

        boardView.setPrevMove(bestMoveReason.first);

        boardView.resetMadeMoveHighlight();

        moveStack.push(bestMoveReason.first);

        round.SwitchPlayers();

        boardView.invalidate();

    }





    /*
    @Override
    public void onMoveMade(boolean isHumanTurn) {
        // Update the player turn text or any other data in the GameActivity
        if (isHumanTurn) {
            humanData.setTextColor(Color.GREEN);
            computerData.setTextColor(Color.BLACK);
        } else {
            humanData.setTextColor(Color.BLACK);
            computerData.setTextColor(Color.GREEN);
        }
    }
     */



    public AlertDialog buildHelp(List<Move> possibleMoves, Pair<Move, String> moveAndReason) {
        // Create AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog properties
        builder.setTitle("Possible Moves")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        dialog.dismiss(); // Close the dialog
                    }
                });

        // Create a StringBuilder to build the message content
        StringBuilder message = new StringBuilder("List of possible moves:\n");
        for (Move move : possibleMoves) {
            message.append("Move: ").append(move.toRankFileNotation()).append("\n");
        }

        Move suggestedMove = moveAndReason.first;

        // Get the best move and reason using strategize method
        message.append("\nSuggested move: ").append(suggestedMove.toRankFileNotation())
        .append(moveAndReason.second).append("\n");

        // Set the message content
        builder.setMessage(message.toString());

        // Create AlertDialog
        return builder.create();
    }


    public void initializeGame() {

        human = new Human();
        computer = new Computer();
        round = new Round();
        board = new Board();



        String gameState = getIntent().getStringExtra("fileContent");
        if (gameState != null) {
            // Split the gameState into separate lines
            String[] lines = gameState.split("\n");
            char[][] inBoard = new char[8][8];

            // Process each line
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("Board:")) {
                    // Process board information
                    Log.d("BoardInfo", "Board information found");

                    // Process the next 8 lines (assuming each line represents a row)
                    for (int j = i + 1; j < i + 9; j++) {
                        if (j < lines.length) {
                            String row = lines[j].trim(); // Trim leading/trailing whitespace
                            Log.d("BoardRow", row); // Log each row of the board
                            for (int k = 0; k < 8; k++) { // Iterate over columns
                                inBoard[j - (i + 1)][k] = row.charAt(k * 2); // Adjust index to skip spaces
                                Log.d("rowInfo", "board[" + (j - (i + 1)) + "][" + k + "]=" + String.valueOf(row.charAt(k*2)));
                            }
                            board.setBoard(inBoard);
                        } else {
                            Log.d("BoardInfo", "Incomplete board information"); // Log if there are insufficient lines
                            break; // Exit the loop if there are not enough lines for the board
                        }
                    }

                    // Skip the next 8 lines as they are already processed
                    i += 8;
                } else if (lines[i].startsWith("Human:")) {
                    // Process human information
                    i++;
                    Log.d("humanInfo", String.valueOf(lines[i].charAt(12)));
                    human.setRoundsWon((int)lines[i].charAt(12));
                    i++;
                    human.setRoundsWon((int)lines[i].charAt(7));
                } else if (lines[i].startsWith("Computer:")) {
                    // Process computer information
                    i++;
                    computer.setRoundsWon((int)lines[i].charAt(12));
                    i++;
                    computer.setRoundsWon((int)lines[i].charAt(7));
                } else if (lines[i].startsWith("Next player:")) {
                    // Process next player information
                    String curPlayer = lines[i].substring(13);
                    Log.d("nextplayer", curPlayer);
                    if (curPlayer.equals("Human")) {
                        round.setCurPlayer(human);
                        round.setNextPlayer(computer);
                    } else {
                        round.setCurPlayer(computer);
                        round.setNextPlayer(human);
                    }
                    i++;
                    String color = lines[i].substring(7);
                    if (color.equals("Black")) {
                        round.getCurPlayer().setColor('b');
                        round.getNextPlayer().setColor('w');
                    } else {
                        round.getCurPlayer().setColor('w');
                        round.getNextPlayer().setColor('b');
                    }
                }
            }
        } else {
            Log.d("load", "null");
        }


    }
}