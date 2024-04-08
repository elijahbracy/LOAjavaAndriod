package com.example.loa.Model;

public class Move {
    private int originRow;
    private int originCol;
    private int destinationRow;
    private int destinationCol;

    public Move(int originRow, int originCol, int destinationRow, int destinationCol) {
        this.originRow = originRow;
        this.originCol = originCol;
        this.destinationRow = destinationRow;
        this.destinationCol = destinationCol;
    }

    public String toRankFileNotation() {
        char originFile = (char) ('a' + originCol);
        int originRank = 8 - originRow;
        char destFile = (char) ('a' + destinationCol);
        int destRank = 8 - destinationRow;
        return "" + originFile + originRank + "-" + destFile + destRank;
    }

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
    // You may also want to override toString() for debugging purposes

    public int getOriginRow() {
        return originRow;
    }

    public void setOriginRow(int originRow) {
        this.originRow = originRow;
    }

    public int getOriginCol() {
        return originCol;
    }

    public void setOriginCol(int originCol) {
        this.originCol = originCol;
    }

    public int getDestinationRow() {
        return destinationRow;
    }

    public void setDestinationRow(int destinationRow) {
        this.destinationRow = destinationRow;
    }

    public int getDestinationCol() {
        return destinationCol;
    }

    public void setDestinationCol(int destinationCol) {
        this.destinationCol = destinationCol;
    }
}

