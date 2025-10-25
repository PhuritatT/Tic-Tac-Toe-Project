package com.example.tictactoe.tictactoe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MoveRequestDTO {

    @NotBlank(message = "Session ID cannot be blank")
    private String sessionId;

    @Min(value = 0, message = "Row index must be 0 or greater")
    private int rowIndex;

    @Min(value = 0, message = "Column index must be 0 or greater")
    private int colIndex;

    @NotBlank(message = "Player cannot be blank")
    private String player;

}
