package org.games.xlspaceship.impl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SpaceshipProtocol {

    @NotBlank(message = "Hostname is mandatory")
    private String hostname;

    @NotBlank(message = "Port is mandatory")
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
