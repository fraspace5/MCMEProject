/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcmiddleearth.pluginutil.region.Region;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Fraspace5
 */
public class WarpData {

    public final UUID idregion;

    public final UUID idproject;

    public final Location location;

    public final World wl;
    
    public final String server;

    public WarpData(UUID idp, UUID idr, Location l, String nameserver) {

        idregion = idr;

        idproject = idp;

        location = l;

        wl = l.getWorld();

        server = nameserver;
    }
}
