package game;

import fileio.CardInput;

public class Minion extends Card {
    boolean attacked;
    boolean frozen;
    int turnsSinceFrozen;
    boolean tank;

    public Minion(CardInput card) {
        super(card);
    }

    public boolean getAttacked() {return attacked;}

    public boolean getFrozen() {return frozen;}

    public int getTurnsSinceFrozen() {return turnsSinceFrozen;}

    public boolean getTank() {return tank;}

    public void setAttacked(boolean attacked) {this.attacked = attacked;}

    public void setFrozen(boolean frozen) {this.frozen = frozen;}

    public void setTurnsSinceFrozen(int turnsSinceFrozen) {this.turnsSinceFrozen = turnsSinceFrozen;}

    public void setTank(boolean tank) {this.tank = tank;}
}
