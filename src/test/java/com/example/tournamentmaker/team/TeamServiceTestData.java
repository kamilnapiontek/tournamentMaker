package com.example.tournamentmaker.team;

import com.example.tournamentmaker.team.player.FootballPosition;
import com.example.tournamentmaker.tournament.Tournament;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TeamServiceTestData {
    static FootballTeamsAndPlayersRequest getFootballTeamsAndPlayersRequest(Tournament tournament) {
        return new FootballTeamsAndPlayersRequest(tournament.getName(),
                List.of(
                        createFootballTeamRequest("FC Barcelona", List.of(
                                createFootballPlayer("Lionel", "Messi", 10, FootballPosition.FORWARD),
                                createFootballPlayer("Andrés", "Iniesta", 8, FootballPosition.MIDFIELDER))),
                        createFootballTeamRequest("Real Madrid", List.of(
                                createFootballPlayer("Cristiano", "Ronaldo", 7, FootballPosition.FORWARD),
                                createFootballPlayer("Iker", "Casillas", 1, FootballPosition.GOALKEEPER)))));
    }

    static FootballTeamRequest createFootballTeamRequest(String teamName, List<FootballPlayerRequestWithoutGivingTeamName> list) {
        return new FootballTeamRequest(teamName, list);
    }

    private static FootballPlayerRequestWithoutGivingTeamName createFootballPlayer(
            String firstName, String lastName, Integer jerseyNumber, FootballPosition position) {
        return new FootballPlayerRequestWithoutGivingTeamName(firstName, lastName, jerseyNumber, position);
    }

    static FootballPlayerRequest createFootballPlayerRequest(String teamName, int jerseyNumber) {
        return new FootballPlayerRequest(
                teamName, "John", "Snow", jerseyNumber, "MIDFIELDER");
    }
}
