package team.JavaTeens.ServerCommand;

import team.JavaTeens.Utils.ConsoleLog;

public class HelpCommand extends Command {

    public HelpCommand(){
        this.name = "help";
    }

    @Override
    protected void execute() {
        //TODO: make a help command
        ConsoleLog.info("help me");
    }
}
