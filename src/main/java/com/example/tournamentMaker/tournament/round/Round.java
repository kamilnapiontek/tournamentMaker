package com.example.tournamentMaker.tournament.round;

import com.example.tournamentMaker.tournament.Tournament;
import com.example.tournamentMaker.tournament.game.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    private Integer turn;
    @ManyToOne
    @JoinColumn(
            name = "tournament_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "tournament_fk"
            )
    )
    private Tournament tournament;
    @OneToMany(
            mappedBy = "round",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    private List<Game> games = new ArrayList<>();

    public Round(Integer turn, Tournament tournament) {
        this.turn = turn;
        this.tournament = tournament;
    }
}
