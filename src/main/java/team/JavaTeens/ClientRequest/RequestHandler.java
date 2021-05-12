package team.JavaTeens.ClientRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Server.ClientConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {

    private final ExecutorService service;
    public List<Request> existingRequests;
    private List<Request> newRequests;
    private ClientMessage message;

    public RequestHandler(ClientMessage message, int threadPool) {
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
            this.newRequests.add(new Request(requestType, connection));
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

                //this causes spaghetti code but i don't have time for a better implementation
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
                if (existingRequest)
                    continue;

                try {
                    handleRequest(requestMessage);

                } catch (IOException e) {
                    System.out.println("error");
                    this.message.getClient().disconnect(e.getMessage());
                }
            }
        }
    }

    public void handleRequest(ClientMessage message) throws IllegalArgumentException, IOException {

        Request request = new Request(getRequestType(message.getMessage()), message.getClient());
        synchronized (this.message){
            request.setMessage(this.message.getMessage());
        }
        synchronized (this.newRequests){
            this.existingRequests.add(request);
            this.service.execute(request);
        }
    }

    private static RequestType getRequestType(ByteBuffer message) throws JsonProcessingException {

        System.out.println(new String(message.array(), StandardCharsets.UTF_8));
        JsonNode node = new ObjectMapper().readTree(new String(message.array(), StandardCharsets.UTF_8));
        String type = node.get("requestType").asText();

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
                return RequestType.ADMIN_GET_ACCOUNTS_INFO;
            case "USER_GET_ACCOUNT_INFO":
                return RequestType.USER_GET_ACCOUNT_INFO;
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