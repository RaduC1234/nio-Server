package team.JavaTeens.ClientRequest;

import team.JavaTeens.Server.ClientConnection;

import java.nio.ByteBuffer;

public class ClientMessage implements Cloneable{

    private ClientConnection client;
    private ByteBuffer message;

    private boolean hasContent = false;

    public ClientConnection getClient() {
        return client;
    }

    public ClientMessage setClient(ClientConnection client) {
        this.client = client;
        return this;
    }

    public ClientMessage() {

    }

    public ClientMessage(ClientMessage message) {
        this.client = message.getClient();
        this.message = message.getMessage();
        this.hasContent = message.hasContent();
    }

    public ByteBuffer getMessage() {
        return message;
    }

    public ClientMessage setMessage(ByteBuffer message) {
        this.message = message;
        return this;
    }
    public boolean hasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
