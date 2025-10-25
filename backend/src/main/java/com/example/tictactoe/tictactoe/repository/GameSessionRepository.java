package com.example.tictactoe.tictactoe.repository;

import com.example.tictactoe.tictactoe.entity.GameSessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSessionModel, Long> {

    Optional<GameSessionModel> findBySessionId(String sessionId);

}
