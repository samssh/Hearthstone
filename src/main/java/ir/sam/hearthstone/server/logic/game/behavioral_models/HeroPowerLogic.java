package ir.sam.hearthstone.server.logic.game.behavioral_models;

import ir.sam.hearthstone.model.main.HeroPower;
import ir.sam.hearthstone.server.logic.game.Side;
import lombok.Getter;
import lombok.Setter;

public class HeroPowerLogic implements ComplexLogic {
    @Getter
    protected HeroPower heroPower;
    @Getter
    protected final Side side;
    @Getter
    @Setter
    protected int lastTurnUse;

    public HeroPowerLogic(Side side, HeroPower heroPower) {
        this.side = side;
        this.heroPower = heroPower.clone();
        lastTurnUse = 0;
    }

    @Override
    public String getName() {
        return heroPower.getName();
    }
}
