package net.llamadevelopment.PlayerSync.provider;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.llamadevelopment.PlayerSync.utils.SyncPlayer;

public class Provider {

    public void open(Config c) {

    }

    public void close() {

    }

    public void savePlayer(String uuid, String invString, String ecString, String health, int food, int level, int exp) {

    }

    public SyncPlayer getPlayer(Player player) { return null; }

    public String getName() {
        return "undefined";
    }
}
