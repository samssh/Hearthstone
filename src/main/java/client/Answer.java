package client;

import controller.Executable;
import model.Player;

public abstract class Answer implements Executable {
    public abstract void execute();
    public static class LoginAnswer extends Answer {
        Player player;
        String message;

        public LoginAnswer(Player player, String message) {
            this.player = player;
            this.message = message;
        }

        @Override
        public void execute() {
            Client.getInstance().login(player,message);
        }
    }

}
