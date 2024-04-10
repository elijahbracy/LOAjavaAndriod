package com.example.loa.Model;

import android.util.Pair;

import java.util.List;

/**
 * Represents a player in the game.
 */
public class Player {
    private char color;
    private int roundsWon = 0;
    private int score = 0;

    /**
     * Default constructor.
     */
    public Player() {}

    /**
     * Sets the color of the player.
     * @param color The color of the player ('w' for white, 'b' for black).
     */
    public void setColor(char color) {
        this.color = color;
    }

    /**
     * Gets the color of the player.
     * @return The color of the player.
     */
    public char getColor() {
        return color;
    }

    /**
     * Sets the number of rounds won by the player.
     * @param roundsWon The number of rounds won.
     */
    public void setRoundsWon(int roundsWon) {
        this.roundsWon = roundsWon;
    }

    /**
     * Gets the number of rounds won by the player.
     * @return The number of rounds won.
     */
    public int getRoundsWon() {
        return roundsWon;
    }

    /**
     * Sets the score of the player.
     * @param score The score of the player.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the score of the player.
     * @return The score of the player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Strategy to determine the move the player should make based on the current state of the board.
     * @param board The board object representing the current state of the game.
     * @return A pair containing the move to make and the reason for making that move.
     */
    public Pair<Move, String> strategize(Board board) {
        Move moveToMake = null;
        String reason = "";

        List<Move> moves = board.getPossibleMoves(this.getColor());

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
                reason = " for lack of a better strategy.\n";
                return new Pair<>(moveToMake, reason);
            }
        }

        return new Pair<>(moveToMake, reason);
    }
}
