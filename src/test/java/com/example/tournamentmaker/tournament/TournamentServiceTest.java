package com.example.tournamentmaker.tournament;

import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {
    @InjectMocks
    private TournamentService tournamentService;
    @Mock
    private TournamentRepository tournamentRepository;

    @Test
    void shouldReturnTrueWhenRegistrationSuccessful() {
        //given
        String name = "Tournament A";
        Optional<Tournament> tournament = Optional.of(new Tournament(name, TournamentType.CUP, Sport.FOOTBALL));
        //when
        when(tournamentRepository.findByName(name)).thenReturn(tournament);
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
}