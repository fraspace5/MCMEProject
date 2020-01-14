/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.region.Region;
import java.util.UUID;

/**
 *
 * @author Fraspace5
 */
public class ProjectGotData {

    public String name;

    public UUID idproject;

    public ProjectStatus status;
    
    public Boolean main;
    
    public String description;

    public ProjectGotData(String namem, UUID idpr, ProjectStatus rn, Boolean bol) {

        name = namem;

        idproject = idpr;

        status = rn;
        
        main = bol;
        
        description = "nothing";

    }

}
