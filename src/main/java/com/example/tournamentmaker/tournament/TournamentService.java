package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.tournament.result.FootballResultRequest;
import com.example.tournamentmaker.tournament.result.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.tournamentmaker.constans.Constans.MINIMUM_TEAMS_NUMBER;
import static com.example.tournamentmaker.constans.Constans.NO_TOURNAMENT_FOUND;

@Service
@RequiredArgsConstructor
class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final LeagueSchedule leagueSchedule;
    private final CupSchedule cupSchedule;
    private final ResultService resultService;

    void createTournament(TournamentRequest request) {
        Tournament tournament = new Tournament(
                request.name(), TournamentType.valueOf(request.tournamentType()), Sport.valueOf(request.sport()));

        tournamentRepository.save(tournament);
    }

    boolean finishRegistration(String tournamentName) {
        Optional<Tournament> optionalTournament = tournamentRepository.findByName(tournamentName);
        optionalTournament.ifPresentOrElse(tournament -> {
                    tournament.setRegistrationCompleted(true);
                    tournamentRepository.save(tournament);
                },
                () -> {
                    throw new NoSuchElementException(NO_TOURNAMENT_FOUND);
                });
        return true;
    }

    void createSchedule(String tournamentName) {
        Optional<Tournament> optionalTournament = tournamentRepository.findByName(tournamentName);
        optionalTournament.ifPresentOrElse(tournament -> {
            if (tournament.getTeamList().size() < MINIMUM_TEAMS_NUMBER) {
                throw new IllegalArgumentException("Tournament does not have the required number of teams");
            }
            tournament.setRegistrationCompleted(true);
            tournamentRepository.save(tournament);
            createScheduleDependingOnType(tournament);
        }, () -> {
            throw new NoSuchElementException(NO_TOURNAMENT_FOUND);
        });
    }

    private void createScheduleDependingOnType(Tournament tournament) {
        TournamentType type = tournament.getTournamentType();
        if (type == TournamentType.CUP) {
            cupSchedule.createSchedule(tournament);
        } else if (type == TournamentType.LEAGUE) {
            leagueSchedule.createSchedule(tournament);
        }
    }

    void launchFootballResult(FootballResultRequest footballResultRequest) {
        resultService.launchFootballResult(footballResultRequest);
    }
}
