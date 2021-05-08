package team.JavaTeens.Server;

import java.io.IOException;

public class Server {

    //server
    public static void main(String[] args) throws IOException {

        ServerInstance thisServer = new ServerInstance();

        Thread t = new Thread(thisServer);
        t.setName("ServerInstance");
        t.start();
    }
}
