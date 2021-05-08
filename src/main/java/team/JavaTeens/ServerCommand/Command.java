package team.JavaTeens.ServerCommand;

import java.util.Calendar;

public abstract class Command {

    public String name = null;

    public String help = "no help available";

    public String arguments = null;

    protected abstract void execute();

}
