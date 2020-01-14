/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcmiddleearth.pluginutil.region.Region;
import java.util.UUID;
import org.bukkit.Location;

/**
 *
 * @author Fraspace5
 */
public class WarpData {

    public final UUID idregion;

    public final UUID idproject;

    public final Location location;

    public WarpData(UUID idp, UUID idr, Location l) {

        idregion = idr;

        idproject = idp;

        location = l;

    }
}
