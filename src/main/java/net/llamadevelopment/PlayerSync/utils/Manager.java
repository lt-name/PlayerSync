package net.llamadevelopment.PlayerSync.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import net.llamadevelopment.PlayerSync.PlayerSync;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Manager {
    /*
     * _id: uuid
     * inventory: string
     * enderchest: string
     * health: string
     * food: int
     * exp: int/long
     *
     * */
    public static ArrayList<String> loaded = new ArrayList<>();
    public static boolean inventory, enderchest, health, food, exp;
    public static int loadDelay;

    public static void savePlayerAsync(Player player) {
        CompletableFuture.runAsync(() -> {
            try {
                savePlayer(player);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void savePlayer(Player player) {
        if (!loaded.contains(player.getName())) return;
        String inv = "empty";
        String ec = "empty";
        if (enderchest && player.getEnderChestInventory().getContents().size() > 0) {
            ec = ItemAPI.invToString(player.getEnderChestInventory());
        }
        if (inventory && player.getInventory().getContents().size() > 0) {
            inv = ItemAPI.invToString(player.getInventory());
        }
        PlayerSync.provider.savePlayer(player.getUniqueId().toString(), inv, ec, player.getHealth() + "", player.getFoodData().getLevel(), player.getExperienceLevel(), player.getExperience());
    }

    public static void loadPlayer(Player player) {

        loaded.remove(player.getName());
        SyncPlayer syncPlayer = PlayerSync.provider.getPlayer(player);

        player.getInventory().clearAll();
        player.getEnderChestInventory().clearAll();
        player.setExperience(0, 0);

        player.sendMessage(Language.getMessage("loadingData"));

        PlayerSync.instance.getServer().getScheduler().scheduleDelayedTask(PlayerSync.instance, () -> {
                if (inventory) {
                    String inv = syncPlayer.invString;
                    if (!inv.equalsIgnoreCase("empty")) {
                        String[] itemStrings = inv.split("/");
                        HashMap<Integer, Item> loadedInv = new HashMap<>();
                        for (String str : itemStrings) {
                            ItemWithSlot its = ItemAPI.fromString(str);
                            loadedInv.put(its.slot, its.item);
                        }
                        player.getInventory().setContents(loadedInv);
                    }
                }
                if (enderchest) {
                    String ecInv = syncPlayer.ecString;
                    if (!ecInv.equals("empty")) {
                        String[] ecitemStrings = ecInv.split("/");
                        HashMap<Integer, Item> loadedEcInv = new HashMap<>();
                        for (String str : ecitemStrings) {
                            ItemWithSlot its = ItemAPI.fromString(str);
                            loadedEcInv.put(its.slot, its.item);
                        }
                        player.getEnderChestInventory().setContents(loadedEcInv);
                    }
                }
                if (health) {
                    player.setHealth(syncPlayer.health);
                }
                if (food) {
                    player.getFoodData().setLevel(syncPlayer.food);
                }
                if (exp) {
                    player.setExperience(syncPlayer.exp, syncPlayer.level);
                }
                loaded.add(player.getName());
            player.sendMessage(Language.getMessage("loadingDone"));
        }, Manager.loadDelay);
    }

}
