package com.example.tictactoe.tictactoe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "result_board")
public class ResultBoardModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer boardSize;

    @Column()
    private String winner;

    @Column(nullable = false)
    private String opponentType;

    @Column(nullable = false)
    private Integer totalMoves;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt;

    // RELATION
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id", nullable = false, unique = true)
    private GameSessionModel gameSession;

}
