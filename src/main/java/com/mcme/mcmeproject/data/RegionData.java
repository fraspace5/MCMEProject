/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import java.util.UUID;
import com.mcmiddleearth.pluginutil.region.Region;
import org.bukkit.Location;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;


/**
 *
 * @author Fraspace5
 */
public class RegionData {


    public final String name;

    public final UUID idr;

    public final UUID idproject;

    public final Region region;

    public RegionData(String namem, UUID idregion, UUID idpr, Region rn) {

        name = namem;
        
        idr = idregion;
        
        idproject = idpr;
        
        region = rn;

    }
   
    
    
    
     public Location getLocation() {
        return region.getLocation();
    }

    public boolean isInside(Location loc) {

        return region.isInside(loc);

    }

}
