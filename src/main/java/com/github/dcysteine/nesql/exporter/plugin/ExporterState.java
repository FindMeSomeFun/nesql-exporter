package com.github.dcysteine.nesql.exporter.plugin;

import com.github.dcysteine.nesql.exporter.main.Logger;
import com.github.dcysteine.nesql.sql.base.item.Item;
import jakarta.persistence.EntityManager;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Class that holds the accessor for the database, stores any needed state, and handles listeners.
 */
public class ExporterState {
    public interface ItemListener extends BiConsumer<Item, ItemStack> {}

    private final EntityManager entityManager;
    private int itemCount;
    private int fluidCount;
    private int entityCount;

    // TODO do we want to provide some sort of RecipeType registry here?
    // If so, something like: Map<Class<T>, T> where T is a plugin-custom recipe repository class.

    // We can do something similar for factories, if it ever becomes an issue that we just construct
    // new ones where they are needed. We can add Map<Class<T>, Factory<T>> to hold singletons.
    // Must add them to map in PluginExporter.initialize() though, to avoid mod dependency issues.

    private final List<ItemListener> itemListeners;

    // TODO do we want to add listeners on iteration through the vanilla crafting recipe list?

    public ExporterState(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.itemCount = 0;
        this.fluidCount = 0;
        this.entityCount = 0;
        this.itemListeners = new ArrayList<>();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Call this after each batch of database persists, to prevent the persistence context from
     * getting too large.
     *
     * <p>See <a href="https://en.wikibooks.org/wiki/Java_Persistence/Persisting#Flush">here</a>.
     */
    public void flushEntityManager() {
        Logger.MOD.info("Flushing entity manager state; this may take a while!");
        entityManager.flush();
        entityManager.clear();
        Logger.MOD.info("Flush complete.");
    }

    public int incrementItemCount() {
        return ++itemCount;
    }

    public int incrementFluidCount() {
        return ++fluidCount;
    }

    public int incrementEntityCount() {
        return ++entityCount;
    }

    public void addItemListener(ItemListener listener) {
        itemListeners.add(listener);
    }

    public void invokeItemListeners(Item item, ItemStack itemStack) {
        itemListeners.forEach(listener -> listener.accept(item, itemStack));
    }
}
