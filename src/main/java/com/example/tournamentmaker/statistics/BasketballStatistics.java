package com.example.tournamentmaker.statistics;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BASKETBALL")
public class BasketballStatistics extends Statistics {
    // the project has been implemented in a way that allows for easy expansion with additional sports
}
