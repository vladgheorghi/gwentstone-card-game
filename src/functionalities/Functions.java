package functionalities;

import fileio.CardInput;
import fileio.Coordinates;

import game.Card;
import game.Hero;
import game.Minion;

import player.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Functions {

    /* Returns:
      1 or 2 -> rows on which a card should be placed
      0 -> if a card is an environment card
     -1 -> for an unexpected case
     */
    static public int findRow(Card card, int playerTurn) {
        switch (card.getName()) {

            // Environment cards
            case "Firestorm", "Winterfell", "Heart Hound":
                return 4;

            // First row cards
            case "The Ripper", "Miraj", "Goliath", "Warden":
                if (playerTurn == 0)
                    return 2;
                return 1;

            // Second row cards
            case "Sentinel", "Berserker", "The Cursed One", "Disciple":
                if (playerTurn == 0)
                    return 3;
                return 0;
            default:
                return -1;
        }
    }

    static public void addCard(CardInput card, ArrayList<Card> deck) {
        switch (card.getName()) {
            case "Sentinel", "Berserker", "Goliath", "Warden" -> deck.add(new Minion(card));

            case "The Ripper", "Miraj", "The Cursed One", "Disciple" -> deck.add(new Minion(card));

            case "Firestorm", "Winterfell", "Heart Hound" -> deck.add(new Card(card));
        }
    }

    static public boolean verifyAttackedCardOwner(int playerTurn, Coordinates tableCard) {
        if (playerTurn == 1) {
            return tableCard.getX() == 2 || tableCard.getX() == 3;
        } else {
            return tableCard.getX() == 0 || tableCard.getX() == 1;
        }
    }

    static public boolean verifyCardAttacked(ArrayList<ArrayList<Card>> table, Coordinates attacker) {
        int x = attacker.getX();
        int y = attacker.getY();

        return ((Minion) table.get(x).get(y)).getAttacked();
    }

    static public boolean verifyCardFrozen(ArrayList<ArrayList<Card>> table, Coordinates attacker) {
        int x = attacker.getX();
        int y = attacker.getY();

        return ((Minion) table.get(x).get(y)).getFrozen();
    }

    static public boolean verifyCardTank(ArrayList<ArrayList<Card>> table, Coordinates attacked) {
        int x = attacked.getX();
        int y = attacked.getY();

        return ((Minion) table.get(x).get(y)).getTank();
    }

    static public boolean verifyIsEnemyRow(int affectedRow, int playerTurn) {
        if (playerTurn == 0) {
            return affectedRow == 0 || affectedRow == 1;
        } else {
            return affectedRow == 2 || affectedRow == 3;
        }
    }

    static public boolean verifyMirrorRowFull(int affectedRow, int playerTurn, ArrayList<ArrayList<Card>> table) {
        if (playerTurn == 0) {
            return table.get(affectedRow + 2).size() != 5;
        } else {
            return table.get(affectedRow - 2).size() != 5;
        }
    }

    static public void applyEnvironmentCard(int affectedRow, int playerTurn, Card chosenEnCard,
                                            ArrayList<ArrayList<Card>> table) {
        switch (chosenEnCard.getName()) {
            case "Firestorm":
                for (int i = 0; i < table.get(affectedRow).size(); i++) {
                    int currentCardHealth = table.get(affectedRow).get(i).getHealth();
                    table.get(affectedRow).get(i).setHealth(currentCardHealth - 1);

                    if (table.get(affectedRow).get(i).getHealth() <= 0) {
                        table.get(affectedRow).remove(i);
                        i--;
                    }
                }
                break;
            case "Winterfell":
                for (Card card : table.get(affectedRow)) {
                    ((Minion) card).setFrozen(true);
                    ((Minion) card).setTurnsSinceFrozen(0);
                }
                break;
            case "Heart Hound":
                if (table.get(affectedRow).size() != 0) {
                    Card maxHealthCard = table.get(affectedRow).get(0);

                    for (Card card : table.get(affectedRow))
                        if (maxHealthCard.getHealth() < card.getHealth())
                            maxHealthCard = card;

                    table.get(affectedRow).remove(maxHealthCard);

                    int mirrorRow = affectedRow;
                    if (playerTurn == 0)
                        mirrorRow += 2;
                    else
                        mirrorRow -= 2;

                    table.get(mirrorRow).add(maxHealthCard);
                }
                break;
        }
    }

    static public void useCardAbility(Coordinates attackerCoord, Coordinates attackedCoord,
                                      ArrayList<ArrayList<Card>> table) {
        Card attacker = table.get(attackerCoord.getX()).get(attackerCoord.getY());
        Card attacked = table.get(attackedCoord.getX()).get(attackedCoord.getY());

        int aux;

        switch (attacker.getName()) {
            case "The Ripper":
                if (attacked.getAttackDamage() - 2 < 0)
                    attacked.setAttackDamage(0);
                else
                    attacked.setAttackDamage(attacked.getAttackDamage() - 2);
                break;
            case "Miraj":
                aux = attacker.getHealth();
                attacker.setHealth(attacked.getHealth());
                attacked.setHealth(aux);
                break;
            case "The Cursed One":
                aux = attacked.getHealth();
                attacked.setHealth(attacked.getAttackDamage());
                attacked.setAttackDamage(aux);

                if (attacked.getHealth() == 0)
                    table.get(attackedCoord.getX()).remove(attackedCoord.getY());
                break;
            case "Disciple":
                attacked.setHealth(attacked.getHealth() + 2);
                break;
        }
    }

    static public void applyHeroAbility(Hero hero, int affectedRow, ArrayList<ArrayList<Card>> table) {
        switch (hero.getName()) {
            case "Lord Royce":
                if (table.get(affectedRow).size() != 0) {
                    Card maxAttackCard = table.get(affectedRow).get(0);

                    for (Card card : table.get(affectedRow))
                        if (maxAttackCard.getAttackDamage() < card.getAttackDamage())
                            maxAttackCard = card;

                    ((Minion) maxAttackCard).setFrozen(true);
                    ((Minion) maxAttackCard).setTurnsSinceFrozen(0);
                }

                break;
            case "Empress Thorina":
                if (table.get(affectedRow).size() != 0) {
                    Card maxHealthCard = table.get(affectedRow).get(0);

                    for (Card card : table.get(affectedRow))
                        if (maxHealthCard.getHealth() < card.getHealth())
                            maxHealthCard = card;

                    table.get(affectedRow).remove(maxHealthCard);
                }
                break;
            case "King Mudface":
                if (table.get(affectedRow).size() != 0)
                    for (Card card : table.get(affectedRow))
                        card.setHealth(card.getHealth() + 1);
                break;
            case "General Kocioraw":
                if (table.get(affectedRow).size() != 0)
                    for (Card card : table.get(affectedRow))
                        card.setAttackDamage(card.getAttackDamage() + 1);
                break;
        }
    }

    static public void resetPlayer(Player player) {
        player.setMana(0);
        player.setHero(null);
        player.setTankOnTable(false);
        ArrayList<Card> newHand = new ArrayList<>();
        player.setHand(newHand);
        player.setChosenDeck(null);
    }
}
