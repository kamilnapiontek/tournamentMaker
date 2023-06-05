package com.example.tournamentMaker.tournament.game;

import com.example.tournamentMaker.statistics.Result;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.tournament.round.Round;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    private Integer hostsId;
    private Integer guestsId;
    @Enumerated(EnumType.STRING)
    private Result hostsResult;
    @ManyToOne
    @JoinColumn(
            name = "round_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "round_fk"
            )
    )
    private Round round;
}
