package org.games.xlspaceship.impl.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SpaceshipProtocol {

    @NotNull
    private String hostname;

    @NotNull
    @Size(min=4, max=4)
    private String port;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "SpaceshipProtocol{" +
                "hostname='" + hostname + '\'' +
                ", port=" + port +
                '}';
    }
}
