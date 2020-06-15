package requests;

import lombok.Getter;
import server.Server;

public class LoginRequest extends Request {
    @Getter
    private final String userName, password;
    @Getter
    private final int mode;

    public LoginRequest(String userName, String password, int mode) {
        this.userName = userName;
        this.password = password;
        this.mode = mode;
    }

    @Override
    public void execute(Server server) {
        server.login(userName, password, mode);
    }

    @Override
    public void accept(RequestLogInfoVisitor requestLogInfoVisitor) {
        requestLogInfoVisitor.setLoginRequest(this);
    }
}