package com.sereneoasis.util.methods;

import com.sereneoasis.SerenityPlayer;
import com.sereneoasis.archetypes.data.ArchetypeDataManager;
import com.sereneoasis.util.temp.TempDisplayBlock;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sakrajin
 * Methods which are related to blocks
 */
public class Blocks {

    public static void selectSourceAnimation(Block block, Color color) {
        if (block.getType() == Material.WATER) {
            new TempDisplayBlock(block.getLocation(), Material.BLUE_STAINED_GLASS, 1000, 1.0, true, color);
        } else {
            new TempDisplayBlock(block.getLocation(), block.getType(), 1000, 1.0, true, color);
        }
    }

    public static TempDisplayBlock selectSourceAnimationManual(Block block, Color color) {
        if (block.getType() == Material.WATER) {
            return new TempDisplayBlock(block.getLocation(), Material.BLUE_STAINED_GLASS, 60000, 1.0, true, color);
        } else {
            return new TempDisplayBlock(block.getLocation(), block.getType(), 60000, 1.0, true, color);
        }
    }

    public static Block getBelowBlock(Block b, double distance) {
        Location loc = b.getLocation();
        Block block = b;
        if (loc.getWorld().rayTraceBlocks(loc, new Vector(0, -1, 0), distance, FluidCollisionMode.NEVER) != null) {
            block = loc.getWorld().rayTraceBlocks(loc, new Vector(0, -1, 0), distance, FluidCollisionMode.NEVER).getHitBlock();
        }
        return block;
    }

    public static boolean isBelowWater(Block b, double distance) {
        Location loc = b.getLocation();
        if (loc.getWorld().rayTraceBlocks(loc, new Vector(0, -1, 0), distance, FluidCollisionMode.ALWAYS) != null) {
            return true;
        }
        return false;
    }

    public static Block getFacingBlock(Player player, double distance) {
        Location loc = player.getEyeLocation().clone();
        Block block = null;
        if (loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), distance, FluidCollisionMode.NEVER) != null) {
            block = loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), distance, FluidCollisionMode.NEVER).getHitBlock();
        }
        return block;
    }

    public static Location getFacingBlockLoc(Player player, double distance) {
        Location loc = player.getEyeLocation().clone();
        RayTraceResult rayTraceResult = loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), distance, FluidCollisionMode.NEVER);
        if (rayTraceResult != null) {
            return new Location(loc.getWorld(), rayTraceResult.getHitPosition().getX(), rayTraceResult.getHitPosition().getY(), rayTraceResult.getHitPosition().getZ());
        }
        return null;
    }

    public static boolean playerLookingAtBlockDisplay(Player player, BlockDisplay target, double maxDistance, double size) {
        Location lowest = target.getLocation();
        Location highest = lowest.clone().add(size, size, size);
        BoundingBox boundingBox = new BoundingBox(lowest.getX(), lowest.getY(), lowest.getZ(), highest.getX(), highest.getY(), highest.getZ());
        Location loc = player.getEyeLocation().clone();
        Vector dir = player.getEyeLocation().getDirection().clone().normalize();
        RayTraceResult rayTraceResult = boundingBox.rayTrace(loc.toVector(), dir, maxDistance);
        if (rayTraceResult != null) {
            return true;
        }
        return false;
    }


    public static Block getFacingBlockOrLiquid(Player player, double distance) {
        Location loc = player.getEyeLocation();
        Block block = null;
        if (loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), distance, FluidCollisionMode.ALWAYS) != null) {
            block = loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), distance, FluidCollisionMode.ALWAYS).getHitBlock();
        }
        return block;
    }

    public static Set<Block> getBlocksAroundPoint(Location loc, double radius) {
        Set<Block> blocks = new HashSet<>();
        radius -= radius / 2;
        for (double y = -radius; y < radius; y++) {
            for (double x = -radius; x < radius; x++) {
                for (double z = -radius; z < radius; z++) {
                    Block block = loc.clone().add(x, y, z).getBlock();
                    if (block.getLocation().distanceSquared(loc) < ((radius) * (radius))) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public static List<Block> getBlocksAroundPoint(Location loc, double radius, Material type) {
        List<Block> blocks = new ArrayList<Block>();
        radius -= radius / 2;
        for (double y = -radius; y < radius; y++) {
            for (double x = -radius; x < radius; x++) {
                for (double z = -radius; z < radius; z++) {
                    Block block = loc.clone().add(x, y, z).getBlock();
                    if (block.getType() == type && block.getLocation().distanceSquared(loc) < ((radius) * (radius))) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public static Set<Material> getArchetypeBlocks(SerenityPlayer serenityPlayer) {
        return ArchetypeDataManager.getArchetypeData(serenityPlayer.getArchetype()).getBlocks();
    }

    public static Block getSourceBlock(Player player, SerenityPlayer sPlayer, double sourceRange) {
        Block source = Blocks.getFacingBlockOrLiquid(player, sourceRange);
        if (source != null && Blocks.getArchetypeBlocks(sPlayer).contains(source.getType())) {
            return source;
        }
        return null;
    }

    public static boolean isTopBlock(Block b) {
        if (b.getLocation().add(0, 1, 0).getBlock().getType().isSolid()) {
            return false;
        }
        return true;
    }

}
