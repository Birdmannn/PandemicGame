/**
 * An object of type PandemicHand represents a hand of cards. The
 * cards belong to the class PandemicCard. A Hand is empty when it
 * is created, and any number of cards can be added to it.
 */

import java.util.ArrayList;
import java.util.Collections;

public class PandemicHand {
    private ArrayList<PandemicCard> hand;  // The cards in the hand.

    /**
     * Create a hand that is initially empty.
     */
    public PandemicHand() {
        hand = new ArrayList<>();
    }

    /**
     * Remove all cards from the hand, leaving it empty.
     */
    public void clear() {
        hand.clear();
    }

    /**
     * Shuffles the card in hand in any random order. Hand shuffling
     * is used when cards are added into a temporary hand, to be shuffled
     * and stacked back into the deck.
     */
    public void shuffle() {
        Collections.shuffle(hand);
    }

    /**
     * Add a card to the hand. It is added at the end of the current hand.
     * @param c the non-null card to be added.
     * @throws NullPointerException if the parameter c is null
     */
    public void addCard(PandemicCard c) {
        if(c == null)
            throw new NullPointerException("Can't add a null card to a hand.");
        hand.add(c);
    }

    public void addCard(int pos, PandemicCard c) {
        hand.add(pos, c);
    }

    /**
     * Remove a card from the hand, if present.
     * @param card the card to be removed. If c is null or if the card is not in the hand,
     *          then nothing is done
     */
    public void removeCard(PandemicCard card) {
        hand.remove(card);
    }

    /**
     * Remove the card in a specified position from the hand.
     * @param position the position of the card that is to be removed, where
     *                 positions are numbered starting from zero.
     * @throws IllegalArgumentException if the position does not exist in the hand,
     *                 that is if the position is less than zero or greater than
     *                 or equal to the number of cards in the hand
     */
    public void removeCard(int position) {
        if(position < 0 || position >= hand.size())
            throw new IllegalArgumentException("Position does not exist in hand: " + position);
        hand.remove(position);
    }

    //returns the number of cards in the hand
    public int getCardCount() {
        return hand.size();
    }

    /**
     * Gets the card in the specified position in the hand.
     * (Note that this card is removed from the hand!)
     */
    public PandemicCard getCard(int position) {
        if (position < 0 || position >= hand.size())
            throw new IllegalArgumentException("Position does not exist in hand " + position);
        return hand.get(position);
    }

    public ArrayList<PandemicCard> getCardArray() {
        return hand;
    }

}
