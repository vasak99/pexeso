package cz.vse.pexeso.util.updater;

import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.model.Game;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Updates a Game instance from a GameUpdatePayload.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameUpdater {
    private static final Logger log = LoggerFactory.getLogger(GameUpdater.class);

    private GameUpdater() {
    }

    /**
     * Synchronizes the given Game’s state with the payload.
     * Updates active player, creates or refreshes board, and refreshes result list.
     *
     * @param game        Game instance to update
     * @param gup         payload containing new game state
     * @param currentTurn set of currently revealed GameCards
     */
    public static void update(Game game, GameUpdatePayload gup, Set<GameCard> currentTurn) {
        long newActive = gup.activePlayer;

        if (game.getGameBoard() == null) {
            game.setActivePlayer(newActive);
            game.setGameBoard(new Board(gup.gameBoard));
            log.info("Initialized new Board with active player {}", newActive);
        } else {
            if (game.getActivePlayer() != newActive) {
                game.setActivePlayer(newActive);
                log.debug("Active player changed to {}", newActive);
            }

            game.getGameBoard().setGameBoardString(gup.gameBoard, game.getPlayerColors().get(newActive), currentTurn);
            log.debug("Board updated for active player {}", newActive);
        }

        // Update results list
        List<LobbyPlayer> newResultList = gup.players.stream()
                .map(sp -> new LobbyPlayer(sp.id, sp.name, sp.score))
                .toList();
        game.getResultList().setAll(newResultList);
        game.getResultList().sort(Comparator.comparingInt(LobbyPlayer::getScore).reversed());
        log.info("Result list updated with {} entries", game.getResultList().size());
    }
}