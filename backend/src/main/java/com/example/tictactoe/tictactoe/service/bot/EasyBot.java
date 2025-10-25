package com.example.tictactoe.tictactoe.service.bot;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EasyBot implements BotStrategy {

    private final Random random = new Random();

    @Override
    public BotMove calculateMove(
            String opponentType,
            int boardSize,
            String[] currentBoardState,
            String botPlayer
    ) {
        List<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < currentBoardState.length; i++) {
            if (currentBoardState[i].equals("E")) {
                emptyCells.add(i);
            }
        }

        int randomIndex = emptyCells.get(random.nextInt(emptyCells.size()));

        int rowIndex = randomIndex / boardSize;
        int colIndex = randomIndex % boardSize;

        return new BotMove(rowIndex, colIndex);
    }

}
