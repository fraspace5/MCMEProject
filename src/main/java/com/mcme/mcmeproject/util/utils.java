/*
 * Copyright (C) 2020 MCME (Fraspace5)
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
package com.mcme.mcmeproject.util;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class utils {

    public static List<Integer> StringtoListInt(String[] s) {

        List<Integer> list = new ArrayList();

        for (String item : s) {
            list.add(Integer.parseInt(item));
        }
        return list;
    }

    public static List<String> convertListString(String[] s) {

        List<String> list = new ArrayList();

        list.addAll(Arrays.asList(s));

        return list;
    }

    public static List<UUID> convertListUUID(String[] s) {

        List<UUID> list = new ArrayList();

        for (String item : s) {
            list.add(UUID.fromString(item));
        }

        return list;
    }

    public static Integer getInt(ResultSet r, UUID projectid, UUID playerid) throws SQLException {
        if (r.first()) {
            do {

                if (r.getString("idproject").equals(projectid.toString()) && r.getString("player_uuid").equals(playerid.toString())) {

                    return r.getInt("blocks");

                } else {

                    return 0;
                }

            } while (r.next());
        } else {

            return 0;
        }

    }

    public static UUID createId() {

        return UUID.randomUUID();

    }

    public static String serialize(UUID uuid, Boolean bool) {
        return uuid + ";" + bool;
    }

    public static String[] unserialize(String line) {

        return line.split(";");

    }

    public static boolean playerPermission(final String prr, CommandSender cs) {
        boolean manager = false;
        boolean head = false;

        final Player pl = (Player) cs;

        if (PluginData.getProjectsAll().get(prr).getAssistants().contains(pl.getUniqueId())) {
            manager = true;

        }
        if (PluginData.getProjectsAll().get(prr).getHead().equals(pl.getUniqueId())) {
            head = true;

        }
        Mcproject.getPluginInstance().getClogger().sendMessage(manager + " " + head + " " + pl.hasPermission("project.owner"));
        if (manager || head || pl.hasPermission("project.owner")) {
            return true;
        } else {
            sendNoPermission(cs);
            return false;
        }

    }

    private static void sendNoPermission(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't manage this project");
    }
}
