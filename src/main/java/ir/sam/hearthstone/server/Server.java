package ir.sam.hearthstone.server;

import ir.sam.hearthstone.Transmitters.ResponseSender;
import ir.sam.hearthstone.requests.Request;
import ir.sam.hearthstone.hibernate.Connector;
import ir.sam.hearthstone.model.account.Deck;
import ir.sam.hearthstone.model.account.Player;
import ir.sam.hearthstone.model.log.*;
import ir.sam.hearthstone.model.main.*;
import ir.sam.hearthstone.resource_manager.Config;
import ir.sam.hearthstone.resource_manager.ConfigFactory;
import ir.sam.hearthstone.response.*;
import ir.sam.hearthstone.server.logic.Collection;
import ir.sam.hearthstone.server.logic.game.AbstractGame;
import ir.sam.hearthstone.server.logic.Shop;
import ir.sam.hearthstone.server.logic.Status;
import ir.sam.hearthstone.server.logic.game.GameBuilder;
import ir.sam.hearthstone.server.logic.game.MultiplayerGameBuilder;
import ir.sam.hearthstone.util.Loop;
import ir.sam.hearthstone.resource_manager.ModelLoader;
import ir.sam.hearthstone.view.model.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static ir.sam.hearthstone.server.logic.game.Side.*;

public class Server {
    public final static int STARTING_MANA;
    public final static int MANA_PER_TURN;
    public final static int CARD_PER_TURN;
    public final static int MAX_DECK_SIZE;
    public final static int STARTING_PASSIVES;
    public final static int STARTING_HAND_CARDS;
    public final static int MAX_MANA;
    public final static int STARTING_COINS;
    public final static int MAX_DECK_NUMBER;
    public final static int TURN_TIME;// to millisecond
    public final static int MAX_HAND_SIZE;
    public final static int MAX_GROUND_SIZE;

    static {
        Config config = ConfigFactory.getInstance().getConfig("SERVER_CONFIG");
        STARTING_MANA = config.getProperty(Integer.class, "STARTING_MANA");
        MANA_PER_TURN = config.getProperty(Integer.class, "MANA_PER_TURN");
        CARD_PER_TURN = config.getProperty(Integer.class, "CARD_PER_TURN");
        MAX_DECK_SIZE = config.getProperty(Integer.class, "MAX_DECK_SIZE");
        STARTING_PASSIVES = config.getProperty(Integer.class, "STARTING_PASSIVES");
        STARTING_HAND_CARDS = config.getProperty(Integer.class, "STARTING_HAND_CARDS");
        MAX_MANA = config.getProperty(Integer.class, "MAX_MANA");
        STARTING_COINS = config.getProperty(Integer.class, "STARTING_COINS");
        MAX_DECK_NUMBER = config.getProperty(Integer.class, "MAX_DECK_NUMBER");
        TURN_TIME = config.getProperty(Integer.class, "TURN_TIME");
        MAX_HAND_SIZE = config.getProperty(Integer.class, "MAX_HAND_SIZE");
        MAX_GROUND_SIZE = config.getProperty(Integer.class, "MAX_GROUND_SIZE");
    }

    private final List<Request> tempRequestList, requestList;
    private final Connector connector;
    @Getter
    private final ModelLoader modelLoader;
    private final Loop executor;
    private Player player;
    private AbstractGame game;
    private GameBuilder gameBuilder;
    private final Collection collection;
    private final Shop shop;
    private final Status status;
    private final ResponseSender responseSender;

    public Server(ResponseSender responseSender) {
        this.responseSender = responseSender;
        tempRequestList = new LinkedList<>();
        requestList = new LinkedList<>();
        connector = new Connector(ConfigFactory.getInstance().getConfigFile("SERVER_HIBERNATE_CONFIG")
                , System.getenv("HearthStone password"));
        modelLoader = new ModelLoader(connector);
        collection = new Collection(connector, modelLoader);
        shop = new Shop(connector, modelLoader);
        status = new Status();
        executor = new Loop(60, this::executeRequests);
        executor.start();
    }

    private void executeRequests() {
        synchronized (tempRequestList) {
            requestList.addAll(tempRequestList);
            tempRequestList.clear();
        }
        requestList.forEach(request -> request.execute(this));
        requestList.clear();
    }

    public void addRequest(Request request) {
        if (request != null) {
            synchronized (tempRequestList) {
                tempRequestList.add(request);
            }
            connector.save(new RequestLog(request, player == null ? null : player.getUserName()));
        }
    }

    public void shutdown() {
        executor.stop();
        connector.close();
    }

    private void sendResponse(Response... responses) {
        for (Response response : responses)
            if (response != null) {
                responseSender.sendResponse(response);
                connector.save(new ResponseLog(response, player.getUserName()));
            }
    }

    public void login(String username, String password, int mode) {
        if (mode == 1)
            signIn(username, password);
        if (mode == 2)
            signUp(username, password);
    }

    private void signIn(String userName, String password) {
        Player fetched = connector.fetch(Player.class, userName);
        if (fetched != null) {
            if (fetched.getPassword().equals(password)) {
                this.player = fetched;
                Response response = new LoginResponse(true, player.getUserName());
                sendResponse(response);
                connector.save(new ResponseLog(response, player.getUserName()));
                connector.save(new AccountLog(player.getUserName(), "sign in"));
            } else {
                Response response = new LoginResponse(false, "wrong password");
                responseSender.sendResponse(response);
                connector.save(new ResponseLog(response, fetched.getUserName()));
            }
        } else {
            Response response = new LoginResponse(false, "username not exist");
            responseSender.sendResponse(response);
            connector.save(new ResponseLog(response, null));
        }
    }

    private void signUp(String username, String password) {
        Player player = connector.fetch(Player.class, username);
        if (player == null) {
            player = new Player(username, password, System.currentTimeMillis(), STARTING_COINS, 0
                    , modelLoader.getFirstCards(), modelLoader.getFirstHeroes(), modelLoader.getFirstDecks());
            connector.save(new HeaderLog(player.getCreatTime(), player.getUserName(), player.getPassword()));
            connector.save(player);
            this.player = player;
            Response response = new LoginResponse(true, this.player.getUserName());
            sendResponse(response);
            connector.save(new AccountLog(this.player.getUserName(), "sign up"));
        } else {
            Response response = new LoginResponse(false, "username already exist");
            responseSender.sendResponse(response);
            connector.save(new ResponseLog(response, null));
        }
    }

    public void logout() {
        if (this.player != null) {
            connector.save(player);
            connector.save(new AccountLog(player.getUserName(), "logout"));
            this.player = null;
        }
    }

    public void deleteAccount() {
        if (this.player != null) {
            connector.delete(player);
            connector.save(new AccountLog(player.getUserName(), "delete account"));
            HeaderLog header = connector.fetch(HeaderLog.class, player.getCreatTime());
            header.setDeletedAt(System.currentTimeMillis() + "");
            connector.save(header);
            this.player = null;
        }
    }

    public void sendShop() {
        sendResponse(shop.sendShop(player));
    }

    public void sellCard(String cardName) {
        sendResponse(shop.sellCard(cardName, player));
    }

    public void buyCard(String cardName) {
        sendResponse(shop.buyCard(cardName, player));
    }

    public void sendStatus() {
        sendResponse(status.sendStatus(player));
    }

    public void selectDeck(String deckName) {
        sendResponse(collection.selectDeck(player, deckName));
    }

    public void sendAllCollectionDetails(String name, String classOfCard, int mana, int lockMode) {
        sendResponse(collection.sendAllCollectionDetails(name, classOfCard, mana, lockMode, player));
    }

    public void applyCollectionFilter(String name, String classOfCard, int mana, int lockMode) {
        sendResponse(collection.applyCollectionFilter(name, classOfCard, mana, lockMode, player));
    }

    public void newDeck(String deckName, String heroName) {
        sendResponse(collection.newDeck(deckName, heroName, player));
    }

    public void deleteDeck(String deckName) {
        sendResponse(collection.deleteDeck(deckName, player));
    }

    public void changeDeckName(String oldDeckName, String newDeckName) {
        sendResponse(collection.changeDeckName(oldDeckName, newDeckName, player));
    }

    public void changeHeroDeck(String deckName, String heroName) {
        sendResponse(collection.changeHeroDeck(deckName, heroName, player));
    }

    public void removeCardFromDeck(String cardName, String deckName) {
        sendResponse(collection.removeCardFromDeck(cardName, deckName, player));
    }

    public void addCardToDeck(String cardName, String deckName) {
        sendResponse(collection.addCardToDeck(cardName, deckName, player, shop));
    }

    public void startPlay() {
        Response response;
        if (canStartGame(player.getSelectedDeck())) {
            response = new GoTo("PLAY_MODE", null);
        } else {
            response = new GoTo("COLLECTION", "your deck is not ready\ngoto collection?");
        }
        sendResponse(response);
    }

    public void selectPlayMode(String modeName) {
        Response response = null;
        switch (modeName) {
            case "multiplayer":
                gameBuilder = new MultiplayerGameBuilder(modelLoader, this);
                response = gameBuilder.setDeckP1(player.getSelectedDeck());
                break;
            case "AI":
                response = new GoTo("MAIN_MENU", "AI add soon\ngoto main menu?");
                break;
            case "deck reader":
                response = new GoTo("MAIN_MENU", "deck reader add soon\ngoto main menu?");
                break;
            case "online":
                response = new GoTo("MAIN_MENU", "online add in next phase\ngoto main menu?");
        }
        sendResponse(response);
    }

    private boolean canStartGame(Deck deck) {
        return deck != null && deck.getSize() == MAX_DECK_SIZE;
    }


    public void selectPassive(String passiveName) {
        if (gameBuilder != null) {
            Optional<Passive> optionalPassive = modelLoader.getPassive(passiveName);
            optionalPassive.ifPresent(passive -> sendResponse(gameBuilder.setPassive(passive, this)));
        }
    }

    public Response sendDecksForSelection(String message) {
        List<SmallDeckOverview> decks = player.getDecks().stream().filter(this::canStartGame)
                .map(SmallDeckOverview::new).collect(Collectors.toList());
        return new PassiveDetails(null, decks, null, message);
    }

    public void selectOpponentDeck(String deckName) {
        if (gameBuilder != null) {
            Optional<Deck> optionalDeck = collection.getDeck(deckName, player);
            if (optionalDeck.isPresent() && canStartGame(optionalDeck.get())) {
                sendResponse(gameBuilder.setDeckP2(optionalDeck.get()));
            }
        }
    }

    public void selectCadOnPassive(int index) {
        if (gameBuilder != null)
            sendResponse(gameBuilder.selectCard(index));
    }

    public void confirm() {
        if (gameBuilder != null) {
            sendResponse(gameBuilder.confirm());
            game = gameBuilder.build();
        }
    }

    public void endTurn() {
        if (game != null) {
            game.nextTurn(PLAYER_ONE);
            sendResponse(game.getResponse(PLAYER_ONE));
        }
    }

    public void selectHero(int side) {
        if (game != null) {
            game.selectHero(PLAYER_ONE, side == 0 ? PLAYER_ONE : PLAYER_TWO);
            sendResponse(game.getResponse(PLAYER_ONE));
        }
    }

    public void selectHeroPower(int side) {
        if (game != null) {
            game.selectHeroPower(PLAYER_ONE, side == 0 ? PLAYER_ONE : PLAYER_TWO);
            sendResponse(game.getResponse(PLAYER_ONE));
        }
    }

    public void selectMinion(int side, int index, int emptyIndex) {
        if (game != null) {
            game.selectMinion(PLAYER_ONE, side == 0 ? PLAYER_ONE : PLAYER_TWO, index, emptyIndex);
            sendResponse(game.getResponse(PLAYER_ONE));
        }
    }

    public void selectCardInHand(int side, int index) {
        if (game != null) {
            game.selectCardInHand(PLAYER_ONE, side == 0 ? PLAYER_ONE : PLAYER_TWO, index);
            sendResponse(game.getResponse(PLAYER_ONE));
        }
    }

    public void exitGame() {
        game.getTimer().cancelTask();
        gameBuilder = null;
        game = null;
        sendResponse(new GoTo("MAIN_MENU", null));
    }
}