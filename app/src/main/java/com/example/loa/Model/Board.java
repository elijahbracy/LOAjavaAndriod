package com.example.loa.Model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board.
 */
public class Board {
    private char[][] board = new char[8][8];

    /**
     * Constructs a new Board object with the provided board configuration.
     *
     * @param board The 2D char array representing the initial board configuration.
     */
    public Board(char[][] board) {
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, 8);
        }
    }

    /**
     * Constructs a new Board object with the default initial board configuration.
     */
    public Board() {
        resetBoard();
    }

    /**
     * Retrieves the current state of the board.
     *
     * @return The 2D char array representing the current board configuration.
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * Resets the board to the default initial configuration.
     */
    public void resetBoard() {
        char[][] defaultBoard = {
                {'x', 'b', 'b', 'b', 'b', 'b', 'b', 'x'},
                {'w', 'x', 'x', 'x', 'x', 'x', 'x', 'w'},
                {'w', 'x', 'x', 'x', 'x', 'x', 'x', 'w'},
                {'w', 'x', 'x', 'x', 'x', 'x', 'x', 'w'},
                {'w', 'x', 'x', 'x', 'x', 'x', 'x', 'w'},
                {'w', 'x', 'x', 'x', 'x', 'x', 'x', 'w'},
                {'w', 'x', 'x', 'x', 'x', 'x', 'x', 'w'},
                {'x', 'b', 'b', 'b', 'b', 'b', 'b', 'x'}
        };

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = defaultBoard[i][j];
            }
        }
    }

    /**
     * Counts the number of pieces of the specified color on the board.
     *
     * @param color The color of the pieces to count.
     * @return The number of pieces of the specified color on the board.
     */
    public int countPieces(char color) {
        int numPieces = 0;

        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j] == color) {
                    numPieces++;
                }
            }
        }

        return numPieces;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return countGroups('b') == 1 || countGroups('w') == 1;
    }

    /**
     * Checks if the specified move results in a winning move for the player of the specified color.
     *
     * @param move     The move to check.
     * @param curColor The color of the player making the move.
     * @return true if the move results in a winning move, false otherwise.
     */
    public boolean isWinningMove(Move move, char curColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
        return buffBoard.countGroups(curColor) == 1;
    }

    /**
     * Checks if the specified move results in capturing opponent's pieces.
     *
     * @param move          The move to check.
     * @param curColor      The color of the player making the move.
     * @param opponentColor The color of the opponent.
     * @return true if the move results in capturing opponent's pieces, false otherwise.
     */
    public boolean isCapture(Move move, char curColor, char opponentColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
        return buffBoard.countPieces(opponentColor) < this.countPieces(opponentColor);
    }

    /**
     * Checks if the specified move results in stalling the opponent's win.
     *
     * @param move          The move to check.
     * @param curColor      The color of the player making the move.
     * @param opponentColor The color of the opponent.
     * @return true if the move results in stalling the opponent's win, false otherwise.
     */
    public boolean isStall(Move move, char curColor, char opponentColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
        return buffBoard.countGroups(opponentColor) != 1;
    }

    /**
     * Checks if the specified move results in connecting groups of the player.
     *
     * @param move     The move to check.
     * @param curColor The color of the player making the move.
     * @return true if the move results in connecting groups, false otherwise.
     */
    public boolean isConnectingGroups(Move move, char curColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
        return buffBoard.countGroups(curColor) + 1 < countGroups(curColor);
    }

    /**
     * Checks if the specified move results in condensing groups of the player.
     *
     * @param move     The move to check.
     * @param curColor The color of the player making the move.
     * @return true if the move results in condensing groups, false otherwise.
     */
    public boolean isCondensingGroups(Move move, char curColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
        return buffBoard.countGroups(curColor) < countGroups(curColor);
    }

    /**
     * Checks if the specified move results in blocking opponent's pieces.
     *
     * @param move          The move to check.
     * @param curColor      The color of the player making the move.
     * @param opponentColor The color of the opponent.
     * @return true if the move results in blocking opponent's pieces, false otherwise.
     */
    public boolean isBlock(Move move, char curColor, char opponentColor) {
        Board buffBoard = new Board(this.board);

        int destRow = move.getDestinationRow();
        int destCol = move.getDestinationCol();

        if ((destCol == 1 || destCol == 6) && buffBoard.getBoard()[destRow][destCol + (destCol == 1 ? -1 : 1)] == opponentColor) {
            buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
            List<Move> opponentMoves = buffBoard.getPossibleMoves(opponentColor);

            for (Move opponentMove : opponentMoves) {
                if (buffBoard.isWinningMove(opponentMove, opponentColor)) {
                    return false;
                }
            }
            return true;
        }

        if ((destRow == 1 || destRow == 6) && buffBoard.getBoard()[destRow + (destRow == 1 ? -1 : 1)][destCol] == opponentColor) {
            buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
            List<Move> opponentMoves = buffBoard.getPossibleMoves(opponentColor);

            for (Move opponentMove : opponentMoves) {
                if (buffBoard.isWinningMove(opponentMove, opponentColor)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Checks if the specified move results in thwarting the opponent's winning move.
     *
     * @param move          The move to check.
     * @param curColor      The color of the player making the move.
     * @param opponentColor The color of the opponent.
     * @return true if the move thwarts the opponent's winning move, false otherwise.
     */
    public boolean isThwart(Move move, char curColor, char opponentColor) {
        Board buffBoard = new Board(this.board);
        buffBoard.makeMove(move.getOriginRow(), move.getOriginCol(), move.getDestinationRow(), move.getDestinationCol(), curColor);
        List<Move> opponentMoves = buffBoard.getPossibleMoves(opponentColor);
        for (Move oppMove : opponentMoves) {
            if (buffBoard.isWinningMove(oppMove, opponentColor)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Counts the number of groups of pieces of the specified color on the board.
     *
     * @param color The color of the pieces to count the groups for.
     * @return The number of groups of pieces of the specified color on the board.
     */
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
        return groups;
    }

    /**
     * Performs flood fill algorithm to mark all connected pieces of the same color.
     *
     * @param row     The row index of the starting position.
     * @param col     The column index of the starting position.
     * @param color   The color of the pieces.
     * @param visited 2D array to mark visited positions.
     */
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

    /**
     * Updates the board with the specified move.
     *
     * @param originRow      The row index of the piece's origin position.
     * @param originCol      The column index of the piece's origin position.
     * @param destinationRow The row index of the piece's destination position.
     * @param destinationCol The column index of the piece's destination position.
     * @param curColor       The color of the piece making the move.
     */
    public void makeMove(int originRow, int originCol, int destinationRow, int destinationCol, char curColor) {
        // Update origin space
        board[originRow][originCol] = 'x';

        // Update destination space
        // If destination space is empty, just make the move
        board[destinationRow][destinationCol] = curColor;

        // If destination space has a character, decrement that player's total pieces
        // Note: You might need to implement a method to track the number of pieces for each player
    }

    /**
     * Retrieves a list of possible moves for the specified color.
     *
     * @param curColor The color of the player for whom to generate possible moves.
     * @return A list of possible Move objects.
     */
    public List<Move> getPossibleMoves(char curColor) {
        List<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == curColor) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if (isValid(i, j, k, l, curColor)) {
                                possibleMoves.add(new Move(i, j, k, l));
                            }
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Retrieves a list of possible moves for the piece at the specified position and color.
     *
     * @param row      The row index of the piece.
     * @param col      The column index of the piece.
     * @param curColor The color of the player for whom to generate possible moves.
     * @return A list of possible Move objects.
     */
    public List<Move> getPossibleMoves(int row, int col, char curColor) {
        List<Move> possibleMoves = new ArrayList<>();
        // Ensure the specified row and column are within bounds
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return possibleMoves; // Return an empty list if the specified position is out of bounds
        }
        // Check if there is a piece at the specified position
        if (board[row][col] == curColor) {
            // Iterate over all possible destination positions
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    // Check if the move from the specified position to the destination position is valid
                    if (isValid(row, col, i, j, curColor)) {
                        possibleMoves.add(new Move(row, col, i, j)); // Add the valid move to the list
                    }
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Checks if a move from the origin to the destination coordinates is valid for the specified color.
     *
     * @param originRow      The row index of the origin coordinate.
     * @param originCol      The column index of the origin coordinate.
     * @param destinationRow The row index of the destination coordinate.
     * @param destinationCol The column index of the destination coordinate.
     * @param curColor       The color of the player making the move.
     * @return true if the move is valid, false otherwise.
     */
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

    /**
     * Calculates the number of spaces to move from the origin to the destination coordinates.
     *
     * @param originRow      The row index of the origin coordinate.
     * @param originCol      The column index of the origin coordinate.
     * @param destinationRow The row index of the destination coordinate.
     * @param destinationCol The column index of the destination coordinate.
     * @return The number of spaces to move.
     */
    public int spacesToMove(int originRow, int originCol, int destinationRow, int destinationCol) {
        int rowDiff = Math.abs(originRow - destinationRow);
        int colDiff = Math.abs(originCol - destinationCol);

        return Math.max(rowDiff, colDiff);
    }

    /**
     * Checks if there are any pieces in the way between the origin and destination coordinates.
     *
     * @param originRow      The row index of the origin coordinate.
     * @param originCol      The column index of the origin coordinate.
     * @param destinationRow The row index of the destination coordinate.
     * @param destinationCol The column index of the destination coordinate.
     * @param curColor       The color of the player making the move.
     * @return true if there are pieces in the way, false otherwise.
     */
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

    /**
     * Calculates the slope between two coordinates.
     *
     * @param originRow      The row index of the origin coordinate.
     * @param originCol      The column index of the origin coordinate.
     * @param destinationRow The row index of the destination coordinate.
     * @param destinationCol The column index of the destination coordinate.
     * @return A Pair object representing the slope, with the first element being the row step and the second element being the column step.
     */
    private Pair<Integer, Integer> slope(int originRow, int originCol, int destinationRow, int destinationCol) {
        int rowStep = (destinationRow - originRow) / spacesToMove(originRow, originCol, destinationRow, destinationCol);
        int colStep = (destinationCol - originCol) / spacesToMove(originRow, originCol, destinationRow, destinationCol);
        return new Pair<>(rowStep, colStep);
    }

    /**
     * Calculates the number of pieces on the line between two coordinates.
     *
     * @param originRow      The row index of the origin coordinate.
     * @param originCol      The column index of the origin coordinate.
     * @param destinationRow The row index of the destination coordinate.
     * @param destinationCol The column index of the destination coordinate.
     * @return The number of pieces on the line between the two coordinates.
     */
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

    /**
     * Sets the current board configuration to the specified new board.
     *
     * @param newBoard The new board configuration to set.
     * @throws IllegalArgumentException if the dimensions of the new board are not 8x8.
     */
    public void setBoard(char[][] newBoard) {
        // Check if the dimensions of the new board are valid
        if (newBoard.length != 8 || newBoard[0].length != 8) {
            throw new IllegalArgumentException("Invalid board dimensions. Board must be 8x8.");
        }

        // Copy the contents of the new board to the current board
        for (int i = 0; i < 8; i++) {
            System.arraycopy(newBoard[i], 0, this.board[i], 0, 8);
        }
    }

    /**
     * Generates a string representation of a move.
     *
     * @param originRow      The row index of the origin coordinate.
     * @param originCol      The column index of the origin coordinate.
     * @param destinationRow The row index of the destination coordinate.
     * @param destinationCol The column index of the destination coordinate.
     * @return The string representation of the move.
     */
    private String generateMoveString(int originRow, int originCol, int destinationRow, int destinationCol) {
        char originColChar = (char) ('A' + originCol);
        char destColChar = (char) ('A' + destinationCol);
        int originRowNum = Math.abs(originRow - 8) + 1;
        int destRowNum = Math.abs(destinationRow - 8) + 1;
        return "" + originColChar + originRowNum + "->" + destColChar + destRowNum;
    }

}