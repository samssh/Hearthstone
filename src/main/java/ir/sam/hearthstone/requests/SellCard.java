package ir.sam.hearthstone.requests;

import lombok.Getter;

public class SellCard extends Request {
    @Getter
    private final String cardName;

    public SellCard(String cardName) {
        this.cardName = cardName;
    }

    @Override
    public void execute(RequestExecutor requestExecutor) {
        requestExecutor.sellCard(cardName);
    }

}