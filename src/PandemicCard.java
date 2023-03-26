/**
 * An object of PandemicCard represents a playing card from a standard Pandemic deck
 * Each Card has a value for it's corresponding city value and a color for it's
 * corresponding city color. Extra 4-6 cards can be added as epidemic cards, showing
 * that this class can be used both as a player card and as an infection card.
 *
 */
public class PandemicCard {
    public static final int BLUE = 0;  // Codes for Colors
    public static final int YELLOW = 1;
    public static final int RED = 2;
    public static final int BLACK = 3;

    //Check. Event cards should be added for actions
    public static final int EVENT_CARD = 4; // Color for all event cards


    // The card color. Both the color and the value cannot be changed
    // after the card is constructed.
    private final int color;

    private final int value;

    // Special static values of all the

    public final static int FLY_ANYWHERE = 48;
    public final static int BUILD_R_ANYWHERE = 49;
    public final static int SOLVE_DISEASE = 50;
    public final static int ADD_MOVE = 51;

    public PandemicCard() {
        color = BLUE;
        value = 49;
    }

    /**
     * Creates the card with specified color and value.
     * @param value the value of new card
     * @param color the color of new card
     *
     * You can use constants like Card.RED, Card.BLUE, etc.
     *              throws IllegalArgumentException if the parameter values are
     *              not in the permissible ranges.
     *
     */
    public PandemicCard(int value, int color) {
        if(value < 0)
            throw new IllegalArgumentException("Illegal value.");
        if(color < 0 || color > 4)
            throw new IllegalArgumentException("Illegal Card color.");
        this.value = value;
        this.color = color;
    }
    // Returns color as an integer value
    public int getAttribute() {
        return color;
    }
    // Returns the value of the card
    public int getValue() {
        return value;
    }

    // Returns the color of the card as a String.
    public String getAttributeAsString() {
        switch (color) {
            case BLUE:      return "Blue";
            case YELLOW:    return "Yellow";
            case RED:       return "Red";
            case BLACK:     return "Black";
            default:        return "Event Card";
        }
    }
}
