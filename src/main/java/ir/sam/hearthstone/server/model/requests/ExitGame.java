package ir.sam.hearthstone.server.model.requests;

import ir.sam.hearthstone.server.util.hibernate.DatabaseDisconnectException;

public class ExitGame extends Request {
    @Override
    public void execute(RequestExecutor requestExecutor) throws DatabaseDisconnectException {
        requestExecutor.exitGame();
    }
}