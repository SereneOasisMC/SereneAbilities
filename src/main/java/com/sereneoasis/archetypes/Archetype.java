package com.sereneoasis.archetypes;

/**
 * @author Sakrajin
 * Enums to represent different archetypes
 */
public enum Archetype {
    NONE("none"),
    OCEAN("ocean"),
    SUN("sun"),

    SKY("sky"),
    WAR("war"),
    EARTH("earth");
    //NETHER("nether"),
    //EARTH("earth"),
    //NATURE("nature");

    private String name;

    Archetype(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
