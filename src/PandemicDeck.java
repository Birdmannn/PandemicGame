/**
 * An object of PandemicDeck represents a deck of pandemic cards.
 * the cards belong to the class PandemicCard. The deck consists of
 * 48 cards, 12 of each 4 COLORS. Then the deck may also consist of
 * epidemic cards
 *
 */

import java.util.Collections;
import java.util.LinkedList;

public class PandemicDeck {

    private final LinkedList<PandemicCard> deck;
    private int cardsUsed;
    private int count = 0;
    private boolean e;  // boolean value if any epidemic card was added.

    // Note that this constructor is the same as calling the latter constructor
    // with the boolean parameter equals to false.
    public PandemicDeck() {
        this(false,0);
    }

    /**
     * Constructor to create a deck of Pandemic Cards
     *
     * @param includeEpidemic boolean variable to create a deck with epidemic cards
     * @param noOfCards int variable to create a specified number of epidemic cards,
     *                  well, only if the former is true.
     *
     */
    public PandemicDeck(boolean includeEpidemic, int noOfCards) {
        deck  = new LinkedList<>();
        createDeck(includeEpidemic, noOfCards);
        e = includeEpidemic;
    }

    private void createDeck(boolean E, int N) {
        for (int color = 0; color < 4; color++) {
            for (int i = 0; i < 12; i++) {
                deck.add(new PandemicCard(count, color));
                count++;
            }
        }
        if(E) {
            for(int i = 0; i < 4; i++) {
                deck.add(new PandemicCard(count, PandemicCard.EVENT_CARD)); // card 48 to 51
            }
            for (int color = 0; color < N; color++) {
                deck.add(new PandemicCard(count, color));   // card 52 to last
                count++;
            }
        }
    }

    // This method shuffles the deck of cards
    public void shuffle() {
        if(deck.isEmpty())
            throw new IllegalStateException("Cannot shuffle an empty deck.");
        Collections.shuffle(deck);
        cardsUsed = 0;
    }
    public int size() {
        return deck.size();
    }

    // Add to the top of the deck (Stack)
    public void addToDeck(PandemicCard c) {
        deck.push(c);
        count++;
        cardsUsed--;
    }
    public PandemicCard removeBottom() {
        if(deck.isEmpty())
            throw new IllegalStateException("No cards are left in the deck.");
        count--;
        return deck.removeLast();
    }
    public void addCard(int pos, PandemicCard c) {
        deck.add(pos, c);
    }

    // Deals the deck of cards to the user
    public PandemicCard dealCard() {
        if (deck.isEmpty())
            throw new IllegalStateException("Can't deal from an empty deck.");
        return deck.pop();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }
}
