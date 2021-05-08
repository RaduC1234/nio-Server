package team.JavaTeens.Server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class ServerInstance implements Runnable {

    private String databasePath;
    private int port;
    private ServerSocketChannel ssc;
    private Selector selector;
    private List<ClientConnection> clients; // a list of all clients connected

    public ServerInstance() {
        ConsoleLog.info("Server Starting...");
        try {
            ConfigValue config = readConfigFile("Config.json");
            this.databasePath = config.dataBasePath;
            this.port = config.port;

            start();

            CommandHandler handler = new CommandHandler()
                    .addCommand(new HelpCommand())
                    .addCommand(new SayCommand(this))
                    .listen();

        } catch (IOException e) {
            e.printStackTrace();
            ConsoleLog.fatalError("Fatal Error:" + e.getMessage());
        }
    }

    public void start() throws IOException {

        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(port));
        this.ssc.configureBlocking(false);
        this.selector = Selector.open();

        this.ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        ConsoleLog.info("Starting Server on port: " + this.port + ".");

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

        }  //here we handle client requests
        else if (key.isReadable()) {
            handleRequests(key);
        } // here we send client requests
        else if (key.isWritable()) {
            sendRequests(key);
        }
    }

    private void handleRequests(SelectionKey key) throws IOException {
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
        ByteBuffer buffer = ByteBuffer.allocate(80);
        buffer.clear();

        try {
            channelClient.read(buffer);
        }
        catch (SocketException e){
            // here we handle client disconnection
            disconnect(clientConnection, e.getMessage());
            clients.remove(clientConnection);
        }

        //here starts the server request handler
        if(clientConnection.isAuthenticated()){
            //we want to handle requests only from authenticated users
            //TODO : continue from here

        }
        else {
            //here we handle unauthenticated users
            System.out.printf("Client says: %s\n", new String(buffer.array()));
            //TODO: Create an authentication protocol
        }

    }
    private void sendRequests(SelectionKey key) throws IOException {

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

    private static class ConfigValue {

        private final String dataBasePath;
        private final int port;

        public ConfigValue(String dataBasePath, int port) {
            this.dataBasePath = dataBasePath;
            this.port = port;
        }
    }
    private static ConfigValue readConfigFile(String filePath) {
        ObjectMapper configMapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = configMapper.readTree(new File(filePath));
        } catch (IOException e) {

            e.printStackTrace();
            ConsoleLog.fatalError(e.getMessage());
        }

        return new ConfigValue(node.get("external").textValue(), node.get("port").asInt());
    }
}