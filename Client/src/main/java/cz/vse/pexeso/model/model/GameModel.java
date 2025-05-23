package cz.vse.pexeso.model.model;

import cz.vse.pexeso.model.service.GameService;

public class GameModel {
    private final GameService gameService;

    public GameModel(GameService gameService) {
        this.gameService = gameService;
    }
}
