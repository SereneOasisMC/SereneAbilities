package com.sereneoasis.abilityuilities.items;

import com.sereneoasis.ability.superclasses.CoreAbility;
import com.sereneoasis.util.AbilityStatus;
import com.sereneoasis.util.methods.Display;
import com.sereneoasis.util.methods.Entities;
import com.sereneoasis.util.methods.Locations;
import com.sereneoasis.util.methods.Vectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ThrowItemDisplay extends CoreAbility {

    private final String name;

    private ItemDisplay itemDisplay;

    private ArmorStand armorStand;


    private double oldPitch, size;

    private boolean mightHaveStopped = false, stick;

    public ThrowItemDisplay(Player player, String name, Location loc, Vector dir, Material material, double size, boolean stick, boolean diagonal) {
        super(player, name);

        this.name = name;
        this.stick = stick;
        this.size = size;

        abilityStatus = AbilityStatus.SHOT;



        Vector offsetFix = new Vector(size/2, 0, size/2).rotateAroundY(-Math.toRadians(loc.getYaw()));
        Location offsetLocation = loc.clone().add(offsetFix);
        itemDisplay = Display.createItemDisplay(offsetLocation, material, size, diagonal);
        armorStand = Display.createArmorStand(offsetLocation);
        armorStand.addPassenger(itemDisplay);
        armorStand.setVelocity(dir.clone().multiply(speed));

        Location tempLoc = armorStand.getLocation().clone().setDirection(armorStand.getVelocity().clone());
        oldPitch = tempLoc.getPitch();

        start();
    }

    @Override
    public void progress() {

        if (abilityStatus != AbilityStatus.COMPLETE) {
            if (armorStand.getVelocity().length() < 0.01) {
                if (mightHaveStopped) {
                    this.abilityStatus = AbilityStatus.COMPLETE;
                }
                mightHaveStopped = true;
            } else {

                Transformation transformation = itemDisplay.getTransformation();
                Quaternionf quaternionf = transformation.getLeftRotation();
                Location tempLoc = armorStand.getLocation().clone().setDirection(armorStand.getVelocity().clone());
                quaternionf.rotateZ((float) -Math.toRadians(tempLoc.getPitch() - oldPitch));


                oldPitch = tempLoc.getPitch();
                transformation.getLeftRotation().set(quaternionf);

                itemDisplay.setTransformation(transformation);


                if (stick) {
                    Block b = getLoc().getBlock();
                    if (b.getType().isSolid()) {
                        armorStand.setVelocity(new Vector(0, 0, 0));
                        armorStand.setGravity(false);

                        abilityStatus = AbilityStatus.COMPLETE;
                    }
                }
            }
        }
    }


    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public Location getLoc()
    {
        Vector offsetFix = new Vector(size/2, 0, size/2).rotateAroundY(-Math.toRadians(armorStand.getLocation().getYaw()));
        return armorStand.getLocation().clone().subtract(offsetFix);
    }

    @Override
    public void remove() {
        super.remove();

        if (itemDisplay != null) {
            itemDisplay.remove();
        }
        if (armorStand != null) {
            armorStand.remove();
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