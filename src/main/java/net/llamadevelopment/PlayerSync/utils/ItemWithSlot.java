package net.llamadevelopment.PlayerSync.utils;

import cn.nukkit.item.Item;

public class ItemWithSlot {

    public Integer slot;
    public Item item;

    public ItemWithSlot(Integer slot, Item item) {
        this.slot = slot;
        this.item = item;
    }

}
