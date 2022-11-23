package game;

import fileio.CardInput;

public class Hero extends Card {

    private boolean attacked;
    public Hero(CardInput card) {
        super(card);
    }

    public boolean getAttacked() {return attacked;}

    public void setAttacked(boolean attacked) {this.attacked = attacked;}
}
