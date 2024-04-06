package com.example.loa.Model;

import com.example.loa.BoardView;

import java.io.Serializable;

public class Tournament implements Serializable {
    private Human human;
    private Computer computer;
    private Round round;
    private Board board;
    public Tournament() {
        human = new Human();
        computer = new Computer();
        round = new Round();
        board = new Board();
    };
}
