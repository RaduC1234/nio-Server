package team.JavaTeens.Server;

import team.JavaTeens.Utils.ConsoleLog;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientConnection extends Thread {

    private final Socket socket;
    private String clientName;
    private final String dataBasePath;

    //to send data to the client
    private PrintWriter writer;

    //to read data coming from the client
    private BufferedReader reader;


    public ClientConnection(Socket socket, String dataBasePath) {
        this.socket = socket;
        this.clientName = socket.getInetAddress().getHostAddress();
        this.dataBasePath = dataBasePath;
    }

    @Override
    public void run() {

        try {
            ConsoleLog.info("New Connection from " + clientName);

            // to send data to the client
            this.writer = new PrintWriter(socket.getOutputStream());

            // to read data coming from the client
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            final boolean[] active = {true};
            this.writer.println("{\"requestType\": \"authentication\"}");
            ConsoleLog.info("Auth send to " + clientName);

            /*while (active[0]){

                message = this.reader.readLine();
                if(message == null) {
                    disconnect("The disconnect was triggered by remote host");
                    return;
                }

                try{
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(message);
                    String username = node.get("username").asText();
                    String password  = node.get("password").asText();

                    File userFile = new File(dataBasePath + username + ".json");

                    if(!userFile.exists()){
                        this.writer.println("{\"responseType\": \"authentication\",\"authentication\": {\"username\": \"#error\",\"password\": \"#error\",\"#message\" : \"User cannot be found\" }}");
                    }

                }
                catch (JsonProcessingException ignored){
                    // send error message
                }
                catch (NullPointerException exception){
                    continue;
                }
                active[0] = false;
            }*/

            while (true) {

                message = this.reader.readLine();
                if(message == null || message.equalsIgnoreCase("quit")){
                    break;
                }
                else {
                    //sending data
                    this.writer.println(message + "\n\r");
                    this.writer.flush();

                    // receiving data
                    ConsoleLog.message(message, clientName);
                }

            }

            disconnect("The disconnect was triggered by remote host");
        }
        catch (SocketException e){
            disconnect(e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(String reason){

        try {
            this.writer.close();
            this.reader.close();
            this.socket.close();

            ConsoleLog.info("Client " + clientName +  " has disconnected. (" + reason + ")");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
