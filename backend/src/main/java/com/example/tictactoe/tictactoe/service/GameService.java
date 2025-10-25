package com.example.tictactoe.tictactoe.service;

import com.example.tictactoe.tictactoe.dto.request.MoveRequestDTO;
import com.example.tictactoe.tictactoe.dto.request.StartGameRequestDTO;
import com.example.tictactoe.tictactoe.dto.request.SurrenderRequestDTO;
import com.example.tictactoe.tictactoe.dto.response.GameStateResponseDTO;
import com.example.tictactoe.tictactoe.dto.response.GameplayRecordDTO;
import com.example.tictactoe.tictactoe.dto.response.ReplayResponseDTO;
import com.example.tictactoe.tictactoe.dto.response.ResultBoardResponseDTO;
import com.example.tictactoe.tictactoe.entity.GameSessionModel;
import com.example.tictactoe.tictactoe.entity.GameplayRecordModel;
import com.example.tictactoe.tictactoe.entity.ResultBoardModel;
import com.example.tictactoe.tictactoe.repository.GameSessionRepository;
import com.example.tictactoe.tictactoe.repository.GameplayRecordRepository;
import com.example.tictactoe.tictactoe.repository.ResultBoardRepository;
import com.example.tictactoe.tictactoe.service.bot.BotMove;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameSessionRepository gameSessionRepository;
    private final GameplayRecordRepository gameplayRecordRepository;
    private final ResultBoardRepository resultBoardRepository;

    private final BotService botService;
    private final WinCheckService winCheckService;

    public GameStateResponseDTO createSession(StartGameRequestDTO request) {
        String emptyBoardState = this.createEmptyBoard(request.getBoardSize());
        GameSessionModel session = GameSessionModel.builder()
                .sessionId(UUID.randomUUID().toString())
                .boardSize(request.getBoardSize())
                .opponentType(request.getOpponentType())
                .humanPlayerSymbol(request.getPlayerSymbol())
                .opponentType(request.getOpponentType())
                .nextPlayer("X")
                .gameStatus("IN_PROGRESS")
                .currentBoardState(emptyBoardState)
                .build();
        GameSessionModel savedSession = gameSessionRepository.save(session);
        if(request.getOpponentType().startsWith("BOT") && request.getPlayerSymbol().equals("O")) {
            String botPlayer = "X";
            BotMove firstMove = botService.calculateMove(
                    savedSession.getOpponentType(),
                    savedSession.getBoardSize(),
                    this.boardStringToArray(savedSession.getCurrentBoardState()),
                    botPlayer
            );
            savedSession = performMove(savedSession, firstMove.rowIndex(), firstMove.colIndex(), botPlayer);
        }
        return this.mapToGameStateDTO(savedSession);
    }

    public GameStateResponseDTO makeMove(MoveRequestDTO request) {
        GameSessionModel session = findSession(request.getSessionId());
        GameSessionModel sessionAfterHumanMove = performMove(
                session,
                request.getRowIndex(),
                request.getColIndex(),
                request.getPlayer()
        );
        if (!sessionAfterHumanMove.getGameStatus().equals("IN_PROGRESS")) {
            return mapToGameStateDTO(sessionAfterHumanMove);
        }
        if (sessionAfterHumanMove.getOpponentType().startsWith("BOT")) {
            String botPlayer = sessionAfterHumanMove.getNextPlayer();
            BotMove botMove = botService.calculateMove(
                    session.getOpponentType(),
                    sessionAfterHumanMove.getBoardSize(),
                    boardStringToArray(sessionAfterHumanMove.getCurrentBoardState()),
                    botPlayer
            );
            GameSessionModel sessionAfterBotMove = performMove(
                    sessionAfterHumanMove,
                    botMove.rowIndex(),
                    botMove.colIndex(),
                    botPlayer
            );
            return mapToGameStateDTO(sessionAfterBotMove);
        }
        return mapToGameStateDTO(sessionAfterHumanMove);
    }

    public GameStateResponseDTO surrenderGame(SurrenderRequestDTO request) {
        GameSessionModel session = findSession(request.getSessionId());
        String winner = request.getPlayer().equals("X") ? "O" : "X";
        GameSessionModel endedSession = handleGameEnd(session, winner);
        return mapToGameStateDTO(endedSession);
    }

    public GameStateResponseDTO resumeGame(String sessionId) {
        GameSessionModel session = findSession(sessionId);
        return mapToGameStateDTO(session);
    }

    public ReplayResponseDTO getReplayData(String sessionId) {
        GameSessionModel session = findSession(sessionId);
        List<GameplayRecordModel> records = gameplayRecordRepository.findByGameSessionOrderByMoveNumberAsc(session);
        List<GameplayRecordDTO> moveDTOs = records.stream()
                .map(record -> GameplayRecordDTO.builder()
                        .moveNumber(record.getMoveNumber())
                        .player(record.getPlayer())
                        .rowIndex(record.getRowIndex())
                        .colIndex(record.getColIndex())
                        .build())
                .collect(Collectors.toList());
        return ReplayResponseDTO.builder()
                .boardSize(session.getBoardSize())
                .humanPlayerSymbol(session.getHumanPlayerSymbol())
                .moves(moveDTOs)
                .build();
    }

    public List<ResultBoardResponseDTO> getResults() {
        List<ResultBoardModel> results = resultBoardRepository.findAll(Sort.by(Sort.Direction.DESC, "completedAt"));
        return results.stream()
                .map(result -> ResultBoardResponseDTO.builder()
                        .gameSessionId(result.getGameSession().getSessionId())
                        .boardSize(result.getBoardSize())
                        .winner(result.getWinner())
                        .opponentType(result.getOpponentType())
                        .totalMoves(result.getTotalMoves())
                        .completedAt(result.getCompletedAt())
                        .build())
                .collect(Collectors.toList());
    }

    //region HELPER
    private GameSessionModel performMove(GameSessionModel session, int row, int col, String player) {
        if (!session.getGameStatus().equals("IN_PROGRESS")) {
            throw new IllegalStateException("Game is already over.");
        }
        if (!session.getNextPlayer().equals(player)) {
            throw new IllegalStateException("It's not player " + player + "'s turn.");
        }

        String[] boardArray = boardStringToArray(session.getCurrentBoardState());
        int index = row * session.getBoardSize() + col;

        if (!boardArray[index].equals("E")) {
            throw new IllegalStateException("Cell [" + row + "," + col + "] is not empty.");
        }

        // BOARD UPDATE
        boardArray[index] = player;
        session.setCurrentBoardState(boardArrayToString(boardArray));

        // RECORD MOVE
        int moveNumber = gameplayRecordRepository.countByGameSession(session) + 1;
        GameplayRecordModel record = GameplayRecordModel.builder()
                .gameSession(session)
                .moveNumber(moveNumber)
                .player(player)
                .rowIndex(row)
                .colIndex(col)
                .build();
        gameplayRecordRepository.save(record);

        // WIN CHECK
        boolean hasWon = winCheckService.checkWinner(
                boardArray,
                session.getBoardSize(),
                row,
                col,
                player
        );

        if (hasWon) {
            return handleGameEnd(session, player);
        }

        // CHECK IMPOSSIBLE TO WIN && BOARD FULL [DRAW]
        boolean isWinningStillPossible = true;
        boolean boardIsFull = !Arrays.asList(boardArray).contains("E");
        if (!boardIsFull) {
            isWinningStillPossible = !winCheckService.isWinningStillPossible(boardArray, session.getBoardSize());
        }
        if (boardIsFull || isWinningStillPossible) {
            return handleGameEnd(session, "DRAW");
        }

        // NEST TURN SETTING
        session.setNextPlayer(player.equals("X") ? "O" : "X");
        return gameSessionRepository.save(session);
    }

    private GameSessionModel handleGameEnd(GameSessionModel session, String winner) {
        session.setGameStatus(winner.equals("DRAW") ? "DRAW" : winner + "_WINS");
        session.setNextPlayer(null);

        ResultBoardModel result = ResultBoardModel.builder()
                .gameSession(session)
                .boardSize(session.getBoardSize())
                .winner(winner.equals("DRAW") ? null : winner)
                .opponentType(session.getOpponentType())
                .totalMoves(gameplayRecordRepository.countByGameSession(session))
                .completedAt(LocalDateTime.now())
                .build();

        resultBoardRepository.save(result);

        return gameSessionRepository.save(session);
    }

    private GameStateResponseDTO mapToGameStateDTO(GameSessionModel session) {
        return GameStateResponseDTO.builder()
                .sessionId(session.getSessionId())
                .boardSize(session.getBoardSize())
                .currentBoardState(session.getCurrentBoardState())
                .nextPlayer(session.getNextPlayer())
                .gameStatus(session.getGameStatus())
                .opponentType(session.getOpponentType())
                .build();
    }

    private GameSessionModel findSession(String sessionId) {
        return gameSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Game not found with session ID: " + sessionId));
    }
    //endregion

    //region UTILITY
    private String createEmptyBoard(int boardSize) {
        int totalCells = boardSize * boardSize;
        return String.join(",", Collections.nCopies(totalCells, "E"));
    }

    private String[] boardStringToArray(String boardState) {
        return boardState.split(",");
    }

    private String boardArrayToString(String[] boardArray) {
        return String.join(",", boardArray);
    }
    //endregion

}
