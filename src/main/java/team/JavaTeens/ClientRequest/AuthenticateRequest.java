package team.JavaTeens.ClientRequest;

import team.JavaTeens.Server.ClientConnection;

public class AuthenticateRequest extends Request{

    private final ClientConnection client;

    public AuthenticateRequest(ClientConnection client) {
        this.admin = false;
        this.client = client;
    }

    @Override
    public void execute() {

    }
}
