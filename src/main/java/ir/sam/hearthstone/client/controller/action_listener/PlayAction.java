package ir.sam.hearthstone.client.controller.action_listener;

import ir.sam.hearthstone.client.controller.Client;
import ir.sam.hearthstone.client.model.log.ButtonLog;
import ir.sam.hearthstone.client.model.requests.*;
import ir.sam.hearthstone.client.util.hibernate.Connector;

import static ir.sam.hearthstone.client.view.PanelType.MAIN_MENU;
import static ir.sam.hearthstone.client.view.PanelType.PLAY;

public class PlayAction {
    private final Connector connector;
    private final Client client;

    public PlayAction(Connector connector, Client client) {
        this.connector = connector;
        this.client = client;
    }


    public void gotoMainMenu(){
        client.goTo(MAIN_MENU.name(),null);
    }

    public void exit() {
        client.addRequest(new ExitGame());
        connector.save(new ButtonLog(client.getUsername(), "exit", PLAY.toString()));
    }

    public void endTurn() {
        client.addRequest(new EndTurn());
        connector.save(new ButtonLog(client.getUsername(), "end turn", PLAY.toString()));
    }

    public void selectCardInHand(int side, String index) {
        int index1 = Integer.parseInt(index);
        client.addRequest(new SelectCardInHand(side, index1));
        connector.save(new ButtonLog(client.getUsername()
                , "selectCardInHand,side:" + side + " index:" + index, PLAY.toString()));
    }

    public void selectMinion(int side, String index) {
        String[] indexes = index.split(",");
        int index1 = Integer.parseInt(indexes[0]);
        int emptyIndex = Integer.parseInt(indexes[1]) + index1;
        client.addRequest(new SelectMinion(side, index1, emptyIndex));
        connector.save(new ButtonLog(client.getUsername()
                , "selectMinion,side:" + side + " index:" + index, PLAY.toString()));
    }

    public void selectHeroPower(int side) {
        client.addRequest(new SelectHeroPower(side));
        connector.save(new ButtonLog(client.getUsername()
                , "selectHeroPower,side:" + side, PLAY.toString()));
    }

    public void selectHero(int side) {
        client.addRequest(new SelectHero(side));
        connector.save(new ButtonLog(client.getUsername()
                , "selectHero,side:" + side, PLAY.toString()));
    }
}