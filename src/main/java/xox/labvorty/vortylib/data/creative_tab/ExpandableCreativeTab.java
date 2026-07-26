package xox.labvorty.vortylib.data.creative_tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import xox.labvorty.vortylib.init.VortyLibDataComponents;

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
        return new Builder(CreativeModeTab.Row.TOP, 0);
    }

    @Override
    public @NotNull Collection<ItemStack> getDisplayItems() {
        Collection<ItemStack> items = new ArrayList<>();

        for (ExpandableGroup group : groups.values()) {
            items.add(group.icon.copy());

            if (ExpansionHelpers.isExpanded(group.icon)) {
                items.addAll(group.items.stream().map(ItemStack::copy).toList());
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
            taggedIcon.set(VortyLibDataComponents.GROUP_COMPONENT, id);
            taggedIcon.set(VortyLibDataComponents.GROUP_ITEM_COMPONENT, id);

            List<ItemStack> taggedItems = items.stream()
                    .map(ItemStack::copy)
                    .peek(stack -> stack.set(
                            VortyLibDataComponents.GROUP_ITEM_COMPONENT,
                            id
                    ))
                    .toList();

            groups.put(id, new ExpandableGroup(taggedIcon, taggedItems));

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
