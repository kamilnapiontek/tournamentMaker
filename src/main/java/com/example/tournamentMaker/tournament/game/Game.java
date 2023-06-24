package com.example.tournamentMaker.tournament.game;

import com.example.tournamentMaker.statistics.MatchResult;
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
    private Long hostId;
    private Long guestId;
    private Integer hostPoints;
    private Integer guestPoints;
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

    public Game(Long hostId, Long guestId, Round round) {
        this.hostId = hostId;
        this.guestId = guestId;
        this.round = round;
    }

    public Game(Long hostId, Round round) {
        this.hostId = hostId;
        this.round = round;
    }
}
