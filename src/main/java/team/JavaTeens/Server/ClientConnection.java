package team.JavaTeens.Server;

import team.JavaTeens.Account.Account;
import team.JavaTeens.ClientRequest.Request;
import team.JavaTeens.Utils.ConsoleLog;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ClientConnection{

    private boolean isAuthenticated;
    private final SocketChannel channel;
    private String guestName;
    private Account account = null;

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

    public Account getAccount() throws NullPointerException{
        if(this.account == null){
            throw new NullPointerException("Account not defined");
        }
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }



    public void disconnect(String reason) {
        ConsoleLog.info("Client " + this.getGuestName() +  " has disconnected. (" + reason + ")");
        try {
            this.getChannel().close();
        } catch (IOException e) {
            ConsoleLog.error("Client " + this.getGuestName() + " is already disconnected");
        }
    }
}
