package org.games.xlspaceship.web.resources;

import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.game.GameTurn;
import org.games.xlspaceship.impl.game.GridStatus;
import org.games.xlspaceship.impl.model.ErrorResponse;
import org.games.xlspaceship.impl.services.GridServices;
import org.games.xlspaceship.impl.services.RestServices;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.impl.services.XLSpaceshipServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

public class UserResourceTest {
    
    @Test
    public void testGetStatusByGameIdWhenGameExist() {
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
    }

    @Test
    public void testGetStatusByGameIdWhenGameDoesNotExist() {
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
    }


}
