package cz.vse.pexeso.view;

import cz.vse.pexeso.navigation.UIConstants;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single card on the game board. Can be hidden, revealed (showing an image),
 * or marked as completed (using a player color). Caches images to improve performance.
 *
 * @author kott10
 * @version June 2025
 */
public class GameCard extends Button {
    private static final Logger log = LoggerFactory.getLogger(GameCard.class);

    private static final Map<String, Image> imageCache = new HashMap<>();

    private final int row;
    private final int column;
    private String imageName;
    private Status status;
    public static final int size = 100;

    /**
     * Constructs a GameCard with given status and coordinates. If status is HIDDEN, shows the default back image.
     *
     * @param status the initial status
     * @param row    row index
     * @param column column index
     */
    public GameCard(Status status, int row, int column) {
        this(status, row, column, "");
    }

    /**
     * Constructs a GameCard with given status, coordinates, and image name (used when status is REVEALED).
     *
     * @param status    initial status
     * @param row       row index
     * @param column    column index
     * @param imageName image url
     */
    public GameCard(Status status, int row, int column, String imageName) {
        this.status = status;
        this.row = row;
        this.column = column;
        this.imageName = imageName;
        setup();
        if (status == Status.HIDDEN) {
            hide();
        } else if (status == Status.REVEALED) {
            reveal(imageName);
        }
        log.debug("Created GameCard at ({}, {}) with status {}", row, column, status);
    }

    /**
     * Configures button size so each card is uniform.
     */
    private void setup() {
        setMinSize(size, size);
        setMaxSize(size, size);
    }

    /**
     * @return the row index of this card
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column index of this card
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return the current status of this card
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return the current image name or resource path
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Reveals this card by setting its status to REVEALED and displaying the image.
     *
     * @param imageName image url
     */
    public final void reveal(String imageName) {
        flip(Status.REVEALED, imageName);
    }

    /**
     * Hides this card by setting status to HIDDEN and showing the default back image.
     */
    public final void hide() {
        flip(Status.HIDDEN, UIConstants.DEFAULT_CARD_IMAGE);
    }

    /**
     * Flips the card to the specified status and updates its graphic.
     *
     * @param status    new status
     * @param imageName image url
     */
    private void flip(Status status, String imageName) {
        this.status = status;
        this.imageName = imageName;

        Image image = imageCache.computeIfAbsent(imageName, name -> new Image(name, size * 0.95, size * 0.95, true, true));
        ImageView imageView = new ImageView(image);
        setGraphic(imageView);
        log.debug("GameCard at ({}, {}) flipped to {} with image {}", row, column, status, imageName);
    }

    /**
     * Marks this card as COMPLETED by setting its status and applying a color style.
     *
     * @param color CSS color string to apply
     */
    public void markCompleted(String color) {
        this.status = Status.COMPLETED;
        setStyle(color);
        log.debug("GameCard at ({}, {}) marked completed with color {}", row, column, color);
    }

    /**
     * Status values for a card: no card (NULL), hidden/back, revealed/front, or completed.
     */
    public enum Status {
        NULL,
        HIDDEN,
        REVEALED,
        COMPLETED
    }
}