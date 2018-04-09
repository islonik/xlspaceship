package org.games.xlspaceship.impl.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"self", "opponent", "game"})
public class GameStatus {

    @JsonIgnore
    private String host;

    @JsonIgnore
    private int port;

    @JsonIgnore
    private int aliveShips;

    @JsonProperty("self")
    private GridStatus self;

    @JsonProperty("opponent")
    private GridStatus opponent;

    @JsonProperty("game")
    private GameTurn gameTurn;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getAliveShips() {
        return aliveShips;
    }

    public void setAliveShips(int aliveShips) {
        this.aliveShips = aliveShips;
    }

    public GridStatus getSelf() {
        return self;
    }

    public void setSelf(GridStatus self) {
        this.self = self;
    }

    public GridStatus getOpponent() {
        return opponent;
    }

    public void setOpponent(GridStatus opponent) {
        this.opponent = opponent;
    }

    public GameTurn getGameTurn() {
        return gameTurn;
    }

    public void setGameTurn(GameTurn gameTurn) {
        this.gameTurn = gameTurn;
    }

}
