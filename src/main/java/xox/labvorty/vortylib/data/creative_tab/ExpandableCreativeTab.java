package xox.labvorty.vortylib.data.creative_tab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ExpandableCreativeTab extends CreativeModeTab {
    private final List<TabEntry> entries;
    public final Map<String, ExpandableGroup> groups = new LinkedHashMap<>();

    protected ExpandableCreativeTab(CreativeModeTab.Builder builder) {
        super(builder);
        if (builder instanceof Builder expandableBuilder) {
            this.entries = List.copyOf(expandableBuilder.entries);
            this.groups.putAll(expandableBuilder.groups);
        } else {
            this.entries = List.of();
        }
    }

    public static Builder builder() {
        return new Builder(Row.TOP, 0);
    }

    @Override
    public @NotNull Collection<ItemStack> getDisplayItems() {
        List<ItemStack> items = new ArrayList<>();

        for (TabEntry entry : entries) {
            if (entry instanceof TabEntry.Single single) {
                items.add(single.stack().copy());
            } else if (entry instanceof TabEntry.Group groupEntry) {
                ExpandableGroup group = groups.get(groupEntry.id());
                if (group == null) {
                    continue;
                }
                items.add(group.icon.copy());
                if (ExpansionHelpers.isExpanded(group.icon)) {
                    items.addAll(group.items.stream().map(ItemStack::copy).toList());
                }
            }
        }

        items.addAll(super.getDisplayItems());

        return items;
    }

    @Override
    public boolean hasAnyItems() {
        return true;
    }

    private sealed interface TabEntry {
        record Single(ItemStack stack) implements TabEntry {}
        record Group(String id) implements TabEntry {}
    }

    public static class Builder extends CreativeModeTab.Builder {
        private final List<TabEntry> entries = new ArrayList<>();
        private final Map<String, ExpandableGroup> groups = new LinkedHashMap<>();

        public Builder(Row row, int column) {
            super(row, column);
            this.withTabFactory(ExpandableCreativeTab::new);
        }

        public Builder addItem(ItemStack item) {
            entries.add(new TabEntry.Single(item.copy()));
            return this;
        }

        public Builder addItems(List<ItemStack> items) {
            for (ItemStack item : items) {
                addItem(item);
            }
            return this;
        }

        public Builder addItems(ItemStack... items) {
            return addItems(List.of(items));
        }

        public Builder addGroup(String id, ItemStack icon, List<ItemStack> items) {
            if (groups.containsKey(id)) {
                throw new IllegalStateException("Duplicate group id registered on this tab: " + id);
            }

            ItemStack taggedIcon = icon.copy();
            taggedIcon.getOrCreateTag().putString("vorty_lib_group_id", id);
            taggedIcon.getOrCreateTag().putString("vorty_lib_group_item_id", id);

            List<ItemStack> taggedItems = items.stream()
                    .map(ItemStack::copy)
                    .peek(stack -> stack.getOrCreateTag().putString("vorty_lib_group_item_id", id))
                    .toList();

            groups.put(id, new ExpandableGroup(taggedIcon, taggedItems));
            entries.add(new TabEntry.Group(id));

            return this;
        }

        @Override
        public CreativeModeTab build() {
            CreativeModeTab tab = super.build();

            if (!(tab instanceof ExpandableCreativeTab)) {
                throw new IllegalStateException("ExpandableCreativeTab.Builder produced " + tab.getClass() + " instead of ExpandableCreativeTab - tabFactory was overridden incorrectly.");
            }

            return tab;
        }
    }
}