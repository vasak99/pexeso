package cz.vse.pexeso.model;

/**
 * Holds parameters required to create or update a game room, including its name,
 * player capacity, and number of cards.
 *
 * @author kott10
 * @version June 2025
 */
public record GameRoomParameters(String name, int capacity, int cardCount) {
}
