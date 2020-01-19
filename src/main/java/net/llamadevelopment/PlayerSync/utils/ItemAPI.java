package net.llamadevelopment.PlayerSync.utils;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

public class ItemAPI {

    public static String invToString(Inventory inventory) {
        StringBuilder stringInv = new StringBuilder();
            inventory.getContents().forEach(((integer, item) -> {
                stringInv.append(toString(integer, item)).append("/");
            }));
        return stringInv.toString().substring(0, stringInv.toString().length() - 1);
    }

    // itemInfo,Lore,Enchantments
    // name#slot#id#damage#count,lore1#lore2#lore2,id:level#id:level

    public static String toString(int slot, Item item) {
        StringBuilder sb = new StringBuilder();
        String customName = item.getCustomName().replace("#", "")
                .replace("/", "")
                .replace(",", "");
        sb.append(customName).append("#");
        sb.append(slot).append("#");
        sb.append(item.getId()).append("#");
        sb.append(item.getDamage()).append("#");
        sb.append(item.getCount()).append(",");

        // #,/

        for (String lore : item.getLore()) {
            String replacedLore = lore.replace("#", "")
                    .replace("/", "")
                    .replace(",", "");
            sb.append(replacedLore).append("#");
        }
        sb.append(",");

        for (Enchantment enchantment : item.getEnchantments()) {
            sb.append(enchantment.getId())
                    .append(":")
                    .append(enchantment.getLevel())
                    .append("#");
        }
        return sb.toString();
    }

    // itemInfo,Lore,Enchantments
    // name#slot#id#damage#count,lore1#lore2#lore2,id:level#id:level

    public static ItemWithSlot fromString(String itemString) throws NumberFormatException {
        String[] info = itemString.split(",");
        String[] general = info[0].split("#");
        String name = general[0];
        int slot = Integer.parseInt(general[1]);
        int itemid = Integer.parseInt(general[2]);
        int damage = Integer.parseInt(general[3]);
        int count = Integer.parseInt(general[4]);
        Item item = Item.get(itemid, damage, count);
        if (name != null && !name.equals("")) {
            item.setCustomName(name);
        }
        try {
            String[] lores = info[1].split("#");
            if (lores.length != 0) {
                item.setLore(lores);
            }
        } catch (IndexOutOfBoundsException ignored) { }
        try {
            String[] enStrings = info[2].split("#");
            for (String encString : enStrings) {
                String[] eInfo = encString.split(":");
                Enchantment enchantment = Enchantment.get(Integer.parseInt(eInfo[0])).setLevel(Integer.parseInt(eInfo[1]));
                item.addEnchantment(enchantment);
            }
        } catch (IndexOutOfBoundsException ignored) { }
        return new ItemWithSlot(slot, item);
    }

}
