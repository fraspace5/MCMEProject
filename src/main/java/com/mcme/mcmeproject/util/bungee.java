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
package com.mcme.mcmeproject.util;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Fraspace5
 */
public class bungee implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("reload")) {
            try {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                String somedata = msgin.readUTF();

                switch (somedata) {

                    case "all":
                        PluginData.loadProjects();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                PluginData.loadRegions();
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        PluginData.loadWarps();
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {
                                                PluginData.loadAllDynmap();

                                            }

                                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                                    }

                                }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);

                        break;
                    case "map":
                        PluginData.loadAllDynmap();
                        break;
                    case "regions":
                        PluginData.loadRegions();
                        break;
                    case "warps":
                        PluginData.loadWarps();
                        break;
                    case "projects":
                        PluginData.loadProjects();
                        break;
                    default:
                        PluginData.loadProjects();
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                PluginData.loadRegions();
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        PluginData.loadWarps();
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {
                                                PluginData.loadAllDynmap();

                                            }

                                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                                    }

                                }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                            }

                        }.runTaskLater(Mcproject.getPluginInstance(), 20L);
                        break;

                }
            } catch (IOException ex) {

            }

        } else if (subchannel.equals("GetServer")) {
            String servern = in.readUTF();
            Mcproject.getPluginInstance().setNameserver(servern);
        }

    }

    public static void sendReload(Player player, String s) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward"); // So BungeeCord knows to forward it
        out.writeUTF("ALL");
        out.writeUTF("reload"); // The channel name to check if this your data

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        switch (s) {

            case "all":
                try {
                msgout.writeUTF("all"); // You can do anything you want with msgout

            } catch (IOException exception) {
            }
            break;
            case "map":
                try {
                msgout.writeUTF("map");

            } catch (IOException exception) {
            }
            break;
            case "regions":
                try {
                msgout.writeUTF("regions");

            } catch (IOException exception) {
            }
            break;
            case "warps":
                try {
                msgout.writeUTF("warps");

            } catch (IOException exception) {
            }
            break;
            case "projects":
                try {
                msgout.writeUTF("projects"); // You can do anything you want with msgout

            } catch (IOException exception) {
            }
            break;
            default:
                 try {
                msgout.writeUTF("all"); // You can do anything you want with msgout

            } catch (IOException exception) {
            }
            break;

        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        player.sendPluginMessage(Mcproject.getPluginInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendNameServer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("GetServer");

        player.sendPluginMessage(Mcproject.getPluginInstance(), "BungeeCord", out.toByteArray());
    }
}
