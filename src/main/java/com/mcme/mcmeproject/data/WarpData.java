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
