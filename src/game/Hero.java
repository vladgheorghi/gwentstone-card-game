package game;

import fileio.CardInput;

// class for heroes
public class Hero extends Card {

    private boolean attacked; // true if they attacked in a round
    public Hero(final CardInput card) {
        super(card);
    }

    /** Get hero attacked status */
    public boolean getAttacked() {
        return attacked; }

    /** Set hero attacked status */
    public void setAttacked(final boolean attacked) {
        this.attacked = attacked; }
}
