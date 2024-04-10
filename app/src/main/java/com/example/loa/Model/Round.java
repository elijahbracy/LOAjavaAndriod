package com.example.loa.Model;

/**
 Represents a round of the game, keeping track of turns played and the current and next players.
 */
public class Round {
    private Player curPlayer = null; // Current player in the round
    private Player nextPlayer = null; // Next player in the round

    /**
     Initializes a new instance of the Round class.
     */
    public Round() {}

    /**
     Switches the current player with the next player.
     */
    public void SwitchPlayers() {
        Player buff = curPlayer;
        curPlayer = nextPlayer;
        nextPlayer = buff;
    }

    /**
     Sets the current player.
     @param curPlayer The current player to set.
     */
    public void setCurPlayer(Player curPlayer) {
        this.curPlayer = curPlayer;
    }

    /**
     Gets the current player.
     @return The current player.
     */
    public Player getCurPlayer() {
        return curPlayer;
    }

    /**
     Sets the next player.
     @param nextPlayer The next player to set.
     */
    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    /**
     Gets the next player.
     @return The next player.
     */
    public Player getNextPlayer() {
        return nextPlayer;
    }
}


