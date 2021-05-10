package team.JavaTeens.Server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.ClientRequest.ClientMessage;
import team.JavaTeens.ClientRequest.Requests;
import team.JavaTeens.ServerCommand.CommandHandler;
import team.JavaTeens.ServerCommand.HelpCommand;
import team.JavaTeens.ServerCommand.SayCommand;
import team.JavaTeens.Utils.ConsoleLog;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class ServerInstance implements Runnable {

    private ConfigFile config;
    private ServerSocketChannel ssc;
    private Selector selector;
    private List<ClientConnection> clients; // a list of all clients connected
    private ExecutorService executors;
    private CommandHandler handler;
    private CyclicBarrier barrier = new CyclicBarrier(1);
    private ByteBuffer buffer;
    private ClientMessage message = new ClientMessage();

    public ServerInstance() {
        ConsoleLog.info("Server Starting...");
        try {

            this.config = readConfigFile("Config.json");
            this.executors = Executors.newFixedThreadPool(3);

            //start
            this.ssc = ServerSocketChannel.open();
            this.ssc.socket().bind(new InetSocketAddress(this.config.port));
            this.ssc.configureBlocking(false);
            this.selector = Selector.open();

            this.ssc.register(selector, SelectionKey.OP_ACCEPT);

            //register new commands here
            this.handler = new CommandHandler()
                    .addCommand(new HelpCommand())
                    .addCommand(new SayCommand(this))
                    .listen();

        } catch (IOException e) {
            e.printStackTrace();
            ConsoleLog.fatalError("Fatal Error:" + e.getMessage());
        }
    }
    @Override
    public void run() {
        ConsoleLog.info("Starting Server on port: " + this.config.port + ".");

        clients = new ArrayList<>();

        Iterator<SelectionKey> it;
        while (ssc.isOpen()) {
            try {
                if (selector.select() != 0) {
                    it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        handleClientConnection(key);
                        it.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleClientConnection(SelectionKey key) throws IOException {

        // here we handle connection requests
        if (key.isAcceptable()) {

            ClientConnection channelClient = new ClientConnection(ssc.accept());

            channelClient.getChannel().configureBlocking(false);
            channelClient.getChannel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            ConsoleLog.info("New Connection from " + channelClient.getGuestName());
            clients.add(channelClient);

            sendRequest(Requests.SERVER_AUTHENTICATE,channelClient);

        }  //here we handle client requests
        else if (key.isReadable()) {
            handleIncomingBytes(key);
        } // here we send client requests
        else if (key.isWritable()) {
            handleOutcomingBytes(key);
        }
    }
    private void handleIncomingBytes(SelectionKey key) throws IOException {
        ClientConnection clientConnection = returnClientConnection(key);
        SocketChannel channelClient;

        try{
            channelClient = clientConnection.getChannel();
        }
        catch (NullPointerException e){
            return;
        }

        if (!channelClient.isOpen()) {
            ConsoleLog.info("Channel terminated by client");
        }

        buffer = ByteBuffer.allocate(80);
        buffer.clear();

        try {
            channelClient.read(buffer);
        }
        catch (SocketException e){
            // here we handle client disconnection
            disconnect(clientConnection, e.getMessage());
            clients.remove(clientConnection);
        }
        System.out.printf("Client says: %s\n", new String(buffer.array()));
        this.message.setClient(clientConnection);
        this.message.setMessage(buffer);
    }
    private void handleOutcomingBytes(SelectionKey key) throws IOException {

    }
    private void sendRequest(Requests request, ClientConnection connection){
        switch (request){

            case SERVER_AUTHENTICATE:
                this.executors.execute(() -> {
                    try {
                        connection.getChannel().write(ByteBuffer.wrap("{\"requestType\":\"SERVER_AUTHENTICATE\"}".getBytes()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                break;
            case SERVER_COMMAND_PING:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_CREATE_USER_ACCOUNT:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_DELETE_USER_ACCOUNT:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_EDIT_USER_ACCOUNT:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_GET_ACCOUNTS_INFO:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_GET_LIST_OF_USERNAMES:
                this.executors.execute(() -> {

                });
                break;
        }
    }
    private void handleRequest(Requests request, String requestContent){
        switch (request){

            case SERVER_AUTHENTICATE:
                this.executors.execute(() -> {

                });
            break;
            case SERVER_COMMAND_PING:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_CREATE_USER_ACCOUNT:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_DELETE_USER_ACCOUNT:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_EDIT_USER_ACCOUNT:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_GET_ACCOUNTS_INFO:
                this.executors.execute(() -> {

                });
                break;
            case ADMIN_GET_LIST_OF_USERNAMES:
                this.executors.execute(() -> {

                });
                break;
            case USER_ADD_EVENT_DAY:
                this.executors.execute(() -> {

                });
                break;
            case USER_EDIT_SELF_ACCOUNT:
                this.executors.execute(() -> {

                });;
            case USER_GET_ACCOUNT_INFO:
                this.executors.execute(() -> {

                });
                break;
            case UNKNOWN:
                this.executors.execute(() -> {

                });
                break;
        }
    }
    private ClientConnection returnClientConnection(SelectionKey key){

        for(ClientConnection forConnection : clients){
            if(forConnection.getChannel().equals(key.channel())){
                return forConnection;
            }
        }
        return null;
    }
    private void disconnect(ClientConnection connection, String reason) throws IOException{
        ConsoleLog.info("Client " + connection.getGuestName() +  " has disconnected. (" + reason + ")");
        connection.getChannel().close();
    }
    public List<ClientConnection> returnClientsList(){
        return this.clients;
    }
    private static class ConfigFile {

        private final String dataBasePath;
        private final int port;

        public ConfigFile(String dataBasePath, int port) {
            this.dataBasePath = dataBasePath;
            this.port = port;
        }
    }
    private static ConfigFile readConfigFile(String filePath) {
        ObjectMapper configMapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = configMapper.readTree(new File(filePath));
        } catch (IOException e) {

            e.printStackTrace();
            ConsoleLog.fatalError(e.getMessage());
        }

        return new ConfigFile(node.get("external").textValue(), node.get("port").asInt());
    }
}