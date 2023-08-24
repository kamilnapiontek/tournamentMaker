package com.example.tournamentmaker.statistics;

import com.example.tournamentmaker.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@NoArgsConstructor
@DiscriminatorValue("FOOTBALL")
@Getter
@Setter
public class FootballStatistics extends Statistics {
    private Integer goalsScored;
    private Integer goalsConceded;
    @ElementCollection
    @CollectionTable(name = "football_statistics_players_goals",
            joinColumns = @JoinColumn(name = "football_statistics_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "goals")
    private Map<Long, Integer> playersIdWithGoal = new HashMap<>();
    @ElementCollection
    @CollectionTable(name = "football_statistics_players_yellow_cards",
            joinColumns = @JoinColumn(name = "football_statistics_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "yellow_cards")
    private Map<Long, Integer> playersIdWithYellowCard;
    @ElementCollection
    @CollectionTable(name = "football_statistics_players_red_cards",
            joinColumns = @JoinColumn(name = "football_statistics_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "red_cards")
    private Map<Long, Integer> playersIdWithRedCard;
    @ElementCollection
    @CollectionTable(name = "football_statistics_players_clean_sheets",
            joinColumns = @JoinColumn(name = "football_statistics_id"))
    @MapKeyColumn(name = "player_id")
    @Column(name = "clean_sheets")
    private Map<Long, Integer> playersIdWithCleanSheets;

    public FootballStatistics(Team team) {
        super(team, 0, 0, 0);
        this.goalsScored = 0;
        this.goalsConceded = 0;
    }
}
