package net.llamadevelopment.PlayerSync.utils;

public class SyncPlayer {

    public float health;
    public int exp;
    public int level;
    public int food;
    public String invString;
    public String ecString;

    public SyncPlayer(String invString, String ecString, float health, int food, int level, int exp) {
        this.invString = invString;
        this.ecString = ecString;
        this.health = health;
        this.exp = exp;
        this.level = level;
        this.food = food;
    }

}
