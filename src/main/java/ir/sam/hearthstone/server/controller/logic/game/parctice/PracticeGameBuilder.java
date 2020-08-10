package ir.sam.hearthstone.server.controller.logic.game.parctice;

import ir.sam.hearthstone.server.controller.ClientHandler;
import ir.sam.hearthstone.server.controller.logic.game.AbstractGameBuilder;
import ir.sam.hearthstone.server.controller.logic.game.Side;
import ir.sam.hearthstone.server.model.account.Deck;
import ir.sam.hearthstone.server.model.main.Passive;
import ir.sam.hearthstone.server.model.response.ChangeCardOnPassive;
import ir.sam.hearthstone.server.model.response.Response;
import ir.sam.hearthstone.server.resource_loader.ModelLoader;
import ir.sam.hearthstone.server.util.hibernate.DatabaseDisconnectException;

import static ir.sam.hearthstone.server.controller.Constants.STARTING_HAND_CARDS;
import static ir.sam.hearthstone.server.controller.logic.game.Side.PLAYER_ONE;
import static ir.sam.hearthstone.server.controller.logic.game.Side.PLAYER_TWO;

public class PracticeGameBuilder extends AbstractGameBuilder {
    public PracticeGameBuilder(ModelLoader modelLoader) {
        super(modelLoader);
    }

    @Override
    protected void build0() {
        result = new PracticeGame(gameStateBuilder.build(), modelLoader);
        result.startGame();
        sendEvents(PLAYER_ONE);
        sendEvents(PLAYER_TWO);
    }

    @Override
    public Response setPassive(Side client, Passive passive, ClientHandler clientHandler) {
        if (client == PLAYER_TWO)
            throw new UnsupportedOperationException();
        if (sideBuilderMap.get(PLAYER_ONE).getSentPassives().contains(passive)) {
            if (gameStateBuilder.getPassive(PLAYER_ONE) == null) {
                gameStateBuilder.setPassive(PLAYER_ONE, passive);
                return sendPassives(PLAYER_TWO, "select opponent passive");
            } else {
                gameStateBuilder.setPassive(PLAYER_TWO, passive);
                return clientHandler.sendDecksForSelection("select opponent deck");
            }
        }
        return null;
    }

    @Override
    public Response setDeck(Side client, Deck deck) {
        if (client == PLAYER_TWO)
            throw new UnsupportedOperationException();
        if (gameStateBuilder.getDeck(PLAYER_ONE) == null) {
            gameStateBuilder.setDeck(PLAYER_ONE, deck);
            return sendPassives(PLAYER_ONE, "select your passive");
        } else {
            gameStateBuilder.setDeck(PLAYER_TWO, deck);
            deckToList(sideBuilderMap.get(PLAYER_ONE).getDeck(), gameStateBuilder.getDeck(PLAYER_ONE));
            pickCards(sideBuilderMap.get(PLAYER_ONE).getHand(), sideBuilderMap.get(PLAYER_ONE).getHandState()
                    , sideBuilderMap.get(PLAYER_ONE).getDeck(), STARTING_HAND_CARDS);
            return sendCards(sideBuilderMap.get(PLAYER_ONE).getHand(), sideBuilderMap.get(PLAYER_ONE).getHandState());
        }

    }

    @Override
    public Response selectCard(Side client, int index) {
        if (client == PLAYER_TWO)
            throw new UnsupportedOperationException();
        if (sideBuilderMap.get(PLAYER_TWO).getHand().size() == 0) {
            return new ChangeCardOnPassive(changeState(sideBuilderMap.get(PLAYER_ONE).getHand()
                    , sideBuilderMap.get(PLAYER_ONE).getHandState(), index), index);
        } else {
            return new ChangeCardOnPassive(changeState(sideBuilderMap.get(PLAYER_TWO).getHand()
                    , sideBuilderMap.get(PLAYER_TWO).getHandState(), index), index);
        }
    }

    @Override
    public Response confirm(Side client) throws DatabaseDisconnectException {
        if (client == PLAYER_TWO)
            throw new UnsupportedOperationException();
        if (sideBuilderMap.get(PLAYER_TWO).getHand().size() == 0) {
            finalizeHand(sideBuilderMap.get(PLAYER_ONE).getHand(), sideBuilderMap.get(PLAYER_ONE).getHandState()
                    , sideBuilderMap.get(PLAYER_ONE).getDeck());
            gameStateBuilder.setHand(PLAYER_ONE, sideBuilderMap.get(PLAYER_ONE).getHand())
                    .setDeckCards(PLAYER_ONE, sideBuilderMap.get(PLAYER_ONE).getDeck());
            deckToList(sideBuilderMap.get(PLAYER_TWO).getDeck(), gameStateBuilder.getDeck(PLAYER_TWO));
            pickCards(sideBuilderMap.get(PLAYER_TWO).getHand(), sideBuilderMap.get(PLAYER_TWO).getHandState()
                    , sideBuilderMap.get(PLAYER_TWO).getDeck(), STARTING_HAND_CARDS);
            return sendCards(sideBuilderMap.get(PLAYER_TWO).getHand(), sideBuilderMap.get(PLAYER_TWO).getHandState());
        } else {
            finalizeHand(sideBuilderMap.get(PLAYER_TWO).getHand(), sideBuilderMap.get(PLAYER_TWO).getHandState()
                    , sideBuilderMap.get(PLAYER_TWO).getDeck());
            gameStateBuilder.setHand(PLAYER_TWO, sideBuilderMap.get(PLAYER_TWO).getHand()).setDeckCards(PLAYER_TWO
                    , sideBuilderMap.get(PLAYER_TWO).getDeck());
            build0();
            return result.getResponse(PLAYER_ONE);
        }
    }
}
