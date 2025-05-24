package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.ResultPayload;
import cz.vse.pexeso.model.GameCard;
import cz.vse.pexeso.model.service.GameService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.network.RedirectService;
import cz.vse.pexeso.util.Updater;

public class GameModel {
    private final GameService gameService;
    private final SessionService sessionService;
    private final RedirectService redirectService;

    public GameModel(GameService gameService, SessionService sessionService, RedirectService redirectService) {
        this.gameService = gameService;
        this.sessionService = sessionService;
        this.redirectService = redirectService;
    }

    public void attemptRevealCard(GameCard card) {
        gameService.sendRevealCardRequest(card, sessionService.getSession().getCurrentGameRoom(), sessionService.getSession().getPlayerId());
    }

    public void updateGame(String data) {
        Updater.updateGame(sessionService.getSession().getCurrentGameRoom(), new GameUpdatePayload(data));
    }

    public void setResult(String data) {
        Updater.setResult(sessionService.getSession().getCurrentGameRoom(), new ResultPayload(data));
    }
}
