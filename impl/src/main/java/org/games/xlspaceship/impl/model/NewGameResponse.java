package org.games.xlspaceship.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"user_id", "full_name", "game_id", "starting"})
public class NewGameResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("game_id")
    private String gameId;

    private String starting;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String user_id) {
        this.userId = user_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String full_name) {
        this.fullName = full_name;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String game_id) {
        this.gameId = game_id;
    }

    public String getStarting() {
        return starting;
    }

    public void setStarting(String starting) {
        this.starting = starting;
    }

    @Override
    public String toString() {
        return "NewGameResponse{" +
                "userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gameId='" + gameId + '\'' +
                ", starting='" + starting + '\'' +
                '}';
    }
}
