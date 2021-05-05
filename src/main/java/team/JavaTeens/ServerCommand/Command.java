package team.JavaTeens.ServerCommand;

import java.util.Calendar;

public abstract class Command {

    protected String name = null;

    protected String help = "no help available";

    protected String arguments = null;

    protected String[] aliases = new String[0];

    protected abstract void execute();

}
