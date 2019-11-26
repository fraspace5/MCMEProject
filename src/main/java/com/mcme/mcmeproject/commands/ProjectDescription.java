/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

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

    @Override
    protected void execute(CommandSender cs, String... args) {

        if (cs instanceof Player) {
            if (PluginData.getProjectdata().containsKey(args[0])) {
                if (playerPermission(args[0], cs)) {
                    conversationFactory.buildConversation((Conversable) cs).begin();
                }

            } else {
                sendNoProject(cs);
            }

        }

    }

    private class descriptionPrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Please give a description. for the project";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            context.setSessionData("description", input);

            return new namePrompt();
        }

    }

    private class namePrompt extends StringPrompt {

        @Override
        public String getPromptText(ConversationContext context) {
            return ChatColor.YELLOW + "Please tell us the name of the project";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {

            if (PluginData.getProjectdata().containsKey(input)) {
                context.setSessionData("name", input);
                return new finishedPrompt();
            } else {
                return new namePrompt();
            }

        }

    }

    private class finishedPrompt extends MessagePrompt {

        @Override
        protected Prompt getNextPrompt(ConversationContext context) {
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public String getPromptText(ConversationContext context) {

            String description = (String) context.getSessionData("description");
            String name = (String) context.getSessionData("name");
            PluginData.getProjectdata().get(name).description = description;
            return ChatColor.YELLOW + "Description updated!";
        }

    }

    public boolean playerPermission(String prr, CommandSender cs) {
        ProjectData pr = PluginData.getProjectdata().get(prr);
        Player pl = (Player) cs;
        if (pr.head.equals(pl.getUniqueId()) || pr.managers.contains(pl.getName()) || pl.hasPermission("project.owner")) {
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
