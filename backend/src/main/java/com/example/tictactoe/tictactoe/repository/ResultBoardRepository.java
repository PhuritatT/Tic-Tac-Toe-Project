package com.example.tictactoe.tictactoe.repository;

import com.example.tictactoe.tictactoe.entity.ResultBoardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultBoardRepository extends JpaRepository<ResultBoardModel, Long> {
}
