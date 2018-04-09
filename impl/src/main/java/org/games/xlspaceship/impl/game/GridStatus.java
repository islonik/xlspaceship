package org.games.xlspaceship.impl.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"user_id", "board"})
public class GridStatus {

    @JsonProperty("user_id")
    private String userId;

    @JsonIgnore
    private Grid grid;

    @JsonProperty("board")
    private List<String> board = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public List<String> getBoard() {
        if (grid != null) {
            board = grid.toList();
        }
        return board;
    }

    public void setBoard(List<String> board) {
        this.board = board;
    }

}
