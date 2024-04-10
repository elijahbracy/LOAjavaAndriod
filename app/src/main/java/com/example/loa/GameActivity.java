package com.example.loa;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Stack;

/**
 * The game activity of the application.
 */
public class GameActivity extends AppCompatActivity {

    private BoardView boardView;
    private Human human;
    private Computer computer;
    private Board board;
    private Round round;
    private String enterFlip;
    private String flipResult;
    private Stack<Move> moveStack;
    private boolean firstMove;
    private String gameState;


    /**
     Initializes the activity, sets up edge-to-edge display, and initializes the game board.
     @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
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

        moveStack = new Stack<>();

        enterFlip = getIntent().getStringExtra("enterFlip");

        flipResult = getIntent().getStringExtra("flipResult");

        initializeGame();

        if (enterFlip != null) {
            Intent intent = new Intent(this, FlipActivity.class);

            intent.putExtra("fileContent", gameState);

            startActivity(intent);
        }

        if (flipResult != null) {
            if (flipResult.equals("win")) {
                round.setCurPlayer(human);
                human.setColor('b');
                round.setNextPlayer(computer);
                computer.setColor('w');
            }
            else if (flipResult.equals("lose")){
                round.setCurPlayer(computer);
                computer.setColor('b');
                round.setNextPlayer(human);
                human.setColor('w');
            }
        }

        boardView.setBoard(board);
        boardView.setRound(round);
        boardView.setMoveStack(moveStack);

        if (round.getCurPlayer() instanceof Computer) {
            computerMove();
        }
    }

    /**
     Provides assistance to the player by suggesting a move and displaying possible moves.
     @param v The View object that was clicked to trigger the help function.
     */
    public void help(View v) {
        if (!boardView.getMoveMade()) {
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
        } else {
            // Show dialog box indicating that the user needs to undo the move first
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("First undo move to receive help")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Close the dialog
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     Retrieves information about the result of the current round.
     @return A string containing information about the round outcome and current standings.
     */
    public String getRoundOverInfo() {
        String info = "";
        String winner;
        int score;
        if (board.countGroups(round.getCurPlayer().getColor()) == 1) {
            if (round.getCurPlayer() instanceof Human) {
                winner = "Human";
                score = board.countPieces(human.getColor()) - board.countPieces(computer.getColor());
                human.setScore(human.getScore() + score);
                human.setRoundsWon(human.getRoundsWon() + 1);
            } else {
                winner = "Computer";
                score = board.countPieces(computer.getColor()) - board.countPieces(human.getColor());
                computer.setScore(computer.getScore() + score);
                computer.setRoundsWon(computer.getRoundsWon() + 1);
            }
        } else {
            if (round.getNextPlayer() instanceof Human) {
                winner = "Human";
                score = board.countPieces(human.getColor()) - board.countPieces(computer.getColor());
                human.setScore(human.getScore() + score);
                human.setRoundsWon(human.getRoundsWon() + 1);
            } else {
                winner = "Computer";
                score = board.countPieces(computer.getColor()) - board.countPieces(human.getColor());
                computer.setScore(computer.getScore() + score);
                computer.setRoundsWon(computer.getRoundsWon() + 1);
            }
        }

        info += winner + " won the round!\n";
        if (score == 1) {
            info += winner + " scored " + score + " point.\n\n";
        } else {
            info += winner + " scored " + score + " points.\n\n";
        }

        info += "Current Standings:\n\n";
        info += "Human:\n";
        info += "Rounds won: " + human.getRoundsWon() + "\n";
        info += "Current Tournament Score: " + human.getScore() + "\n\n";
        info += "Computer:\n";
        info += "Rounds won: " + computer.getRoundsWon() + "\n";
        info += "Current Tournament Score: " + computer.getScore();



        return info;
    }

    /**
     Confirms the move made by the player and switches players if necessary. Displays a dialog if no move has been made.
     @param v The View object that was clicked to trigger the confirm function.
     */
    public void confirm(View v) {
        // check for win
        if (board.isGameOver()) {
            displayWinnerDialog();
            return;
        }
        if ((firstMove && moveStack.size() % 2 != 0) ||
                (!firstMove && moveStack.size() % 2 == 0)) {
            round.SwitchPlayers();
            boardView.setMoveMade(false);
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

    /**
     Displays a dialog showing the move log of the game.
     @param v The View object that was clicked to trigger the showLog function.
     */
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

    /**
     Undoes the last move made by the player if possible, updating the game state accordingly.
     @param v The View object that was clicked to trigger the undo function.
     */
    public void undo(View v) {
        if ((firstMove && moveStack.size() % 2 != 0) ||
                (!firstMove && moveStack.size() % 2 == 0)) {
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

            // set moveMade to false
            boardView.setMoveMade(false);


            // Update the view to reflect the undone move
            boardView.setBoard(board);
            boardView.resetSelectedTile();;
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

    /**
     Quits the game, prompting the player to save the current game state if no move is in progress.
     @param v The View object that was clicked to trigger the quit function.
     */
    public void quit(View v) {
        if (!boardView.getMoveMade()) {
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
        } else {
            // Show dialog box indicating that the user needs to undo the move first
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("First confirm or undo move in progress to quit game")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Close the dialog
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     Displays the current round information in an AlertDialog.
     @param v The View object that was clicked to trigger the displayInfo function.
     */
    public void displayInfo(View v) {
        // Get the current round information (replace this with your actual logic)
        String roundInfo = getCurrentRoundInfo();

        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set dialog properties
        builder.setTitle("Round Information")
                .setMessage(roundInfo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle OK button click if needed
                    }
                });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     Displays a dialog box showing the winner of the round and offers options to play again or exit the game.
     */
    public void displayWinnerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Round Results");
        builder.setMessage(getRoundOverInfo())
                .setCancelable(false)
                .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog box
                        dialog.dismiss();
                        // Add code here to start a new round or game
                        startNewRoundOrGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Create a dialog to display tournament results
                        AlertDialog.Builder resultDialogBuilder = new AlertDialog.Builder(GameActivity.this);
                        resultDialogBuilder.setTitle("Tournament Results");

                        // Append tournament results to the dialog message
                        StringBuilder results = new StringBuilder();
                        results.append("Human Rounds Won: ").append(human.getRoundsWon()).append("\n");
                        results.append("Human Score: ").append(human.getScore()).append("\n");
                        results.append("\nComputer Rounds Won: ").append(computer.getRoundsWon()).append("\n");
                        results.append("Computer Score: ").append(computer.getScore()).append("\n");
                        resultDialogBuilder.setMessage(results.toString())
                                .setCancelable(false);

                        // Add OK button to dismiss the dialog
                        resultDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish(); // Exit the game or activity
                            }
                        });

                        // Show the result dialog
                        AlertDialog resultDialog = resultDialogBuilder.create();
                        resultDialog.show();
                    }
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     Starts a new round or game, resetting the board and updating player states.
     */
    private void startNewRoundOrGame() {
        String content = "";

        // Reset the board for a new game
        board.resetBoard();

        // Append board state to content
        content += "Board:\n";
        char[][] boardState = board.getBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                content += boardState[i][j] + " ";
            }
            content += "\n";
        }
        content += "\n";

        // Append human player state to content
        content += "Human:\n";
        content += "Rounds won: " + human.getRoundsWon() + "\n";
        content += "Score: " + human.getScore() + "\n\n";

        // Append computer player state to content
        content += "Computer:\n";
        content += "Rounds won: " + computer.getRoundsWon() + "\n";
        content += "Score: " + computer.getScore() + "\n\n";

        // Append next player and color to content
        content += "Next player: " + (human.getRoundsWon() > computer.getRoundsWon() ? "Human" : "Computer") + "\n";
        content += "Color: " + "Black" + "\n";

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("fileContent", content);
        Log.d("startNewRoundOrGame", "File content: " + content);
        if (human.getRoundsWon() == computer.getRoundsWon()) {
            intent.putExtra("enterFlip", "true");
        }
        startActivity(intent);

    }

    /**
     Retrieves and returns information about the current round.
     @return A string containing information about the current round.
     */
    private String getCurrentRoundInfo() {
        String info = "";
        String curPlayer;
        if (round.getCurPlayer() instanceof Human) {
            curPlayer = "Human";
        } else {
            curPlayer = "Computer";
        }

        char curPlayerColorChar = round.getCurPlayer().getColor();
        String curPlayerColor = String.valueOf(curPlayerColorChar);

        info += "Current Player: " + curPlayer;
        info += " (" + curPlayerColor + ")\n\n";
        info += "Human:\n";
        info += "Rounds won: " + human.getRoundsWon() + "\n";
        Log.d("humanInfo", String.valueOf(human.getRoundsWon()));
        info += "Current Round Score: " + (board.countPieces(human.getColor()) - board.countPieces(computer.getColor())) + "\n";
        info += "Current Tournament Score: " + human.getScore() + "\n\n";
        info += "Computer:\n";
        info += "Rounds won: " + computer.getRoundsWon() + "\n";
        Log.d("humanInfo", String.valueOf(computer.getRoundsWon()));
        info += "Current Round Score: " + (board.countPieces(computer.getColor()) - board.countPieces(human.getColor())) + "\n";
        info += "Current Tournament Score: " + computer.getScore();

        return info;
    }


    /**
     Prompts the user to enter a filename for saving the game state.
     */
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
                            saveGameToFile(fileName); // ADD confimation

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

    /**
     Validates a filename to ensure it contains only alphanumeric characters, underscores, and periods.
     @param fileName The filename to be validated.
     @return true if the filename is valid, false otherwise.
     */
    private boolean isValidFileName(String fileName) {
        // Regular expression to match valid filename characters
        String regex = "^[a-zA-Z0-9_.]+$";
        // Return true if the filename matches the regex
        return fileName.matches(regex);
    }

    /**
     Saves the current game state to a file with the specified filename.
     @param filename The filename to save the game state to.
     */
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

            // Display a toast indicating success
            Toast.makeText(this, "File write successful.", Toast.LENGTH_SHORT).show();

            Log.d("FilePath", "Saved file path: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     Loads a game state from the specified file.
     @param filename The filename from which to load the game state.
     */
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


    /**
     Initiates the computer's move by strategizing the best move, making it on the board, and updating the UI.
     */
    public void computerMove() {
        Pair<Move, String> bestMoveReason = computer.strategize(board);

        Move computerMove = bestMoveReason.first;

        // Display dialog with computer's move and reason
        displayComputerMoveDialog(computerMove, bestMoveReason.second );

        board.makeMove(computerMove.getOriginRow(),
                computerMove.getOriginCol(),
                computerMove.getDestinationRow(),
                computerMove.getDestinationCol(),
                computer.getColor());

        boardView.setBoard(board);

        boardView.setPrevMove(computerMove);

        boardView.resetSelectedTile();

        moveStack.push(computerMove);

        round.SwitchPlayers();

        // Check for win after the computer makes its move
        if (board.isGameOver()) {
            displayWinnerDialog();
            return;
        }

        boardView.invalidate();
    }

    /**
     Displays a dialog showing the computer's move and the reason behind it.
     @param computerMove The move made by the computer.
     @param reason The reason behind the move.
     */
    private void displayComputerMoveDialog(Move computerMove, String reason) {
        List<Move> possibleMoves = board.getPossibleMoves(computer.getColor());

        StringBuilder message = new StringBuilder();
        message.append(computerMove.toRankFileNotation()).append("\n\nReason: ").append(reason);

        // Append all possible moves to the message content
        message.append("\n\nPossible Moves:\n");
        for (Move move : possibleMoves) {
            message.append(move.toRankFileNotation()).append("\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Computer's Move")
                .setMessage(message.toString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }




    /**
     Builds an AlertDialog to display the list of possible moves and the suggested move.
     @param possibleMoves The list of possible moves.
     @param moveAndReason The suggested move and its reason.
     @return The built AlertDialog.
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

    /**
     Initializes the game by creating instances of players, round, and board, and loads the game state if provided.
     */
    public void initializeGame() {
        // Create instances of human, computer, round, and board
        human = new Human();
        computer = new Computer();
        round = new Round();
        board = new Board();

        // Get the game state from intent extras
        gameState = getIntent().getStringExtra("fileContent");

        // If game state is provided, load the game state
        if (gameState != null) {
            // Split the gameState into separate lines
            String[] lines = gameState.split("\n");
            char[][] inBoard = new char[8][8];

            // Process each line
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith("Board:")) {
                    // Process board information
                    Log.d("BoardInfo", "Board information found");

                    // Process the next 8 lines
                    for (int j = i + 1; j < i + 9; j++) {
                        if (j < lines.length) {
                            // Trim leading/trailing whitespace
                            String row = lines[j].trim();
                            // Iterate over columns
                            for (int k = 0; k < 8; k++) {
                                // Adjust index to skip spaces
                                inBoard[j - (i + 1)][k] = row.charAt(k * 2);
                                Log.d("rowInfo", "board[" + (j - (i + 1)) + "][" + k + "]=" + String.valueOf(row.charAt(k*2)));
                            }
                            board.setBoard(inBoard);
                        } else {
                            // Exit the loop if there are not enough lines for the board
                            break;
                        }
                    }

                    // Skip the next 8 lines as they are already processed
                    i += 8;
                } else if (lines[i].startsWith("Human:")) {
                    // Process human information
                    i++;
                    char charValue = lines[i].charAt(12);
                    int intValue = Character.getNumericValue(charValue);
                    human.setRoundsWon(intValue);
                    i++;
                    charValue = lines[i].charAt(7);
                    intValue = Character.getNumericValue(charValue);
                    human.setScore(intValue);
                } else if (lines[i].startsWith("Computer:")) {
                    // Process computer information
                    i++;
                    char charValue = lines[i].charAt(12);
                    int intValue = Character.getNumericValue(charValue);
                    computer.setRoundsWon(intValue);
                    i++;
                    charValue = lines[i].charAt(7);
                    intValue = Character.getNumericValue(charValue);
                    computer.setScore(intValue);
                } else if (lines[i].startsWith("Next player:")) {
                    // Process next player information
                    String curPlayer = lines[i].substring(13);
                    if (curPlayer.equals("Human")) {
                        round.setCurPlayer(human);
                        round.setNextPlayer(computer);
                        firstMove = true;
                    } else {
                        round.setCurPlayer(computer);
                        round.setNextPlayer(human);
                        firstMove = false;
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
            // If game state is not provided, set the first move based on flip result
            if (flipResult != null) {
                firstMove = flipResult.equals("win");
            }
        }
    }

}