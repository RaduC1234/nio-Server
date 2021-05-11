package team.JavaTeens.ClientRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Account.Account;
import team.JavaTeens.Server.ClientConnection;
import team.JavaTeens.Server.ServerInstance;
import team.JavaTeens.Utils.ConsoleLog;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Request implements Runnable {

    private final RequestType requestType;
    private final ClientConnection client;
    private ByteBuffer message;

    public Request(RequestType requestType, ClientConnection client) {
        this.requestType = requestType;
        this.client = client;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public ClientConnection getClient() {
        return client;
    }

    public ByteBuffer getMessage() {
        return message;
    }

    public void setMessage(ByteBuffer message) {
        this.message = message;
    }

    @Override
    public void run() {

        try {
            switch (requestType) {

                case SERVER_AUTHENTICATE:
                    getClient().getChannel().write(ByteBuffer.wrap("{\"requestType\":\"SERVER_AUTHENTICATE\"}".getBytes()));
                    synchronized (this) {
                        this.wait();
                    }
                    ByteBuffer message = this.message;
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode;
                    try {
                        jsonNode = mapper.readTree(message.array());
                    } catch (JsonParseException exception) {
                        this.client.disconnect("Invalid Request");
                        return;
                    }
                    JsonNode auth = jsonNode.get("SERVER_AUTHENTICATE");
                    Account.Auth authentication = new Account.Auth(auth.get("username").asText(), auth.get("password").asText());
                    System.out.println(authentication.getUserName() + " " + authentication.getPassword());

                    break;
                case SERVER_COMMAND_PING:
                    ConsoleLog.info("Ping!");
                    this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"Pong!\"}".getBytes()));
                    break;
                case ADMIN_CREATE_USER_ACCOUNT:
                    if (this.client.isAuthenticated() && this.client.getAccount().isAdmin()) {
                        //place code here
                    } else {
                        this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes()));
                    }
                    break;
                case ADMIN_DELETE_USER_ACCOUNT:
                    if (this.client.isAuthenticated() && this.client.getAccount().isAdmin()) {
                        //place code here
                    } else {
                        this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes()));
                    }
                    break;
                case ADMIN_EDIT_USER_ACCOUNT:
                    if (this.client.isAuthenticated() && this.client.getAccount().isAdmin()) {
                        //place code here
                    } else {
                        this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes()));
                    }
                    break;
                case ADMIN_GET_ACCOUNTS_INFO:
                    if (this.client.isAuthenticated() && this.client.getAccount().isAdmin()) {
                        //place code here
                    } else {
                        this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes()));
                    }
                    break;
                case ADMIN_GET_LIST_OF_USERNAMES:
                    if (this.client.isAuthenticated() && this.client.getAccount().isAdmin()) {
                        //place code here
                    } else {
                        this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes()));
                    }
                    break;
                case USER_ADD_EVENT_DAY:
                    if (this.client.isAuthenticated()) {
                        //place code here
                    }
                    break;
                case USER_EDIT_SELF_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        //place code here
                    }
                    break;
                case USER_GET_ACCOUNT_INFO:
                    if (this.client.isAuthenticated()) {
                        //place code here
                    }
                    break;
                case UNKNOWN:
                    this.client.disconnect("Invalid Request");
                    return;
            }

        } catch (InterruptedException | IOException interruptedException) {
            interruptedException.printStackTrace();
        }
        finally {
            ServerInstance.requestHandler.existingRequests.remove(this);
        }
    }
}
