package com.example.tictactoe.tictactoe.controller;

import com.example.tictactoe.tictactoe.dto.request.MoveRequestDTO;
import com.example.tictactoe.tictactoe.dto.request.StartGameRequestDTO;
import com.example.tictactoe.tictactoe.dto.request.SurrenderRequestDTO;
import com.example.tictactoe.tictactoe.dto.response.GameStateResponseDTO;
import com.example.tictactoe.tictactoe.dto.response.ReplayResponseDTO;
import com.example.tictactoe.tictactoe.dto.response.ResultBoardResponseDTO;
import com.example.tictactoe.tictactoe.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/start")
    public ResponseEntity<GameStateResponseDTO> createGame(
            @Valid @RequestBody StartGameRequestDTO requestDTO
    ) {
        GameStateResponseDTO gameState = gameService.createSession(requestDTO);
        return ResponseEntity.ok(gameState);
    }

    @PostMapping("/games/move")
    public ResponseEntity<GameStateResponseDTO> makeMove(
            @Valid @RequestBody MoveRequestDTO requestDTO
    ) {
        GameStateResponseDTO gameState = gameService.makeMove(requestDTO);
        return ResponseEntity.ok(gameState);
    }

    @GetMapping("/games/resume")
    public ResponseEntity<GameStateResponseDTO> resumeGame(
            @RequestParam String sessionId
    ) {
        GameStateResponseDTO gameState = gameService.resumeGame(sessionId);
        return ResponseEntity.ok(gameState);
    }

    @GetMapping("/results")
    public ResponseEntity<List<ResultBoardResponseDTO>> getResults() {
        List<ResultBoardResponseDTO> results = gameService.getResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/replays/{sessionId}")
    public ResponseEntity<ReplayResponseDTO> getReplay(
            @PathVariable String sessionId
    ) {
        ReplayResponseDTO replayData = gameService.getReplayData(sessionId);
        return ResponseEntity.ok(replayData);
    }

    @PostMapping("/games/surrender")
    public ResponseEntity<GameStateResponseDTO> surrenderGame(
            @Valid @RequestBody SurrenderRequestDTO requestDTO
    ) {
        GameStateResponseDTO gameState = gameService.surrenderGame(requestDTO);
        return ResponseEntity.ok(gameState);
    }

}
