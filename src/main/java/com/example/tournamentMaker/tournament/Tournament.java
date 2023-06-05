package com.example.tournamentMaker.tournament;

import com.example.tournamentMaker.statistics.Statistics;
import com.example.tournamentMaker.team.Team;
import com.example.tournamentMaker.tournament.enums.Sport;
import com.example.tournamentMaker.tournament.enums.Type;
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
    private Type type;
    @Enumerated(EnumType.STRING)
    private Sport sport;

    @OneToMany(
            mappedBy = "tournament",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    private List<Team> teamList = new ArrayList<>();

    @OneToOne(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "statistics_id",
            referencedColumnName = "id"
    )
    private Statistics statistics;

    public Tournament(String name, Type type, Sport sport) {
        this.name = name;
        this.type = type;
        this.sport = sport;
    }
}
