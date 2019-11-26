/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import com.mcme.mcmeproject.util.DynmapUtil;
import com.mcme.mcmeproject.util.ProjectStatus;
import com.mcmiddleearth.pluginutil.region.CuboidRegion;
import com.mcmiddleearth.pluginutil.region.PrismoidRegion;
import com.mcmiddleearth.pluginutil.region.Region;
import com.mcmiddleearth.pluginutil.region.SphericalRegion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class ProjectData {

    public String name;

    public String description;

    public Integer minutes;

    public UUID head;

    public Long updated;

    public Long time;

    public Double percentage;

    public String link;

    public HashMap<String, Region> regions;

    public HashMap<String, Location> warps;

    public HashMap<String, Double> people;

    public List<UUID> informed;

    public List<String> managers;

    public ProjectStatus status;

    public List<String> jobs;

    public boolean main;

    public List<String> news;

    public ProjectData(String namem, Player pl) {

        name = namem;
        updated = System.currentTimeMillis();
        status = ProjectStatus.FINISHED;
        description = "Nothing";
        minutes = 0;
        percentage = 0.0;
        link = "Nothing";
        managers = new ArrayList<>();
        people = new HashMap<>();
        time = System.currentTimeMillis();
        head = pl.getUniqueId();
        regions = new HashMap<>();
        warps = new HashMap<>();
        jobs = new ArrayList<>();
        informed = new ArrayList<>();
        main = false;
        news = new ArrayList<>();
        /*
         name = ((String) config.get("name"));
         description = (String) config.get("description");
         minutes = ((Double) config.getDouble("minutes"));
         head = ((UUID) config.get("head"));
         time = ((Long) config.getLong("time"));
         percentage = ((Double) config.getDouble("percentage"));
         link = ((String) config.get("link"));
         people = ((List<String>) config.getList("people"));
         managers = ((List<String>) config.getList("managers"));
         updated = ((Long) config.getLong("updated")); 
         */
    }

    public void save(File file) throws IOException {

        YamlConfiguration config = new YamlConfiguration();

        config.set("minutes", minutes);
        config.set("name", name);
        config.set("managers", managers);
        config.set("head", head.toString());
        config.set("time", time);
        config.set("percentage", percentage);
        config.set("link", link);
        config.set("updated", updated);
        config.set("status", status.name().toUpperCase());
        config.set("jobs", jobs);
        config.set("main", main);
        config.set("news", news);
        ConfigurationSection regionSection = config.createSection("regions");
        ConfigurationSection warpSection = config.createSection("warps");
        ConfigurationSection peopleSection = config.createSection("people");

        for (Map.Entry region : regions.entrySet()) {

            ConfigurationSection section = regionSection.createSection(region.getKey().toString());
            regions.get(region.getKey()).save(section);

        }
        for (Map.Entry warp : warps.entrySet()) {
            ConfigurationSection section = warpSection.createSection(warp.getKey().toString());
            Location loc = (Location) warp.getValue();
            section.set("x", loc.getX());
            section.set("y", loc.getY());
            section.set("z", loc.getZ());
            section.set("world", loc.getWorld().getName());

        }
        for (Map.Entry person : people.entrySet()) {
            ConfigurationSection section = peopleSection.createSection(person.getKey().toString());
            section.set("block", people.get(person).toString());
        }

        config.save(file);
    }

    public Location getLocation(String i) {
        return regions.get(i).getLocation();
    }

    public boolean isInside(Location loc, String i) {

        return regions.get(i).isInside(loc);

    }

    public ProjectData(File file) throws IOException, FileNotFoundException, InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();

        config.load(file);

        name = (String) config.get("name", "Nothing");
        description = (String) config.get("description", "Nothing");
        minutes = (Integer) config.get("minutes");
        head = (UUID) UUID.fromString(config.getString("head"));
        time = (Long) config.get("time", 0);
        percentage = (Double) config.get("percentage", 0.0);
        link = (String) config.get("link", "Nothing");
        updated = (Long) config.get("updated", 0);
        main = config.getBoolean("main");
        status = ProjectStatus.valueOf((String) config.get("status"));
        managers = (List<String>) config.getList("managers");
        jobs = (List<String>) config.getList("jobs");
        regions = new HashMap<String, Region>();
        people = new HashMap<String, Double>();
        warps = new HashMap<String, Location>();
        informed = new ArrayList<>();
        news = (List<String>) config.getList("news");
        ConfigurationSection regionSection = config.getConfigurationSection("regions");
        ConfigurationSection warpSection = config.getConfigurationSection("warps");
        ConfigurationSection peopleSection = config.getConfigurationSection("people");
        for (String key : regionSection.getKeys(false)) {
            ConfigurationSection section = regionSection.getConfigurationSection(key);

            if (SphericalRegion.isValidConfig(section)) {
                regions.put(key,
                        CuboidRegion.load(section));
                DynmapUtil.createMarkeronLoadCuboid(key, name, CuboidRegion.load(section));
            } else if (PrismoidRegion.isValidConfig(section)) {
                regions.put(key,
                        PrismoidRegion.load(section));
                DynmapUtil.createMarkeronLoad(key, name, PrismoidRegion.load(section));

            } else if (CuboidRegion.isValidConfig(section) || section.contains("xSize")) { // xSize is to notice old data format
                regions.put(key,
                        CuboidRegion.load(section));
                DynmapUtil.createMarkeronLoadCuboid(key, name, CuboidRegion.load(section));
            }
        }

        for (String nameee : warpSection.getKeys(false)) {
            ConfigurationSection section = warpSection.getConfigurationSection(nameee);
            Location loc = new Location(Bukkit.getWorld(section.getString("world")), section.getDouble("x"), section.getDouble("x"), section.getDouble("z"));
            String n = nameee.toUpperCase() + " (" + name.toLowerCase() + ")";
            warps.put(nameee, loc);
            DynmapUtil.createMarkerWarp(n, loc);
        }

        for (String name : peopleSection.getKeys(false)) {
            ConfigurationSection section = peopleSection.getConfigurationSection(name);
            people.put(name, parseDouble(section.getString("block")));

        }

    }

}
