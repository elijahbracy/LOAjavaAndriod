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

import java.util.Arrays;

public class BoardView extends View implements View.OnTouchListener {

    private Board board;

    private Paint paint;

    private int left;
    private int top;
    private int cellWidth;
    private int cellHeight;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private int movedRow = -1;
    private int movedCol = -1;



    public BoardView(Context context) {
        super(context);
        init();
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        setOnTouchListener((OnTouchListener) this);
        //setOnClickListener((OnClickListener) this);
    }

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
                    paint.setColor(Color.parseColor("#D2B48C")); // Light brown
                } else {
                    paint.setColor(Color.parseColor("#CD853F")); // Slightly darker brown
                }

                // Draw the cell
                canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, paint);
            }
        }

        // Draw highlighting for the selected square
        if (selectedRow != -1 && selectedCol != -1) {
            int highlightColor = Color.parseColor("#FFFF00"); // Yellow color for highlighting
            int highlightLeft = left + selectedCol * cellWidth;
            int highlightTop = top + selectedRow * cellHeight;
            int highlightRight = highlightLeft + cellWidth;
            int highlightBottom = highlightTop + cellHeight;
            paint.setColor(highlightColor);
            canvas.drawRect(highlightLeft, highlightTop, highlightRight, highlightBottom, paint);
        }

        // Draw lighter highlighting for the place the piece moved to
        if (movedRow != -1 && movedCol != -1) {
            int lighterHighlightColor = Color.parseColor("#FFFF00"); // Lighter yellow color for highlighting
            int highlightLeft = left + movedCol * cellWidth;
            int highlightTop = top + movedRow * cellHeight;
            int highlightRight = highlightLeft + cellWidth;
            int highlightBottom = highlightTop + cellHeight;
            paint.setColor(lighterHighlightColor);
            canvas.drawRect(highlightLeft, highlightTop, highlightRight, highlightBottom, paint);
        }


        // Draw the pieces based on the board state
        if (board.getBoard() != null) {
            Log.d("board", Arrays.toString(board.getBoard()));
            for (int row = 0; row < board.getBoard().length; row++) {
                for (int col = 0; col < board.getBoard()[row].length; col++) {
                    char pieceType = board.getBoard()[row][col];
                    //Log.d("piecetype", String.valueOf(pieceType));
                    if (pieceType != 'x') {
                        // Draw the piece
                        // You can use different shapes, colors, or bitmaps for different piece types
                        // Example: drawing a circle for a piece
                        paint.setColor(pieceType == 'W' ? Color.WHITE : Color.BLACK); // Assuming 'W' represents white and 'B' represents black
                        // Calculate the center coordinates of the cell
                        int cellLeft = left + col * cellWidth;
                        int cellTop = top + row * cellHeight;
                        int centerX = cellLeft + cellWidth / 2;
                        int centerY = cellTop + cellHeight / 2;
                        // Assuming a radius for the piece
                        int radius = Math.min(cellWidth, cellHeight) / 2 - 10;
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
            String fileLabel = Character.toString((char) ('a' + i));
            drawTextCentered(canvas, fileLabel, left + (i + 1) * cellWidth - cellWidth / 2, top + boardHeight + 50, paint);
        }
    }

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
                handleTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                // Ignore move events after touch down
                break;
            case MotionEvent.ACTION_UP:
                // Touch up event
                handleTouchUp(event);
                break;
        }

        // Return true to indicate that the event has been consumed
        return true;
    }


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
    }

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
                if (board.isValid(selectedRow, selectedCol, rowUp, colUp, 'B')) {
                    board.makeMove(selectedRow, selectedCol, rowUp, colUp, 'B');
                    // if move is valid updated moved coordinates for highlighting on redraw
                    movedRow = rowUp;
                    movedCol = colUp;
                    invalidate();
                }
            }
        } else {
            Log.d("TouchEvent", "Touch up outside board bounds");
        }
    }

    public void updateBoardModel(int touchDownRow, int touchDownCol, int touchUpRow, int touchUpCol) {
        // Log the entered parameters
        Log.d("updateBoardModel", "Touch down row: " + touchDownRow + ", Touch down col: " + touchDownCol
                + ", Touch up row: " + touchUpRow + ", Touch up col: " + touchUpCol);
        // Ensure that both touch down and touch up coordinates are valid
        if (touchDownRow >= 0 && touchDownCol >= 0 && touchUpRow >= 0 && touchUpCol >= 0) {
            // Update the board model based on the touch coordinates
            char temp = board.getBoard()[touchDownRow][touchDownCol];
            board.getBoard()[touchDownRow][touchDownCol] = board.getBoard()[touchUpRow][touchUpCol];
            board.getBoard()[touchUpRow][touchUpCol] = temp;

            // Invalidate the view to trigger a redraw
            invalidate();
        }
    }

    // Helper method to draw centered text
    private void drawTextCentered(Canvas canvas, String text, float x, float y, Paint paint) {
        paint.setColor(Color.BLACK); // Set the text color to black
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40); // Set the text size here
        canvas.drawText(text, x, y, paint);
    }

    public void setBoard(Board board) {
        this.board = board;
        // Trigger a redraw of the view when the board is updated
        invalidate();
    }
}
