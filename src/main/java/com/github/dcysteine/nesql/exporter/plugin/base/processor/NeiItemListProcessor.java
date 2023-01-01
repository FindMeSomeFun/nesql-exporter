package com.github.dcysteine.nesql.exporter.plugin.base.processor;

import codechicken.nei.ItemList;
import com.github.dcysteine.nesql.exporter.main.Logger;
import com.github.dcysteine.nesql.exporter.plugin.base.factory.ItemFactory;
import com.github.dcysteine.nesql.exporter.util.ItemUtil;
import jakarta.persistence.EntityManager;
import net.minecraft.item.ItemStack;

public class NeiItemListProcessor {
    private final EntityManager entityManager;

    public NeiItemListProcessor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void process() {
        int total = ItemList.items.size();
        Logger.MOD.info("Processing {} NEI items...", total);

        ItemFactory itemFactory = new ItemFactory(entityManager);
        int count = 0;
        for (ItemStack itemStack : ItemList.items) {
            count++;
            try {
                itemFactory.getItem(itemStack);
            } catch (Exception e) {
                // GTNH has some bad items, so we have to do this =(
                // For whatever reason, the exceptions thrown by those items don't even have stack
                // traces!
                Logger.MOD.info("Found a bad item: " + itemStack.getDisplayName());
                e.printStackTrace();
            }

            if (Logger.intermittentLog(count)) {
                Logger.MOD.info("Processed NEI item {} of {}", count, total);
                Logger.MOD.info("Most recent item: {}", itemStack.getDisplayName());
            }
        }

        Logger.MOD.info("Finished processing NEI items!");
    }

}