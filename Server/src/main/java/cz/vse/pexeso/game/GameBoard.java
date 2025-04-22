package cz.vse.pexeso.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.utils.Rand;

public class GameBoard {

    private static final Logger log = LoggerFactory.getLogger(GameBoard.class);

    private int[][] matrix;
    private Deck deck;

    public GameBoard(int cardCount) throws Exception {
        log.info("Attempting to create new game board");

        this.deck = new Deck();

        if(cardCount < deck.deckSize) {
            throw new Exception("Not enough cards in deck");
        }

        int[] shuff = this.generateShuffle(cardCount, this.deck.deckSize);

        int[] dimensions = generateDimensions(cardCount);
        this.matrix = new int[dimensions[0]][dimensions[1]];

        for(int i = 0; i < shuff.length; i++) {
            int row = (int) ((float) i / (float) dimensions[1]);
            int col = i % dimensions[1];
            this.matrix[row][col] = shuff[i];
        }

        for(var row : this.matrix) {
            String rr = "";
            for(var col : row) {
                rr += col + "\t";
            }
            System.out.println(rr);
        }

        log.info("Game board created successfully");
    }

    private int[] generateDimensions(int cardCount) {
        int[] dims = new int[] {cardCount, cardCount};

        for(int i = 1; i <= cardCount; i++) {
            int ct = i*i;
            if(ct >= cardCount) {
                if(i*(i - 1) >= cardCount) {
                    dims[0] = i;
                    dims[1] = i - 1;
                } else {
                    dims[0] = dims[1] = i;
                }
                break;
            }
        }

        return dims;
    }

    private int[] generateShuffle(int cardCount, int deckSize) {
        int[] ret = new int[cardCount];
        Set<Integer> ord = new HashSet<Integer>();

        while(ord.size() < cardCount/2) {
            int id = Rand.between(0, deckSize);
            ord.add(id);
        }

        ArrayList<Integer> openPositions = new ArrayList<Integer>();
        for(int i = 0; i < cardCount; i++) {
            openPositions.add(i);
        }

        for(var cc : ord) {
            int opindex1 = Rand.between(0, openPositions.size() - 1);
            int index1 = openPositions.remove(opindex1);

            int opindex2 = Rand.between(0, openPositions.size() - 1);
            int index2 = openPositions.remove(opindex2);

            ret[index1] = cc;
            ret[index2] = cc;
        }

        return ret;
    }

}
