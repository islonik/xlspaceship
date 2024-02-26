package org.games.xlspaceship.web.ui;

import lombok.RequiredArgsConstructor;
import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.game.GameTurn;
import org.games.xlspaceship.impl.services.GridServices;
import org.games.xlspaceship.impl.services.UserServices;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.web.ui.model.Pilot;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MVCController {

    private final UserServices userServices;
    private final ValidationServices validationServices;
    private final GridServices gridServices;

    @GetMapping
    public ModelAndView index() {
        Pilot pilot = new Pilot();
        pilot.setUserId(userServices.getUserId());
        pilot.setFullName(userServices.getFullName());
        return new ModelAndView("index", "pilot", pilot);
    }

    @GetMapping("gameId/{gameId}")
    public ModelAndView gameId(@PathVariable("gameId") String gameId) {
        GameStatus gameStatus = validationServices.getStatusByGameId(gameId);

        GameTurn gameTurn = gameStatus.getGameTurn();

        boolean isClickable = true;
        String attrName  = (gameTurn.getPlayerTurn() != null) ? "playerTurn" : "won";
        if (attrName.equalsIgnoreCase("won")) {
            isClickable = false;
        }
        String attrValue = (gameTurn.getPlayerTurn() != null) ? gameTurn.getPlayerTurn() : gameTurn.getWon();
        return new ModelAndView(
                "game",
                "gameStatus",
                validationServices.getStatusByGameId(gameId)
        ).addObject(attrName, attrValue
        ).addObject("gameId", gameId
        ).addObject("aliveShips", gameStatus.getAliveShips()
        ).addObject("myGrid", gridServices.getGridInHtml(gameStatus.getSelf(), false)
        ).addObject("opponentGrid", gridServices.getGridInHtml(gameStatus.getOpponent(), isClickable)
        );
    }



}
