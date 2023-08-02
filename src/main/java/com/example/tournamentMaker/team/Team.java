package com.example.tournamentMaker.team;

import com.example.tournamentMaker.statistics.Statistics;
import com.example.tournamentMaker.team.player.Player;
import com.example.tournamentMaker.tournament.Tournament;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@NoArgsConstructor
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;
    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String name;

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

    @OneToMany(
            mappedBy = "team",
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY
    )
    private List<Player> players = new ArrayList<>();

    public Team(String name, Tournament tournament) {
        this.name = name;
        this.tournament = tournament;
    }
}
