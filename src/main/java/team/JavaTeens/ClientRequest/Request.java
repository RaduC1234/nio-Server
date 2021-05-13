package team.JavaTeens.ClientRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import team.JavaTeens.Account.Account;
import team.JavaTeens.Account.CalendarEvent;
import team.JavaTeens.Server.ClientConnection;
import team.JavaTeens.Server.ServerInstance;
import team.JavaTeens.Utils.ConsoleLog;
import team.JavaTeens.Utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Request implements Runnable {

    private final RequestType requestType;
    private final ClientConnection client;
    private final String dataBasePath;
    private ByteBuffer message;
    private final Charset charset = StandardCharsets.UTF_8;

    public Request(RequestType requestType, ClientConnection client, String dataBasePath) {
        this.requestType = requestType;
        this.client = client;
        this.dataBasePath = dataBasePath;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public ClientConnection getClient() {
        return client;
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
                    sendMessage("Pong!", true);
                    break;
                case ADMIN_CREATE_USER_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {

                            ByteBuffer message = this.message;
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(new String(message.array(), charset));
                            JsonNode content = jsonNode.get("ADMIN_CREATE_USER_ACCOUNT");

                            Account account = new Account()
                                    .setAdmin(content.get("admin").asBoolean())
                                    .setFirstName(content.get("firstName").asText())
                                    .setLastName(content.get("lastName").asText())
                                    .setMiddleName(jsonToArray(content, "middleName"))
                                    .setAuthentication(new Account.Auth(content.get("authentication").get("userName").asText(), content.get("authentication").get("password").asText()))
                                    .setDateEmployed(new CalendarEvent(LocalDate.now(), "Date Employed"));

                            File file = new File(this.dataBasePath + "\\" + account.getAuthentication().getUserName() + ".json");

                            if (file.exists()) {
                                sendMessage("User already exists", false);
                                return;
                            }

                            try {
                                FileUtils.parseToJson(file, account);
                            } catch (IOException e) {
                                sendMessage("\"Cannot write to file \"" + file.getAbsolutePath() + " \" + e.getMessage()",false);
                                ConsoleLog.error("Cannot write to file " + file.getAbsolutePath() + " " + e.getMessage());
                                e.printStackTrace();
                            }

                            sendMessage("User successfully created",true);
                            ConsoleLog.info("User " + this.client.getGuestName() + " successfully created a new user with the following characteristics: \n" +
                                    "Username: " + account.getAuthentication().getUserName() + "\n" +
                                    "Password: " + account.getAuthentication().getPassword() + "\n" +
                                    "Admin Status: " + account.isAdmin() + "\n" +
                                    "First name: " + account.getFirstName() + "\n" +
                                    "Last name: " + account.getLastName() + "\n" +
                                    "Middle name: " + Arrays.toString(account.getMiddleName()));
                        } else {
                            sendMessage("You are not an admin",false);
                        }
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case ADMIN_DELETE_USER_ACCOUNT:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            ByteBuffer message = this.message;
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(new String(message.array(), charset));
                            JsonNode content = jsonNode.get("ADMIN_DELETE_USER_ACCOUNT");

                            final String target = content.get("username").asText();

                            File file = new File(this.dataBasePath + "\\" + target + ".json");

                            if(!file.exists()){
                                sendMessage("The user does not exist",false);
                                return;
                            }
                            if(file.delete()){
                                sendMessage("User successfully deleted",true);
                                ConsoleLog.info("User " + this.client.getGuestName() + " successfully deleted user " + target + ".");
                            }
                            else sendMessage("Cannot delete user",false);

                        } else {
                            sendMessage("You are not an admin",false);
                        }
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case ADMIN_EDIT_USER_ACCOUNT: //TODO : test
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            ByteBuffer message = this.message;
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(new String(message.array(), charset));
                            JsonNode content = jsonNode.get("ADMIN_EDIT_USER_ACCOUNT");

                            File file = new File(this.dataBasePath + "\\" + content.get("authentication").get("userName").asText() + ".json");

                            if(!file.exists()){
                                sendMessage("User does not exist", false);
                                return;
                            }

                            Account account = new Account()
                                    .setAdmin(content.get("admin").asBoolean())
                                    .setFirstName(content.get("firstName").asText())
                                    .setLastName(content.get("lastName").asText())
                                    .setMiddleName(jsonToArray(content, "middleName"))
                                    .setAuthentication(new Account.Auth(content.get("authentication").get("userName").asText(), content.get("authentication").get("password").asText()))
                                    .setDateEmployed(new CalendarEvent(LocalDate.now(), "Date Employed"));

                            try {
                                FileUtils.parseToJson(file, account);
                            } catch (IOException e) {
                                sendMessage("\"Cannot write to file \"" + file.getAbsolutePath() + " \" + e.getMessage()",false);
                                ConsoleLog.error("Cannot write to file " + file.getAbsolutePath() + " " + e.getMessage());
                                e.printStackTrace();
                            }
                            sendMessage("User modified successfully",true);
                            ConsoleLog.info(this.client.getGuestName() + " modified user " + account.getAuthentication().getUserName());

                        } else {
                            sendMessage("You are not an admin",false);
                        }
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case ADMIN_GET_ACCOUNT_INFO: //TODO : test
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {
                            ByteBuffer message = this.message;
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode jsonNode = mapper.readTree(new String(message.array(), charset));
                            JsonNode jsonContent = jsonNode.get("ADMIN_GET_ACCOUNT_INFO");

                            final String target = jsonContent.get("username").asText();

                            File file = new File(this.dataBasePath + "\\" + target + ".json");

                            if(!file.exists()){
                                sendMessage("The user does not exist",false);
                                return;
                            }

                            Account account = (Account) FileUtils.parseJson(FileUtils.readFile(file.getAbsolutePath()), Account.class);

                            ObjectNode rootNode = mapper.createObjectNode();

                            rootNode.put("admin", account.isAdmin());
                            rootNode.put("firstName", account.getFirstName());
                            rootNode.put("lastName", account.getLastName());
                            ArrayNode middlename = mapper.valueToTree(Arrays.asList(account.getMiddleName()));
                            rootNode.putArray("middleName").addAll(middlename);
                            rootNode.putObject("authentication").put("username", account.getAuthentication().getUserName()).put("password" ,account.getAuthentication().getPassword());
                            this.client.getChannel().write(ByteBuffer.wrap(("\"responseType\":\"ADMIN_GET_LIST_OF_USERNAMES\",\"ADMIN_GET_LIST_OF_USERNAMES\":" + rootNode.asText() +"}}").getBytes()));

                        } else {
                            sendMessage("You are not an admin",false);
                        }
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case ADMIN_GET_LIST_OF_USERNAMES:
                    if (this.client.isAuthenticated()) {
                        if (this.client.getAccount().isAdmin()) {

                            File dataBase = new File(dataBasePath);

                            if(!dataBase.isDirectory()){
                                //TODO: handle error
                                return;
                            }
                            //i don't know what 'requireNonNull' does but i hope it works.
                            ArrayList<File> usersFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(dataBase.listFiles())));

                           this.client.getChannel().write(ByteBuffer.wrap(("{\"responseType\":\"ADMIN_GET_LIST_OF_USERNAMES\",\"ADMIN_GET_LIST_OF_USERNAMES\":{\"#listStart\": " + usersFiles.size() + " }}}").getBytes(charset)));

                            for(File file : usersFiles){
                                this.client.getChannel().write(ByteBuffer.wrap(("{\"requestType\" : \"ADMIN_GET_LIST_OF_USERNAMES\", \"#list : {\"username:\":\"" + file.getName() +"\"}}").getBytes(charset)));
                                Thread.sleep(250);
                            }

                            this.client.getChannel().write(ByteBuffer.wrap(("{\"responseType\":\"ADMIN_GET_LIST_OF_USERNAMES\",\"ADMIN_GET_LIST_OF_USERNAMES\":{\"#listEnd\": \"#listEnd\"}}}").getBytes(charset)));
                        } else {
                            sendMessage("You are not an admin",false);
                        }
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case USER_ADD_EVENT_DAY:
                    if (this.client.isAuthenticated()) {
                        ByteBuffer message = this.message;
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode;
                        try {
                            jsonNode = mapper.readTree(message.array());
                        } catch (JsonParseException exception) {
                            this.client.disconnect("Invalid Request");
                            return;
                        }
                        JsonNode content = jsonNode.get("USER_ADD_EVENT_DAY");

                        try {
                            File file = FileUtils.findFile(this.client.getAccount().getAuthentication().getUserName() + ".json", new File(this.dataBasePath));
                            Account account = (Account) FileUtils.parseJson(FileUtils.readFile(file.getAbsolutePath()), Account.class);

                            String category = content.get("category").asText();

                            switch (category){
                                case "workFromHomeDays" :
                                    ArrayList<CalendarEvent> workFromHomeDays;
                                    if(account.getWorkFromHomeDays() == null){
                                        workFromHomeDays = new ArrayList<>();
                                    }
                                    else {
                                        workFromHomeDays = account.getWorkFromHomeDays();
                                    }
                                    workFromHomeDays.add(
                                            new CalendarEvent(LocalDate.of(content.get("year").asInt(), content.get("mouth").asInt(), content.get("day").asInt()), content.get("description").asText()));
                                    account.setWorkFromHomeDays(workFromHomeDays);

                                    FileUtils.parseToJson(file, account);
                                    break;
                                case "absentDays":
                                    ArrayList<CalendarEvent> absentDays;
                                    if(account.getWorkFromHomeDays() == null){
                                        absentDays = new ArrayList<>();
                                    }
                                    else {
                                        absentDays = account.getWorkFromHomeDays();
                                    }
                                    absentDays.add(
                                            new CalendarEvent(LocalDate.of(content.get("year").asInt(), content.get("mouth").asInt(), content.get("day").asInt()), content.get("description").asText()));
                                    account.setAbsentDays(absentDays);

                                    FileUtils.parseToJson(file, account);
                                    break;
                                default:
                                    sendMessage("Invalid category",false);
                                    return;
                            }
                            sendMessage("Success",true);
                        }
                        catch (FileNotFoundException e){
                            sendMessage("User not found",false);
                        }
                    }
                    break;
                case USER_EDIT_SELF_ACCOUNT: // TODO: test
                    if (this.client.isAuthenticated()) {
                        ByteBuffer message = this.message;
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(new String(message.array(), charset));
                        JsonNode content = jsonNode.get("USER_EDIT_SELF_ACCOUNT");

                        File file = new File(this.dataBasePath + "\\" + content.get("authentication").get("userName").asText() + ".json");

                        if(!file.exists()){
                            sendMessage("User does not exist", false);
                            return;
                        }

                        Account account = new Account()
                                .setAdmin(content.get("admin").asBoolean())
                                .setFirstName(content.get("firstName").asText())
                                .setLastName(content.get("lastName").asText())
                                .setMiddleName(jsonToArray(content, "middleName"))
                                .setAuthentication(new Account.Auth(content.get("authentication").get("userName").asText(), content.get("authentication").get("password").asText()))
                                .setDateEmployed(new CalendarEvent(LocalDate.now(), "Date Employed"));

                        try {
                            FileUtils.parseToJson(file, account);
                        } catch (IOException e) {
                            sendMessage("\"Cannot write to file \"" + file.getAbsolutePath() + " \" + e.getMessage()",false);
                            ConsoleLog.error("Cannot write to file " + file.getAbsolutePath() + " " + e.getMessage());
                            e.printStackTrace();
                        }
                        sendMessage("User modified successfully",true);
                        ConsoleLog.info(this.client.getGuestName() + " modified user " + account.getAuthentication().getUserName());
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case USER_GET_SELF_ACCOUNT: //TODO: test
                    if (this.client.isAuthenticated()) {

                        ObjectMapper mapper = new ObjectMapper();
                        File file = new File(this.dataBasePath + "\\" + this.client.getGuestName() + ".json");

                        Account account = (Account) FileUtils.parseJson(FileUtils.readFile(file.getAbsolutePath()), Account.class);

                        ObjectNode rootNode = mapper.createObjectNode();

                        rootNode.put("admin", account.isAdmin());
                        rootNode.put("firstName", account.getFirstName());
                        rootNode.put("lastName", account.getLastName());
                        ArrayNode middlename = mapper.valueToTree(Arrays.asList(account.getMiddleName()));
                        rootNode.putArray("middleName").addAll(middlename);

                        rootNode.putObject("authentication").put("username", account.getAuthentication().getUserName()).put("password" ,account.getAuthentication().getPassword());
                        this.client.getChannel().write(ByteBuffer.wrap(("\"responseType\":\"USER_GET_SELF_ACCOUNT\",\"USER_GET_SELF_ACCOUNT\":" + rootNode.asText() +"}}").getBytes()));
                    } else {
                        sendMessage("You are not authenticated",false);
                        authenticate();
                    }
                    break;
                case USER_LOG_OFF:
                    if (!this.client.isAuthenticated()) {
                        sendMessage("Already Logged Off",false);
                        return;
                    }
                    sendMessage("Logged Off",true);
                    this.client.setGuestName(this.client.getChannel().getRemoteAddress().toString());
                    this.client.setAuthenticated(false);
                    authenticate();
                    break;
                case UNKNOWN:
                    this.client.disconnect("Invalid Request");
            }

        } catch (InterruptedException | IOException exception) {
            ConsoleLog.error(exception.getMessage());
            this.client.disconnect(exception.getMessage());
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
            // findFile is redundant. TODO: remove redundant call.
            File file = FileUtils.findFile(auth.get("username").asText() + ".json", new File(this.dataBasePath));
            Account account = (Account) FileUtils.parseJson(FileUtils.readFile(file.getAbsolutePath()), Account.class);

            if (!account.getAuthentication().getPassword().equalsIgnoreCase(auth.get("password").asText())) {
                sendMessage("Invalid credentials",false);
                authenticate();
                return;
            }
            this.client.setAccount(account);
            this.client.setAuthenticated(true);
            this.client.setGuestName(account.getAuthentication().getUserName() + "(" + this.client.getChannel().getRemoteAddress() + ")");
            ConsoleLog.info("Client " + this.client.getChannel().getRemoteAddress() + " successfully authenticated as " + account.getAuthentication().getUserName() + ".");
            sendMessage("Access Granted",true);
        } catch (FileNotFoundException e) {
            sendMessage("Invalid credentials",false);
            authenticate();
        }
    }

    private String[] jsonToArray(JsonNode main, String objectName){
        ArrayList<String> arrayList = new ArrayList<>();
        final JsonNode arrNode = main.get(objectName);

        if(arrNode.isArray()){
            for(JsonNode objNode : arrNode){
                arrayList.add(objNode.asText());
            }
        }
        else arrayList.add(arrNode.asText());

        return arrayList.toArray(new String[arrayList.size()]);
    }
    private void sendMessage(String message, boolean status) throws IOException{
        this.client.getChannel().write(ByteBuffer.wrap(("{\"responseMessage\":\""  + message + "\", \"responseStatus\": " + status + " }").getBytes(charset)));
    }
}