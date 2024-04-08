package com.example.loa.Model;

public class Round {
    private int turnsPlayed = 0;
    private Player curPlayer = null;
    private Player nextPlayer = null;

    public Round() {}

    public void roundStart(Player p1, Player p2) {
        // Implementation of this method depends on the Player class
    }

    public void Score(Player winner, int numOpponentPieces) {
        // Implementation of this method depends on your requirements
    }

    public void SwitchPlayers() {
        Player buff = curPlayer;
        curPlayer = nextPlayer;
        nextPlayer = buff;
    }

    public int getTurnsPlayed() {
        return turnsPlayed;
    }

    public void setTurnsPlayed(int turnsPlayed) {
        this.turnsPlayed = turnsPlayed;
    }

    public void announceRoundWin(Player curPlayer, Player nextPlayer) {
        // Implementation of this method depends on your requirements
    }

    public void setCurPlayer(Player curPlayer) {
        this.curPlayer = curPlayer;
    }

    public Player getCurPlayer() {
        return curPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

}

