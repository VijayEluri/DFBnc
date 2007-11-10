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
 *
 * SVN: $Id$
 */
package uk.org.dataforce.dfbnc.servers.irc;

import uk.org.dataforce.dfbnc.commands.Command;
import uk.org.dataforce.dfbnc.commands.CommandManager;
import uk.org.dataforce.dfbnc.UserSocket;
import uk.org.dataforce.dfbnc.DFBnc;
import uk.org.dataforce.dfbnc.UnableToConnectException;

import java.util.ArrayList;
import java.util.List;

/**
 * This file represents the 'Connect' command
 */
public class ConnectCommand extends Command {
	/**
	 * Handle an Connect command.
	 *
	 * @param user the UserSocket that performed this command
	 * @param params Params for command (param 0 is the command name)
	 */
	public void handle(final UserSocket user, final String[] params) {
		user.sendBotMessage("----------------");
		if (user.getAccount().getConnectionHandler() == null) {
			user.sendBotMessage("Connecting...");
			try {
				IRCConnectionHandler handler = new IRCConnectionHandler(user, -1);
				user.getAccount().setConnectionHandler(handler);
			} catch (UnableToConnectException utce) {
				user.sendBotMessage("There was an error connecting: "+utce.getMessage());
			}
		} else {
			user.sendBotMessage("Already connected.");
		}
	}
	
	/**
	 * What does this Command handle.
	 *
	 * @return String[] with the names of the tokens we handle.
	 */
	public String[] handles() {
		return new String[]{"connect"};
	}
	
	/**
	 * Create a new instance of the Command Object
	 *
	 * @param manager CommandManager that is in charge of this Command
	 */
	public ConnectCommand (final CommandManager manager) { super(manager);	}
	
	/**
	 * Get a description of what this command does
	 *
	 * @return A description of what this command does
	 */
	public String getDescription() {
		return "This command lets you connect to an IRC Server if not already connected";
	}
	
	/**
	 * Get SVN information.
	 *
	 * @return SVN Info String
	 */
	public static String getSvnInfo () { return "$Id: Process001.java 1508 2007-06-11 20:08:12Z ShaneMcC $"; }	
}