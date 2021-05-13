package team.JavaTeens.ClientRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Server.ClientConnection;
import team.JavaTeens.Utils.ConsoleLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {

    private final ExecutorService service;
    private final String dataBasePath;
    public  List<Request> existingRequests;
    private List<Request> newRequests;
    private ClientMessage message;

    public RequestHandler(String dataBasePath, ClientMessage message, int threadPool) {
        this.dataBasePath = dataBasePath;
        this.message = message;
        this.existingRequests = new ArrayList<>();
        this.newRequests = new ArrayList<>();
        this.service = Executors.newFixedThreadPool(threadPool);

        Thread requestHandler = new Thread(this::run);
        requestHandler.setName("RequestHandler");
        requestHandler.start();
    }

    public void sendRequest(RequestType requestType, ClientConnection connection) {
        synchronized (this.existingRequests) {
            this.newRequests.add(new Request(requestType, connection, this.dataBasePath));
        }
    }

    private void run() {
        while (true) {
            synchronized (this.newRequests) {
                for (Request newReq : newRequests) {
                    this.newRequests.remove(newReq);
                    this.existingRequests.add(newReq);
                    this.service.execute(newReq);
                    break;
                }
                ClientMessage requestMessage;
                synchronized (this.message) {
                    requestMessage = new ClientMessage(this.message);
                    this.message.setHasContent(false);
                }
                if (!requestMessage.hasContent())
                    continue;

                //this causes spaghetti code but i don't have time for a better implementation.
                boolean existingRequest = false;
                synchronized (this.existingRequests) {
                    for (Request existReq : existingRequests) {
                        if (requestMessage.getClient().equals(existReq.getClient())) {
                            synchronized (existReq) {
                                existReq.setMessage(requestMessage.getMessage());
                                existReq.notify();
                            }
                            existingRequest = true;
                            break;
                        }
                    }
                }
                if (existingRequest) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                try {
                    handleRequest(requestMessage);

                } catch (IOException e) {
                    ConsoleLog.error(e.getMessage());
                    this.message.getClient().disconnect(e.getMessage());
                }
            }
        }
    }

    public void handleRequest(ClientMessage message) throws IllegalArgumentException, IOException {

        Request request = new Request(getRequestType(message), message.getClient(),this.dataBasePath);
        synchronized (this.message){
            request.setMessage(this.message.getMessage());
        }
        synchronized (this.newRequests){
            this.existingRequests.add(request);
            this.service.execute(request);
        }
    }

    private static RequestType getRequestType(ClientMessage clientMessage){

        JsonNode node; String type;

        try {
            node = new ObjectMapper().readTree(new String(clientMessage.getMessage().array(), StandardCharsets.UTF_8));
            type = node.get("requestType").asText();
        } catch (JsonProcessingException e) {
            ConsoleLog.warn("Incoming unknown request coming from " + clientMessage.getClient().getGuestName());
            return RequestType.UNKNOWN;
        }

        //this is a bad implementation but i'm already bored of this handler.
        switch (type){
            case "SERVER_AUTHENTICATE":
                return RequestType.SERVER_AUTHENTICATE;
            case "SERVER_COMMAND_PING":
                return RequestType.SERVER_COMMAND_PING;
            case "ADMIN_CREATE_USER_ACCOUNT":
                return RequestType.ADMIN_CREATE_USER_ACCOUNT;
            case "ADMIN_DELETE_USER_ACCOUNT":
                return RequestType.ADMIN_DELETE_USER_ACCOUNT;
            case "ADMIN_EDIT_USER_ACCOUNT":
                return RequestType.ADMIN_EDIT_USER_ACCOUNT;
            case "ADMIN_GET_ACCOUNTS_INFO":
                return RequestType.ADMIN_GET_ACCOUNT_INFO;
            case "USER_GET_ACCOUNT_INFO":
                return RequestType.USER_GET_SELF_ACCOUNT;
            case "ADMIN_GET_LIST_OF_USERNAMES":
                return RequestType.ADMIN_GET_LIST_OF_USERNAMES;
            case "USER_ADD_EVENT_DAY":
                return RequestType.USER_ADD_EVENT_DAY;
            case "USER_EDIT_SELF_ACCOUNT":
                return RequestType.USER_EDIT_SELF_ACCOUNT;
            case "USER_LOG_OFF":
                return RequestType.USER_LOG_OFF;
            default: return RequestType.UNKNOWN;
        }
    }
}