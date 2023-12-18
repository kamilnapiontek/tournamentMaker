package com.example.tournamentmaker.util;

import com.example.tournamentmaker.tournament.Tournament;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TournamentUtil {
    public static Tournament createTournament() {
        return new Tournament("Tournament A", TournamentType.LEAGUE, Sport.FOOTBALL);
    }

    public static Tournament creteTournament(String tournamentName) {
        return new Tournament(tournamentName, TournamentType.LEAGUE, Sport.FOOTBALL);
    }
}
