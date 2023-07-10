package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.TournamentType;
import com.example.tournamentMaker.tournament.round.Round;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@NoArgsConstructor
@Getter
@Setter
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TournamentType tournamentType;
    @Enumerated(EnumType.STRING)
    private Sport sport;
    private boolean registrationCompleted = false;

    @OneToMany(
            mappedBy = "tournament",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    private List<Team> teamList = new ArrayList<>();

    @OneToMany(
            mappedBy = "tournament",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    private List<Round> rounds = new ArrayList<>();

    public Tournament(String name, TournamentType tournamentType, Sport sport) {
        this.name = name;
        this.tournamentType = tournamentType;
        this.sport = sport;
    }
}
