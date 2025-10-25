package com.example.tictactoe.tictactoe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "game_session")
public class GameSessionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private Integer boardSize;

    @Lob
    @Column(nullable = false)
    private String currentBoardState;

    @Column()
    private String nextPlayer;

    @Column(nullable = false)
    private String gameStatus;

    @Column(nullable = false)
    private String opponentType;

    @Column(nullable = false)
    private String humanPlayerSymbol;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // RELATION
    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameplayRecordModel> gameMoves;

    @OneToOne(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResultBoardModel result;

}
