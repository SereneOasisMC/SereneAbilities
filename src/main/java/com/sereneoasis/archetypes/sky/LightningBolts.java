package com.sereneoasis.archetypes.sky;

import com.sereneoasis.ability.superclasses.CoreAbility;
import com.sereneoasis.ability.superclasses.MasterAbility;
import com.sereneoasis.abilityuilities.particles.SourcedBlast;
import com.sereneoasis.archetypes.DisplayBlock;
import com.sereneoasis.util.AbilityStatus;
import com.sereneoasis.util.methods.ArchetypeVisuals;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class LightningBolts extends MasterAbility {

    public static final String name = "LightningBolts";

    private int shots = 5, currentShots = 0;

    private Set<SourcedBlast> bolts = new HashSet<>();


    public LightningBolts(Player player) {
        super(player, name);

        if (shouldStart()){
            start();
            setHasSneaked();
        }
    }

    @Override
    public void progress() throws ReflectiveOperationException {
        if (currentShots == shots) {
            if (bolts.stream().allMatch(sourcedBlast -> sourcedBlast.getAbilityStatus() == AbilityStatus.COMPLETE))
            {
                this.remove();
            }
        }

        bolts.forEach(sourcedBlast -> {
            if (!DisplayBlock.LIGHTNING.getBlocks().contains(sourcedBlast.getLoc().getBlock().getType()) && !sourcedBlast.getLoc().getBlock().isPassable() && sourcedBlast.getAbilityStatus() != AbilityStatus.COMPLETE){
                SkyUtils.lightningStrikeFloorCircle(this,sourcedBlast.getLoc());
                sourcedBlast.setAbilityStatus(AbilityStatus.COMPLETE);
            }
        });
    }

    public void setHasSneaked(){
        if (currentShots < shots){
            currentShots++;
            SourcedBlast sourcedBlast = new SourcedBlast(player, name, false, new ArchetypeVisuals.LightningVisual(), false, true);
            bolts.add(sourcedBlast);
        }
    }

    public void setHasClicked(){
        bolts.stream().filter(sourcedBlast -> sourcedBlast.getAbilityStatus() == AbilityStatus.SOURCE_SELECTED)
                .forEach(SourcedBlast::setHasClicked);
    }

    @Override
    public void remove() {
        super.remove();
        bolts.forEach(CoreAbility::remove);
        sPlayer.addCooldown(name, cooldown);
    }

    @Override
    public String getName() {
        return null;
    }
}