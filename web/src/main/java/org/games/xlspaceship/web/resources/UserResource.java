package org.games.xlspaceship.web.resources;

import org.games.xlspaceship.impl.model.FireRequest;
import org.games.xlspaceship.impl.model.NewGameResponse;
import org.games.xlspaceship.impl.model.SpaceshipProtocol;
import org.games.xlspaceship.impl.services.RestServices;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.impl.services.XLSpaceshipServices;
import org.games.xlspaceship.impl.RestResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;

@RestController
@RequestMapping(value = RestResources.USER_PATH)
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);
    private static final String CONNECTION_REFUSED = "Remote server not found by host('%s') and port('%s').";

    @Autowired
    private ValidationServices validationServices;

    @Autowired
    private RestServices restServices;

    @Autowired
    private XLSpaceshipServices xl;

    /*
    GET http://localhost:8079/xl-spaceship/user/game/match-1
     */
    @RequestMapping(
            value = "/game/{gameId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getStatusByGameId(
            @PathVariable String gameId) {

        boolean isExist = xl.isGameIdExist(gameId);
        if (!isExist) {
            return RestResources.jsonError(
                    String.format(ValidationServices.GAME_NOT_FOUND, gameId),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<Object>(
                xl.status(gameId),
                HttpStatus.OK
        );
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
    @RequestMapping(
            value = "/game/new",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public ResponseEntity<?> createNewGame(@RequestBody SpaceshipProtocol incomingSp) {
        ResponseEntity validResponse = validationServices.validateRemotePort(incomingSp.getPort());
        if (validResponse != null) {
            return validResponse;
        }

        String removeHost = incomingSp.getHostname();
        int removePort = Integer.parseInt(incomingSp.getPort());

        SpaceshipProtocol outgoingSp = new SpaceshipProtocol();
        outgoingSp.setHostname(restServices.getCurrentHostname());
        outgoingSp.setPort(Integer.toString(restServices.getCurrentPort()));

        NewGameResponse newGameResponse = null;
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
    @RequestMapping(
            value = "/game/{gameId}/fire",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> shotByMyself(
            @PathVariable String gameId,
            @RequestBody FireRequest fireRequestByMyself) {
        ResponseEntity validResponse = validationServices.validateFireRequest(fireRequestByMyself);
        if (validResponse != null) {
            return validResponse;
        }

        return validationServices.shotByMyself(gameId, fireRequestByMyself);
    }
}
