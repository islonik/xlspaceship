package org.games.xlspaceship.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.games.xlspaceship.impl.game.GameTurn;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@JsonPropertyOrder({"salvo", "game"})
public class FireResponse {

    @JsonProperty("salvo")
    private Map<String, String> salvo = new LinkedHashMap<>();

    @JsonProperty("game")
    private GameTurn gameTurn;

}
