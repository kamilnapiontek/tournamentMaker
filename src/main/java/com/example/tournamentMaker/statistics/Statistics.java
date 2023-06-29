package com.example.tournamentMaker.statistics;

import com.example.tournamentMaker.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "statistics")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "statistics_type")
@Getter
@Setter
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(mappedBy = "statistics")
    private Team team;
    private Integer countWins;
    private Integer countLoses;
    private Integer countDraws;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<MatchResult> recentMatchResults = new ArrayList<>();

    Statistics(Team team, Integer countWins, Integer countLoses, Integer countDraws) {
        this.team = team;
        this.countWins = countWins;
        this.countLoses = countLoses;
        this.countDraws = countDraws;
    }
}
