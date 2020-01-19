package net.llamadevelopment.PlayerSync.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import net.llamadevelopment.PlayerSync.PlayerSync;
import net.llamadevelopment.PlayerSync.utils.Manager;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(DataPacketReceiveEvent event) {
        if (event.getPacket() instanceof SetLocalPlayerAsInitializedPacket) {
            Manager.loadPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!Manager.loaded.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Manager.savePlayerAsync(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!Manager.loaded.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

}
