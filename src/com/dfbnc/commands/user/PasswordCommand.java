/*
 * Copyright (c) 2006-2017 DFBnc Developers
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
 */

package com.dfbnc.commands.user;

import com.dfbnc.Account;
import com.dfbnc.DFBnc;
import com.dfbnc.commands.Command;
import com.dfbnc.commands.CommandManager;
import com.dfbnc.commands.CommandOutputBuffer;
import com.dfbnc.sockets.UserSocket;

/**
 * This file represents the 'password' command
 */
public class PasswordCommand extends Command {

    /**
     * Handle a version command.
     *
     * @param user the UserSocket that performed this command
     * @param params Params for command (param 0 is the command name)
     * @param output CommandOutputBuffer where output from this command should go.
     */
    @Override
    public void handle(final UserSocket user, final String[] params, final CommandOutputBuffer output) {
        final Account account;
        final String password;
        final String username;
        final String subclient;
        if (params.length == 2) {
            username = user.getAccount().getName();
            subclient = null;
            password = params[1];
            account = user.getAccount();
        } else if (params.length > 2) {
            if (user.isReadOnly()) {
                output.addBotMessage("Sorry, read-only sub-clients are unable to change passwords.");
                return;
            }

            final String[] bits = params[1].split("\\+");
            username = bits[0];
            if (user.getAccount().isAdmin() || username.equalsIgnoreCase(user.getAccount().getName())) {
                subclient = (bits.length > 1) ? bits[1].toLowerCase() : null;
                password = params[2];
                account = DFBnc.getAccountManager().get(username);
            } else {
                output.addBotMessage("Error: Only admins can set the password for other users.");
                return;
            }
        } else {
            if (user.getAccount().isAdmin()) {
                output.addBotMessage("%s [user[+subclient]] newpasswd", params[0]);
            } else {
                output.addBotMessage("%s [%s[+subclient]] newpasswd", params[0], user.getAccount().getName());
            }
            return;
        }
        if (account == null) {
            output.addBotMessage("Account %s doesnt exist", username);
        } else {
            account.setPassword(subclient, password);
            output.addBotMessage("Password successfully changed to: %s", password);
        }
    }

    /**
     * What does this Command handle.
     *
     * @return String[] with the names of the tokens we handle.
     */
    @Override
    public String[] handles() {
        return new String[]{"setpassword", "*password", "*passwd"};
    }

    /**
     * Create a new instance of the Command Object
     *
     * @param manager CommandManager that is in charge of this Command
     */
    public PasswordCommand(final CommandManager manager) {
        super(manager);
    }

    /**
     * Get a description of what this command does
     *
     * @param command The command to describe (incase one Command does multiple
     *                things under different names)
     * @return A description of what this command does
     */
    @Override
    public String getDescription(final String command) {
        return "This command sets a users password";
    }

    /**
     * Get detailed help for this command.
     *
     * @param params Parameters the user wants help with.
     *               params[0] will be the command name.
     * @return String[] with the lines to send to the user as the help, or null
     *         if no detailed help is available.
     */
    @Override
    public String[] getHelp(final String[] params) {
        return new String[]{
            "passwd [user] newpasswd",
            "Sets the password for yourself or the specified user (only " +
                    "admins can set other peoples passwords"
        };
    }
}
