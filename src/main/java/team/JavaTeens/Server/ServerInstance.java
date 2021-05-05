package team.JavaTeens.Server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Utils.ConsoleLog;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerInstance {

    private ConfigValue config;

    public ServerInstance() {

    }
    public void run(){

        ServerSocket serverSocket = null;
        ConsoleLog.info("Server Starting...");

        this.config = readConfigFile("Config.json"); //TODO: replace so it can be modified in the args.

        try {
            serverSocket = new ServerSocket(this.config.port);
            ConsoleLog.info("Starting Server on port: " + this.config.port);
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleLog.fatalError("Cannot assign a port to the server");
        }
/*        Socket socket = serverSocket.accept();
        System.out.println("Accept Connection");

        // to send data to the client
        PrintStream printStream = new PrintStream(socket.getOutputStream());

        // to read data coming from the client
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));*/
        // to read data from the keyboard
        BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(System.in));

        ConsoleLog.info("Server finished loading");
        // server executes continuously
        while (true) {

            try {
                Socket clientSocket = serverSocket.accept();
                new ClientConnection(clientSocket, this.config.dataBasePath).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException nullPointerException){
                ConsoleLog.error(nullPointerException.getMessage());
            }
        } // end of while
    }
    private ConfigValue readConfigFile(String filePath) {
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
    private static class ConfigValue {

        private final String dataBasePath;
        private final int port;

        public ConfigValue(String dataBasePath, int port) {
            this.dataBasePath = dataBasePath;
            this.port = port;
        }
    }
}
