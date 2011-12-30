/*
 * Copyright (c) 2006-2007 Shane Mc Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.org.dataforce.dfbnc.commands.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import uk.org.dataforce.dfbnc.commands.Command;
import uk.org.dataforce.dfbnc.commands.CommandManager;
import uk.org.dataforce.dfbnc.sockets.UserSocket;
import uk.org.dataforce.dfbnc.DFBnc;
import uk.org.dataforce.dfbnc.servers.ServerType;
import uk.org.dataforce.dfbnc.servers.ServerTypeNotFound;

/**
 * This file represents the 'ServerType' command
 */
public class ServerTypeCommand extends Command {
    /**
     * Handle a ServerType command.
     *
     * @param user the UserSocket that performed this command
     * @param params Params for command (param 0 is the command name)
     */
    @Override
    public void handle(final UserSocket user, final String[] params) {
        String[] actualParams = params;

        actualParams[1] = getFullParam(user, actualParams, 1, Arrays.asList("settype", "help"));
        if (actualParams[1] == null) { return; }

        if (actualParams.length > 1 && actualParams[1].equalsIgnoreCase("settype")) {
            user.sendBotMessage("----------------");
            if (actualParams.length > 2) {
                final Collection<String> availableTypes = DFBnc.getServerTypeManager().getServerTypeNames();
                availableTypes.add("none");
                actualParams[2] = getFullParam(user, actualParams, 2, availableTypes);
                if (actualParams[2] == null) { return; }

                final ServerType currentType = user.getAccount().getServerType();
                if (actualParams[2].equalsIgnoreCase("none")) {
                    user.getAccount().getConfig().setOption("server", "servertype", "");
                    user.sendBotMessage("You now have no servertype.");
                    if (currentType != null) { currentType.deactivate(user.getAccount()); }
                } else {
                    try {
                        ServerType serverType = DFBnc.getServerTypeManager().getServerType(actualParams[2]);
                        if (currentType != null) { currentType.deactivate(user.getAccount()); }
                        serverType.activate(user.getAccount());
                        user.getAccount().getConfig().setOption("server", "servertype", actualParams[2].toLowerCase());
                        user.sendBotMessage("Your ServerType is now "+actualParams[2].toLowerCase()+".");
                    } catch (ServerTypeNotFound e) {
                        user.sendBotMessage("Sorry, "+e);
                    }
                }
            } else {
                user.sendBotMessage("Available Types:");
                for (String name : DFBnc.getServerTypeManager().getServerTypeNames()) {
                    try {
                        final ServerType type = DFBnc.getServerTypeManager().getServerType(name);
                        user.sendBotMessage("    " + name + " - " + type.getDescription());
                    } catch (ServerTypeNotFound ex) { /* It will be found. */ }
                }
            }
        } else if (actualParams.length > 1 && actualParams[1].equalsIgnoreCase("help")) {
            user.sendBotMessage("----------------");
            user.sendBotMessage("This command allows you to set the servertype for this account.");
            final String currentType = user.getAccount().getConfig().getOption("server", "servertype", "");
            if (currentType.equals("")) {
                user.sendBotMessage("You currently do not have a servertype selected.");
            } else {
                String info = "";
                final ServerType st = user.getAccount().getServerType();
                if (st == null) {
                    info = " (Currently not available)";
                }
                user.sendBotMessage("Your current servertype is: "+currentType+info);
            }
            user.sendBotMessage("");
            user.sendBotMessage("You can set your type using the command: /dfbnc "+actualParams[0]+" settype <type>");
            user.sendBotMessage("A list of available types can be seen by ommiting the <type> param");
        } else {
            user.sendBotMessage("For usage information use /dfbnc "+actualParams[0]+" help");
        }
    }

    /**
     * What does this Command handle.
     *
     * @return String[] with the names of the tokens we handle.
     */
    @Override
    public String[] handles() {
        return new String[]{"servertype", "*st"};
    }

    /**
     * Create a new instance of the Command Object
     *
     * @param manager CommandManager that is in charge of this Command
     */
    public ServerTypeCommand (final CommandManager manager) { super(manager); }

    /**
     * Get a description of what this command does
     *
     * @param command The command to describe (incase one Command does multiple
     *                things under different names)
     * @return A description of what this command does
     */
    @Override
    public String getDescription(final String command) {
        return "This command changes your ServerType";
    }
}