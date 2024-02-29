package org.games.xlspaceship.web.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.game.GameTurn;
import org.games.xlspaceship.impl.game.GridStatus;
import org.games.xlspaceship.impl.model.ErrorResponse;
import org.games.xlspaceship.impl.model.FireRequest;
import org.games.xlspaceship.impl.model.SpaceshipProtocol;
import org.games.xlspaceship.impl.services.GridServices;
import org.games.xlspaceship.impl.services.RestServices;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.impl.services.XLSpaceshipServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class UserResourceTest {
    
    @Test
    public void testGetStatusByGameIdWhenGameExist() throws Exception {
        var validServices = mock(ValidationServices.class);
        var restServices = mock(RestServices.class);
        var shipServices = mock(XLSpaceshipServices.class);
        var gridServices = mock(GridServices.class);

        GameTurn gameTurn = new GameTurn();
        gameTurn.setPlayerTurn("nikilipa");

        GridStatus myStatus = new GridStatus();
        myStatus.setUserId("nikilipa");
        GridStatus aiStatus = new GridStatus();
        aiStatus.setUserId("ai-500");

        GameStatus expectedGameStatus = new GameStatus();
        expectedGameStatus.setHost("localhost");
        expectedGameStatus.setPort(8080);
        expectedGameStatus.setAliveShips(5);
        expectedGameStatus.setGameTurn(gameTurn);
        expectedGameStatus.setSelf(myStatus);
        expectedGameStatus.setOpponent(aiStatus);

        when(shipServices.isGameIdExist(anyString())).thenReturn(true);
        when(shipServices.status(anyString())).thenReturn(expectedGameStatus);

        UserResource userResource = new UserResource(
                validServices, restServices, shipServices, gridServices
        );

        ResponseEntity<?> actualGameStatus = userResource.getStatusByGameId("game-1-2-3");
        Assertions.assertNotNull(actualGameStatus);
        Assertions.assertInstanceOf(GameStatus.class, actualGameStatus.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals("""
                {
                  "self" : {
                    "user_id" : "nikilipa",
                    "board" : [ ]
                  },
                  "opponent" : {
                    "user_id" : "ai-500",
                    "board" : [ ]
                  },
                  "game" : {
                    "player_turn" : "nikilipa"
                  }
                }""",
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(actualGameStatus.getBody())
        );
    }

    @Test
    public void testGetStatusByGameIdWhenGameDoesNotExist() throws Exception {
        var validServices = mock(ValidationServices.class);
        var restServices = mock(RestServices.class);
        var shipServices = mock(XLSpaceshipServices.class);
        var gridServices = mock(GridServices.class);

        when(shipServices.isGameIdExist(anyString())).thenReturn(false);

        UserResource userResource = new UserResource(
                validServices, restServices, shipServices, gridServices
        );

        ResponseEntity<?> actualGameStatus = userResource.getStatusByGameId("game-1-2-3");
        Assertions.assertNotNull(actualGameStatus);
        Assertions.assertInstanceOf(ErrorResponse.class, actualGameStatus.getBody());
        Assertions.assertEquals("Game ('game-1-2-3') is not found!",
                ((ErrorResponse) actualGameStatus.getBody()).getError());
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals("""
                {
                  "error_message" : "Game ('game-1-2-3') is not found!"
                }""",
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(actualGameStatus.getBody())
        );
    }

    @Test
    public void testCreateNewGameWithWrongPort() throws Exception {
        var restServices = mock(RestServices.class);
        var shipServices = mock(XLSpaceshipServices.class);
        var gridServices = mock(GridServices.class);

        var validServices = new ValidationServices(shipServices);

        when(shipServices.isGameIdExist(anyString())).thenReturn(false);

        UserResource userResource = new UserResource(
                validServices, restServices, shipServices, gridServices
        );

        SpaceshipProtocol sp = new SpaceshipProtocol();
        sp.setHostname("localhost");
        sp.setPort("port");

        ResponseEntity<?> errorResponse = userResource.createNewGame(sp);

        Assertions.assertNotNull(errorResponse);
        Assertions.assertInstanceOf(ErrorResponse.class, errorResponse.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals("""
                {
                  "error_message" : "Port should consist from numbers ('port')."
                }""",
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(errorResponse.getBody())
        );
    }

    @Test
    public void testCreateNewGameWithGatewayTimeout() throws Exception {
        var restServices = mock(RestServices.class);
        var shipServices = mock(XLSpaceshipServices.class);
        var gridServices = mock(GridServices.class);

        var validServices = new ValidationServices(shipServices);

        GameTurn gameTurn = new GameTurn();
        gameTurn.setPlayerTurn("nikilipa");

        GridStatus myStatus = new GridStatus();
        myStatus.setUserId("nikilipa");
        GridStatus aiStatus = new GridStatus();
        aiStatus.setUserId("ai-500");

        GameStatus expectedGameStatus = new GameStatus();
        expectedGameStatus.setHost("localhost");
        expectedGameStatus.setPort(8080);
        expectedGameStatus.setAliveShips(5);
        expectedGameStatus.setGameTurn(gameTurn);
        expectedGameStatus.setSelf(myStatus);
        expectedGameStatus.setOpponent(aiStatus);

        when(shipServices.status(anyString())).thenReturn(expectedGameStatus);
        when(restServices.getCurrentHostname()).thenReturn("127.0.0.1");
        when(restServices.getCurrentPort()).thenReturn(8081);
        when(restServices.sendPostNewGameRequest(anyString(), anyInt(), any()))
                .thenThrow(HttpServerErrorException.create(
                        "Remote server timed out.",
                        HttpStatus.GATEWAY_TIMEOUT,
                        "Gateway timeout",
                        new HttpHeaders(),
                        "".getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                        )
                );

        UserResource userResource = new UserResource(
                validServices, restServices, shipServices, gridServices
        );

        ResponseEntity<?> errorResponse = userResource.createNewGame("match-1-3-6");

        Assertions.assertNotNull(errorResponse);
        Assertions.assertInstanceOf(ErrorResponse.class, errorResponse.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals("""
                {
                  "error_message" : "Remote server timed out."
                }""",
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(errorResponse.getBody())
        );
    }

    @Test
    void testFireRequestMoreThanFive() throws JsonProcessingException {
        var restServices = mock(RestServices.class);
        var shipServices = mock(XLSpaceshipServices.class);
        var gridServices = mock(GridServices.class);

        var validServices = new ValidationServices(shipServices);

        when(shipServices.isGameIdExist(anyString())).thenReturn(false);

        UserResource userResource = new UserResource(
                validServices, restServices, shipServices, gridServices
        );

        List<String> salvo6 = new ArrayList<>();
        salvo6.add("0x0");
        salvo6.add("1x1");
        salvo6.add("2x2");
        salvo6.add("3x3");
        salvo6.add("4x4");
        salvo6.add("5x5");
        FireRequest fireRequestMoreThanFive = new FireRequest();
        fireRequestMoreThanFive.setSalvo(salvo6);

        ResponseEntity<?> fireResponseMoreThanFive = userResource.fireRequest(
                "match-1-1",
                fireRequestMoreThanFive);

        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals("""
                {
                  "error_message" : "More then 5 shots!"
                }""",
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(fireResponseMoreThanFive.getBody())
        );
    }


}
