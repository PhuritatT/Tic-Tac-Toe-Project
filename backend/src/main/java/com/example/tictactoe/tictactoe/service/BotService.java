package com.example.tictactoe.tictactoe.service;

import com.example.tictactoe.tictactoe.service.bot.BotMove;
import com.example.tictactoe.tictactoe.service.bot.EasyBot;
import com.example.tictactoe.tictactoe.service.bot.HardBot;
import com.example.tictactoe.tictactoe.service.bot.MediumBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {

    private final EasyBot easyBot;
    private final MediumBot mediumBot;
    private final HardBot hardBot;

    public BotMove calculateMove(
            String opponentType,
            int boardSize,
            String[] currentBoardState,
            String botPlayer
    ) {
        switch (opponentType) {
            case "BOT_MEDIUM":
                return mediumBot.calculateMove(opponentType, boardSize, currentBoardState, botPlayer);
            case "BOT_HARD":
                return hardBot.calculateMove(opponentType, boardSize, currentBoardState, botPlayer);
            case "BOT_EASY" :
            default :
                return easyBot.calculateMove(opponentType, boardSize, currentBoardState, botPlayer);
        }
    }
}
