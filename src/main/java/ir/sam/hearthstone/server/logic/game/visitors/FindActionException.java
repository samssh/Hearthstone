package ir.sam.hearthstone.server.logic.game.visitors;

public class FindActionException extends Exception{
    public FindActionException(String message, Throwable cause) {
        super("cant find action with name:" + message, cause);
    }
}
