package functionalities;

import fileio.CardInput;
import fileio.Coordinates;

import game.Card;
import game.Hero;
import game.Minion;

import player.Player;

import java.util.ArrayList;

import static functionalities.Constant.ROW_SIZE;
import static functionalities.Constant.ENVIRONMENT_CHECK;
import static functionalities.Constant.FIRST_ROW;
import static functionalities.Constant.SECOND_ROW;
import static functionalities.Constant.THIRD_ROW;
import static functionalities.Constant.FOURTH_ROW;

public final class Functions {
    private Functions() {
    }

    /** Returns:
      1 or 2 -> rows on which a card should be placed
      0 -> if a card is an environment card
     -1 -> for an unexpected case
     */
    public static int findRow(final Card card, final int playerTurn) {
        switch (card.getName()) {

            // Environment cards
            case "Firestorm", "Winterfell", "Heart Hound":
                return ENVIRONMENT_CHECK;

            // First row cards
            case "The Ripper", "Miraj", "Goliath", "Warden":
                if (playerTurn == 0) {
                    return THIRD_ROW;
                }
                return SECOND_ROW;

            // Second row cards
            case "Sentinel", "Berserker", "The Cursed One", "Disciple":
                if (playerTurn == 0) {
                    return FOURTH_ROW;
                }
                return FIRST_ROW;
            default:
                return -1;
        }
    }

    /** Function for adding an input card to a deck list */
    public static void addCard(final CardInput card, final ArrayList<Card> deck) {
        switch (card.getName()) {
            case "Sentinel", "Berserker", "Goliath", "Warden", "The Ripper", "Miraj",
                    "The Cursed One", "Disciple" -> deck.add(new Minion(card));
            case "Firestorm", "Winterfell", "Heart Hound" -> deck.add(new Card(card));
        }
    }

    /** Returns true if the card that is to be attacked belongs to the enemy player */
    public static boolean verifyAttackedCardOwner(final int playerTurn,
                                                  final Coordinates tableCard) {
        if (playerTurn == 1) {
            return tableCard.getX() == FOURTH_ROW || tableCard.getX() == THIRD_ROW;
        } else {
            return tableCard.getX() == FIRST_ROW || tableCard.getX() == SECOND_ROW;
        }
    }

    /** Returns true if a card from the table has already attacked this turn */
    public static boolean verifyCardAttacked(final ArrayList<ArrayList<Card>> table,
                                             final Coordinates attacker) {
        int x = attacker.getX();
        int y = attacker.getY();

        return ((Minion) table.get(x).get(y)).getAttacked();
    }

    /** Returns true if a card from the table is frozen */
    public static boolean verifyCardFrozen(final ArrayList<ArrayList<Card>> table,
                                           final Coordinates attacker) {
        int x = attacker.getX();
        int y = attacker.getY();

        return ((Minion) table.get(x).get(y)).getFrozen();
    }

    /** Returns true if a card from the table is a tank */
    public static boolean verifyCardTank(final ArrayList<ArrayList<Card>> table,
                                         final Coordinates attacked) {
        int x = attacked.getX();
        int y = attacked.getY();

        return ((Minion) table.get(x).get(y)).getTank();
    }

    /** Returns true if a given row if an enemy row */
    public static boolean verifyIsEnemyRow(final int affectedRow, final int playerTurn) {
        if (playerTurn == 0) {
            return affectedRow == FIRST_ROW || affectedRow == SECOND_ROW;
        } else {
            return affectedRow == THIRD_ROW || affectedRow == FOURTH_ROW;
        }
    }

    /** Returns true if the mirror row to a given row is full */
    public static boolean verifyMirrorRowFull(final int affectedRow, final int playerTurn,
                                              final ArrayList<ArrayList<Card>> table) {
        if (playerTurn == 0) {
            return table.get(affectedRow + 2).size() != ROW_SIZE;
        } else {
            return table.get(affectedRow - 2).size() != ROW_SIZE;
        }
    }

    /** Applies the effects of a given environment card */
    public static void applyEnvironmentCard(final int affectedRow, final int playerTurn,
                                            final Card chosenEnCard,
                                            final ArrayList<ArrayList<Card>> table) {
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

                    for (Card card : table.get(affectedRow)) {
                        if (maxHealthCard.getHealth() < card.getHealth()) {
                            maxHealthCard = card;
                        }
                    }

                    table.get(affectedRow).remove(maxHealthCard);

                    int mirrorRow;
                    if (playerTurn == 0) {
                        if (affectedRow == THIRD_ROW) {
                            mirrorRow = SECOND_ROW;
                        } else {
                            mirrorRow = FIRST_ROW;
                        }
                    } else {
                        if (affectedRow == FIRST_ROW) {
                            mirrorRow = FOURTH_ROW;
                        } else {
                            mirrorRow = THIRD_ROW;
                        }
                    }

                    table.get(mirrorRow).add(maxHealthCard);
                }
                break;
            default:
                break;
        }
    }

    /** Uses a card's ability */
    public static void useCardAbility(final Coordinates attackerCoord,
                                      final Coordinates attackedCoord,
                                      final ArrayList<ArrayList<Card>> table) {
        Card attacker = table.get(attackerCoord.getX()).get(attackerCoord.getY());
        Card attacked = table.get(attackedCoord.getX()).get(attackedCoord.getY());

        int aux;

        switch (attacker.getName()) {
            case "The Ripper":
                if (attacked.getAttackDamage() - 2 < 0) {
                    attacked.setAttackDamage(0);
                } else {
                    attacked.setAttackDamage(attacked.getAttackDamage() - 2);
                }
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

                if (attacked.getHealth() == 0) {
                    table.get(attackedCoord.getX()).remove(attackedCoord.getY());
                }
                break;
            case "Disciple":
                attacked.setHealth(attacked.getHealth() + 2);
                break;
            default:
                break;
        }
    }

    /** Uses a hero's ability */
    public static void applyHeroAbility(final Hero hero, final int affectedRow,
                                        final ArrayList<ArrayList<Card>> table) {
        switch (hero.getName()) {
            case "Lord Royce":
                if (table.get(affectedRow).size() != 0) {
                    Card maxAttackCard = table.get(affectedRow).get(0);

                    for (Card card : table.get(affectedRow)) {
                        if (maxAttackCard.getAttackDamage() < card.getAttackDamage()) {
                            maxAttackCard = card;
                        }
                    }

                    ((Minion) maxAttackCard).setFrozen(true);
                    ((Minion) maxAttackCard).setTurnsSinceFrozen(0);
                }

                break;
            case "Empress Thorina":
                if (table.get(affectedRow).size() != 0) {
                    Card maxHealthCard = table.get(affectedRow).get(0);

                    for (Card card : table.get(affectedRow)) {
                        if (maxHealthCard.getHealth() < card.getHealth()) {
                            maxHealthCard = card;
                        }
                    }

                    table.get(affectedRow).remove(maxHealthCard);
                }
                break;
            case "King Mudface":
                if (table.get(affectedRow).size() != 0) {
                    for (Card card : table.get(affectedRow)) {
                        card.setHealth(card.getHealth() + 1);
                    }
                }
                break;
            case "General Kocioraw":
                if (table.get(affectedRow).size() != 0) {
                    for (Card card : table.get(affectedRow)) {
                        card.setAttackDamage(card.getAttackDamage() + 1);
                    }
                }
                break;
            default:
                break;
        }
    }

    /** Resets a player's fields for next game */
    public static void resetPlayer(final Player player) {
        player.setMana(0);
        player.setHero(null);
        player.setTankOnTable(false);
        ArrayList<Card> newHand = new ArrayList<>();
        player.setHand(newHand);
        player.setChosenDeck(null);
    }
}
