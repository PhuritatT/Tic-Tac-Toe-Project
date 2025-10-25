package com.example.tictactoe.tictactoe.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameStateResponseDTO {

    private String sessionId;
    private int boardSize;
    private String currentBoardState;
    private String nextPlayer;
    private String gameStatus;
    private String opponentType;

}
