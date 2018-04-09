package org.games.xlspaceship.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewGameRequest {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("spaceship_protocol")
    private SpaceshipProtocol spaceshipProtocol;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public SpaceshipProtocol getSpaceshipProtocol() {
        return spaceshipProtocol;
    }

    public void setSpaceshipProtocol(SpaceshipProtocol spaceshipProtocol) {
        this.spaceshipProtocol = spaceshipProtocol;
    }

    @Override
    public String toString() {
        return "NewGameRequest{" +
                "userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", spaceshipProtocol=" + spaceshipProtocol +
                '}';
    }
}
