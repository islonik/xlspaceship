package org.games.xlspaceship.web.resources;

import lombok.extern.slf4j.Slf4j;
import org.games.xlspaceship.impl.model.FireRequest;
import org.games.xlspaceship.impl.model.NewGameRequest;
import org.games.xlspaceship.impl.model.NewGameResponse;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.impl.services.XLSpaceshipServices;
import org.games.xlspaceship.impl.RestResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = RestResources.PROTOCOL_PATH)
public class ProtocolResource {

    @Autowired
    private ValidationServices validationServices;

    @Autowired
    private XLSpaceshipServices xlSpaceshipServices;

    /*
    Content-Type : application/json

    POST http://localhost:8079/xl-spaceship/protocol/game/new
    {
        "user_id" : "nikilipa",
        "full_name" : "Nikita Lipatov",
        "spaceship_protocol" : {
            "hostname" : "127.0.0.0",
            "port" : 9001
        }
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
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public NewGameResponse createNewGame(@RequestBody NewGameRequest newGameRequest) {
        log.info("A new game request {}", newGameRequest);

        return xlSpaceshipServices.createRemoteGame(newGameRequest);
    }

    /*
    Content-Type : application/json

    POST http://localhost:8079/xl-spaceship/protocol/game/{gameId}
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
            value = "/game/{gameId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> shotByOpponent(
            @PathVariable("gameId") String gameId,
            @RequestBody FireRequest fireRequestByOpponent) {
        ResponseEntity<?> validResponse = validationServices.validateFireRequest(fireRequestByOpponent);
        if (validResponse != null) {
            return validResponse;
        }

        return validationServices.shotByOpponent(gameId, fireRequestByOpponent);
    }

}
