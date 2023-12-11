package org.games.xlspaceship.web;

import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.game.GridStatus;
import org.games.xlspaceship.impl.model.*;
import org.games.xlspaceship.impl.services.RestServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                ApplicationTest.AI_PORT
        }
)
public class ApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

    public static final String REMOTE_PORT = "8056";

    public static final String AI_PORT = "server.port=" + REMOTE_PORT;
    public static final String CONNECTION_REFUSED = "Remote server not found by host('127.0.0.1') and port('8001').";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestServices restServices;

    @Test
    public void testCreateGameByUserAPICase01() throws Exception {
        SpaceshipProtocol sp = new SpaceshipProtocol();
        sp.setHostname("127.0.0.1");
        sp.setPort(REMOTE_PORT);

        NewGameResponse ngr = this.restTemplate.postForObject("/xl-spaceship/user/game/new", sp, NewGameResponse.class);
        Assertions.assertNotNull(ngr);
        Assertions.assertEquals("AI", ngr.getUserId());
        Assertions.assertEquals("AI-1000", ngr.getFullName());
        Assertions.assertEquals("match-1-3-6", ngr.getGameId());

        GameStatus gameStatus = this.restTemplate.getForObject("/xl-spaceship/user/game/%s".formatted(ngr.getGameId()), GameStatus.class);

        Assertions.assertNotNull(gameStatus);
        Assertions.assertNotNull(gameStatus.getSelf());
        GridStatus myGrid = gameStatus.getSelf();

        Assertions.assertEquals(
                "...**......**...\n" +
                        "...*.*....*.....\n" +
                        "...**......**...\n" +
                        "...*.*.......*..\n" +
                        "...**......**...\n" +
                        "................\n" +
                        "...*.*..........\n" +
                        "...*.*.....*....\n" +
                        "....*.....*.*...\n" +
                        "...*.*....***...\n" +
                        "...*.*....*.*...\n" +
                        "................\n" +
                        ".......*........\n" +
                        ".......*........\n" +
                        ".......*........\n" +
                        ".......***......\n",
                getMyGrid(myGrid)
        );
    }

    @Test
    public void testCreateGameByUserAPICase02() throws Exception {
        String gameId = "match-1-3-6";
        GameStatus gameStatus = this.restTemplate.getForObject("/xl-spaceship/user/game/{gameId}", GameStatus.class, gameId);

        Assertions.assertNotNull(gameStatus);

        FireRequest fireRequest = new FireRequest();
        List<String> salvoList = new ArrayList<>();
        salvoList.add("0x0");
        salvoList.add("1x1");
        salvoList.add("2x2");
        salvoList.add("3x3");
        salvoList.add("4x4");
        fireRequest.setSalvo(salvoList);

        FireResponse fireResponse = restServices.fireShotByAi("127.0.0.1", Integer.parseInt(REMOTE_PORT), gameId, fireRequest);
        Assertions.assertNotNull(fireResponse);
        Assertions.assertNotNull("miss", fireResponse.getSalvo().get("0x0"));
        Assertions.assertNotNull("miss", fireResponse.getSalvo().get("1x1"));
        Assertions.assertNotNull("miss", fireResponse.getSalvo().get("2x2"));
        Assertions.assertNotNull("hit", fireResponse.getSalvo().get("3x3"));
        Assertions.assertNotNull("hit", fireResponse.getSalvo().get("4x4"));
    }

    @Test
    public void testCreateGameByUserAPICase03() throws Exception {
        SpaceshipProtocol sp = new SpaceshipProtocol();
        sp.setHostname("127.0.0.1");
        sp.setPort("8001"); // fake port
        ErrorResponse error = this.restTemplate.postForObject("/xl-spaceship/user/game/new", sp, ErrorResponse.class);
        Assertions.assertNotNull(error);
        Assertions.assertEquals(CONNECTION_REFUSED, error.getError());
    }

    private String getMyGrid(GridStatus gridStatus) {
        StringBuilder grid = new StringBuilder();
        for (String row : gridStatus.getBoard()) {
            grid.append(row);
            grid.append("\n");
        }
        return grid.toString();
    }

}
