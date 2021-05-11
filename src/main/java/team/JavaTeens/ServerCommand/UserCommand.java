package team.JavaTeens.ServerCommand;

import team.JavaTeens.Account.Account;
import team.JavaTeens.Account.CalendarEvent;
import team.JavaTeens.Utils.ConsoleLog;
import team.JavaTeens.Utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class UserCommand extends Command {

    private File dataBase;

    public UserCommand(File dataBase) {
        this.dataBase = dataBase;
        this.name = "user";
        this.help = "user {<add> <username> <password> <admin?(true/false)> <firstName> <lastName> <middleName?(== null? null : middleName)>},{<delete> <username>}";
    }

    @Override
    protected void execute() {
        String[] args = this.arguments.split(" ");
        File file = new File(dataBase + "\\" + args[2] + ".json");

        switch (args[1]) { //TODO: add user info
            case "add":

                if (file.exists()) {
                    ConsoleLog.warn("User already exists.");
                    return;
                }
                Account account = new Account() // example: user add AndreiP1234 password1234 true Ciprian Popescu null
                        .setAuthentication(new Account.Auth(args[2], args[3]))
                        .setFirstName(args[5])
                        .setLastName(args[6])
                        .setDateEmployed(new CalendarEvent(LocalDate.now(), "Date Employed"));

                account.setAdmin(args[4].equalsIgnoreCase("true"));

                if (args[7] == null) {
                    account.setMiddleName(null);
                } else account.setMiddleName(args[7].split(" "));

                try {
                    FileUtils.parseToJson(file, account);
                } catch (IOException e) {
                    ConsoleLog.error("Cannot write to file " + file.getAbsolutePath() + " " + e.getMessage());
                    e.printStackTrace();
                }
                ConsoleLog.info("Successfully create new user with the following characteristics: \n" +
                        "Username: " + account.getAuthentication().getUserName() + "\n" +
                        "Password: " + account.getAuthentication().getPassword() + "\n" +
                        "Admin Status: " + account.isAdmin() + "\n" +
                        "First name: " + account.getFirstName() + "\n" +
                        "Last name: " + account.getLastName() + "\n" +
                        "Middle name: " + Arrays.toString(account.getMiddleName()));
                break;
            case "delete":
                if(!file.exists()){
                    ConsoleLog.warn("The user does not exist");
                    return;
                }
                ConsoleLog.info(file.delete()? "File successfully deleted." : "Cannot delete file: " + file.getAbsolutePath());
                break;

            default:
                ConsoleLog.info("Invalid argument. Select add or delete");
        }
    }
}
