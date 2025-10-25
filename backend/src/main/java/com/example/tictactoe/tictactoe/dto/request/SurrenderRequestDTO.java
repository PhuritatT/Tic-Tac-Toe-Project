package com.example.tictactoe.tictactoe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SurrenderRequestDTO {

    @NotBlank(message = "Session ID cannot be blank")
    private String sessionId;

    @NotBlank(message = "Player cannot be blank")
    private String player;

}
