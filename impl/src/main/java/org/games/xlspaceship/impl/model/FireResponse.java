package org.games.xlspaceship.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.games.xlspaceship.impl.game.GameTurn;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonPropertyOrder({"salvo", "game"})
public class FireResponse {

    @JsonProperty("salvo")
    private Map<String, String> salvo = new LinkedHashMap<>();

    @JsonProperty("game")
    private GameTurn gameTurn;

    public Map<String, String> getSalvo() {
        return salvo;
    }

    public void setSalvo(Map<String, String> salvo) {
        this.salvo = salvo;
    }

    public GameTurn getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(GameTurn gameTurn) {
        this.gameTurn = gameTurn;
    }

}
