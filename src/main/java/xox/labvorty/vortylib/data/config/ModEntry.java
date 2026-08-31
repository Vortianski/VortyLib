package xox.labvorty.vortylib.data.config;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModEntry {
    private final String modId;
    private final Component displayName;
    private final ResourceLocation banner;
    private final int bannerWidth;
    private final int bannerHeight;
    @Nullable private final ConfigHolder clientConfig;
    @Nullable private final ConfigHolder commonConfig;
    private final List<SocialLink> socials;
    private final FeaturedItemsMode featuredItemsMode;
    private final List<ResourceLocation> chosenFeaturedItems;

    private ModEntry(Builder b) {
        this.modId = b.modId;
        this.displayName = b.displayName;
        this.banner = b.banner;
        this.bannerWidth = b.bannerWidth;
        this.bannerHeight = b.bannerHeight;
        this.clientConfig = b.clientConfig;
        this.commonConfig = b.commonConfig;
        this.socials = Collections.unmodifiableList(new ArrayList<>(b.socials));
        this.featuredItemsMode = b.featuredItemsMode;
        this.chosenFeaturedItems = Collections.unmodifiableList(new ArrayList<>(b.chosenFeaturedItems));
    }

    public String getModId() { return modId; }
    public Component getDisplayName() { return displayName; }
    public ResourceLocation getBanner() { return banner; }
    public int getBannerWidth() { return bannerWidth; }
    public int getBannerHeight() { return bannerHeight; }
    @Nullable public ConfigHolder getClientConfig() { return clientConfig; }
    @Nullable public ConfigHolder getCommonConfig() { return commonConfig; }
    public List<SocialLink> getSocials() { return socials; }
    public FeaturedItemsMode getFeaturedItemsMode() { return featuredItemsMode; }
    public List<ResourceLocation> getChosenFeaturedItems() { return chosenFeaturedItems; }

    public static Builder builder(String modId, Component displayName) {
        return new Builder(modId, displayName);
    }

    public static class Builder {
        private final String modId;
        private final Component displayName;
        @Nullable private ResourceLocation banner;
        private int bannerWidth = 64;
        private int bannerHeight = 64;
        @Nullable private ConfigHolder clientConfig;
        @Nullable private ConfigHolder commonConfig;
        private final List<SocialLink> socials = new ArrayList<>();
        private FeaturedItemsMode featuredItemsMode = FeaturedItemsMode.NONE;
        private final List<ResourceLocation> chosenFeaturedItems = new ArrayList<>();

        private Builder(String modId, Component displayName) {
            this.modId = modId;
            this.displayName = displayName;
        }

        public Builder banner(ResourceLocation banner, int width, int height) {
            this.banner = banner;
            this.bannerWidth = width;
            this.bannerHeight = height;
            return this;
        }

        public Builder banner(ResourceLocation banner) {
            return banner(banner, 64, 64);
        }

        public Builder clientConfig(ConfigHolder holder) { this.clientConfig = holder; return this; }
        public Builder commonConfig(ConfigHolder holder) { this.commonConfig = holder; return this; }

        public Builder addSocial(SocialLink link) {
            this.socials.add(link);
            return this;
        }

        public Builder featuredItemsAllFromModId() {
            this.featuredItemsMode = FeaturedItemsMode.ALL_FROM_MODID;
            return this;
        }

        public Builder featuredItemsChosen(List<ResourceLocation> items) {
            this.featuredItemsMode = FeaturedItemsMode.CHOSEN;
            this.chosenFeaturedItems.clear();
            this.chosenFeaturedItems.addAll(items);
            return this;
        }

        public Builder featuredItemsRandom() {
            this.featuredItemsMode = FeaturedItemsMode.RANDOM;
            return this;
        }

        public ModEntry build() {
            return new ModEntry(this);
        }
    }
}