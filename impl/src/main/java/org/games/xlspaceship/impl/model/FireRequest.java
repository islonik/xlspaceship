package org.games.xlspaceship.impl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FireRequest {

    @JsonProperty("salvo")
    private List<String> salvo = new ArrayList<>();

    public List<String> getSalvo() {
        return salvo;
    }

    public void setSalvo(List<String> salvo) {
        this.salvo = salvo;
    }

    @Override
    public String toString() {
        return "FireRequest{" +
                "salvo=" + salvo.toString() +
                '}';
    }

}
