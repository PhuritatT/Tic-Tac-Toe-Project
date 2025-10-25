package com.example.tictactoe.tictactoe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartGameRequestDTO {

    @Min(value = 3, message = "Board size must be at least 3")
    private int boardSize;

    @NotBlank(message = "Opponent type cannot be blank")
    private String opponentType;

    @NotBlank(message = "Player symbol choice cannot be blank")
    private String playerSymbol;

}
