/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.region.Region;
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

    public Boolean main;

    public String description;

    public List<String> jobs;

    public UUID head;

    public Long time;

    public Integer percentage;

    public String link;

    public Long updated;

    public ProjectData(String namem, UUID idpr, ProjectStatus rn, Boolean bol, List jj, UUID he, Long t, Integer per, String desc, String l, Long up) {

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

    }

}
