package game;

import fileio.CardInput;

// class for minions
public class Minion extends Card {
    private boolean attacked; // true if the minion attacked this turn
    private boolean frozen; // true if the minion is frozen
    private int turnsSinceFrozen; // turns since the minion has been frozen (max 2)
    private boolean tank; // true if the minion is a tank

    public Minion(final CardInput card) {
        super(card);
    }

    /** Get minion attacked status */
    public boolean getAttacked() {
        return attacked; }

    /** Get minion frozen status */
    public boolean getFrozen() {
        return frozen; }

    /** Get minion turn count since it was frozen */
    public int getTurnsSinceFrozen() {
        return turnsSinceFrozen; }

    /** Get minion tanked status */
    public boolean getTank() {
        return tank; }

    /** Set minion attacked status */
    public void setAttacked(final boolean attacked) {
        this.attacked = attacked; }

    /** Set minion frozen status */
    public void setFrozen(final boolean frozen) {
        this.frozen = frozen; }

    /** Set minion turn count since it was frozen */
    public void setTurnsSinceFrozen(final int turnsSinceFrozen) {
        this.turnsSinceFrozen = turnsSinceFrozen; }

    /** Set minion tanked status */
    public void setTank(final boolean tank) {
        this.tank = tank; }
}
