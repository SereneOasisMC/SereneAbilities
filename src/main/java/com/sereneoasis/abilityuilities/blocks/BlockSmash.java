package com.sereneoasis.abilityuilities.blocks;

import com.sereneoasis.ability.superclasses.CoreAbility;
import com.sereneoasis.archetypes.DisplayBlock;
import com.sereneoasis.util.AbilityStatus;
import com.sereneoasis.util.DamageHandler;
import com.sereneoasis.util.methods.Blocks;
import com.sereneoasis.util.methods.Entities;
import com.sereneoasis.util.methods.Locations;
import com.sereneoasis.util.methods.Vectors;
import com.sereneoasis.util.temp.TempDisplayBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

/**
 * @author Sakrajin
 */
public class BlockSmash extends CoreAbility {

    private final String name;

    private Location loc;

    private HashMap<Integer, TempDisplayBlock> smash;


    private boolean hasShot = false;

    private DisplayBlock displayBlock;

    public BlockSmash(Player player, String name, DisplayBlock displayBlock) {
        super(player, name);

        this.name = name;
        this.displayBlock = displayBlock;

        Block source = Blocks.getSourceBlock(player, sPlayer, sourceRange);
        abilityStatus = AbilityStatus.NO_SOURCE;
        if (source != null) {
            loc = source.getLocation();
            smash = new HashMap<>();
            smash = Entities.handleDisplayBlockEntities(smash, Locations.getOutsideSphereLocs(loc, radius, 0.5), displayBlock, 0.5);
            abilityStatus = AbilityStatus.SOURCE_SELECTED;
            start();
        }

    }

    @Override
    public void progress() {
        if (player.isSneaking() && !hasShot) {
            Location targetLoc = player.getEyeLocation().
                    add(player.getEyeLocation().getDirection().multiply(Math.max(radius + 1, loc.distance(player.getEyeLocation()))));
            if (loc.distance(targetLoc) > 1) {
                Vector dir = Vectors.getDirectionBetweenLocations(loc, targetLoc).normalize();
                loc.add(dir.clone().multiply(speed));
                smash = Entities.handleDisplayBlockEntities(smash, Locations.getOutsideSphereLocs(loc, radius, 0.5), displayBlock, 0.5);
            }

        } else if (hasShot) {
            if (loc.distance(player.getLocation()) > range) {
                abilityStatus = AbilityStatus.COMPLETE;
                return;
            }
            loc.add(player.getEyeLocation().getDirection().multiply(speed));
            smash = Entities.handleDisplayBlockEntities(smash, Locations.getOutsideSphereLocs(loc, radius, 0.5), displayBlock, 0.5);
            DamageHandler.damageEntity(Entities.getAffected(loc, radius, player), player, this, damage);
        }


        if (!hasShot && System.currentTimeMillis() > startTime + duration) {
            abilityStatus = AbilityStatus.COMPLETE;
        }

    }

    @Override
    public void remove() {
        super.remove();
        for (TempDisplayBlock tb : smash.values()) {
            tb.revert();
        }
    }

    public void setHasClicked() {
        if (!hasShot) {
            hasShot = true;
        }
    }


    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return name;
    }
}