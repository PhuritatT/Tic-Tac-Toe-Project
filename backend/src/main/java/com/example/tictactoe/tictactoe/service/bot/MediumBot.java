package com.example.tictactoe.tictactoe.service.bot;

import com.example.tictactoe.tictactoe.service.WinCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediumBot implements BotStrategy{

    private final EasyBot easyBot;
    private final WinCheckService winCheckService;

    @Override
    public BotMove calculateMove(
            String opponentType,
            int boardSize,
            String[] currentBoardState,
            String botPlayer
    ) {
        String humanPlayer = botPlayer.equals("X") ? "O" : "X";

        // TRY TO WIN
        BotMove winningMove = findMove(currentBoardState, boardSize, botPlayer);
        if (winningMove != null) {
            return winningMove;
        }

        // TRY TO BLOCK
        BotMove blockingMove = findMove(currentBoardState, boardSize, humanPlayer);
        if (blockingMove != null) {
            return blockingMove;
        }

        // JUST RANDOM
        return easyBot.calculateMove(opponentType, boardSize, currentBoardState, botPlayer);
    }

    private BotMove findMove(String[] board, int boardSize, String player) {
        for (int i = 0; i < board.length; i++) {
            if (board[i].equals("E")) {
                board[i] = player;
                int row = i / boardSize;
                int col = i % boardSize;

                boolean isWinningMove = winCheckService.checkWinner(board, boardSize, row, col, player);

                board[i] = "E";

                if (isWinningMove) {
                    return new BotMove(row, col);
                }
            }
        }
        return null;
    }

}
