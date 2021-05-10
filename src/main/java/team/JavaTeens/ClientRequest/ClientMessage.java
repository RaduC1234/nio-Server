package team.JavaTeens.ClientRequest;

import team.JavaTeens.Server.ClientConnection;

import java.nio.ByteBuffer;

public class ClientMessage {

    private ClientConnection client;
    private ByteBuffer message;

    public ClientConnection getClient() {
        return client;
    }

    public ClientMessage setClient(ClientConnection client) {
        this.client = client;
        return this;
    }

    public ByteBuffer getMessage() {
        return message;
    }

    public ClientMessage setMessage(ByteBuffer message) {
        this.message = message;
        return this;
    }
}
