package team.JavaTeens.ClientRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import team.JavaTeens.Account.Account;
import team.JavaTeens.Server.ClientConnection;
import team.JavaTeens.Server.ServerInstance;
import team.JavaTeens.Utils.ConsoleLog;
import team.JavaTeens.Utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Request implements Runnable {

    private final RequestType requestType;
    private final ClientConnection client;
    private ByteBuffer message;
    private Charset charset = StandardCharsets.UTF_8;

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

    public ByteBuffer getMessage() {
        return message;
    }

    public void setMessage(ByteBuffer message) {
        this.message = message;
    }

    @Override
    public void run() {

        try {
            switch (requestType) {

                case SERVER_AUTHENTICATE:
                    authenticate();
                    break;
                case SERVER_COMMAND_PING:
                    ConsoleLog.info("Ping!");
                    this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"Pong!\"}".getBytes(charset)));
                    break;
                case ADMIN_CREATE_USER_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {

                            ByteBuffer message = this.message;
                            ObjectMapper mapper = new ObjectMapper();

                            JsonNode jsonNode = mapper.readTree(new String(message.array(), StandardCharsets.UTF_8));

                            JsonNode content = jsonNode.get("ADMIN_CREATE_USER_ACCOUNT");

                            Account account = (Account) FileUtils.parseJson(content.asText(), Account.class);
                            File file = new File(ServerInstance.config.getDataBasePath() + "\\" + account.getAuthentication().getUserName() + ".json");


                            FileUtils.parseToJson(file, account);

                            ConsoleLog.info("User " + this.client.getGuestName() + " successfully create new user with the following characteristics: \n" +
                                    "Username: " + account.getAuthentication().getUserName() + "\n" +
                                    "Password: " + account.getAuthentication().getPassword() + "\n" +
                                    "Admin Status: " + account.isAdmin() + "\n" +
                                    "First name: " + account.getFirstName() + "\n" +
                                    "Last name: " + account.getLastName() + "\n" +
                                    "Middle name: " + Arrays.toString(account.getMiddleName()));
                        } else {
                            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes(charset)));
                        }
                    } else {
                        authenticate();
                    }
                    break;
                case ADMIN_DELETE_USER_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            //place code here
                        } else {
                            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes(charset)));
                        }
                    } else {
                        authenticate();
                    }
                    break;
                case ADMIN_EDIT_USER_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            //place code here
                        } else {
                            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes(charset)));
                        }
                    } else {
                        authenticate();
                    }
                    break;
                case ADMIN_GET_ACCOUNTS_INFO:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            //place code here
                        } else {
                            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes(charset)));
                        }
                    } else {
                        authenticate();
                    }
                    break;
                case ADMIN_GET_LIST_OF_USERNAMES:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            //place code here
                        } else {
                            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"You are not an admin\"}".getBytes(charset)));
                        }
                    } else {
                        authenticate();
                    }
                    break;
                case USER_ADD_EVENT_DAY:
                    if (this.client.isAuthenticated()) {
                        //place code here
                    }
                    break;
                case USER_EDIT_SELF_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        //place code here
                    }
                    break;
                case USER_GET_ACCOUNT_INFO:
                    if (this.client.isAuthenticated()) {
                        //place code here
                    }
                    break;
                case USER_LOG_OFF:
                    if (!this.client.isAuthenticated()) {
                        this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"Already Logged Off\"}".getBytes(charset)));
                        return;
                    }
                    this.client.getChannel().write(ByteBuffer.wrap("(\"responseMessage\":\"Logged Off\"}".getBytes(charset)));
                    this.client.setAuthenticated(false);
                    authenticate();
                    break;
                case UNKNOWN:
                    this.client.disconnect("Invalid Request");
                    return;
            }

        } catch (InterruptedException | IOException exception) {
            ConsoleLog.error(exception.getMessage());
            //this.client.disconnect(exception.getMessage());
            exception.printStackTrace();
        } finally {
            ServerInstance.requestHandler.existingRequests.remove(this);
        }
    }

    private void authenticate() throws IOException, InterruptedException {
        getClient().getChannel().write(ByteBuffer.wrap("{\"requestType\":\"SERVER_AUTHENTICATE\"}".getBytes(charset)));
        synchronized (this) {
            this.wait();
        }
        ByteBuffer message = this.message;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(message.array());
        } catch (JsonParseException exception) {
            this.client.disconnect("Invalid Request");
            return;
        }
        JsonNode auth = jsonNode.get("SERVER_AUTHENTICATE");

        try {
            File file = FileUtils.findFile(auth.get("username").asText() + ".json", new File(ServerInstance.config.getDataBasePath()));
            Account account = (Account) FileUtils.parseJson(FileUtils.readFile(file.getAbsolutePath()), Account.class);

            if (!account.getAuthentication().getPassword().equalsIgnoreCase(auth.get("password").asText())) {
                this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"Invalid credentials\"}".getBytes(charset)));
                authenticate();
                return;
            }
            this.client.setAccount(account);
            this.client.setAuthenticated(true);
            ConsoleLog.info("Client: " + this.client.getGuestName() + " successfully authenticated as " + account.getAuthentication().getUserName() + ".");
            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"Access Granted\"}".getBytes(charset)));
        } catch (FileNotFoundException e) {
            this.client.getChannel().write(ByteBuffer.wrap("{\"responseMessage\":\"Invalid credentials\"}".getBytes(charset)));
            authenticate();
        }
    }
}