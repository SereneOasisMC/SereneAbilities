package com.sereneoasis.util.methods;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author Sakrajin
 * Methods which are related to locations
 */
public class Locations {
    public static Location getFacingLocation(Location loc, Vector dir, double distance)
    {
        return loc.add(dir.normalize().multiply(distance));
    }

    public static List<Location> getDisplayEntityLocs(Location loc, double size, double increment)
    {
        List<Location>locs = new ArrayList<>();
        for (double x = -size/2; x <size/2 ; x += increment)
        {
            for (double y = -size/2; y <size/2 ; y += increment)
            {
                for (double z = -size/2; z <size/2 ; z += increment)
                {
                    locs.add(loc.clone().add(x,y,z));
                }
            }
        }
        return locs;
    }



    public static List<Location> getSphere(Location loc, double radii, int density) {
        final List<Location> sphere = new ArrayList<Location>();
        for (double i = 0; i <= Math.PI; i += Math.PI / density) {
            double radius = Math.sin(i) * radii;
            double y = Math.cos(i) * radii;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI*2 / density) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                sphere.add(loc.clone().add(x,y,z));
            }
        }
        return sphere;
    }
    public static List<Location> getCircle(Location loc, double radii, int points) {
        final List<Location> circle = new ArrayList<>();
        for (double i = 0; i < Math.PI *2; i+= Math.PI*2 / points) {
            double x = Math.sin(i) * radii;
            double z = Math.cos(i) * radii;
            Location location = loc.clone().add(x,0,z);
            circle.add(location);
        }
        return circle;
    }

    public static List<Location> getArcFromTrig(Location loc, double radius, int points, Vector dir, int orientation, int startAngle, int endAngle, boolean clockwise)
    {
        int increment = Math.floorDiv(endAngle-startAngle, points);
        List<Location>locs = new ArrayList<>();
        for (int i = startAngle; i < endAngle; i+=increment)
        {
            double radian = Math.toRadians(i);
            double x, z;
            if (!clockwise) {
                 x = Math.sin(radian) ;
                 z = Math.cos(radian) ;
            }
            else{
                 z = Math.sin(radian) ;
                 x = Math.cos(radian) ;
            }
            Vector v = new Vector(x, 0, z).multiply(radius).rotateAroundAxis(dir,Math.toRadians(orientation));
            locs.add(loc.clone().add(v));
        }
        return locs;
    }

    public static Set<Location>getPerpArcFromVector(Location loc, Vector dir, double radius, int startAngle, int endAngle, int points)
    {
        int increment = Math.floorDiv(endAngle-startAngle, points);
        Set<Location>locs = new HashSet<>();
        for (int i = startAngle; i < endAngle; i+=increment)
        {
            locs.add(loc.clone().add(dir.clone().rotateAroundY(Math.toRadians(i)).multiply(radius)));
        }
        return locs;
    }


    public static List<Location>getShotLocations(Location loc, int points, Vector dir, double speed)
    {
        double increment = speed/points;
        List<Location>locs = new ArrayList<>();
        for (double d = 0; d < speed ; d+= increment)
        {
            locs.add(loc.clone().add(dir.clone().multiply(d)));
        }
        return locs;
    }

    public static List<Location>getBezierCurveLocations(Location loc, int points, LinkedHashMap<Vector,Double> directions, double speed)
    {
        double distance = directions.values().stream().reduce(0.0, Double::sum);
        double increment = (speed * distance)/points;
        List<Location>locs = new ArrayList<>();
        Line line = new Line(directions);
        for (double d = 0; d < distance; d+= increment)
        {
            locs.add(loc.clone().add(line.getVector(increment/distance)));
        }

        return locs;
    }

    private static class Line {

        private Vector dir1;
        private Vector dir2;

        private Line previous;

        Line(Line previous, Vector dir1, Vector dir2)
        {
            this.previous = previous;
            this.dir1 = dir1;
            this.dir2 = dir2;
        }

        Line(LinkedHashMap<Vector,Double> directions)
        {
            Vector oldVector = new Vector(0,0,0);
            Line line = null;
            int i = 0;
            for (Map.Entry<Vector,Double> entry : directions.entrySet())
            {
                i++;
                if (i == directions.size()) {
                    this.previous = line;
                    this.dir1 = oldVector;
                    this.dir2 = entry.getKey().multiply(entry.getValue()).add(oldVector);
                    return;
                }

                Line newLine = new Line(line, oldVector,  entry.getKey().multiply( entry.getValue()).add(oldVector));
                oldVector = oldVector.add(entry.getKey());
                line = newLine;

            }

        }

        private Vector getVector(double time)
        {
            if (previous == null)
            {
                return dir1.multiply(1-time).add(dir2.multiply(time));
            }
            else {
                return previous.getVector(1 - time).add(dir2.multiply(time));
            }
        }

    }

    public static List<Location> getPolygon(Location loc, double radii, int points){
        final List<Location> polygon = new ArrayList<Location>();
        for (int i = 0; i < points; i++) {
            double angle = 360.0 / points * i;
            double nextAngle = 360.0 / points * (i + 1); // the angle for the next point.
            angle = Math.toRadians(angle);
            nextAngle = Math.toRadians(nextAngle); // convert to radians.
            double x = Math.cos(angle);
            double z = Math.sin(angle);
            double x2 = Math.cos(nextAngle);
            double z2 = Math.sin(nextAngle);
            double deltaX = x2 - x; // get the x-difference between the points.
            double deltaZ = z2 - z; // get the z-difference between the points.
            double distance = Math.sqrt((deltaX - x) * (deltaX - x) + (deltaZ - z) * (deltaZ - z));
            for (double d = 0; d < distance - .1; d += .1) { // we subtract .1 from the distance because otherwise it would make 1 step too many.
                loc.add(x + deltaX * d, 0, z + deltaZ * d);
                polygon.add(loc);
                loc.subtract(x + deltaX * d, 0, z + deltaZ * d);
            }
        }
        return polygon;
    }

    public static List<Location> getHollowPolygon(Location loc, double radii, int points){
        final List<Location> hpolygon = new ArrayList<Location>();
        for (int i = 0; i < points; i++) {
            double angle = 360.0 / points * i;
            angle = Math.toRadians(angle);
            double x = Math.cos(angle);
            double z = Math.sin(angle);
        }
        return hpolygon;
    }


    public static Set<Location> getLocsAroundPoint(Location loc, double radius, double distance)
    {
        Set<Location>locs = new HashSet<>();
        radius -= radius/2;
        for (double y = -radius ; y < radius ; y+= distance)
        {
            for (double x = -radius ; x < radius ; x+= distance)
            {
                for (double z = -radius ; z < radius ; z+= distance)
                {
                    locs.add(loc.clone().add(x,y,z));
                }
            }
        }
        return locs;
    }

    public static Set<Location> getOutsideSphereLocs(Location loc, double radius, double distance)
    {
        Set<Location>locs = new HashSet<>();
        radius -= radius/2;
        for (double y = -radius ; y < radius ; y+= distance)
        {
            for (double x = -radius ; x < radius ; x+= distance)
            {
                for (double z = -radius ; z < radius ; z+= distance)
                {
                    Location temploc = loc.clone().add(x,y,z);
                    if (temploc.distanceSquared(loc) < radius*radius && temploc.distance(loc) > radius-distance-0.1) {
                        locs.add(temploc);
                    }
                }
            }
        }
        return locs;
    }

    public static Location getLeftSide(final Location location, final double distance) {
        final float angle = (float) Math.toRadians(location.getYaw());
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getRightSide(final Location location, final double distance) {
        final float angle = (float) Math.toRadians(location.getYaw());
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getMainHandLocation(final Player player) {
        double y = 1.2 - (player.isSneaking() ? 0.4 : 0);
        if (player.getMainHand() == MainHand.LEFT) {
            return getLeftSide(player.getLocation(), .55).add(0, y, 0)
                    .add(player.getLocation().getDirection().multiply(0.8));
        } else {
            return getRightSide(player.getLocation(), .55).add(0, y, 0)
                    .add(player.getLocation().getDirection().multiply(0.8));
        }
    }

    public static Location getOffHandLocation(final Player player) {
        double y = 1.2 - (player.isSneaking() ? 0.4 : 0);
        if (player.getMainHand() == MainHand.RIGHT) {
            return getLeftSide(player.getLocation(), .55).add(0, y, 0)
                    .add(player.getLocation().getDirection().multiply(0.8));
        } else {
            return getRightSide(player.getLocation(), .55).add(0, y, 0)
                    .add(player.getLocation().getDirection().multiply(0.8));
        }
    }
}
