/*
 *Copyright (C) 2020 MCME (Fraspace5)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class ProjectDescription extends ProjectCommand {

    private final ConversationFactory conversationFactory;

    public ProjectDescription(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Add a description to a project");
        setUsageDescription(" <ProjectName> : Add a description to a project (!exit command)");
        conversationFactory = new ConversationFactory(Mcproject.getPluginInstance())
                .withModality(true)
                .withFirstPrompt(new descriptionPrompt())
                .withEscapeSequence("!exit")
                .withTimeout(600)
                .thatExcludesNonPlayersWithMessage("You must be a player to send this command");

    }

    private String name;

    private boolean manager;

    private boolean head;

    private Player pl;

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {
            manager = false;
            head = false;
            if (PluginData.projectsAll.containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    Player pp = (Player) cs;
                    name = args[0];
                    conversationFactory.buildConversation((Conversable) cs).begin();
                    pl = pp;
                }

            } else {
                sendNoProject(cs);
            }

        }

    }

    private class descriptionPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Please give a description for the project";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("description", input);

            return new finishedPrompt();
        }

    }

    private class finishedPrompt extends MessagePrompt {

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext context) {

            final String description = (String) context.getSessionData("description");

            new BukkitRunnable() {

                @Override
                public void run() {

                    try {
                        String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET description = '" + description + "' WHERE idproject = '" + PluginData.projectsAll.get(name).idproject.toString() + "' ;";
                        Mcproject.getPluginInstance().con.prepareStatement(stat).executeUpdate(stat);
                        PluginData.loadProjects();
                        Mcproject.getPluginInstance().sendReload(pl, "projects");
                    } catch (SQLException ex) {
                        Logger.getLogger(ProjectDescription.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }.runTaskAsynchronously(Mcproject.getPluginInstance());

            return ChatColor.YELLOW + "Description updated!";
        }

    }

    public boolean playerPermission(final String prr, CommandSender cs) {
        final Player pl = (Player) cs;

        if (PluginData.projectsAll.get(prr).assistants.contains(pl.getUniqueId())) {
            manager = true;

        }
        if (PluginData.projectsAll.get(prr).head.equals(pl.getUniqueId())) {
            head = true;

        }

        if (manager || head || pl.hasPermission("project.owner")) {
            return true;
        } else {
            sendNoPermission(cs);
            return false;
        }

    }

    private void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project does not exists");
    }

    private void sendDone(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Description updated!");
    }
}
