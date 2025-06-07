package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.CardCoordinates;
import cz.vse.pexeso.network.ConnectionService;
import cz.vse.pexeso.util.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for sending in-game requests (reveal card, give up) to the server.
 *
 * @author kott10
 * @version June 2025
 */
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final ConnectionService connectionService;

    /**
     * Constructs a GameService with the provided ConnectionService.
     *
     * @param connectionService ConnectionService to send messages
     */
    public GameService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Sends a reveal-card request for the specified coordinates, gameId, and playerId.
     *
     * @param coordinates CardCoordinates specifying the card to reveal
     * @param gameId      ID of the game room
     * @param playerId    ID of the revealing player
     */
    public void sendRevealCardRequest(CardCoordinates coordinates, String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildRevealCardMessage(coordinates, gameId, playerId));
        log.debug("Sent REVEAL_CARD request for coordinates=({},{}), gameId='{}', playerId={}",
                coordinates.row(), coordinates.column(), gameId, playerId);
    }

    /**
     * Sends a give-up request to leave the game.
     *
     * @param gameId   ID of the game room
     * @param playerId ID of the surrendering player
     */
    public void sendGiveUpRequest(String gameId, long playerId) {
        connectionService.send(MessageBuilder.buildGiveUpMessage(gameId, playerId));
        log.debug("Sent GIVE_UP request for gameId='{}', playerId={}", gameId, playerId);
    }
}