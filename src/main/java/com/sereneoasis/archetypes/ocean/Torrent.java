package com.sereneoasis.archetypes.ocean;


import com.sereneoasis.ability.superclasses.CoreAbility;

import com.sereneoasis.abilityuilities.blocks.BlockRingAroundPlayer;
import com.sereneoasis.abilityuilities.blocks.ShootBlockFromPlayer;
import com.sereneoasis.abilityuilities.blocks.SourceBlockToPlayer;
import com.sereneoasis.util.SourceStatus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Torrent extends CoreAbility {

    private SourceBlockToPlayer sourceBlockToPlayer;

    private BlockRingAroundPlayer blockRingAroundPlayer;

    private ShootBlockFromPlayer shootBlockFromPlayer;

    private boolean hasSourced = false;

    private boolean hasShot = false;
    private Location loc;

    public Torrent(Player player) {
        super(player);


        sourceBlockToPlayer = new SourceBlockToPlayer(player, "Torrent", Material.WATER, 4);
        if (! (sourceBlockToPlayer.getSourceStatus() == SourceStatus.NO_SOURCE))
        {
            start();
        }
    }

    @Override
    public void progress() {
        if (!player.isSneaking())
        {
            this.remove();
        }

        if (sourceBlockToPlayer.getSourceStatus() == SourceStatus.SOURCED)
        {
            hasSourced = true;
            loc = sourceBlockToPlayer.getLocation();
            sourceBlockToPlayer.remove();
        }

        if (hasSourced) {
            if (hasShot) {


                if (shootBlockFromPlayer == null)
                {
                    this.remove();
                }

            }
            else{
                if (blockRingAroundPlayer == null)
                {
                    blockRingAroundPlayer = new BlockRingAroundPlayer(player, "Torrent", loc, Material.WATER, 3, 0);
                }
            }
        }


    }

    public void setHasClicked()
    {
        if (hasSourced)
        {
            hasShot = true;
            shootBlockFromPlayer = new ShootBlockFromPlayer(player, "Torrent", blockRingAroundPlayer.getLocation(), Material.WATER, true);
            blockRingAroundPlayer.remove();
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (hasSourced) {
            blockRingAroundPlayer.remove();
        }
        else {
            sourceBlockToPlayer.remove();
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return "Torrent";
    }
}
