package org.games.xlspaceship.impl.services;

import lombok.RequiredArgsConstructor;
import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.model.FireRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.games.xlspaceship.impl.RestResources.jsonError;

@Service
@RequiredArgsConstructor
public class ValidationServices {

    public static final String MORE_THEN_5 = "More then 5 shots!";
    public static final String WRONG_TURN = "It's not your turn.";
    public static final String WRONG_SHOT_FORMAT = "Wrong format. Shot = '%s'.";
    public static final String WRONG_PORT_FORMAT = "Port should consist from numbers ('%s').";
    public static final String GAME_NOT_FOUND = "Game ('%s') is not found!";

    private static final Object lockShotByOpponent = new Object();
    private static final Object lockShotByMyself = new Object();

    private final XLSpaceshipServices xl;

    public ResponseEntity<?> validateFireRequest(FireRequest fireRequest) {
        if (fireRequest.getSalvo().size() > 5) {
            return jsonError(MORE_THEN_5, HttpStatus.NOT_FOUND);
        }
        for (String shot : fireRequest.getSalvo()) {
            if (shot.length() != 3) {
                return jsonError(String.format(WRONG_SHOT_FORMAT, shot), HttpStatus.NOT_FOUND);
            }
            int x = parse2radix10(shot.substring(0, 1));
            if (x < 0 || x > 15) {
                return jsonError(String.format(WRONG_SHOT_FORMAT, shot), HttpStatus.NOT_FOUND);
            }
            String temp2 = shot.substring(1, 2);
            if (!temp2.equalsIgnoreCase("x")) {
                return jsonError(String.format(WRONG_SHOT_FORMAT, shot), HttpStatus.NOT_FOUND);
            }
            int y = parse2radix10(shot.substring(2, 3));
            if (y < 0 || y > 15) {
                return jsonError(String.format(WRONG_SHOT_FORMAT, shot), HttpStatus.NOT_FOUND);
            }
        }
        return null;
    }

    private int parse2radix10(String value) {
        try {
            return Integer.parseInt(value, 16);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public ResponseEntity<?> validateRemotePort(String port) {
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            return jsonError(String.format(WRONG_PORT_FORMAT, port), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    public GameStatus getStatusByGameId(String gameId) {
        return xl.status(gameId);
    }

    public ResponseEntity<?> shotByOpponent(String gameId, FireRequest fireRequestFromOpponent) {
        boolean isExist = xl.isGameIdExist(gameId);
        if (!isExist) {
            return jsonError(String.format(GAME_NOT_FOUND, gameId), HttpStatus.BAD_REQUEST);
        }
        boolean turnIsAllowed = xl.isOpponentTurn(gameId);
        if (turnIsAllowed) {
            synchronized (lockShotByOpponent) {
                turnIsAllowed = xl.isOpponentTurn(gameId);
                if (turnIsAllowed) {
                    return new ResponseEntity<Object>(
                            xl.shotByOpponent(gameId, fireRequestFromOpponent),
                            HttpStatus.OK
                    );
                }
            }
        }
        return jsonError(WRONG_TURN, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> shotByMyself(String gameId, FireRequest fireRequestFromOpponent) {
        boolean isExist = xl.isGameIdExist(gameId);
        if (!isExist) {
            return jsonError(String.format(GAME_NOT_FOUND, gameId), HttpStatus.BAD_REQUEST);
        }
        boolean turnIsAllowed = xl.isMyselfTurn(gameId);
        if (turnIsAllowed) {
            synchronized (lockShotByMyself) {
                turnIsAllowed = xl.isMyselfTurn(gameId);
                if (turnIsAllowed) {
                    return new ResponseEntity<Object>(
                            xl.shotByMyself(gameId, fireRequestFromOpponent),
                            HttpStatus.OK
                    );
                }
            }
        }
        return jsonError(WRONG_TURN, HttpStatus.BAD_REQUEST);
    }
}
