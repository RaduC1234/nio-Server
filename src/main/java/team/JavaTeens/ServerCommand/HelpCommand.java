package team.JavaTeens.ServerCommand;

import team.JavaTeens.Utils.ConsoleLog;

public class HelpCommand extends Command {

    public HelpCommand(){
        this.name = "help";
    }

    @Override
    protected void execute() {
        ConsoleLog.info("help me");
    }
}
