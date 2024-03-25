package org.games.xlspaceship.web.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.model.*;
import org.games.xlspaceship.impl.services.GridServices;
import org.games.xlspaceship.impl.services.RestServices;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.impl.services.XLSpaceshipServices;
import org.games.xlspaceship.impl.RestResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;

@Slf4j
@RestController
@Tag(name = "User", description = "List of APIs for user actions")
@RequestMapping(value = RestResources.USER_PATH)
@RequiredArgsConstructor
public class UserResource {

    private static final String CONNECTION_REFUSED = "Remote server not found by host('%s') and port('%s').";

    private final ValidationServices validationServices;
    private final RestServices restServices;
    private final XLSpaceshipServices xl;
    private final GridServices gridServices;

    /*
    GET http://localhost:8079/xl-spaceship/user/game/match-1

    @return GameStatus
     */
    @Operation(
            summary = "Get status by gameId.",
            description = "API to get status of the game."
    )
    @RequestMapping(
            value = "/game/{gameId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getStatusByGameId(
            @PathVariable("gameId") String gameId) {
        ResponseEntity<ErrorResponse> gameNotFoundResponse = validationServices.validateGameIdExist(gameId);
        return Objects.requireNonNullElseGet(gameNotFoundResponse, () -> new ResponseEntity<Object>(
                xl.status(gameId),
                HttpStatus.OK
        ));
    }

    /*
    Content-Type : application/json

    POST http://localhost:8079/xl-spaceship/user/game/new
    {
        "hostname" : "127.0.0.0",
        "port" : 9001
    }

    Json response:
    {
        "user_id": "AI",
        "full_name": "AI-72",
        "game_id": "match-1",
        "starting": "nikilipa"
    }

     */
    @Operation(
            summary = "Get create a new game.",
            description = "API to get a status of the game."
    )
    @RequestMapping(
            value = "/game/new",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public ResponseEntity<?> createNewGame(@RequestBody SpaceshipProtocol incomingSp) {
        ResponseEntity<?> validResponse = validationServices.validateRemotePort(incomingSp.getPort());
        if (validResponse != null) {
            return validResponse;
        }

        String removeHost = incomingSp.getHostname();
        int removePort = Integer.parseInt(incomingSp.getPort());

        return createNewGame(removeHost, removePort);
    }

    /*
    Content-Type : application/json

    GET http://localhost:8079/xl-spaceship/user/game/{gameId}/new

    Json response:
    {
        "user_id": "AI",
        "full_name": "AI-72",
        "game_id": "match-1",
        "starting": "nikilipa"
    }

     */
    @Operation(
            summary = "Create a new game based on the previous input.",
            description = "API to create a new game without providing host and port."
    )
    @RequestMapping(
            value = "/game/{gameId}/new",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createNewGame(
            @PathVariable("gameId") String gameId) {
        ResponseEntity<ErrorResponse> gameNotFoundResponse = validationServices.validateGameIdExist(gameId);
        if (gameNotFoundResponse == null) {
            GameStatus gameStatus = validationServices.getStatusByGameId(gameId);

            String name = gameStatus.getGameTurn().getWon();
            if (name == null) { // check if previous game ended or not
                return RestResources.jsonError(
                        ValidationServices.GAME_ALREADY_EXISTS.formatted(gameId),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        GameStatus gameStatus = validationServices.getStatusByGameId(gameId);

        String removeHost = gameStatus.getHost();
        int removePort = gameStatus.getPort();

        return createNewGame(removeHost, removePort);
    }

    private ResponseEntity<?> createNewGame(String removeHost, int removePort) {
        SpaceshipProtocol outgoingSp = new SpaceshipProtocol();
        outgoingSp.setHostname(restServices.getCurrentHostname());
        outgoingSp.setPort(Integer.toString(restServices.getCurrentPort()));

        log.info("remoteHost = {} remotePort = {} restServiceHost = {} restServicePort = {}",
                removeHost, removePort, restServices.getCurrentHostname(), restServices.getCurrentPort()
        );

        NewGameResponse newGameResponse;
        try {
            newGameResponse = restServices.sendPostNewGameRequest(removeHost, removePort, outgoingSp);
        } catch (RestClientException rce) {
            log.warn(rce.getMessage());
            if (rce.getCause() instanceof ConnectException) {
                return RestResources.jsonError(
                        String.format(CONNECTION_REFUSED, removeHost, removePort),
                        HttpStatus.BAD_REQUEST
                );
            } else {
                return RestResources.jsonError(
                        rce.getMessage(),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        String uniqueGameId = newGameResponse.getGameId();

        xl.createLocalGame(removeHost, removePort, uniqueGameId, newGameResponse);

        return new ResponseEntity<Object>(
                newGameResponse,
                HttpStatus.OK
        );
    }


    /*
    Content-Type : application/json

    POST http://localhost:8079/xl-spaceship/user/game/{gameId}
    {
        "salvo" : ["0x0", "8x4", "DxA", "AxA", "7xF"]
    }

    Json response:
    {
        "salvo" : {
            "0x0" : "hit",
            "8x4" : "hit",
            "DxA" : "kill",
            "AxA" : "miss",
            "7xF" : "miss"
        }
        "game" : {
            "player_turn" : "nikilipa"
        }
    }

     */
    @Operation(
            summary = "Shoot a barrage by a human.",
            description = "API to shoot a barrage by a human."
    )
    @RequestMapping(
            value = "/game/{gameId}/fire",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> fireInEnemy(
            @PathVariable("gameId") String gameId,
            @RequestBody FireRequest fireRequestByMyself) {
        ResponseEntity<ErrorResponse> gameNotFoundResponse = validationServices.validateGameIdExist(gameId);
        if (gameNotFoundResponse != null) {
            return gameNotFoundResponse;
        }

        ResponseEntity<?> validResponse = validationServices.validateFireRequest(fireRequestByMyself);
        if (validResponse != null) {
            return validResponse;
        }

        return validationServices.fireInEnemy(gameId, fireRequestByMyself);
    }

    /*
    Content-Type : application/json

    POST http://localhost:8079/xl-spaceship/user/game/{gameId}/status
    {
        "salvo" : ["0x0", "8x4", "DxA", "AxA", "7xF"]
    }

    Json response:
    {
        "grid": "<table><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"shot\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"shot\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"sunk\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"shot\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"ship\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr><tr class=\"row\"><td class=\"shot\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/><td class=\"empty\"/></tr></table>",
        "game": {
            "player_turn": "nikilipa"
        },
        "aliveShips": 5
    }

     */
    @Operation(
            summary = "Get my game status.",
            description = "API to get the latest grid for the player and turn."
    )
    @RequestMapping(
            value = "/game/{gameId}/status",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> statusRequest(
            @PathVariable("gameId") String gameId) {
        ResponseEntity<ErrorResponse> gameNotFoundResponse = validationServices.validateGameIdExist(gameId);
        if (gameNotFoundResponse != null) {
            return gameNotFoundResponse;
        }

        GameStatus gameStatus = validationServices.getStatusByGameId(gameId);

        GameStatusOutput output = new GameStatusOutput();
        output.setGrid(gridServices.getGridInHtml(gameStatus.getSelf(), false));
        output.setGame(gameStatus.getGameTurn());
        output.setAliveShips(gameStatus.getAliveShips());

        return new ResponseEntity<Object>(
                output,
                HttpStatus.OK
        );
    }

}
