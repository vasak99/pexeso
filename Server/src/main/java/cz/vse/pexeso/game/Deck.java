package cz.vse.pexeso.game;

import java.io.File;
import java.util.ArrayList;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.exceptions.DeckException;
import cz.vse.pexeso.utils.ArrayUtils;

public class Deck {

    public int deckSize = 60;
    private ArrayList<String> cards;

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

    public String getImage(int id) {
        return this.cards.get(id);
    }

}

