package client;

import util.Loop;
import hibernate.Connector;
import util.Updatable;
import view.model.CardOverview;
import server.Request;
import server.Server;
import view.MyFrame;
import view.model.BigDeckOverview;
import view.model.SmallDeckOverview;
import view.panel.*;
import view.PanelType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.awt.event.ItemEvent;
import java.util.*;

public class Client {
    private static final Client instance = new Client();
    private final JFrame frame;
    private final Map<PanelType, JPanel> panels;
    private final Stack<PanelType> history;
    private PanelType now;
    private Connector connector;
    private final List<Answer> tempAnswerList, answerList;
    private final Loop executor;
    private String username;

    public Client() {
        this.frame = new MyFrame();
        panels = new HashMap<>();
        history = new Stack<>();
        panels.put(PanelType.LOGIN_PANEL, new LoginPanel(new LoginPanelAction()));
        panels.put(PanelType.MAIN_MENU, new MainMenuPanel(new MainMenuAction()));
        panels.put(PanelType.SHOP, new ShopPanel(new ShopAction()));
        panels.put(PanelType.STATUS, new StatusPanel(new StatusAction()));
        panels.put(PanelType.COLLECTION, new CollectionPanel(new CollectionAction()));
        now = PanelType.LOGIN_PANEL;
        updateFrame();
        tempAnswerList = new ArrayList<>();
        answerList = new ArrayList<>();
        executor = new Loop(60, this::executeAnswers);
        executor.start();

    }

    public static Client getInstance() {
        return instance;
    }

    private void executeAnswers() {
        answerList.forEach(Answer::execute);
        answerList.clear();
        synchronized (tempAnswerList) {
            answerList.addAll(tempAnswerList);
            tempAnswerList.clear();
        }
    }

    public void putAnswer(Answer answer) {
        if (answer != null)
            synchronized (tempAnswerList) {
                tempAnswerList.add(answer);
            }
    }

    private void shutdown() {
        // connector
        executor.stop();
    }

    private void updateFrame() {
        frame.setContentPane(panels.get(now));
        history.push(now);
    }

    private void exit() {
        Server.getInstance().shutdown();
        this.shutdown();
        System.exit(0);
    }

    void login(boolean success, String message) {
        LoginPanel panel = (LoginPanel) panels.get(PanelType.LOGIN_PANEL);
        if (success) {
            panel.reset();
            now = PanelType.MAIN_MENU;
            updateFrame();
            username = message;
            ((MainMenuPanel) panels.get(PanelType.MAIN_MENU)).update();
        } else {
            panel.setMessage(message);
        }
    }

    private void logout() {
        Request request = new Request.LogoutRequest();
        Server.getInstance().addRequest(request);
    }

    private void deleteAccount() {
        Request request = new Request.DeleteAccount();
        Server.getInstance().addRequest(request);
    }

    private void back() {
        history.pop();
        now = history.peek();
        ((Updatable)panels.get(now)).update();
        frame.setContentPane(panels.get(now));
    }

    private void backMainMenu() {
        while (history.peek() != PanelType.MAIN_MENU)
            history.pop();
        now = history.peek();
        frame.setContentPane(panels.get(now));
    }

    private void sendLoginRequest(String username,String pass,LoginPanel.Mode mode){
        Request request = new Request.LoginRequest(username, pass, mode);
        Server.getInstance().addRequest(request);
    }

    private void sendShopRequest(){
        Request request = new Request.Shop();
        Server.getInstance().addRequest(request);
    }

    private void sendStatusRequest(){
        Request request = new Request.Status();
        Server.getInstance().addRequest(request);
    }

    private void sendFirstCollectionRequest(){
        Request request = new Request.FirstCollection();
        Server.getInstance().addRequest(request);
    }
    private void sendCollectionRequest(String name, String classOfCard, int mana, int lockMode, String deckName){
        Request request = new Request.CollectionDetails(name, classOfCard, mana, lockMode, deckName);
        Server.getInstance().addRequest(request);
    }

    void setShopDetails(List<CardOverview> sell, List<CardOverview> buy, int coin) {
        if (now != PanelType.SHOP) {
            now = PanelType.SHOP;
            updateFrame();
        }
        ShopPanel shopPanel = (ShopPanel) panels.get(PanelType.SHOP);
        shopPanel.setSell(sell);
        shopPanel.setBuy(buy);
        shopPanel.setCoin(coin);
    }

    void setStatusDetails(List<BigDeckOverview> bigDeckOverviews) {
        if (now != PanelType.STATUS) {
            now = PanelType.STATUS;
            updateFrame();
        }
        StatusPanel statusPanel = (StatusPanel) panels.get(PanelType.STATUS);
        statusPanel.setDeckBoxList(bigDeckOverviews);
    }

    void setFirstCollectionDetail(List<String> heroNames, List<String> classOfCardNames) {
        CollectionPanel collectionPanel = (CollectionPanel) panels.get(PanelType.COLLECTION);
        collectionPanel.setFirstDetails(heroNames, classOfCardNames);
    }

    void setCollectionDetail(List<CardOverview> cards, List<SmallDeckOverview> decks,
                             List<CardOverview> deckCards, boolean canAddDeck,
                             boolean canChangeHero, String deckName) {
        CollectionPanel collectionPanel = (CollectionPanel) panels.get(PanelType.COLLECTION);
        collectionPanel.setDetails(cards, decks, deckCards, canAddDeck, canChangeHero , deckName);
    }

    void showMessage(String message){
        JOptionPane.showMessageDialog(frame,message);
    }

    void gotoShop(){
        int c= JOptionPane.showConfirmDialog(frame,"you can buy this card in shop\ngoto shop?",
                "goto shop", JOptionPane.YES_NO_OPTION);
        if (c==JOptionPane.YES_OPTION){
            now = PanelType.SHOP;
            updateFrame();
            sendShopRequest();
        }
    }

    public class LoginPanelAction {
        public void changeMode(LoginPanel loginPanel) {
            if (loginPanel.getMode() == LoginPanel.Mode.SIGN_IN) {
                loginPanel.setMode(LoginPanel.Mode.SIGN_UP);
            } else {
                loginPanel.setMode(LoginPanel.Mode.SIGN_IN);
            }
            loginPanel.reset();
        }

        public void login(LoginPanel loginPanel, String username, String pass, String pass2) {
            if ("Enter username".equals(username) || "".equals(username))
                return;
            if (loginPanel.getMode() == LoginPanel.Mode.SIGN_UP && !pass.equals(pass2)) {
                loginPanel.setMessage("password not same");
                return;
            }
            if ("Enter password".equals(pass))
                return;
            sendLoginRequest(username,pass,loginPanel.getMode());
        }

        public void exit() {
            Client.this.exit();
        }
    }

    public class MainMenuAction {
        public void exit() {
            Client.this.logout();
            Client.this.exit();
        }

        public void logout() {
            Client.this.logout();
            // reset panels
            ((CollectionPanel)panels.get(PanelType.COLLECTION)).reset();
            now = PanelType.LOGIN_PANEL;
            updateFrame();
            history.clear();
        }

        public void deleteAccount() {
            Client.this.deleteAccount();
            // reset panels
            now = PanelType.LOGIN_PANEL;
            updateFrame();
        }

        public void shop() {
            now = PanelType.SHOP;
            updateFrame();
            sendShopRequest();
        }

        public void status() {
            now = PanelType.STATUS;
            updateFrame();
            sendStatusRequest();
        }

        public void collection() {
            now = PanelType.COLLECTION;
            updateFrame();
            sendFirstCollectionRequest();
        }

        public void update(){
            ((MainMenuPanel)panels.get(PanelType.MAIN_MENU)).setMessage("welcome " + username);
        }
    }

    public class ShopAction {
        public void sell(String cardName) {
            Request request = new Request.SellCard(cardName);
            Server.getInstance().addRequest(request);
        }

        public void buy(String cardName) {
            Request request = new Request.BuyCard(cardName);
            Server.getInstance().addRequest(request);
        }

        public void exit() {
            Client.this.logout();
            Client.this.exit();
        }

        public void back() {
            Client.this.back();
        }

        public void backMainMenu() {
            Client.this.backMainMenu();
        }

        public void update(){
            sendShopRequest();
        }
    }

    public class StatusAction {
        public void exit() {
            Client.this.logout();
            Client.this.exit();
        }

        public void back() {
            Client.this.back();
        }

        public void backMainMenu() {
            Client.this.backMainMenu();
        }

        public void update(){
            sendStatusRequest();
        }
    }

    public class CollectionAction {
        private String name = null, classOfCard = null, deckName = null;
        private int mana = 0, lockMode = 0;

        public void exit() {
            Client.this.logout();
            Client.this.exit();
        }

        public void back() {
            Client.this.back();
        }

        public void backMainMenu() {
            Client.this.backMainMenu();
        }

        public void mana(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem().equals("all")) mana = 0;
                else mana = Integer.parseInt((String) e.getItem());
                sendRequest();
            }
        }

        public void lockMode(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item.equals("all cards")) lockMode = 0;
                if (item.equals("locked cards")) lockMode = 1;
                if (item.equals("unlocked cards")) lockMode = 2;
                sendRequest();
            }
        }

        public void search(DocumentEvent e) {
            try {
                name = e.getDocument().getText(0, e.getDocument().getLength());
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
            sendRequest();
        }

        public void classOfCard(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object item = e.getItem();
                if (item.equals("All classes")) classOfCard = null;
                else classOfCard = (String) item;
                sendRequest();
            }
        }

        public void selectDeck(String deckName) {
            if (deckName.equals(this.deckName)) this.deckName = null;
            else this.deckName = deckName;
            sendRequest();
        }

        public void newDeck(String deckName,String heroName){
            Request request = new Request.NewDeck(deckName,heroName);
            Server.getInstance().addRequest(request);
        }

        public void deleteDeck(String deckName){
            Request request = new Request.DeleteDeck(deckName);
            Server.getInstance().addRequest(request);
        }

        public void changeDeckName(String oldDeckName,String newDeckName){
            Request request = new Request.ChangeDeckName(oldDeckName, newDeckName);
            Server.getInstance().addRequest(request);
        }

        public void changeHeroDeck(String deckName,String heroName){
            Request request = new Request.ChangeHeroDeck(deckName, heroName);
            Server.getInstance().addRequest(request);
        }

        public void addCardToDeck(String cardName){
            Request request = new Request.AddCardToDeck(cardName,deckName);
            Server.getInstance().addRequest(request);
        }

        public void removeCardFromDeck(String cardName){
            Request request = new Request.RemoveCardFromDeck(cardName,deckName);
            Server.getInstance().addRequest(request);
        }

        public void reset() {
            name = null;
            classOfCard = null;
            deckName = null;
            mana = 0;
            lockMode = 0;
        }

        public void sendRequest() {
            sendCollectionRequest(name,classOfCard,mana,lockMode,deckName);
        }

        public void update(){
            sendRequest();
        }
    }
}
