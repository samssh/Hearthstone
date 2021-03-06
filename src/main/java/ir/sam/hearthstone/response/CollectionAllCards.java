package ir.sam.hearthstone.response;

import ir.sam.hearthstone.client.Client;
import ir.sam.hearthstone.view.model.CardOverview;
import lombok.Getter;

import java.util.List;

public class CollectionAllCards extends Response{
    @Getter
    private final List<CardOverview> cards;
    @Getter
    private final List<CardOverview> deckCards;
    @Getter
    private final boolean canAddDeck, canChangeHero;
    @Getter
    private final String deckName;

    public CollectionAllCards(List<CardOverview> cards, List<CardOverview> deckCards,
                              boolean canAddDeck, boolean canChangeHero, String deckName) {
        this.cards = cards;
        this.deckCards = deckCards;
        this.canAddDeck = canAddDeck;
        this.canChangeHero = canChangeHero;
        this.deckName = deckName;
    }

    @Override
    public void execute(Client client) {
        client.setCollectionDetail(cards,null,deckCards,canAddDeck,canChangeHero,deckName,
                null,null);
    }

    @Override
    public void accept(ResponseLogInfoVisitor responseLogInfoVisitor) {

    }
}
