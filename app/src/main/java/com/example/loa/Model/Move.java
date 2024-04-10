package com.example.loa.Model;

/**
 * Represents a move in a game.
 */
public class Move {
    private int originRow;
    private int originCol;
    private int destinationRow;
    private int destinationCol;

    /**
     * Constructs a Move object with the specified origin and destination coordinates.
     * @param originRow The row index of the piece's origin position.
     * @param originCol The column index of the piece's origin position.
     * @param destinationRow The row index of the piece's destination position.
     * @param destinationCol The column index of the piece's destination position.
     */
    public Move(int originRow, int originCol, int destinationRow, int destinationCol) {
        this.originRow = originRow;
        this.originCol = originCol;
        this.destinationRow = destinationRow;
        this.destinationCol = destinationCol;
    }

    /**
     * Converts the move to Rank-File notation.
     * @return The move in Rank-File notation, e.g., "A2-B3".
     */
    public String toRankFileNotation() {
        char originFile = (char) ('A' + originCol);
        int originRank = 8 - originRow;
        char destFile = (char) ('A' + destinationCol);
        int destRank = 8 - destinationRow;
        return "" + originFile + originRank + "-" + destFile + destRank;
    }

    /**
     * Converts Rank-File notation to a Move object.
     * @param notation The Rank-File notation of the move, e.g., "A2-B3".
     * @return The Move object corresponding to the notation.
     * @throws IllegalArgumentException if the notation format is invalid.
     */
    public static Move fromRankFileNotation(String notation) {
        // Split the notation into origin and destination parts
        String[] parts = notation.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid notation format: " + notation);
        }

        // Parse origin file and rank
        char originFile = parts[0].charAt(0);
        int originRank = Integer.parseInt(parts[0].substring(1));

        // Parse destination file and rank
        char destFile = parts[1].charAt(0);
        int destRank = Integer.parseInt(parts[1].substring(1));

        // Convert file characters to column indices (subtracting 'a')
        int originCol = originFile - 'a';
        int destCol = destFile - 'a';

        // Convert rank numbers to row indices (subtracting 8 and taking the absolute value)
        int originRow = Math.abs(8 - originRank);
        int destRow = Math.abs(8 - destRank);

        // Create and return the Move object
        return new Move(originRow, originCol, destRow, destCol);
    }

    // Getters and setters

    /**
     Gets the origin row of the move.
     @return The origin row of the move.
     */
    public int getOriginRow() {
        return originRow;
    }

    /**
     Sets the origin row of the move.
     @param originRow The origin row to set.
     */
    public void setOriginRow(int originRow) {
        this.originRow = originRow;
    }

    /**
     Gets the origin column of the move.
     @return The origin column of the move.
     */
    public int getOriginCol() {
        return originCol;
    }

    /**
     Sets the origin column of the move.
     @param originCol The origin column to set.
     */
    public void setOriginCol(int originCol) {
        this.originCol = originCol;
    }

    /**
     Gets the destination row of the move.
     @return The destination row of the move.
     */
    public int getDestinationRow() {
        return destinationRow;
    }

    /**
     Sets the destination row of the move.
     @param destinationRow The destination row to set.
     */
    public void setDestinationRow(int destinationRow) {
        this.destinationRow = destinationRow;
    }

    /**
     Gets the destination column of the move.
     @return The destination column of the move.
     */
    public int getDestinationCol() {
        return destinationCol;
    }

    /**
     Sets the destination column of the move.
     @param destinationCol The destination column to set.
     */
    public void setDestinationCol(int destinationCol) {
        this.destinationCol = destinationCol;
    }

}
