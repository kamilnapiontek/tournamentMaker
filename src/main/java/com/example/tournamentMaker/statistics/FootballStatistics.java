package com.example.tournamentMaker.statistics;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FOOTBALL")
public class FootballStatistics extends Statistics {
    // implementacja pól i metod specyficznych dla piłki nożnej
}
