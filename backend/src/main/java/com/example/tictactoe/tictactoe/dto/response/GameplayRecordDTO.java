package com.example.tictactoe.tictactoe.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameplayRecordDTO {

    private int moveNumber;
    private String player;
    private int rowIndex;
    private int colIndex;

}
