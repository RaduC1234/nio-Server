package team.JavaTeens.ServerCommand;

import team.JavaTeens.Server.ClientConnection;
import team.JavaTeens.Server.ServerInstance;
import team.JavaTeens.Utils.ConsoleLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class SayCommand extends Command {

    private List<ClientConnection> clients;
    private ServerInstance instance;

    public SayCommand(ServerInstance instance){
        this.instance = instance;
        this.help = "say <user> <message>";
        this.name = "say";
    }

    @Override
    protected void execute() {

        this.clients = this.instance.returnClientsList();

        String[] args = this.arguments.split(" ");

        if(clients == null || clients.size() == 0){
            ConsoleLog.info("There are no clients connected to the server.");
            return;
        }

        if(this.arguments.equalsIgnoreCase("") || this.arguments.split(" ").length == 0) {
            //TODO: replace with a help
            ConsoleLog.warn("Please give a message");
            return;
        }

        for(ClientConnection clientConnection : clients){
            try {
                //TODO: replace with a json format
                clientConnection.getChannel().write(ByteBuffer.wrap(this.arguments.getBytes()));
                ConsoleLog.info("Echo:" + this.arguments);
            } catch (IOException e) {
                ConsoleLog.error("Cannot send the message. Reason: " + e.getMessage());
                e.printStackTrace();
            }
        }
        /*
        TODO: add ability to select a user to send a message
        boolean clientFound = false;

        for(ClientConnection connection : clients){
            if (connection.getGuestName().equalsIgnoreCase(args[0])) {
                clientFound = true;
                String message = Arrays.toString(args).replace(args[0], "");

                try {
                    connection.getChannel().write(ByteBuffer.wrap(message.getBytes()));
                } catch (IOException e) {
                    ConsoleLog.error("Cannot send message to " + args[0] + " Reason: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            }
        }*/
    }
}