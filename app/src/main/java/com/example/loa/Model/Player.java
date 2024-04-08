package com.example.loa.Model;

import android.util.Pair;

import java.util.List;

public class Player {
    private char color;
    private int roundsWon = 0;
    private int score = 0;
    private int tournamentScore = 0;
    private int piecesOnBoard = 12;

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
        this.setPiecesOnBoard(board.countPieces(this.getColor()));
    }

    public void play(Board board) {
        // Implementation of this method depends on the Board class
    }

    public Pair<Move, String> strategize(Board board) {
        Move moveToMake = null;
        String reason = "";

        List<Move> moves = board.getPossibleMoves(this.getColor());
        //Collections.sort(moves); // Assuming Move class implements Comparable


        char opponentColor = (this.getColor() == 'w') ? 'b' : 'w';

        // check for winning move
        boolean winThisTurn = false;
        for (Move move : moves) {
            if (board.isWinningMove(move, this.getColor())) {
                winThisTurn = true;
                break;
            }
        }

        // check for delay
        if (winThisTurn) {
            for (Move move : moves) {
                if (board.isCapture(move, this.getColor(), opponentColor) &&
                        board.isThwart(move, this.getColor(), opponentColor) &&
                        !board.isWinningMove(move, this.getColor())) {
                    moveToMake = move;
                    reason = ", delaying win to increase score.\n";
                    return new Pair<>(moveToMake, reason);
                }
            }
        }

        // check for winning move
        for (Move move : moves) {
            if (board.isWinningMove(move, this.getColor())) {
                moveToMake = move;
                reason = " to win the round.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        // Next check for thwart
        // Need to know if opponent's next move is win
        List<Move> opponentMoves = board.getPossibleMoves(opponentColor);
        boolean winIncoming = false;
        for (Move move : opponentMoves) {
            if (board.isWinningMove(move, opponentColor)) {
                winIncoming = true;
                break;
            }
        }

        // Test for thwarts if win is incoming
        if (winIncoming) {
            for (Move move : moves) {
                if (board.isThwart(move, this.getColor(), opponentColor)) {
                    moveToMake = move;
                    reason = " to thwart opponent's win.\n";
                    return new Pair<>(moveToMake, reason);
                }
            }
        }

        // Test for connecting 2 groups
        for (Move move : moves) {
            if (board.isConnectingGroups(move, this.getColor()) && board.isThwart(move, this.getColor(), opponentColor)) {
                moveToMake = move;
                reason = " to connect 2 groups.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        // Test for condensing groups
        for (Move move : moves) {
            if (board.isCondensingGroups(move, this.getColor()) && board.isThwart(move, this.getColor(), opponentColor)) {
                moveToMake = move;
                reason = " to condense groups.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        // Test for blocks
        for (Move move : moves) {
            if (board.isBlock(move, this.getColor(), opponentColor)) {
                moveToMake = move;
                reason = " to block opponent.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        // Capture
        for (Move move : moves) {
            if (board.isCapture(move, this.getColor(), opponentColor) && board.isThwart(move, this.getColor(), opponentColor)) {
                moveToMake = move;
                reason = " to capture opponent's piece.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        // Last resort capture
        // If win incoming
        if (winIncoming) {
            for (Move move : moves) {
                if (board.isCapture(move, this.getColor(), opponentColor)) {
                    moveToMake = move;
                    reason = " to capture piece as last resort.\n";
                    return new Pair<>(moveToMake, reason);
                }
            }
        }

        // Last resort stall
        for (Move move : moves) {
            if (board.isStall(move, this.getColor(), opponentColor)) {
                moveToMake = move;
                reason = " to stall defeat.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        return new Pair<>(moveToMake, reason);
    }
}

