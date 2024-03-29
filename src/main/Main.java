package main;

import checker.Checker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;

import fileio.CardInput;
import fileio.ActionsInput;
import fileio.StartGameInput;
import fileio.DecksInput;
import fileio.GameInput;
import fileio.Input;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Random;

import game.Card;
import game.Hero;
import game.Minion;

import functionalities.Command;
import functionalities.Functions;

import static functionalities.Constant.HERO_HEALTH;
import static functionalities.Constant.TABLE_ROWS;
import static functionalities.Constant.ROUND_MAX_MANA;

import player.Player;
import player.PlayerTurn;



/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        // TODO add here the entry point to your implementation

        DecksInput playerOneDecks = inputData.getPlayerOneDecks(); // Player one deck list
        DecksInput playerTwoDecks = inputData.getPlayerTwoDecks(); // Player two deck list
        ArrayList<GameInput> games = inputData.getGames(); // list of games

        int gamesPlayed = 0; // counter for games played

        ArrayList<Player> players = new ArrayList<>(); // list of players
        players.add(new Player()); // create player1 (index 0)
        players.add(new Player()); // create player2 (index 1)

        // List of decks for both players
        ArrayList<ArrayList<CardInput>> p1Decks = playerOneDecks.getDecks();
        ArrayList<ArrayList<CardInput>> p2Decks = playerTwoDecks.getDecks();

        // Going through each game
        for (GameInput game : games) {
            // Initializations
            StartGameInput startGame = game.getStartGame();
            ArrayList<ActionsInput> actions = game.getActions();

            gamesPlayed++;

            players.get(0).setGamesPlayed(gamesPlayed);
            players.get(1).setGamesPlayed(gamesPlayed);

            // Each player chooses his deck by index
            int p1DeckIdx = startGame.getPlayerOneDeckIdx();
            int p2DeckIdx = startGame.getPlayerTwoDeckIdx();

            // Creating chosen decks
            ArrayList<Card> p1ChosenDeck = new ArrayList<>();
            ArrayList<Card> p2ChosenDeck = new ArrayList<>();

            // Adding cards from the deck list to the chosen deck
            for (CardInput card : p1Decks.get(p1DeckIdx)) {
                Functions.addCard(card, p1ChosenDeck);
            }

            for (CardInput card : p2Decks.get(p2DeckIdx)) {
                Functions.addCard(card, p2ChosenDeck);
            }

            // Assign each chosen deck to their specific player
            players.get(0).setChosenDeck(p1ChosenDeck);
            players.get(1).setChosenDeck(p2ChosenDeck);

            // Each player chooses his hero
            Hero p1ChosenHero = new Hero(startGame.getPlayerOneHero());
            p1ChosenHero.setHealth(HERO_HEALTH);
            Hero p2ChosenHero = new Hero(startGame.getPlayerTwoHero());
            p2ChosenHero.setHealth(HERO_HEALTH);

            // Assign each chosen hero to their specific player
            players.get(0).setHero(p1ChosenHero);
            players.get(1).setHero(p2ChosenHero);

            // Shuffle chosen decks using the shuffle seed
            Collections.shuffle(players.get(0).getChosenDeck(),
                    new Random(startGame.getShuffleSeed()));
            Collections.shuffle(players.get(1).getChosenDeck(),
                    new Random(startGame.getShuffleSeed()));

            // Make Goliaths and Wardens tanks
            for (Card card : players.get(0).getChosenDeck()) {
                if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
                    ((Minion) card).setTank(true);
                }
            }

            for (Card card : players.get(1).getChosenDeck()) {
                if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
                    ((Minion) card).setTank(true);
                }
            }

            int roundCounter = 1;
            int turnCounter = 0;
            PlayerTurn turn = new PlayerTurn();
            turn.setPlayerTurn(startGame.getStartingPlayer() - 1); // Starting player

            // Creating the game table with TABLE_ROWS rows
            ArrayList<ArrayList<Card>> table = new ArrayList<>();
            for (int i = 0; i < TABLE_ROWS; i++) {
                table.add(new ArrayList<>());
            }

            // Add initial mana for both players
            players.get(0).setMana(roundCounter);
            players.get(1).setMana(roundCounter);

            // Add first card to hand for both players
            if (players.get(0).getChosenDeck().size() != 0) {
                players.get(0).getHand().add(players.get(0).getChosenDeck().get(0));
                players.get(0).getChosenDeck().remove(0);
            }

            if (players.get(1).getChosenDeck().size() != 0) {
                players.get(1).getHand().add(players.get(1).getChosenDeck().get(0));
                players.get(1).getChosenDeck().remove(0);
            }

            // Game starts
            for (ActionsInput action : actions) {

                // Handles each specified command
                Command.commandHandle(action, players, table, turn, output, objectMapper);

                if (action.getCommand().equals("endPlayerTurn")) {
                    turnCounter++;

                    // Unfroze cards that have been frozen for two turns
                    for (int i = 0; i < TABLE_ROWS; i++) {
                        for (Card card : table.get(i)) {
                            if (((Minion) card).getFrozen()) {
                                int turnsSinceFrozen = ((Minion) card).getTurnsSinceFrozen();
                                ((Minion) card).setTurnsSinceFrozen(turnsSinceFrozen + 1);

                                if (((Minion) card).getTurnsSinceFrozen() == 2) {
                                    ((Minion) card).setTurnsSinceFrozen(0);
                                    ((Minion) card).setFrozen(false);
                                }
                            }
                        }
                    }

                    // Both turns ended so next round starts
                    if (turnCounter == 2) {
                        turnCounter = 0;
                        roundCounter++;

                        // Reset cards' attack
                        for (int i = 0; i < TABLE_ROWS; i++) {
                            for (Card card : table.get(i)) {
                                if (((Minion) card).getAttacked()) {
                                    ((Minion) card).setAttacked(false);
                                }
                            }
                        }

                        // Reset heroes' attack
                        players.get(0).getHero().setAttacked(false);
                        players.get(1).getHero().setAttacked(false);

                        // Add mana for next round to both players
                        int p1Mana = players.get(0).getMana();
                        int p2Mana = players.get(1).getMana();

                        if (roundCounter < ROUND_MAX_MANA) {
                            players.get(0).setMana(p1Mana + roundCounter);
                            players.get(1).setMana(p2Mana + roundCounter);
                        } else {
                            players.get(0).setMana(p1Mana + ROUND_MAX_MANA);
                            players.get(1).setMana(p2Mana + ROUND_MAX_MANA);
                        }

                        // Adds cards to players' hands from the chosen deck
                        if (players.get(0).getChosenDeck().size() != 0) {
                            players.get(0).getHand().add(players.get(0).getChosenDeck().get(0));
                            players.get(0).getChosenDeck().remove(0);
                        }

                        if (players.get(1).getChosenDeck().size() != 0) {
                            players.get(1).getHand().add(players.get(1).getChosenDeck().get(0));
                            players.get(1).getChosenDeck().remove(0);
                        }
                    }
                }
            }

            // Reset player fields for next game (without how many games they played/won)
            Functions.resetPlayer(players.get(0));
            Functions.resetPlayer(players.get(1));

            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(new File(filePath2), output);
        }
    }
}


