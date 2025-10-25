package com.example.tictactoe.tictactoe.service.bot;

public interface BotStrategy {

    BotMove calculateMove(
            String opponentType,
            int boardSize,
            String[] currentBoardState,
            String botPlayer
    );
}
