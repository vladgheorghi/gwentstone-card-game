package game;

import java.util.ArrayList;
import fileio.CardInput;

public class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    String name;

    public Card(){
    }

    public Card(CardInput copyCard) {
        this.mana = copyCard.getMana();
        this.attackDamage = copyCard.getAttackDamage();
        this.health = copyCard.getHealth();
        this.description = copyCard.getDescription();
        this.colors = new ArrayList<>();
        this.colors.addAll(copyCard.getColors());
        this.name = copyCard.getName();
    }

    public int getMana() {return mana;}

    public int getAttackDamage() {return attackDamage;}

    public int getHealth() {return health;}

    public String getDescription() {return description;}

    public ArrayList<String> getColors() {return colors;}

    public String getName() {return name;}

    public void setMana(int mana) {this.mana = mana;}

    public void setAttackDamage(int attackDamage) {this.attackDamage = attackDamage;}

    public void setHealth(int health) {this.health = health;}

    public void setDescription(String description) {this.description = description;}

    public void setColors(ArrayList<String> colors) {this.colors = colors;}

    public void setName(String name) {this.name = name;}
}
