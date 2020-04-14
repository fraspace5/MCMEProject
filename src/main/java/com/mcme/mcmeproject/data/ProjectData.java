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

import com.mcme.mcmeproject.util.ProjectStatus;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Fraspace5
 */
public class ProjectData {

    public String name;

    public UUID idproject;

    public ProjectStatus status;

    public boolean main;

    public String description;

    public List<String> jobs;

    public UUID head;

    public Long time;

    public Integer percentage;

    public String link;

    public Long updated;

    public int minutes;

    public int blocks;

    public List<UUID> assistants;

    public List<UUID> currentpl;

    public ProjectData(String namem, 
            UUID idpr, 
            ProjectStatus rn, 
            Boolean bol,
            List jj, 
            UUID he, 
            Long t, 
            Integer per,
            String desc,
            String l,
            Long up,
            int min,
            List as, List pll, int bl) {

        name = namem;

        idproject = idpr;

        status = rn;

        main = bol;

        description = desc;

        head = he;

        jobs = jj;

        time = t;

        percentage = per;

        link = l;

        updated = up;

        minutes = min;

        assistants = as;

        currentpl = pll;

        blocks = bl;

    }

}
