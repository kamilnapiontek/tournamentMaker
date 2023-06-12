package com.example.tournamentMaker.statistics;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BASKETBALL")
public class BasketballStatistics extends Statistics {
    // implementacja pól i metod specyficznych dla koszykówki
}
