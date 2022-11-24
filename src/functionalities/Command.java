package functionalities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import fileio.ActionsInput;

import game.Card;

import player.Player;
import player.PlayerTurn;

import java.util.ArrayList;

public final class Command {
    private Command() {
    }
    /** Function for handling action commands. Calls functions from CommandFunctions */
    public static void commandHandle(final ActionsInput action, final ArrayList<Player> players,
                                     final ArrayList<ArrayList<Card>> table, final PlayerTurn turn,
                                     final ArrayNode output, final ObjectMapper objectMapper) {
        String command = action.getCommand();

        int playerTurn = turn.getPlayerTurn();

        ArrayList<Card> currentHand = players.get(playerTurn).getHand();

        switch (command) {
            case "endPlayerTurn":
                CommandFunction.endPlayerTurnFunc(playerTurn, turn);
                break;
            case "placeCard":
                CommandFunction.placeCardFunc(objectMapper, output, command, action, currentHand,
                        players, playerTurn, table);
                break;
            case "cardUsesAttack":
                CommandFunction.cardUsesAttackFunc(objectMapper, output, command, action, players,
                        playerTurn, table);
                break;
            case "cardUsesAbility":
                CommandFunction.cardUsesAbilityFunc(objectMapper, output, command, action,
                        players, playerTurn, table);
                break;
            case "useAttackHero":
                CommandFunction.useAttackHeroFunc(objectMapper, output, command, action,
                        players, playerTurn, table);
                break;
            case "useHeroAbility":
                CommandFunction.useHeroAbilityFunc(objectMapper, output, command, action,
                        players, playerTurn, table);
                break;
            case "useEnvironmentCard":
                CommandFunction.useEnvironmentCardFunc(objectMapper, output, command, action,
                        currentHand, players, playerTurn, table);
                break;
            case "getCardsInHand":
                CommandFunction.getCardsInHandFunc(objectMapper, output, command, action,
                        players, playerTurn);
                break;
            case "getPlayerDeck":
                CommandFunction.getPlayerDeckFunc(objectMapper, output, command, action,
                        players, playerTurn);
                break;
            case "getCardsOnTable":
                CommandFunction.getCardsOnTableFunc(objectMapper, output, command,
                        playerTurn, table);
                break;
            case "getPlayerTurn":
                CommandFunction.getPlayerTurnFunc(objectMapper, output, command, playerTurn);
                break;
            case "getPlayerHero":
                CommandFunction.getPlayerHeroFunc(objectMapper, output, command, action, players);
                break;
            case "getCardAtPosition":
                CommandFunction.getCardAtPositionFunc(objectMapper, output, command,
                        action, table);
                break;
            case "getPlayerMana":
                CommandFunction.getPlayerManaFunc(objectMapper, output, command, action, players);
                break;
            case "getEnvironmentCardsInHand":
                CommandFunction.getEnvironmentCardsInHandFunc(objectMapper, output, command,
                        action, players);
                break;
            case "getFrozenCardsOnTable":
                CommandFunction.getFrozenCardsOnTableFunc(objectMapper, output, command, table);
                break;
            case "getTotalGamesPlayed":
                CommandFunction.getTotalGamesPlayedFunc(objectMapper, output, command, players);
                break;
            case "getPlayerOneWins":
                CommandFunction.getPlayerOneWinsFunc(objectMapper, output, command, players);
                break;
            case "getPlayerTwoWins":
                CommandFunction.getPlayerTwoWinsFunc(objectMapper, output, command, players);
                break;
            default:
                break;
        }
    }
}
