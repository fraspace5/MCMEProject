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
package com.mcme.mcmeproject.data;

import java.util.UUID;
import com.mcmiddleearth.pluginutil.region.Region;
import org.bukkit.Location;

/**
 *
 * @author Fraspace5
 */
public class RegionData {

    public final String name;

    public final UUID idr;
    
    public final UUID idproject;

    public final Region region;

    public final String server;

    public final String type;
    
    public final Integer weight;

    public RegionData(String namem, UUID idregion, UUID idpr, Region rn, String sr, String t,Integer wei) {

        name = namem;

        idr = idregion;

        idproject = idpr;

        region = rn;

        server = sr;

        type = t;
        
        weight = wei;

    }

    public Location getLocation() {
        return region.getLocation();
    }

    public boolean isInside(Location loc) {

        return region.isInside(loc);

    }

}
