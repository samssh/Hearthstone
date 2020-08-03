package ir.sam.hearthstone.client.model.response;

import lombok.Getter;

public class LoginResponse extends Response {
    @Getter
    private final boolean success;
    @Getter
    private final String message;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public void execute(ResponseExecutor responseExecutor) {
        responseExecutor.login(success, message);
    }
}