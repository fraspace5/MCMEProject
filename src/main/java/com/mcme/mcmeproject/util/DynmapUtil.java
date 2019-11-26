/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.util;

import com.mcme.mcmeproject.Mcproject;
import com.mcme.mcmeproject.data.PluginData;
import com.mcme.mcmeproject.data.ProjectData;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.Marker;

/**
 *
 * @author Fraspace5
 */
public class DynmapUtil {

    private static boolean init = false;
    private static final boolean enabled = getDynmapConfig().getBoolean("enabled", false);

    private static DynmapAPI dynmapPlugin;
    private static MarkerAPI markerAPI;
    private static MarkerSet markerSet;
    private static int borderColor;
    private static MarkerSet markerWarpSet;
    private static int areaColor;
    private static int borderWidth;
    private static double borderOpacity;
    private static double areaOpacity;
    private static final String DEFAULT_ICON_ID = "greenflag-flag";
    private static Plugin plugin;

    private static void init() {
        if (!enabled) {
            return;
        }
        Plugin dynmap = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if (dynmap == null) {
            Logger.getGlobal().info("Dynmap not found");
        } else {
            try {
                dynmapPlugin = (DynmapAPI) dynmap;
                markerSet = dynmapPlugin.getMarkerAPI().createMarkerSet("projectregions.markerset", "ProjectRegions", dynmapPlugin.getMarkerAPI().getMarkerIcons(), false);
                markerWarpSet = dynmapPlugin.getMarkerAPI().createMarkerSet("projectregions.warpset", "Projectwarp", dynmapPlugin.getMarkerAPI().getMarkerIcons(), false);
                markerSet.setHideByDefault(getDynmapConfig().getBoolean("hide", true));
                markerWarpSet.setHideByDefault(getDynmapConfig().getBoolean("hide", true));
                borderColor = getDynmapConfig().getColor("borderColor", Color.RED).asRGB();
                areaColor = getDynmapConfig().getColor("areaColor", Color.RED).asRGB();
                borderWidth = getDynmapConfig().getInt("borderWidth", 2);
                borderOpacity = getDynmapConfig().getDouble("borderOpacity", 0.15);
                areaOpacity = getDynmapConfig().getDouble("areaOpacity", 0.25);

                markerWarpSet.addAllowedMarkerIcon(dynmapPlugin.getMarkerAPI().getMarkerIcon("construction"));
                markerWarpSet.setDefaultMarkerIcon(dynmapPlugin.getMarkerAPI().getMarkerIcon("construction"));

                Mcproject.getPluginInstance().saveConfig();
                init = true;
            } catch (Exception e) {
                Logger.getLogger(DynmapUtil.class.getName()).log(Level.WARNING, "Dynmap plugin not compatible", e);
            }
        }
    }

    private static ConfigurationSection getDynmapConfig() {
        ConfigurationSection section = Mcproject.getPluginInstance()
                .getConfig().getConfigurationSection("dynmap");
        if (section == null) {
            section = Mcproject.getPluginInstance().getConfig().createSection("dynmap");
            section.set("enabled", true);
        }
        return section;
    }

    public static void deleteWarp(String n) {

        markerWarpSet.findMarker(n).deleteMarker();

    }

    public static void clearMarkers() {
        if (!enabled) {
            return;
        }
        if (!init) {
            init();
        }
        if (init) {
            for (AreaMarker marker : markerSet.getAreaMarkers()) {
                marker.deleteMarker();
            }
        }
    }

    public static void removeMarker(String region) {
        if (!enabled) {
            return;
        }
        if (!init) {
            init();
        }
        if (init) {
            String id = region.toLowerCase() + ".marker";

            for (AreaMarker marker : markerSet.getAreaMarkers()) {
                if (marker.getMarkerID().equals(id)) {
                    marker.deleteMarker();
                }

            }
        }

    }

    public static void createMarkerWarp(String name, Location l) {

        if (!enabled) {
            return;
        }
        if (!init) {
            init();
        }
        if (init) {

            //  markerWarpSet.getDefaultMarkerIcon().setMarkerIconImage(Mcproject.getPluginInstance().getResource("greenflag.png"));
            Marker m = markerWarpSet.createMarker(name, name, true, l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), markerWarpSet.getDefaultMarkerIcon(), false);

        }
    }

    public static void createMarker(String region, String project, Region r) {
        if (!enabled) {
            return;
        }
        if (!init) {
            init();
        }
        if (init) {
            ProjectData pr = PluginData.getProjectdata().get(project);

            String newMarkerId = region.toLowerCase() + ".marker";
            for (AreaMarker marker : markerSet.getAreaMarkers()) {
                if (marker.getMarkerID().equals(newMarkerId)) {

                    marker.setCornerLocations(getXPoints(r), getZPoints(r));
                    return;

                }
            }

            AreaMarker areaMarker = markerSet.createAreaMarker(newMarkerId, region,
                    false, r.getWorld().getName(),
                    getXPoints(r),
                    getZPoints(r), false);
            areaMarker.setFillStyle(areaOpacity, areaColor);
            areaMarker.setLineStyle(borderWidth, borderOpacity, borderColor);
            areaMarker.setDescription(region);
        }
    }

    public static void createMarkeronLoad(String region, String project, com.mcmiddleearth.pluginutil.region.PrismoidRegion r) {
        if (!enabled) {
            return;
        }
        if (!init) {
            init();
        }
        if (init) {
            ProjectData pr = PluginData.getProjectdata().get(project);

            Integer[] xx = r.getXPoints();
            Integer[] zz = r.getZPoints();

            double[] x = new double[xx.length];
            double[] z = new double[zz.length];

            for (int i = 0; i < x.length; i++) {

                x[i] = xx[i];

            }

            for (int i = 0; i < z.length; i++) {

                z[i] = zz[i];

            }

            String newMarkerId = region.toLowerCase() + ".marker";
            for (AreaMarker marker : markerSet.getAreaMarkers()) {
                if (marker.getMarkerID().equals(newMarkerId)) {

                    marker.setCornerLocations(x, z);
                    return;

                }
            }

            AreaMarker areaMarker = markerSet.createAreaMarker(newMarkerId, region,
                    false, r.getWorld().getName(), x,
                    z, false);
            areaMarker.setFillStyle(areaOpacity, areaColor);
            areaMarker.setLineStyle(borderWidth, borderOpacity, borderColor);
            areaMarker.setDescription(region);
        }
    }

    public static void createMarkeronLoadCuboid(String region, String project, com.mcmiddleearth.pluginutil.region.CuboidRegion r) {
        if (!enabled) {
            return;
        }
        if (!init) {
            init();
        }
        if (init) {
            ProjectData pr = PluginData.getProjectdata().get(project);

            String newMarkerId = region.toLowerCase() + ".marker";
            for (AreaMarker marker : markerSet.getAreaMarkers()) {
                if (marker.getMarkerID().equals(newMarkerId)) {

                    marker.setCornerLocations(getX(r), getZ(r));
                    return;

                }
            }

            AreaMarker areaMarker = markerSet.createAreaMarker(newMarkerId, region,
                    false, r.getWorld().getName(), getX(r),
                    getZ(r), false);
            areaMarker.setFillStyle(areaOpacity, areaColor);
            areaMarker.setLineStyle(borderWidth, borderOpacity, borderColor);
            areaMarker.setDescription(region);
        }
    }

    private static double[] getXPoints(Region region) {

        if (region instanceof Polygonal2DRegion) {
            double[] result = new double[((Polygonal2DRegion) region).getPoints().size()];
            for (int i = 0; i < result.length; i++) {
                BlockVector2D vector = ((Polygonal2DRegion) region).getPoints().get(i).toBlockVector2D();
                result[i] = vector.getX();
            }
            return result;
        } else {
            Region weRegion = region;
            return new double[]{weRegion.getMaximumPoint().getBlockX(),
                weRegion.getMaximumPoint().getBlockX(),
                weRegion.getMinimumPoint().getBlockX(),
                weRegion.getMinimumPoint().getBlockX()};
        }
    }

    private static double[] getX(com.mcmiddleearth.pluginutil.region.CuboidRegion r) {

        return new double[]{r.getMaxCorner().getBlockX(),
            r.getMaxCorner().getBlockX(),
            r.getMinCorner().getBlockX(),
            r.getMinCorner().getBlockX()};

    }

    private static double[] getZ(com.mcmiddleearth.pluginutil.region.CuboidRegion r) {

        return new double[]{r.getMaxCorner().getBlockZ(),
            r.getMinCorner().getBlockZ(),
            r.getMinCorner().getBlockZ(),
            r.getMaxCorner().getBlockZ()};

    }

    private static double[] getZPoints(Region region) {
        if (region instanceof Polygonal2DRegion) {
            double[] result = new double[((Polygonal2DRegion) region).getPoints().size()];
            for (int i = 0; i < result.length; i++) {
                BlockVector2D vector = ((Polygonal2DRegion) region).getPoints().get(i);
                result[i] = vector.getZ();
            }
            return result;
        } else {
            Region weRegion = region;
            return new double[]{weRegion.getMaximumPoint().getBlockZ(),
                weRegion.getMinimumPoint().getBlockZ(),
                weRegion.getMinimumPoint().getBlockZ(),
                weRegion.getMaximumPoint().getBlockZ()};
        }
    }

}
