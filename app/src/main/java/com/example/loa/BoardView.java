package com.example.loa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.loa.Model.Board;
import com.example.loa.Model.Move;
import com.example.loa.Model.Round;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Custom View class representing the game board.
 */
public class BoardView extends View implements View.OnTouchListener {

    private Board board;
    private Round round;
    private Stack<Move> moveStack;
    private Paint paint;
    private int left;
    private int top;
    private int cellWidth;
    private int cellHeight;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int suggestedDestRow = -1;
    private int suggestedDestCol = -1;

    private int suggestedOriginRow = -1;
    private int suggestedOriginCol = -1;

    private Move prevMove = null;

    private boolean moveMade = false;

    private List<Move> validMoves = new ArrayList<>();

    /**
     * Overrides the onDraw method to draw the game board and pieces on the canvas.
     * @param canvas The canvas on which the game board is drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewWidth = getWidth() - 150;
        int viewHeight = getHeight() - 150;

        int numRows = 8;
        int numCols = 8;

        int boardSize = Math.min(viewWidth, viewHeight); // Ensure the board fits within the view

        // Calculate cell width and height
        cellWidth = boardSize / numCols;
        cellHeight = boardSize / numRows;

        // Calculate the actual width and height of the board based on the cell size
        int boardWidth = cellWidth * numCols;
        int boardHeight = cellHeight * numRows;

        // Calculate the starting point (left and top coordinates) to center the board
        int leftMargin = 100; // Adjust this value to set the margin for the numbers
        left = (viewWidth - boardWidth) / 2 + leftMargin;
        top = (viewHeight - boardHeight) / 2;

        // Draw the board
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                // Calculate the coordinates for the current cell
                int cellLeft = left + col * cellWidth;
                int cellTop = top + row * cellHeight;
                int cellRight = cellLeft + cellWidth;
                int cellBottom = cellTop + cellHeight;

                // Alternate colors for each cell
                if ((row + col) % 2 == 0) {
                    paint.setColor(Color.parseColor("#ebecd0")); // green
                } else {
                    paint.setColor(Color.parseColor("#779556")); // Slightly darker brown
                }

                // Draw the cell
                canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, paint);
            }
        }


        // Draw highlighting for the selected piece
        if (selectedRow != -1 && selectedCol != -1) {
            int highlightColor = Color.parseColor("#eb4034"); // green color for highlighting
            int highlightLeft = left + selectedCol * cellWidth;
            int highlightTop = top + selectedRow * cellHeight;
            int highlightRight = highlightLeft + cellWidth;
            int highlightBottom = highlightTop + cellHeight;
            paint.setColor(highlightColor);
            canvas.drawRect(highlightLeft, highlightTop, highlightRight, highlightBottom, paint);
        }

        //Draw highlighting for previous move
        if (prevMove != null) {
            int lighterHighlightColor = Color.parseColor("#f6f879"); // khaki color for highlighting
            int prevMoveOriginRow = prevMove.getOriginRow();
            int prevMoveOriginCol = prevMove.getOriginCol();
            int prevMoveDestRow = prevMove.getDestinationRow();
            int prevMoveDestCol = prevMove.getDestinationCol();

            // Highlight the origin square of the previous move
            int originHighlightLeft = left + prevMoveOriginCol * cellWidth;
            int originHighlightTop = top + prevMoveOriginRow * cellHeight;
            int originHighlightRight = originHighlightLeft + cellWidth;
            int originHighlightBottom = originHighlightTop + cellHeight;
            paint.setColor(lighterHighlightColor);
            canvas.drawRect(originHighlightLeft, originHighlightTop, originHighlightRight, originHighlightBottom, paint);

            lighterHighlightColor = Color.parseColor("#b9cc36");
            paint.setColor(lighterHighlightColor);

            // Highlight the destination square of the previous move
            int destHighlightLeft = left + prevMoveDestCol * cellWidth;
            int destHighlightTop = top + prevMoveDestRow * cellHeight;
            int destHighlightRight = destHighlightLeft + cellWidth;
            int destHighlightBottom = destHighlightTop + cellHeight;
            canvas.drawRect(destHighlightLeft, destHighlightTop, destHighlightRight, destHighlightBottom, paint);
        }

        // Draw highlighting for the valid move destinations
        for (Move move : validMoves) {
            int destRow = move.getDestinationRow();
            int destCol = move.getDestinationCol();
            int highlightLeft = left + destCol * cellWidth;
            int highlightTop = top + destRow * cellHeight;
            int highlightRight = highlightLeft + cellWidth;
            int highlightBottom = highlightTop + cellHeight;
            paint.setColor(Color.parseColor("#bc4749")); // red
            canvas.drawRect(highlightLeft, highlightTop, highlightRight, highlightBottom, paint);
        }


        // Draw highlighting for the suggested move
        if (suggestedOriginRow != -1 && suggestedOriginCol != -1) {
            int lighterHighlightColor = Color.parseColor("#428af5");
            int highlightLeft = left + suggestedOriginCol * cellWidth;
            int highlightTop = top + suggestedOriginRow * cellHeight;
            int highlightRight = highlightLeft + cellWidth;
            int highlightBottom = highlightTop + cellHeight;
            paint.setColor(lighterHighlightColor);
            canvas.drawRect(highlightLeft, highlightTop, highlightRight, highlightBottom, paint);
        }

        if (suggestedDestRow != -1 && suggestedDestCol != -1) {
            int lighterHighlightColor = Color.parseColor("#428af5");
            int highlightLeft = left + suggestedDestCol * cellWidth;
            int highlightTop = top + suggestedDestRow * cellHeight;
            int highlightRight = highlightLeft + cellWidth;
            int highlightBottom = highlightTop + cellHeight;
            paint.setColor(lighterHighlightColor);
            canvas.drawRect(highlightLeft, highlightTop, highlightRight, highlightBottom, paint);
        }


        // Draw black outline around the board
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3); // Adjust the thickness of the outline as needed
        canvas.drawRect(left, top, left + boardWidth, top + boardHeight, paint);



        // draw pieces on board
        if (board.getBoard() != null) {
            Log.d("board", Arrays.toString(board.getBoard()));
            for (int row = 0; row < board.getBoard().length; row++) {
                for (int col = 0; col < board.getBoard()[row].length; col++) {
                    char pieceType = board.getBoard()[row][col];
                    if (pieceType != 'x') {
                        // Calculate the center coordinates of the cell
                        int cellLeft = left + col * cellWidth;
                        int cellTop = top + row * cellHeight;
                        int centerX = cellLeft + cellWidth / 2;
                        int centerY = cellTop + cellHeight / 2;
                        int radius = Math.min(cellWidth, cellHeight) / 2 - 10;

                        // Draw the black outline circle
                        paint.setColor(Color.BLACK);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(6);
                        canvas.drawCircle(centerX, centerY, radius, paint); // Adjust the radius for the outline

                        // Draw the filled circle for the piece
                        int pieceColor = pieceType == 'w' ? Color.parseColor("#f8f8f8") : Color.parseColor("#565352");
                        paint.setColor(pieceColor);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(centerX, centerY, radius, paint);
                    }
                }
            }
        }


        // Draw rank labels (numbers) along the left edge
        for (int i = 0; i < numRows; i++) {
            String rankLabel = Integer.toString(8 - i);
            drawTextCentered(canvas, rankLabel, left - 50, top + (i + 1) * cellHeight - cellHeight / 2, paint);
        }

        // Draw file labels (letters) along the bottom edge
        for (int i = 0; i < numCols; i++) {
            String fileLabel = Character.toString((char) ('A' + i));
            drawTextCentered(canvas, fileLabel, left + (i + 1) * cellWidth - cellWidth / 2, top + boardHeight + 50, paint);
        }
    }

    /**
     * Helper method to draw centered text on the canvas.
     * @param canvas The canvas on which to draw the text.
     * @param text The text to be drawn.
     * @param x The x-coordinate of the center of the text.
     * @param y The y-coordinate of the center of the text.
     * @param paint The Paint object specifying the text style and color.
     */
    private void drawTextCentered(Canvas canvas, String text, float x, float y, Paint paint) {
        paint.setColor(Color.WHITE); // Set the text color to black
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40); // Set the text size here
        canvas.drawText(text, x, y, paint);
    }

    /**
     * Constructor for the BoardView class.
     * @param context The context in which the BoardView is created.
     */
    public BoardView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor for the BoardView class.
     * @param context The context in which the BoardView is created.
     * @param attrs The attributes of the AttributeSet.
     */
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor for the BoardView class.
     * @param context The context in which the BoardView is created.
     * @param attrs The attributes of the AttributeSet.
     * @param defStyleAttr The default style attribute.
     */
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the BoardView by setting up the paint object and touch listener.
     */
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        setOnTouchListener((OnTouchListener) this);
    }

    /**
     * Overrides the onTouch method to handle touch events on the BoardView.
     * @param v The view that received the touch event.
     * @param event The MotionEvent object containing information about the touch event.
     * @return True if the event was consumed, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Ensure the touch event occurred within the BoardView
        if (v != this) {
            return false;
        }

        // Get the action associated with this event
        int action = event.getAction();

        // Check the action type
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Touch down event
                if (!moveMade) {
                    handleTouchDown(event);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                // Ignore move events after touch down
                break;
            case MotionEvent.ACTION_UP:
                // Touch up event
                if (!moveMade) {
                    handleTouchUp(event);
                }
                break;
        }

        // Return true to indicate that the event has been consumed
        return true;
    }

    /**
     * Handles the touch down event on the BoardView.
     * @param event The MotionEvent object containing information about the touch event.
     */
    private void handleTouchDown(MotionEvent event) {
        // Get the touch coordinates relative to the BoardView
        float xDown = event.getX();
        float yDown = event.getY();
        // Calculate the row and column based on the touch coordinates
        int rowDown = (int) ((yDown - top) / cellHeight);
        int colDown = (int) ((xDown - left) / cellWidth);
        Log.d("TouchEvent", "Touch down at row: " + rowDown + ", col: " + colDown);

        selectedRow = rowDown;
        selectedCol = colDown;

        // Check if the touch coordinates are within the board bounds
        if (rowDown >= 0 && rowDown < 8 && colDown >= 0 && colDown < 8) {
            // Check if the selected position contains a piece of the current player's color
            if (board.getBoard()[rowDown][colDown] == round.getCurPlayer().getColor()) {
                resetSuggestedMove();
                addValidMoves(selectedRow, selectedCol);
                invalidate();
            }
        } else {
            Log.d("TouchEvent", "Touch down outside board bounds");
        }
    }

    /**
     * Handles the touch up event on the BoardView.
     * @param event The MotionEvent object containing information about the touch event.
     */
    private void handleTouchUp(MotionEvent event) {
        // Get the touch coordinates relative to the BoardView
        float xUp = event.getX();
        float yUp = event.getY();
        // Calculate the row and column based on the touch coordinates
        int rowUp = (int) ((yUp - top) / cellHeight);
        int colUp = (int) ((xUp - left) / cellWidth);
        Log.d("TouchEvent", "Touch up at row: " + rowUp + ", col: " + colUp);


        // Check if a piece is selected and the touch up is at a valid position
        // Check if the touch up coordinates are within the board bounds
        if (rowUp >= 0 && rowUp < 8 && colUp >= 0 && colUp < 8) {
            Log.d("TouchEvent", "Touch up at row: " + rowUp + ", col: " + colUp);
            // Check if a piece is selected and the touch up is at a valid position
            if (selectedRow != -1 && selectedCol != -1) {
                Log.d("isValid", "SelectedRow: " + selectedRow + ", SelectedCol: " + selectedCol + ", RowUp: " + rowUp + ", ColUp: " + colUp);
                if (board.isValid(selectedRow, selectedCol, rowUp, colUp, round.getCurPlayer().getColor())) {
                    board.makeMove(selectedRow, selectedCol, rowUp, colUp, round.getCurPlayer().getColor());
                    // if move is valid updated moved coordinates for highlighting on redraw
                    invalidate();
                    Move move = new Move(selectedRow, selectedCol, rowUp, colUp);
                    moveStack.push(move);
                    prevMove = move;
                    validMoves.clear();
                    resetSuggestedMove();
                    moveMade = true;
                }
            }
        } else {
            Log.d("TouchEvent", "Touch up outside board bounds");
        }
    }

    /**
     * Sets the current board state.
     * @param board The Board object representing the current state of the board.
     */
    public void setBoard(Board board) {
        this.board = board;

        // push board to history
        //boardHistory.push(board.getBoard());
        Log.d("setBoard", Arrays.toString(board.getBoard()));
        // Trigger a redraw of the view when the board is updated
        invalidate();
    }

    /**
     * Sets the current round.
     * @param inRound The Round object representing the current round.
     */
    public void setRound(Round inRound) {
        this.round = inRound;
    }

    /**
     * Sets the move stack.
     * @param inStack The Stack object representing the move stack.
     */
    public void setMoveStack(Stack<Move> inStack) { this.moveStack = inStack;};

    /**
     * Sets whether a move has been made.
     * @param inValue True if a move has been made, false otherwise.
     */
    public void setMoveMade(boolean inValue) {
        moveMade = inValue;
    }

    /**
     * Gets whether a move has been made.
     * @return True if a move has been made, false otherwise.
     */
    public boolean getMoveMade() {
        return moveMade;
    }

    /**
     * Sets the previous move made on the board.
     * @param move The Move object representing the previous move.
     */
    public void setPrevMove(Move move) {
        prevMove = move;
    }


    /**
     * Highlights the best move on the board.
     * @param move The Move object representing the best move.
     */
    public void highlightBestMove(Move move) {
        validMoves.clear();
        resetSelectedTile();
        // Set the coordinates of the origin of best move to highlight
        suggestedOriginRow = move.getOriginRow();
        suggestedOriginCol = move.getOriginCol();

        // Set the coordinates of the destination of best move to highlight
        suggestedDestRow = move.getDestinationRow();
        suggestedDestCol = move.getDestinationCol();

        // Invalidate the view to trigger redraw and highlight the best move
        invalidate();
    }

    /**
     * Resets the highlight for the suggested move.
     */
    public void resetSuggestedMove() {
        suggestedOriginRow = -1;
        suggestedOriginCol = -1;
        suggestedDestRow = -1;
        suggestedDestCol = -1;
    }

    /**
     * Resets the coordinates for the tile selected by the player.
     */
    public void resetSelectedTile() {
        selectedRow = -1;
        selectedCol = -1;
    }

    /**
     * Adds valid move positions for a selected piece to the list of valid moves.
     * @param selectedRow The row index of the selected piece.
     * @param selectedCol The column index of the selected piece.
     */
    private void addValidMoves(int selectedRow, int selectedCol) {
        // Get valid moves for the selected piece
        List<Move> valid = board.getPossibleMoves(selectedRow, selectedCol, round.getCurPlayer().getColor());

        // Log all valid moves
        for (Move move : validMoves) {
            Log.d("ValidMove", "From: " + selectedRow + ", " + selectedCol + " To: " + move.getDestinationRow() + ", " + move.getDestinationCol());
        }

        // Add valid moves to the list
        validMoves.clear(); // Clear the list before adding new valid moves
        validMoves.addAll(valid);
    }
}
