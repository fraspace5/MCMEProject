/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.commands;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class CurrentProject  {
/*
    public CurrentProject(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Change your current project");
        setUsageDescription(" <ProjectName>: Set the name of your current project. If you do that you don't have to repeat for each command the name of the project ");
    }

    private boolean manager;

    private boolean head;

    @Override
    protected void execute(final CommandSender cs, final String... args) {

        head = false;
        manager = false;
        List<UUID> projects = new ArrayList();
        if (cs instanceof Player) {
            final Player pl = (Player) cs;
            if (PluginData.getProjectsAll().containsKey(args[0])) {
                try {

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            try {
                                ProjectData proj = PluginData.getProjectsAll().get(args[0]);
                                String statement = "SELECT * FROM " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data ;";
                                final ResultSet r = Mcproject.getPluginInstance().getConnection().prepareStatement(statement).executeQuery();
                                projects.clear();
                                if (r.first()) {
                                    do {

                                        String p = r.getString("plcurrent");
                                        List<UUID> players = PluginData.convertListUUID(PluginData.unserialize(p));
                                        if (players.contains(pl.getUniqueId())) {
                                            projects.add(UUID.fromString(r.getString("idproject")));
                                        }

                                    } while (r.next());

                                }

                                if (projects.contains(proj.idproject)) {
                                    sendSameProject(cs);

                                } else {

                                    final List<UUID> assist = getList(r, proj.idproject);

                                    assist.add(pl.getUniqueId());
                                    String s = serialize(assist);
                                    String stat = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET plcurrent = '" + s + "' WHERE idproject = '" + PluginData.getProjectsAll().get(args[0]).getIdproject().toString() + "' ;";
                                    Mcproject.getPluginInstance().getConnection().prepareStatement(stat).executeUpdate();
                                    final List<UUID> assist2 = getList(r, projects.get(0));

                                    assist2.remove(pl.getUniqueId());
                                    String s2 = serialize(assist2);
                                    String stat2 = "UPDATE " + Mcproject.getPluginInstance().database + ".mcmeproject_project_data SET plcurrent = '" + s2 + "' WHERE idproject = '" + projects.get(0).toString() + "' ;";
                                    Mcproject.getPluginInstance().getConnection().prepareStatement(stat2).executeUpdate();
                                    PluginData.loadProjects();
                                    Mcproject.getPluginInstance().sendReload(pl, "projects");
                                    sendDone(cs, proj.getName());
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(ProjectAdd.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }.runTaskAsynchronously(Mcproject.getPluginInstance());

                } catch (NullPointerException e) {

                }

            } else {

                sendNoProject(cs);

            }

        }

    }

    private String serialize(List<UUID> intlist) {

        StringBuilder builder = new StringBuilder();
        if (!intlist.isEmpty()) {
            for (UUID s : intlist) {

                builder.append(s.toString()).append(";");

            }
        }
        return builder.toString();

    }

    private List<UUID> getList(ResultSet r, UUID uuid) throws SQLException {
        List<UUID> list = new ArrayList();
        if (r.first()) {
            do {
                if (UUID.fromString(r.getString("idproject")).equals(uuid)) {
                    list = PluginData.convertListUUID(PluginData.unserialize(r.getString("current.players")));
                }

            } while (r.next());
        }

        return list;

    }

    private void sendNoProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This Project doesn't exists");
    }

    private void sendSameProject(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This is your current project");
    }

    private void sendDone(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, name + " is now set as your current project!");
    }
    
    */
    
}
