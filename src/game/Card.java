package game;

import java.util.ArrayList;
import fileio.CardInput;

// class for cards (similar to CardInput - class if final so couldn't inherit it)
public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public Card() {
    }

    public Card(final CardInput copyCard) {
        this.mana = copyCard.getMana();
        this.attackDamage = copyCard.getAttackDamage();
        this.health = copyCard.getHealth();
        this.description = copyCard.getDescription();
        this.colors = new ArrayList<>();
        this.colors.addAll(copyCard.getColors());
        this.name = copyCard.getName();
    }

    /** Get card mana */
    public int getMana() {
        return mana; }

    /** Get card attack damage */
    public int getAttackDamage() {
        return attackDamage; }

    /** Get card health */
    public int getHealth() {
        return health; }

    /** Get card description */
    public String getDescription() {
        return description; }

    /** Get card colors */
    public ArrayList<String> getColors() {
        return colors; }

    /** Get card name */
    public String getName() {
        return name; }

    /** Set card mana */
    public void setMana(final int mana) {
        this.mana = mana; }

    /** Set card attack damage */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage; }

    /** Set card health */
    public void setHealth(final int health) {
        this.health = health; }

    /** Set card description */
    public void setDescription(final String description) {
        this.description = description; }

    /** Set card colors */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors; }

    /** Set card name */
    public void setName(final String name) {
        this.name = name; }
}
