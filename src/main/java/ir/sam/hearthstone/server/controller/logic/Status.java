package ir.sam.hearthstone.server.controller.logic;

import ir.sam.hearthstone.server.model.account.Deck;
import ir.sam.hearthstone.server.model.account.Player;
import ir.sam.hearthstone.server.model.client.BigDeckOverview;
import ir.sam.hearthstone.server.model.main.Card;
import ir.sam.hearthstone.server.model.account.CardDetails;
import ir.sam.hearthstone.server.model.response.Response;
import ir.sam.hearthstone.server.model.response.StatusDetails;

import java.util.*;
import java.util.stream.Collectors;

public class Status {

    public Status() {
    }

    public Response sendStatus(Player player) {
        return new StatusDetails(makeStatusDetails(player));
    }

    private List<BigDeckOverview> makeStatusDetails(Player player) {
        return player.getDecks().stream().sorted(Comparator.comparing(Deck::getWinRate)
                .thenComparing(Deck::getWins).thenComparing(Deck::getManaAverage).thenComparing(Deck::getName))
                .map(this::createBigDeckOverview).collect(Collectors.toList());
    }

    private BigDeckOverview createBigDeckOverview(Deck deck) {
        return new BigDeckOverview(deck, (getMVC(deck).map(Card::getName).orElse(null)));
    }

    private Optional<Card> getMVC(Deck deck) {
        Map<Card, CardDetails> map = deck.getCards();
        List<Card> result = new ArrayList<>();
        map.values().stream().map(CardDetails::getRepeatedTimes).max(Integer::compareTo)
                .ifPresent(
                        integer ->
                                map.entrySet().stream().filter(
                                        entry -> entry.getValue().getUsage() == integer
                                ).forEach(
                                        entry -> result.add(entry.getKey())
                                )
                );
        return result.stream().max(Comparator.comparing(Card::getRarity).thenComparing(Card::getMana)
                .thenComparing(Card::getInstanceValue).thenComparing(Card::getName));
    }

}
