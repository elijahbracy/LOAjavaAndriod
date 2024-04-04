package com.example.loa.Model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private char[][] board = new char[8][8];
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private static final char EMPTY = 'x';

    public boolean isWinningMove(int originRow, int originCol, int destinationRow, int destinationCol, char curColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(originRow, originCol, destinationRow, destinationCol, curColor);
        return buffBoard.countGroups(curColor) == 1;
    }

    public boolean isConnectingGroups(int originRow, int originCol, int destinationRow, int destinationCol, char curColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(originRow, originCol, destinationRow, destinationCol, curColor);
        return buffBoard.countGroups(curColor) + 1 < countGroups(curColor);
    }

    public boolean isThwart(int originRow, int originCol, int destinationRow, int destinationCol, char curColor, char opponentColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(originRow, originCol, destinationRow, destinationCol, curColor);
        List<String> opponentMoves = buffBoard.getPossibleMoves(opponentColor);
        for (String oppMove : opponentMoves) {
            if (true/*buffBoard.isWinningMove(oppMove, opponentColor)*/) {
                return false;
            }
        }
        return true;
    }


    public int countGroups(char color) {
        int groups = 0;
        boolean[][] visited = new boolean[8][8];

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j] == color && !visited[i][j]) {
                    // Found a new group
                    groups++;
                    // Perform flood fill to mark all connected pieces
                    floodFill(i, j, color, visited);
                }
            }
        }
        // System.out.println("number of groups for " + color + ": " + groups);
        return groups;
    }

    public void floodFill(int row, int col, char color, boolean[][] visited) {
        // if space is out of bounds, piece is not the same as color, or space has been visited, return
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || board[row][col] != color || visited[row][col]) {
            return;
        }
        // mark space as visited
        visited[row][col] = true;

        // recursively check adjacent spaces
        floodFill(row - 1, col, color, visited);
        floodFill(row + 1, col, color, visited);
        floodFill(row, col - 1, color, visited);
        floodFill(row, col + 1, color, visited);
        // check diagonals
        floodFill(row - 1, col + 1, color, visited);
        floodFill(row + 1, col - 1, color, visited);
        floodFill(row + 1, col + 1, color, visited);
        floodFill(row - 1, col - 1, color, visited);
    }



    public void makeMove(int originRow, int originCol, int destinationRow, int destinationCol, char curColor) {
        // Update origin space
        board[originRow][originCol] = EMPTY;

        // Update destination space
        // If destination space is empty, just make the move
        board[destinationRow][destinationCol] = curColor;

        // If destination space has a character, decrement that player's total pieces
        // Note: You might need to implement a method to track the number of pieces for each player
    }


    // Define other methods similarly

    public Board(char[][] board) {
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, 8);
        }
    }

    public Board() {
        //board = new char[8][8];
        resetBoard();
    }

    public char[][] getBoard() {
        return board;
    }

    public void resetBoard() {
        char[][] defaultBoard = {
                {'x', 'B', 'B', 'B', 'B', 'B', 'B', 'x'},
                {'W', 'x', 'x', 'x', 'x', 'x', 'x', 'W'},
                {'W', 'x', 'x', 'x', 'x', 'x', 'x', 'W'},
                {'W', 'x', 'x', 'x', 'x', 'x', 'x', 'W'},
                {'W', 'x', 'x', 'x', 'x', 'x', 'x', 'W'},
                {'W', 'x', 'x', 'x', 'x', 'x', 'x', 'W'},
                {'W', 'x', 'x', 'x', 'x', 'x', 'x', 'W'},
                {'x', 'B', 'B', 'B', 'B', 'B', 'B', 'x'}
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = defaultBoard[i][j];
            }
        }
    }

    // Define other constructors similarly

    public List<String> getPossibleMoves(char curColor) {
        List<String> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == curColor) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            //String move = generateMoveString(i, j, k, l);
                            if (isValid(i, j, j, k, curColor)) {
                                //possibleMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    // Define other methods similarly

    public boolean isValid(int originRow, int originCol, int destinationRow, int destinationCol, char curColor) {

        // Get the pieces at the origin and destination coordinates
        char originSpace = board[originRow][originCol];
        char destinationSpace = board[destinationRow][destinationCol];

        // Steps must equal the number of pieces on that line
        if (spacesToMove(originRow, originCol, destinationRow, destinationCol) != piecesOnLine(originRow, originCol, destinationRow, destinationCol)) {
            // Return false if steps do not equal the number of pieces on that line
            return false;
        }

        // Origin coordinate must have a piece that belongs to the player
        if (originSpace != curColor) {
            // Return false if origin coordinate does not have a piece that belongs to the player
            return false;
        }

        // Destination must be empty or have an enemy piece
        if (destinationSpace == curColor) {
            // Return false if destination coordinate is not empty or does not have an enemy piece
            return false;
        }

        // Path between origin and destination must have no enemy pieces
        if (piecesInWay(originRow, originCol, destinationRow, destinationCol, curColor)) {
            // Return false if there are enemy pieces in the path between origin and destination
            return false;
        }

        // Return true if the move is valid based on all conditions
        return true;
    }

    public int spacesToMove(int originRow, int originCol, int destinationRow, int destinationCol) {
        int rowDiff = Math.abs(originRow - destinationRow);
        int colDiff = Math.abs(originCol - destinationCol);

        return Math.max(rowDiff, colDiff);
    }

    public boolean piecesInWay(int originRow, int originCol, int destinationRow, int destinationCol, char curColor) {
        // Get difference in row and col coordinates
        int rowDiff = Math.abs(originRow - destinationRow);
        int colDiff = Math.abs(originCol - destinationCol);

        // If difference is same for row and col, moving diagonally
        // If difference is not same but one value is zero, moving vertically/horizontally
        // If difference is not same for both but both values are non-zero, invalid move
        if (rowDiff != colDiff && rowDiff != 0 && colDiff != 0) {
            return true;
        }

        // Get number of spaces we need to move
        int steps = Math.max(rowDiff, colDiff);

        // If steps is zero, no move being made, invalid
        if (steps == 0) {
            return true;
        }

        // Determine direction by taking (destination - origin) / steps
        // Will give 1, 0, or -1 for up, neutral, or down
        Pair<Integer, Integer> slope = slope(originRow, originCol, destinationRow, destinationCol);
        int rowStep = slope.first;
        int colStep = slope.second;

        // Get starting space to check (origin + a step in direction we are moving)
        int row = originRow + rowStep;
        int col = originCol + colStep;

        // Iterate from that space to destination checking for collision
        // Stop 1 before destination
        for (int i = 0; i < steps - 1; i++) {
            char curSpace = board[row][col];
            if (curSpace != 'x' && curSpace != curColor) {
                return true;
            }
            // Move in predetermined direction
            row += rowStep;
            col += colStep;
        }
        return false;
    }

    private Pair<Integer, Integer> slope(int originRow, int originCol, int destinationRow, int destinationCol) {
        int rowStep = (destinationRow - originRow) / spacesToMove(originRow, originCol, destinationRow, destinationCol);
        int colStep = (destinationCol - originCol) / spacesToMove(originRow, originCol, destinationRow, destinationCol);
        return new Pair<>(rowStep, colStep);
    }


    private int piecesOnLine(int originRow, int originCol, int destinationRow, int destinationCol) {
        int numPieces = 0;

        // If the first coordinates are the same, check the row
        if (originRow == destinationRow) {
            for (int i = 0; i < 8; i++) {
                char curSpace = board[originRow][i];
                if (curSpace != 'x') {
                    numPieces++;
                }
            }
        }
        // If the second coordinates are the same, check the column
        else if (originCol == destinationCol) {
            for (int i = 0; i < 8; i++) {
                char curSpace = board[i][originCol];
                if (curSpace != 'x') {
                    numPieces++;
                }
            }
        }
        // If both coordinates are different, check the diagonal
        else {
            Pair<Integer, Integer> slope = slope(originRow, originCol, destinationRow, destinationCol);
            int rowStep = slope.first;
            int colStep = slope.second;

            // Start at the origin and follow the slope to the edge
            int x = originRow;
            int y = originCol;

            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                char curSpace = board[x][y];
                if (curSpace != 'x') {
                    numPieces++;
                }
                x += rowStep;
                y += colStep;
            }

            // Start at the origin plus one step in the opposite direction and follow to the edge going opposite direction
            x = originRow + (rowStep * -1);
            y = originCol + (colStep * -1);

            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                char curSpace = board[x][y];
                if (curSpace != 'x') {
                    numPieces++;
                }
                x += (rowStep * -1);
                y += (colStep * -1);
            }
        }

        return numPieces;
    }






    private String generateMoveString(int originRow, int originCol, int destinationRow, int destinationCol) {
        char originColChar = (char) ('A' + originCol);
        char destColChar = (char) ('A' + destinationCol);
        int originRowNum = Math.abs(originRow - 8) + 1;
        int destRowNum = Math.abs(destinationRow - 8) + 1;
        return "" + originColChar + originRowNum + "->" + destColChar + destRowNum;
    }
}
