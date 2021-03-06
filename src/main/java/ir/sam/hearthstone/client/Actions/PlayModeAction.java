package ir.sam.hearthstone.client.Actions;

import ir.sam.hearthstone.client.Client;
import ir.sam.hearthstone.hibernate.Connector;
import ir.sam.hearthstone.model.log.ButtonLog;
import ir.sam.hearthstone.requests.Request;
import ir.sam.hearthstone.requests.SelectPlayMode;

import static ir.sam.hearthstone.view.PanelType.PLAY_MODE;

public class PlayModeAction {
    private final Connector connector;
    private final Client client;

    public PlayModeAction(Connector connector, Client client) {
        this.connector = connector;
        this.client = client;
    }

    public void select(String modeName){
        connector.save(new ButtonLog(client.getUsername(), "play mode: "+modeName, PLAY_MODE.toString()));
        Request request = new SelectPlayMode(modeName);
        client.getRequestSender().sendRequest(request);
    }
}
