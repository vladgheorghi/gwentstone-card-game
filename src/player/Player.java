package player;

import java.util.ArrayList;
import game.Card;
import game.Hero;

public class Player {
    private Hero hero;
    private ArrayList<Card> chosenDeck;
    private ArrayList<Card> hand = new ArrayList<>();
    private int mana = 0;
    private boolean tankOnTable = false;
    private int gamesPlayed = 0;
    private int gamesWon = 0;

    public Player() {
    }

    public Player(Hero hero, ArrayList<Card> chosenDeck, ArrayList<Card> hand,
                  int mana, boolean tankOnTable, int gamesPlayed, int gamesWon) {
        this.hero = hero;
        this.chosenDeck = chosenDeck;
        this.hand = hand;
        this.mana = mana;
        this.tankOnTable = tankOnTable;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public Hero getHero() {return hero;}

    public ArrayList<Card> getChosenDeck() {return chosenDeck;}

    public ArrayList<Card> getHand() {return hand;}

    public int getMana() {return mana;}

    public boolean getTankOnTable() {return tankOnTable;}

    public int getGamesPlayed() {return gamesPlayed;}

    public int getGamesWon() {return gamesWon;}

    public void setHero(Hero hero) {this.hero = hero;}

    public void setChosenDeck(ArrayList<Card> chosenDeck) {this.chosenDeck = chosenDeck;}

    public void setHand(ArrayList<Card> hand) {this.hand = hand;}

    public void setMana(int mana) {this.mana = mana;}

    public void setTankOnTable(boolean tankOnTable) {this.tankOnTable = tankOnTable;}

    public void setGamesPlayed(int gamesPlayed) {this.gamesPlayed = gamesPlayed;}

    public void setGamesWon(int gamesWon) {this.gamesWon = gamesWon;}
}
