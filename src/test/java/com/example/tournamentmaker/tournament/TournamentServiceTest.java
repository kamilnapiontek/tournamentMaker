package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.constans.Constans;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.example.tournamentmaker.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.example.tournamentmaker.constans.Constans.MINIMUM_TEAMS_NUMBER;
import static com.example.tournamentmaker.util.Util.createTeams;
import static com.example.tournamentmaker.util.Util.createTournament;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {
    @InjectMocks
    private TournamentService tournamentService;
    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private CupSchedule cupSchedule;

    @Test
    void shouldReturnTrueWhenRegistrationSuccessful() {
        //given
        Tournament tournament = createTournament();
        String name = tournament.getName();
        //when
        when(tournamentRepository.findByName(name)).thenReturn(Optional.of(tournament));
        boolean success = tournamentService.finishRegistration(name);
        //then
        Assertions.assertTrue(success);
    }

    @Test
    void shouldContainExceptionWhenTournamentNameNotFound() {
        //given
        String name = "Tournament A";
        //when
        Assertions.assertThrows(NoSuchElementException.class, () -> tournamentService.finishRegistration(name));
    }

    @Test
    void shouldCreateCupSchedule() {
        // given
        Tournament tournament = createTournament();
        String tournamentName = tournament.getName();
        tournament.setTournamentType(TournamentType.CUP);
        int teamsNumber = 4;
        createTeams(tournament, teamsNumber);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        tournamentService.createSchedule(tournamentName);
        // then
        verify(cupSchedule, times(1)).createSchedule(any(Tournament.class));
    }

    @Test
    void shouldThrowExceptionWhenTeamsNumberBelowMinimumTeamsNumber() {
        // given
        Tournament tournament = createTournament();
        String tournamentName = tournament.getName();
        int tooFewTeamsNumber = MINIMUM_TEAMS_NUMBER - 1;
        createTeams(tournament, tooFewTeamsNumber);
        // when
        when(tournamentRepository.findByName(tournamentName)).thenReturn(Optional.of(tournament));
        Assertions.assertThrows(IllegalArgumentException.class, () -> tournamentService.createSchedule(tournamentName));
    }
}