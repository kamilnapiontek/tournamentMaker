package com.example.tournamentmaker.statistics;

import com.example.tournamentmaker.team.Team;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
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
    private Integer points;
    private Integer countWins;
    private Integer countLoses;
    private Integer countDraws;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<MatchResult> recentMatchResults = new ArrayList<>();

    public Statistics(Team team, Integer countWins, Integer countLoses, Integer countDraws, Integer points) {
        this.team = team;
        this.countWins = countWins;
        this.countLoses = countLoses;
        this.countDraws = countDraws;
        this.points = points;
    }
}

