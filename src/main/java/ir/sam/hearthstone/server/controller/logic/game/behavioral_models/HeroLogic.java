package ir.sam.hearthstone.server.controller.logic.game.behavioral_models;

import ir.sam.hearthstone.server.controller.logic.game.AbstractGame;
import ir.sam.hearthstone.server.controller.logic.game.GameState;
import ir.sam.hearthstone.server.controller.logic.game.Side;
import ir.sam.hearthstone.server.model.client.HeroOverview;
import ir.sam.hearthstone.server.model.main.Hero;
import ir.sam.hearthstone.server.model.response.PlayDetails;
import lombok.Getter;
import lombok.Setter;

public class HeroLogic extends CharacterLogic implements LiveCharacter {
    @Getter
    @Setter
    protected Hero hero;
    @Getter
    @Setter
    protected int hp, defence;


    public HeroLogic(Side side, Hero hero) {
        super(side);
        this.hero = hero.clone();
        hp = hero.getHp();
        defence = 0;
    }

    public void setDefence(int defence, GameState gameState) {
        if (defence != this.defence) {
            setDefence(defence);
            addChangeEvent(gameState);
        }
    }

    /**
     * attack enemy to this hero
     * this can be hero with weapon or minion
     *
     * @param damage damage that deals to this minion
     */
    public void dealDamage(int damage, AbstractGame game, boolean sendEvent) {
        defence -= damage;
        if (defence < 0) {
            hp += defence;
            defence = 0;
        }
        if (sendEvent) addChangeEvent(game.getGameState());
        if (hp < 0) {
            game.endGame(side);
        }
    }

    public void restore(int restore, GameState gameState) {
        restore = Math.min(hero.getHp() - hp, restore);
        if (restore > 0) {
            hp += restore;
            addChangeEvent(gameState);
        }
    }

    protected void addChangeEvent(GameState gameState) {
        PlayDetails.Event event = new PlayDetails.EventBuilder(PlayDetails.EventType.SET_HERO)
                .setOverview(getHeroOverview()).setSide(side.getIndex()).build();
        gameState.getEvents().add(event);
    }

    public HeroOverview getHeroOverview() {
        return new HeroOverview(this);
    }

    @Override
    public String getName() {
        return hero.getName();
    }
}
