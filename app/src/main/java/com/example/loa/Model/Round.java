package com.example.loa.Model;

public class Round {
    private int turnsPlayed = 0;
    private Player curPlayer = null;
    private Player nextPlayer = null;
    private boolean suspend = false;
    private boolean resumingGame = false;
    private boolean playAgain = false;

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

    public void PlayAgain() {
        // Implementation of this method depends on your requirements
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

    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
    }

    public boolean getSuspend() {
        return suspend;
    }

    public void setResumingGame(boolean resumingGame) {
        this.resumingGame = resumingGame;
    }

    public boolean getResumingGame() {
        return resumingGame;
    }

    public void setPlayAgain(boolean playAgain) {
        this.playAgain = playAgain;
    }

    public boolean getPlayAgain() {
        return playAgain;
    }

    public void suspendGame() {
        // Implementation of this method depends on your requirements
    }

    public boolean resumeGame() {
        // Implementation of this method depends on your requirements
        return false; // Placeholder return value
    }

    public void loadGameState(Board board, Player human, Player computer) {
        // Implementation of this method depends on the Board and Player classes
    }

    public String getPath() {
        // Implementation of this method depends on your requirements
        return ""; // Placeholder return value
    }
}

