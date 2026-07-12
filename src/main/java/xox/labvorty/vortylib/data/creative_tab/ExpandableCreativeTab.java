package xox.labvorty.vortylib.data.creative_tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ExpandableCreativeTab extends CreativeModeTab {
    public final Map<String, ExpandableGroup> groups = new LinkedHashMap<>();

    protected ExpandableCreativeTab(Builder builder) {
        super(builder);
    }

    public ExpandableCreativeTab(CreativeModeTab.Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder(Row.TOP, 0);
    }

    @Override
    public @NotNull Collection<ItemStack> getDisplayItems() {
        Collection<ItemStack> items = new ArrayList<>();

        for (ExpandableGroup group : groups.values()) {
            items.add(group.icon);

            if (ExpansionHelpers.isExpanded(group.icon)) {
                items.addAll(group.items);
            }
        }

        items.addAll(super.getDisplayItems());

        return items;
    }

    @Override
    public boolean hasAnyItems() {
        return true;
    }

    public static class Builder extends CreativeModeTab.Builder {
        private final Map<String, ExpandableGroup> groups = new LinkedHashMap<>();

        public Builder(Row row, int column) {
            super(row, column);
            this.withTabFactory(ExpandableCreativeTab::new);
        }

        public Builder addGroup(String id, ItemStack icon, List<ItemStack> items) {
            ItemStack taggedIcon = icon.copy();
            taggedIcon.getOrCreateTag().putString("vorty_lib_group_id", id);
            groups.put(id, new ExpandableGroup(taggedIcon, items));
            return this;
        }

        @Override
        public CreativeModeTab build() {
            CreativeModeTab tab = super.build();

            if (tab instanceof ExpandableCreativeTab expandableCreativeTab) {
                expandableCreativeTab.groups.putAll(this.groups);
            } else {
                throw new IllegalStateException("ExpandableCreativeTab.Builder produced " + tab.getClass() + " instead of ExpandableCreativeTab - tabFactory was overridden incorrectly.");
            }

            return tab;
        }
    }
}
