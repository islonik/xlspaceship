package org.games.xlspaceship.web.resources;

import org.games.xlspaceship.impl.game.Grid;
import org.games.xlspaceship.impl.model.NewGameRequest;
import org.games.xlspaceship.impl.model.NewGameResponse;
import org.games.xlspaceship.impl.model.SpaceshipProtocol;
import org.games.xlspaceship.impl.services.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ProtocolResourceTest {

    @Test
    public void testCreateNewGame() {
        UserServices userServices = mock(UserServices.class);
        GridServices gridServices = mock(GridServices.class);
        GameServices gameServices = mock(GameServices.class);
        RestServices restServices = mock(RestServices.class);
        AIServices aiServices = mock(AIServices.class);

        XLSpaceshipServices spaceshipServices = new XLSpaceshipServices(userServices, gridServices, gameServices, restServices, aiServices);
        ValidationServices validationServices = new ValidationServices(spaceshipServices);

        when(userServices.getUserId()).thenReturn("ai");
        when(userServices.getFullName()).thenReturn("Nikita");
        when(gameServices.nextUniqueGameId()).thenReturn("match-1-2");
        when(gridServices.newUnknownGrid()).thenReturn(new Grid());
        when(gridServices.newRandomGrid()).thenReturn(new Grid());
        when(gameServices.chooseStartingPlayer(anyString(), anyString())).thenReturn("ai");

        ProtocolResource protocolResource = new ProtocolResource(validationServices, spaceshipServices);
        NewGameResponse newGameResponse = protocolResource.createNewGame(createNewGameRequest());

        Assertions.assertNotNull(newGameResponse);
        Assertions.assertEquals("ai", newGameResponse.getUserId());
        Assertions.assertEquals("Nikita", newGameResponse.getFullName());
        Assertions.assertEquals("match-1-2", newGameResponse.getGameId());
        Assertions.assertEquals("ai", newGameResponse.getStarting());
    }

    private NewGameRequest createNewGameRequest() {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setFullName("Nikita");
        newGameRequest.setUserId("nl");
        SpaceshipProtocol sp = new SpaceshipProtocol();
        sp.setHostname("localhost");
        sp.setPort("8080");
        newGameRequest.setSpaceshipProtocol(sp);
        return newGameRequest;
    }
}
