package cz.vse.pexeso.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.exceptions.DeckException;
import cz.vse.pexeso.utils.ArrayUtils;

/**
 * Collection of the available cards
 */
public class Deck {

    private List<String> cards;

    private String[] allowedExtensions = new String[] { "jpg", "jpeg", "png" };

    public Deck() throws DeckException {
        File folder = new File(System.getProperty("user.dir") + Variables.STATIC_PATH);
        if(!folder.exists() || !folder.isDirectory()) {
            throw new DeckException("No images folder detected");
        }
        this.cards = new ArrayList<>();
        for(var file : folder.listFiles()) {
            String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            if(ext != null && ArrayUtils.contains(this.allowedExtensions, ext)) {
                this.cards.add(file.getName());
            }
        }
    }

    /**
     * Returns the image path from id
     * @param id card id
     * @return String
     */
    public String getImage(int id) {
        return "http://" + Variables.SERVER_ADDR + Variables.STATIC_PATH + "/" + this.cards.get(id);
    }

    /**
     * Returns the number of available cards (images)
     * @return int
     */
    public int deckSize() {
        return this.cards.size();
    }

}

