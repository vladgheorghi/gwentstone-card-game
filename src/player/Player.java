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

    public Player(final Hero hero, final ArrayList<Card> chosenDeck, final ArrayList<Card> hand,
                  final int mana, final boolean tankOnTable, final int gamesPlayed,
                  final int gamesWon) {
        this.hero = hero;
        this.chosenDeck = chosenDeck;
        this.hand = hand;
        this.mana = mana;
        this.tankOnTable = tankOnTable;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    /** Get player hero */
    public Hero getHero() {
        return hero; }

    /** Get player chosen deck */
    public ArrayList<Card> getChosenDeck() {
        return chosenDeck; }

    /** Get player hand */
    public ArrayList<Card> getHand() {
        return hand; }

    /** Get player mana */
    public int getMana() {
        return mana; }

    /** Get status if the player has a tank on the table */
    public boolean getTankOnTable() {
        return tankOnTable; }

    /** Get player games played */
    public int getGamesPlayed() {
        return gamesPlayed; }

    /** Get player games won */
    public int getGamesWon() {
        return gamesWon; }

    /** Set player hero */
    public void setHero(final Hero hero) {
        this.hero = hero; }

    /** Set player chosen deck */
    public void setChosenDeck(final ArrayList<Card> chosenDeck) {
        this.chosenDeck = chosenDeck; }

    /** Set player hand */
    public void setHand(final ArrayList<Card> hand) {
        this.hand = hand; }

    /** Set player mana */
    public void setMana(final int mana) {
        this.mana = mana; }

    /** Set status if the player has a tank on the table */
    public void setTankOnTable(final boolean tankOnTable) {
        this.tankOnTable = tankOnTable; }

    /** Set player games played */
    public void setGamesPlayed(final int gamesPlayed) {
        this.gamesPlayed = gamesPlayed; }

    /** Set player games won */
    public void setGamesWon(final int gamesWon) {
        this.gamesWon = gamesWon; }
}
