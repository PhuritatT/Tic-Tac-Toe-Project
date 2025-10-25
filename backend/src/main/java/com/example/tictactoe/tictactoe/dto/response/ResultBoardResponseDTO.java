package com.example.tictactoe.tictactoe.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResultBoardResponseDTO {

    private String gameSessionId;
    private int boardSize;
    private String winner;
    private String opponentType;
    private int totalMoves;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

}
