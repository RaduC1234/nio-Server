package team.JavaTeens.Server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ClientConnection{

    private boolean isAuthenticated;
    private final SocketChannel channel;
    private String guestName;

    public ClientConnection(SocketChannel channel) throws IOException {
        this.isAuthenticated = false;
        this.channel = channel;
        this.guestName = channel.getRemoteAddress().toString();
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public synchronized void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public String getGuestName() {
        return guestName;
    }

    public synchronized void setGuestName(String guestName) {
        this.guestName = guestName;
    }

}
