package ir.sam.hearthstone.requests;

import ir.sam.hearthstone.server.Server;
import lombok.Getter;

public class PlayCard extends Request {
    @Getter
    private final String cardName;

    public PlayCard(String cardName) {
        this.cardName = cardName;
    }

    @Override
    public void execute(Server server) {
        server.playCard(cardName);
    }

    @Override
    public void accept(RequestLogInfoVisitor requestLogInfoVisitor) {
        requestLogInfoVisitor.setPlayCard(this);
    }
}