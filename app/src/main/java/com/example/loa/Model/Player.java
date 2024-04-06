package com.example.loa.Model;

import android.util.Pair;

public class Player {
    private char color;
    private int roundsWon = 0;
    private int score = 0;
    private int tournamentScore = 0;
    private int piecesOnBoard = 12;
    private String name;

    public Player() {}

    public void setColor(char color) {
        this.color = color;
    }

    public char getColor() {
        return color;
    }

    public void setRoundsWon(int roundsWon) {
        this.roundsWon = roundsWon;
    }

    public int getRoundsWon() {
        return roundsWon;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setTournamentScore(int tournamentScore) {
        this.tournamentScore = tournamentScore;
    }

    public int getTournamentScore() {
        return tournamentScore;
    }

    public void setPiecesOnBoard(int piecesOnBoard) {
        this.piecesOnBoard = piecesOnBoard;
    }

    public int getPiecesOnBoard() {
        return piecesOnBoard;
    }

    public void updateNumPieces(Board board) {
        // Implementation of this method depends on the Board class
    }

    public void play(Board board) {
        // Implementation of this method depends on the Board class
    }

    public Pair<String, String> strategize(Board board) {
        // Implementation of this method depends on the Board class
        return null; // Placeholder return value
    }

    public void displayName() {
        // Implementation of this method
    }
}

