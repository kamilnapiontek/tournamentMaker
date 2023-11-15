package com.example.tournamentmaker;

import com.example.tournamentmaker.tournament.TournamentRepository;
import com.example.tournamentmaker.tournament.TournamentRequest;
import com.example.tournamentmaker.tournament.enums.Sport;
import com.example.tournamentmaker.tournament.enums.TournamentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TournamentIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TournamentRepository repository;

    @Test
    @WithMockUser(username = "user", authorities = {"admin:update"})
    void shouldCreateTournament() throws Exception {
        // given
        TournamentRequest request = new TournamentRequest("Tournament", Sport.FOOTBALL, TournamentType.LEAGUE);
        String mappedRequest = objectMapper.writeValueAsString(request);
        // when
        MvcResult result = mockMvc.perform(post("/api/tournament/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedRequest))
                .andExpect(status().isOk())
                .andReturn();
        // then

    }
}