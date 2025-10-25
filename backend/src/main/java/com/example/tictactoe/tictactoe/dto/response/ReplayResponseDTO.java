package com.example.tictactoe.tictactoe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReplayResponseDTO {
    private String humanPlayerSymbol;
    private Integer boardSize;
    private List<GameplayRecordDTO> moves;
}
