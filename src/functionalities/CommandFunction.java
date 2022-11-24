package functionalities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fileio.ActionsInput;

import game.Card;
import game.Hero;
import game.Minion;

import player.Player;
import player.PlayerTurn;

import static functionalities.Constant.ENVIRONMENT_CHECK;
import static functionalities.Constant.TABLE_ROWS;
import static functionalities.Constant.ROW_SIZE;

import java.util.ArrayList;

public final class CommandFunction {
    private CommandFunction() {
    }
    /** Function for command 'endPlayerTurn' */
    public static void endPlayerTurnFunc(final int playerTurn, final PlayerTurn turn) {
        // changes player turns
        if (playerTurn == 0) {
            turn.setPlayerTurn(1);
        } else {
            turn.setPlayerTurn(0);
        }
    }

    /** Function for command 'placeCard' */
    public static void placeCardFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                     final String command, final ActionsInput action,
                                     final ArrayList<Card> currentHand,
                                     final ArrayList<Player> players, final int playerTurn,
                                     final ArrayList<ArrayList<Card>> table) {
        ObjectNode placeCard = objectMapper.createObjectNode();

        placeCard.put("command", command);
        placeCard.put("handIdx", action.getHandIdx());

        // selects chosen card
        Card chosenCard = currentHand.get(action.getHandIdx());

        // finds the row on which the card should be placed
        int row = Functions.findRow(chosenCard, playerTurn);

        // chosen card is an environment card and should not be placed
        if (row == ENVIRONMENT_CHECK) {
            placeCard.put("error", "Cannot place environment card on table.");

            output.add(placeCard);
            return;
        }

        // the player does not have enough mana to place the card
        if (players.get(playerTurn).getMana() < chosenCard.getMana()) {
            placeCard.put("error", "Not enough mana to place card on table.");

            output.add(placeCard);
            return;
        }

        // the row on which the card should be placed is full
        if (table.get(row).size() == ROW_SIZE) {
            placeCard.put("error", "Cannot place card on table since row is full.");

            output.add(placeCard);
            return;
        }

        // verifies if the player's rows have tanks
        if (((Minion) chosenCard).getTank() && !players.get(playerTurn).getTankOnTable()) {
            players.get(playerTurn).setTankOnTable(true);
        }

        // places the card on corresponding row
        table.get(row).add(chosenCard);

        // subtracts card's mana from player's mana
        int playerMana = players.get(playerTurn).getMana();
        players.get(playerTurn).setMana(playerMana - chosenCard.getMana());

        // remove the card from the hand
        currentHand.remove(action.getHandIdx());
    }

    /** Function for command 'cardUsesAttack' */
    public static void cardUsesAttackFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                          final String command, final ActionsInput action,
                                          final ArrayList<Player> players, final int playerTurn,
                                          final ArrayList<ArrayList<Card>> table) {
        ObjectNode cardUsesAttack = objectMapper.createObjectNode();

        cardUsesAttack.put("command", command);

        ObjectNode coordAttacker = objectMapper.createObjectNode();
        coordAttacker.put("x", action.getCardAttacker().getX());
        coordAttacker.put("y", action.getCardAttacker().getY());
        cardUsesAttack.set("cardAttacker", coordAttacker);

        ObjectNode coordAttacked = objectMapper.createObjectNode();
        coordAttacked.put("x", action.getCardAttacked().getX());
        coordAttacked.put("y", action.getCardAttacked().getY());
        cardUsesAttack.set("cardAttacked", coordAttacked);

        if (!Functions.verifyAttackedCardOwner(playerTurn, action.getCardAttacked())) {
            cardUsesAttack.put("error", "Attacked card does not belong to the enemy.");

            output.add(cardUsesAttack);
            return;
        }

        if (Functions.verifyCardAttacked(table, action.getCardAttacker())) {
            cardUsesAttack.put("error", "Attacker card has already attacked this turn.");

            output.add(cardUsesAttack);
            return;
        }

        if (Functions.verifyCardFrozen(table, action.getCardAttacker())) {
            cardUsesAttack.put("error", "Attacker card is frozen.");

            output.add(cardUsesAttack);
            return;
        }

        int otherPlayer;
        if (playerTurn == 0) {
            otherPlayer = 1;
        } else {
            otherPlayer = 0;
        }

        if (players.get(otherPlayer).getTankOnTable()) {
            if (!Functions.verifyCardTank(table, action.getCardAttacked())) {
                cardUsesAttack.put("error", "Attacked card is not of type 'Tank'.");

                output.add(cardUsesAttack);
                return;
            }
        }

        // coordinates of the attacker on the table
        int xAttacker = action.getCardAttacker().getX();
        int yAttacker = action.getCardAttacker().getY();

        Minion attacker = ((Minion) table.get(xAttacker).get(yAttacker));

        // coordinates of the attacked on the table
        int xAttacked = action.getCardAttacked().getX();
        int yAttacked = action.getCardAttacked().getY();

        Minion attacked = ((Minion) table.get(xAttacked).get(yAttacked));

        // subtracts the attacked health from the attacker's damage
        int currentHealth = attacked.getHealth();
        attacked.setHealth(currentHealth - attacker.getAttackDamage());

        // if card's dead, it is removed from the game table
        if (attacked.getHealth() <= 0) {
            table.get(action.getCardAttacked().getX()).remove(attacked);
        }

        // the card attacked and can no longer attack this round
        attacker.setAttacked(true);
    }

    /** Function for command 'cardUsesAbility' */
    public static void cardUsesAbilityFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                           final String command, final ActionsInput action,
                                           final ArrayList<Player> players, final int playerTurn,
                                           final ArrayList<ArrayList<Card>> table) {
        ObjectNode cardUsesAbility = objectMapper.createObjectNode();

        cardUsesAbility.put("command", command);

        ObjectNode coordAbAttacker = objectMapper.createObjectNode();
        coordAbAttacker.put("x", action.getCardAttacker().getX());
        coordAbAttacker.put("y", action.getCardAttacker().getY());
        cardUsesAbility.set("cardAttacker", coordAbAttacker);

        ObjectNode coordAbAttacked = objectMapper.createObjectNode();
        coordAbAttacked.put("x", action.getCardAttacked().getX());
        coordAbAttacked.put("y", action.getCardAttacked().getY());
        cardUsesAbility.set("cardAttacked", coordAbAttacked);

        if (Functions.verifyCardFrozen(table, action.getCardAttacker())) {
            cardUsesAbility.put("error", "Attacker card is frozen.");

            output.add(cardUsesAbility);
            return;
        }

        if (Functions.verifyCardAttacked(table, action.getCardAttacker())) {
            cardUsesAbility.put("error", "Attacker card has already attacked this turn.");

            output.add(cardUsesAbility);
            return;
        }

        int xAbAttacker = action.getCardAttacker().getX();
        int yAbAttacker = action.getCardAttacker().getY();

        Minion abAttacker = ((Minion) table.get(xAbAttacker).get(yAbAttacker));

        if (abAttacker.getName().equals("Disciple")) {
            if (Functions.verifyAttackedCardOwner(playerTurn, action.getCardAttacked())) {
                cardUsesAbility.put("error",
                        "Attacked card does not belong to the current player.");

                output.add(cardUsesAbility);
                return;
            }
        } else {
            if (!Functions.verifyAttackedCardOwner(playerTurn, action.getCardAttacked())) {
                cardUsesAbility.put("error", "Attacked card does not belong to the enemy.");

                output.add(cardUsesAbility);
                return;
            }

            int otherPlayerAb;
            if (playerTurn == 0) {
                otherPlayerAb = 1;
            } else {
                otherPlayerAb = 0;
            }

            if (players.get(otherPlayerAb).getTankOnTable()) {
                if (!Functions.verifyCardTank(table, action.getCardAttacked())) {
                    cardUsesAbility.put("error", "Attacked card is not of type 'Tank'.");

                    output.add(cardUsesAbility);
                    return;
                }
            }
        }

        Functions.useCardAbility(action.getCardAttacker(), action.getCardAttacked(), table);

        abAttacker.setAttacked(true);
    }

    /** Function for command 'useAttackHero' */
    public static void useAttackHeroFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                         final String command, final ActionsInput action,
                                         final ArrayList<Player> players, final int playerTurn,
                                         final ArrayList<ArrayList<Card>> table) {
        ObjectNode useAttackHero = objectMapper.createObjectNode();

        if (Functions.verifyCardFrozen(table, action.getCardAttacker())) {
            useAttackHero.put("command", command);

            ObjectNode coordHeroAttacker = objectMapper.createObjectNode();
            coordHeroAttacker.put("x", action.getCardAttacker().getX());
            coordHeroAttacker.put("y", action.getCardAttacker().getY());
            useAttackHero.set("cardAttacker", coordHeroAttacker);

            useAttackHero.put("error", "Attacker card is frozen.");

            output.add(useAttackHero);
            return;
        }

        if (Functions.verifyCardAttacked(table, action.getCardAttacker())) {
            useAttackHero.put("command", command);

            ObjectNode coordHeroAttacker = objectMapper.createObjectNode();
            coordHeroAttacker.put("x", action.getCardAttacker().getX());
            coordHeroAttacker.put("y", action.getCardAttacker().getY());
            useAttackHero.set("cardAttacker", coordHeroAttacker);

            useAttackHero.put("error", "Attacker card has already attacked this turn.");

            output.add(useAttackHero);
            return;
        }

        int otherPlayerHero;
        if (playerTurn == 0) {
            otherPlayerHero = 1;
        } else {
            otherPlayerHero = 0;
        }

        if (players.get(otherPlayerHero).getTankOnTable()) {
            useAttackHero.put("command", command);

            ObjectNode coordHeroAttacker = objectMapper.createObjectNode();
            coordHeroAttacker.put("x", action.getCardAttacker().getX());
            coordHeroAttacker.put("y", action.getCardAttacker().getY());
            useAttackHero.set("cardAttacker", coordHeroAttacker);

            useAttackHero.put("error", "Attacked card is not of type 'Tank'.");

            output.add(useAttackHero);
            return;
        }

        int xHeroAttacker = action.getCardAttacker().getX();
        int yHeroAttacker = action.getCardAttacker().getY();
        Minion heroAttacker = ((Minion) table.get(xHeroAttacker).get(yHeroAttacker));

        int heroHealth = players.get(otherPlayerHero).getHero().getHealth();
        players.get(otherPlayerHero).getHero().setHealth(heroHealth
                - heroAttacker.getAttackDamage());

        heroAttacker.setAttacked(true);

        if (players.get(otherPlayerHero).getHero().getHealth() <= 0) {
            if (playerTurn == 0) {
                useAttackHero.put("gameEnded", "Player one killed the enemy hero.");

                int gamesWon = players.get(playerTurn).getGamesWon();
                players.get(playerTurn).setGamesWon(gamesWon + 1);
            } else {
                useAttackHero.put("gameEnded", "Player two killed the enemy hero.");

                int gamesWon = players.get(playerTurn).getGamesWon();
                players.get(playerTurn).setGamesWon(gamesWon + 1);
            }

            output.add(useAttackHero);
        }
    }

    /** Function for command 'useHeroAbility' */
    public static void useHeroAbilityFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                          final String command, final ActionsInput action,
                                          final ArrayList<Player> players, final int playerTurn,
                                          final ArrayList<ArrayList<Card>> table) {
        ObjectNode useHeroAbility = objectMapper.createObjectNode();

        useHeroAbility.put("command", command);
        useHeroAbility.put("affectedRow", action.getAffectedRow());

        Hero playerHero = players.get(playerTurn).getHero();

        if (players.get(playerTurn).getMana() < playerHero.getMana()) {
            useHeroAbility.put("error", "Not enough mana to use hero's ability.");

            output.add(useHeroAbility);
            return;
        }

        if (playerHero.getAttacked()) {
            useHeroAbility.put("error", "Hero has already attacked this turn.");

            output.add(useHeroAbility);
            return;
        }

        if (playerHero.getName().equals("Lord Royce")
                || playerHero.getName().equals("Empress Thorina")) {
            if (!Functions.verifyIsEnemyRow(action.getAffectedRow(), playerTurn)) {
                useHeroAbility.put("error", "Selected row does not belong to the enemy.");

                output.add(useHeroAbility);
                return;
            }
        } else {
            if (Functions.verifyIsEnemyRow(action.getAffectedRow(), playerTurn)) {
                useHeroAbility.put("error", "Selected row does not belong to the current player.");

                output.add(useHeroAbility);
                return;
            }
        }

        int playerManaHero = players.get(playerTurn).getMana();
        players.get(playerTurn).setMana(playerManaHero - playerHero.getMana());

        Functions.applyHeroAbility(playerHero, action.getAffectedRow(), table);

        playerHero.setAttacked(true);
    }

    /** Function for command 'useEnvironmentCard' */
    public static void useEnvironmentCardFunc(final ObjectMapper objectMapper,
                                              final ArrayNode output, final String command,
                                              final ActionsInput action,
                                              final ArrayList<Card> currentHand,
                                              final ArrayList<Player> players,
                                              final int playerTurn,
                                              final ArrayList<ArrayList<Card>> table) {
        ObjectNode useEnvironmentCard = objectMapper.createObjectNode();

        useEnvironmentCard.put("command", command);
        useEnvironmentCard.put("handIdx", action.getHandIdx());
        useEnvironmentCard.put("affectedRow", action.getAffectedRow());

        Card chosenEnCard = currentHand.get(action.getHandIdx());

        // verifies if the card is an environment card
        int verifyEnCard = Functions.findRow(chosenEnCard, playerTurn);

        // chosen card is not an environment card
        if (verifyEnCard != ENVIRONMENT_CHECK) {
            useEnvironmentCard.put("error", "Chosen card is not of type environment.");

            output.add(useEnvironmentCard);
            return;
        }

        // the player does not have enough mana to use the card
        if (players.get(playerTurn).getMana() < chosenEnCard.getMana()) {
            useEnvironmentCard.put("error", "Not enough mana to use environment card.");

            output.add(useEnvironmentCard);
            return;
        }

        // selected row does not belong to the enemy
        if (!Functions.verifyIsEnemyRow(action.getAffectedRow(), playerTurn)) {
            useEnvironmentCard.put("error", "Chosen row does not belong to the enemy.");

            output.add(useEnvironmentCard);
            return;
        }

        if (chosenEnCard.getName().equals("Heart Hound")) {
            if (Functions.verifyMirrorRowFull(action.getAffectedRow(), playerTurn, table)) {
                useEnvironmentCard.put("error",
                        "Cannot steal enemy card since the player's row is full.");

                output.add(useEnvironmentCard);
                return;
            }
        }

        // use the environment card
        Functions.applyEnvironmentCard(action.getAffectedRow(), playerTurn, chosenEnCard, table);

        // subtracts card's mana from player's mana
        int playerEnMana = players.get(playerTurn).getMana();
        players.get(playerTurn).setMana(playerEnMana - chosenEnCard.getMana());

        // remove the card from the hand
        currentHand.remove(action.getHandIdx());
    }

    /** Function for command 'getCardsInHand' */
    public static void getCardsInHandFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                          final String command, final ActionsInput action,
                                          final ArrayList<Player> players, final int playerTurn) {
        ObjectNode getCardsInHand = objectMapper.createObjectNode();

        getCardsInHand.put("command", command);
        getCardsInHand.put("playerIdx", action.getPlayerIdx());

        ArrayNode arrayHand = objectMapper.createArrayNode();

        for (Card card : players.get(action.getPlayerIdx() - 1).getHand()) {
            ObjectNode getHandCardsInfo = objectMapper.createObjectNode();

            getHandCardsInfo.put("mana", card.getMana());
            if (Functions.findRow(card, playerTurn) != ENVIRONMENT_CHECK) {
                getHandCardsInfo.put("attackDamage", card.getAttackDamage());
                getHandCardsInfo.put("health", card.getHealth());
            }
            getHandCardsInfo.put("description", card.getDescription());

            ArrayNode cardColors = objectMapper.createArrayNode();
            for (String color : card.getColors()) {
                cardColors.add(color);
            }

            getHandCardsInfo.set("colors", cardColors);
            getHandCardsInfo.put("name", card.getName());

            arrayHand.add(getHandCardsInfo);
        }

        getCardsInHand.set("output", arrayHand);

        output.add(getCardsInHand);
    }

    /** Function for command 'getPlayerDeck' */
    public static void getPlayerDeckFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                         final String command, final ActionsInput action,
                                         final ArrayList<Player> players, final int playerTurn) {
        ObjectNode getPlayerDeck = objectMapper.createObjectNode();

        getPlayerDeck.put("command", command);
        getPlayerDeck.put("playerIdx", action.getPlayerIdx());

        ArrayNode arrayDeck = objectMapper.createArrayNode();

        for (Card card : players.get(action.getPlayerIdx() - 1).getChosenDeck()) {
            ObjectNode getPlayerDeckInfo = objectMapper.createObjectNode();

            getPlayerDeckInfo.put("mana", card.getMana());
            if (Functions.findRow(card, playerTurn) != ENVIRONMENT_CHECK) {
                getPlayerDeckInfo.put("attackDamage", card.getAttackDamage());
                getPlayerDeckInfo.put("health", card.getHealth());
            }
            getPlayerDeckInfo.put("description", card.getDescription());

            ArrayNode cardColors = objectMapper.createArrayNode();
            for (String color : card.getColors()) {
                cardColors.add(color);
            }

            getPlayerDeckInfo.set("colors", cardColors);
            getPlayerDeckInfo.put("name", card.getName());

            arrayDeck.add(getPlayerDeckInfo);
        }

        getPlayerDeck.set("output", arrayDeck);

        output.add(getPlayerDeck);
    }

    /** Function for command 'getCardsOnTable' */
    public static void getCardsOnTableFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                           final String command, final int playerTurn,
                                           final ArrayList<ArrayList<Card>> table) {
        ObjectNode getCardsOnTable = objectMapper.createObjectNode();

        getCardsOnTable.put("command", command);

        ArrayNode arrayTableCards = objectMapper.createArrayNode();

        for (int i = 0; i < TABLE_ROWS; i++) {
            ArrayNode arrayTableCardsRows = objectMapper.createArrayNode();

            for (Card card : table.get(i)) {
                ObjectNode getCardsOnTableInfo = objectMapper.createObjectNode();

                getCardsOnTableInfo.put("mana", card.getMana());
                if (Functions.findRow(card, playerTurn) != ENVIRONMENT_CHECK) {
                    getCardsOnTableInfo.put("attackDamage", card.getAttackDamage());
                    getCardsOnTableInfo.put("health", card.getHealth());
                }
                getCardsOnTableInfo.put("description", card.getDescription());

                ArrayNode cardColors = objectMapper.createArrayNode();
                for (String color : card.getColors()) {
                    cardColors.add(color);
                }

                getCardsOnTableInfo.set("colors", cardColors);
                getCardsOnTableInfo.put("name", card.getName());

                arrayTableCardsRows.add(getCardsOnTableInfo);
            }
            arrayTableCards.add(arrayTableCardsRows);
        }

        getCardsOnTable.set("output", arrayTableCards);

        output.add(getCardsOnTable);
    }

    /** Function for command 'getPlayerTurn' */
    public static void getPlayerTurnFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                         final String command, final int playerTurn) {
        ObjectNode getPlayerTurn = objectMapper.createObjectNode();

        getPlayerTurn.put("command", command);
        getPlayerTurn.put("output", playerTurn + 1);

        output.add(getPlayerTurn);
    }

    /** Function for command 'getPlayerHero' */
    public static void getPlayerHeroFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                         final String command, final ActionsInput action,
                                         final ArrayList<Player> players) {
        ObjectNode getPlayerHero = objectMapper.createObjectNode();

        getPlayerHero.put("command", command);
        getPlayerHero.put("playerIdx", action.getPlayerIdx());

        Hero currentHero = players.get(action.getPlayerIdx() - 1).getHero();

        ObjectNode getPlayerHeroInfo = objectMapper.createObjectNode();

        getPlayerHeroInfo.put("mana", currentHero.getMana());
        getPlayerHeroInfo.put("description", currentHero.getDescription());

        ArrayNode heroColors = objectMapper.createArrayNode();
        for (String color : currentHero.getColors()) {
            heroColors.add(color);
        }

        getPlayerHeroInfo.set("colors", heroColors);
        getPlayerHeroInfo.put("name", currentHero.getName());
        getPlayerHeroInfo.put("health", currentHero.getHealth());

        getPlayerHero.set("output", getPlayerHeroInfo);

        output.add(getPlayerHero);
    }

    /** Function for command 'getCardAtPosition' */
    public static void getCardAtPositionFunc(final ObjectMapper objectMapper,
                                             final ArrayNode output, final String command,
                                             final ActionsInput action,
                                             final ArrayList<ArrayList<Card>> table) {
        ObjectNode getCardAtPosition = objectMapper.createObjectNode();

        getCardAtPosition.put("command", command);

        if (table.size() <= action.getX()) {
            getCardAtPosition.put("output", "No card available at that position.");
            getCardAtPosition.put("x", action.getX());
            getCardAtPosition.put("y", action.getY());

            output.add(getCardAtPosition);
            return;
        }

        if (table.get(action.getX()).size() <= action.getY()) {
            getCardAtPosition.put("output", "No card available at that position.");
            getCardAtPosition.put("x", action.getX());
            getCardAtPosition.put("y", action.getY());

            output.add(getCardAtPosition);
            return;
        }

        Card currentCard = table.get(action.getX()).get(action.getY());

        ObjectNode getCardAtPositionInfo = objectMapper.createObjectNode();

        getCardAtPositionInfo.put("mana", currentCard.getMana());
        getCardAtPositionInfo.put("attackDamage", currentCard.getAttackDamage());
        getCardAtPositionInfo.put("health", currentCard.getHealth());
        getCardAtPositionInfo.put("description", currentCard.getDescription());

        ArrayNode cardColors = objectMapper.createArrayNode();
        for (String color : currentCard.getColors()) {
            cardColors.add(color);
        }

        getCardAtPositionInfo.set("colors", cardColors);
        getCardAtPositionInfo.put("name", currentCard.getName());

        getCardAtPosition.set("output", getCardAtPositionInfo);

        getCardAtPosition.put("x", action.getX());
        getCardAtPosition.put("y", action.getY());

        output.add(getCardAtPosition);
    }

    /** Function for command 'getPlayerMana' */
    public static void getPlayerManaFunc(final ObjectMapper objectMapper, final ArrayNode output,
                                         final String command, final ActionsInput action,
                                         final ArrayList<Player> players) {
        ObjectNode getPlayerMana = objectMapper.createObjectNode();

        getPlayerMana.put("command", command);
        getPlayerMana.put("playerIdx", action.getPlayerIdx());
        getPlayerMana.put("output", players.get(action.getPlayerIdx() - 1).getMana());

        output.add(getPlayerMana);
    }

    /** Function for command 'getEnvironmentCardsInHand' */
    public static void getEnvironmentCardsInHandFunc(final ObjectMapper objectMapper,
                                                     final ArrayNode output, final String command,
                                                     final ActionsInput action,
                                                     final ArrayList<Player> players) {
        ObjectNode getEnvironmentCardsInHand = objectMapper.createObjectNode();

        getEnvironmentCardsInHand.put("command", command);
        getEnvironmentCardsInHand.put("playerIdx", action.getPlayerIdx());

        ArrayNode arrayEnvironmentCards = objectMapper.createArrayNode();

        for (Card card : players.get(action.getPlayerIdx() - 1).getHand()) {
            if (Functions.findRow(card, action.getPlayerIdx() - 1) == ENVIRONMENT_CHECK) {
                ObjectNode getEnvironmentCardsInHandInfo = objectMapper.createObjectNode();

                getEnvironmentCardsInHandInfo.put("mana", card.getMana());
                getEnvironmentCardsInHandInfo.put("description", card.getDescription());

                ArrayNode environmentColors = objectMapper.createArrayNode();
                for (String color : card.getColors()) {
                    environmentColors.add(color);
                }

                getEnvironmentCardsInHandInfo.set("colors", environmentColors);
                getEnvironmentCardsInHandInfo.put("name", card.getName());

                arrayEnvironmentCards.add(getEnvironmentCardsInHandInfo);
            }
        }

        getEnvironmentCardsInHand.set("output", arrayEnvironmentCards);

        output.add(getEnvironmentCardsInHand);
    }

    /** Function for command 'getFrozenCardsOnTable' */
    public static void getFrozenCardsOnTableFunc(final ObjectMapper objectMapper,
                                                 final ArrayNode output, final String command,
                                                 final ArrayList<ArrayList<Card>> table) {
        ObjectNode getFrozenCardsOnTheTable = objectMapper.createObjectNode();

        getFrozenCardsOnTheTable.put("command", command);

        ArrayNode arrayFrozenCards = objectMapper.createArrayNode();

        for (int i = 0; i < TABLE_ROWS; i++) {
            for (Card card : table.get(i)) {
                if (((Minion) card).getFrozen()) {
                    ObjectNode getFrozenCardsInfo = objectMapper.createObjectNode();

                    getFrozenCardsInfo.put("mana", card.getMana());
                    getFrozenCardsInfo.put("attackDamage", card.getAttackDamage());
                    getFrozenCardsInfo.put("health", card.getHealth());
                    getFrozenCardsInfo.put("description", card.getDescription());

                    ArrayNode frozenCardsColors = objectMapper.createArrayNode();
                    for (String color : card.getColors()) {
                        frozenCardsColors.add(color);
                    }

                    getFrozenCardsInfo.set("colors", frozenCardsColors);
                    getFrozenCardsInfo.put("name", card.getName());

                    arrayFrozenCards.add(getFrozenCardsInfo);
                }
            }
        }

        getFrozenCardsOnTheTable.set("output", arrayFrozenCards);

        output.add(getFrozenCardsOnTheTable);
    }

    /** Function for command 'getTotalGamesPlayed' */
    public static void getTotalGamesPlayedFunc(final ObjectMapper objectMapper,
                                               final ArrayNode output, final String command,
                                               final ArrayList<Player> players) {
        ObjectNode getTotalGamesPlayed = objectMapper.createObjectNode();

        getTotalGamesPlayed.put("command", command);
        getTotalGamesPlayed.put("output", players.get(0).getGamesPlayed());

        output.add(getTotalGamesPlayed);
    }

    /** Function for command 'getPlayerOneWins' */
    public static void getPlayerOneWinsFunc(final ObjectMapper objectMapper,
                                            final ArrayNode output, final String command,
                                            final ArrayList<Player> players) {
        ObjectNode getPlayerOneWins = objectMapper.createObjectNode();

        getPlayerOneWins.put("command", command);
        getPlayerOneWins.put("output", players.get(0).getGamesWon());

        output.add(getPlayerOneWins);
    }

    /** Function for command 'getPlayerTwoWins' */
    public static void getPlayerTwoWinsFunc(final ObjectMapper objectMapper,
                                            final ArrayNode output, final String command,
                                            final ArrayList<Player> players) {
        ObjectNode getPlayerTwoWins = objectMapper.createObjectNode();

        getPlayerTwoWins.put("command", command);
        getPlayerTwoWins.put("output", players.get(1).getGamesWon());

        output.add(getPlayerTwoWins);
    }
}
