package com.example.tournamentMaker.statistics;

import com.example.tournamentMaker.tournament.Tournament;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "statistics")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "statistics_type")
@Getter
@Setter
public abstract class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(mappedBy = "statistics")
    private Tournament tournament;
}
