package team.JavaTeens.ClientRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Account.Account;
import team.JavaTeens.Server.ClientConnection;

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

    public void setMessage(ByteBuffer message) {
        this.message = message;
    }

    public ByteBuffer getMessage() {
        return message;
    }

    @Override
    public void run() {
        switch (requestType){

            case SERVER_AUTHENTICATE:
                try {
                    getClient().getChannel().write(ByteBuffer.wrap("{\"requestType\":\"SERVER_AUTHENTICATE\"}".getBytes()));
                    synchronized (this){
                        this.wait();
                    }
                    ByteBuffer message = this.message;
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(message.array());
                    JsonNode auth = jsonNode.get("SERVER_AUTHENTICATE");
                    Account.Auth authentication = new Account.Auth(auth.get("username").asText(),auth.get("password").asText());
                    System.out.println(authentication.getUserName() + " " +authentication.getPassword());


                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case SERVER_COMMAND_PING:
                break;
            case ADMIN_CREATE_USER_ACCOUNT:
                break;
            case ADMIN_DELETE_USER_ACCOUNT:
                break;
            case ADMIN_EDIT_USER_ACCOUNT:
                break;
            case ADMIN_GET_ACCOUNTS_INFO:
                break;
            case ADMIN_GET_LIST_OF_USERNAMES:
                break;
            case USER_ADD_EVENT_DAY:
                break;
            case USER_EDIT_SELF_ACCOUNT:
                break;
            case USER_GET_ACCOUNT_INFO:
                break;
            case UNKNOWN:
                break;
        }
    }
}
