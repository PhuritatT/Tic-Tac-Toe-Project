package com.example.tictactoe.tictactoe.repository;

import com.example.tictactoe.tictactoe.entity.GameSessionModel;
import com.example.tictactoe.tictactoe.entity.GameplayRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameplayRecordRepository extends JpaRepository<GameplayRecordModel, Long> {

    int countByGameSession(GameSessionModel gameSession);

    List<GameplayRecordModel> findByGameSessionOrderByMoveNumberAsc(GameSessionModel gameSession);

}
