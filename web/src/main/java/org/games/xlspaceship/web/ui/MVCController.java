package org.games.xlspaceship.web.ui;

import org.games.xlspaceship.impl.game.GameStatus;
import org.games.xlspaceship.impl.game.GameTurn;
import org.games.xlspaceship.impl.game.GridStatus;
import org.games.xlspaceship.impl.services.UserServices;
import org.games.xlspaceship.impl.services.ValidationServices;
import org.games.xlspaceship.web.ui.model.Pilot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
public class MVCController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private ValidationServices validationServices;

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

        boolean isWon = false;
        String attrName  = (gameTurn.getPlayerTurn() != null) ? "playerTurn" : "won";
        if (attrName.equalsIgnoreCase("won")) {
            isWon = true;
        }
        String attrValue = (gameTurn.getPlayerTurn() != null) ? gameTurn.getPlayerTurn() : gameTurn.getWon();
        return new ModelAndView(
                "game",
                "gameStatus",
                validationServices.getStatusByGameId(gameId)
        ).addObject(attrName, attrValue
        ).addObject("gameId", gameId
        ).addObject("aliveShips", gameStatus.getAliveShips()
        ).addObject("myGrid", myGridTable(gameStatus.getSelf())
        ).addObject("opponentGrid", opponentGridTable(gameStatus.getOpponent(), isWon)
        );
    }

    private String myGridTable(GridStatus gridStatus) {
        StringBuilder html = new StringBuilder();
        List<String> rows = gridStatus.getBoard();
        int y = 0;
        html.append("<table>");
        for (String row : rows) {
            html.append("<tr class=\"row\">");
            char[] chars = row.toCharArray();
            int x = 0;
            for (Character ch : chars) {
                String x16 = Integer.toString(x, 16);
                String y16 = Integer.toString(y, 16);
                String idValue = String.format("%sx%s", x16, y16);
                String value = Character.toString(ch);
                html.append(String.format("<td shot=\"%s\">", idValue));

                pictureName(html, value);

                html.append("</td>");
                x++;
            }
            html.append("</tr>");
            y++;
        }
        html.append("</table>");
        return html.toString();
    }

    private String opponentGridTable(GridStatus gridStatus, boolean isWon) {
        StringBuilder html = new StringBuilder();
        List<String> rows = gridStatus.getBoard();
        html.append("<table>");
        int y = 0;
        for (String row : rows) {
            html.append("<tr class=\"row\">");
            char[] chars = row.toCharArray();
            int x = 0;
            for (Character ch : chars) {
                String x16 = Integer.toString(x, 16);
                String y16 = Integer.toString(y, 16);
                String idValue = String.format("%sx%s", x16, y16);
                String value = Character.toString(ch);
                html.append(String.format("<td shot=\"%s\">", idValue));

                pictureName(html, idValue, value, isWon);

                html.append("</td>");
                x++;
            }
            html.append("</tr>");
            y++;
        }
        html.append("</table>");
        return html.toString();
    }

    private void pictureName(StringBuilder html, String value) {
        String temp;
        if (value.equalsIgnoreCase("*")) {
            temp = "ship.png";
        } else if (value.equalsIgnoreCase("x")) {
            temp = "sunk.png";
        } else if (value.equalsIgnoreCase("-")) {
            temp = "shot.png";
        } else {
            temp = "empty.png";
        }
        html.append(String.format("<img src=\"../../im/%s\"/>", temp));
    }

    private void pictureName(StringBuilder html, String idValue, String value, boolean isWon) {
        boolean isClickable = false;
        String temp;
        if (value.equalsIgnoreCase("*")) {
            temp = "ship.png";
        } else if (value.equalsIgnoreCase("x")) {
            temp = "sunk.png";
        } else if (value.equalsIgnoreCase("-")) {
            temp = "shot.png";
        } else {
            temp = "empty.png";
            if (!isWon) {
                isClickable = true;
            }
        }
        if (isClickable) {
            html.append(String.format("<img id=\"%s\" src=\"../../im/%s\" onclick='addShot(this);' />", idValue, temp));
        } else {
            html.append(String.format("<img id=\"%s\" src=\"../../im/%s\" />", idValue, temp));
        }
    }

}
