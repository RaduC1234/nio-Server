package team.JavaTeens.ClientRequest;

import team.JavaTeens.Server.ClientConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {

    private final ExecutorService service;
    private List<Request> existingRequests;
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

    public void handleRequest(RequestType request, String requestContent) {

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
            System.out.printf("Client says: %s\n", new String(this.message.getMessage().array()));
            //this causes spaghetti code but i don't have time for a better implementation
            synchronized (existingRequests) {

                //try{
                for (Request existReq : existingRequests) {
                    if (requestMessage.getClient().equals(existReq.getClient())) {
                        synchronized (existReq) {
                        }
                            existReq.setMessage(requestMessage.getMessage());
                            existReq.getClient().notify();
                        }
                        break;
                    }
                }
                //}
                //catch (NullPointerException e){
//
                //}
            }
        }
    }
}
