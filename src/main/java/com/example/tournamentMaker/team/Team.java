package com.example.tournamentMaker.team;

import com.example.tournamentMaker.tournament.Tournament;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@NoArgsConstructor
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

}
