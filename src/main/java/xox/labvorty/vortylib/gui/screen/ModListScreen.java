package xox.labvorty.vortylib.gui.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.VersionChecker;
import xox.labvorty.vortylib.configs.ClientConfig;
import xox.labvorty.vortylib.data.config.*;
import xox.labvorty.vortylib.gui.theme.PanoramaTheme;
import xox.labvorty.vortylib.gui.theme.UiTheme;
import xox.labvorty.vortylib.gui.widget.ConfettiEffect;
import xox.labvorty.vortylib.gui.widget.FeaturedItemsBar;
import xox.labvorty.vortylib.gui.widget.ThemedButton;

import java.util.*;
import java.util.stream.Collectors;

public class ModListScreen extends Screen {
    private static final int SOCIAL_SIZE = 18;
    private static final int SOCIAL_PADDING = 4;
    private static final int NAME_ROW_Y = 8;
    private static final int NAME_ROW_HEIGHT = 20;
    private static final int NAME_TEXT_Y = NAME_ROW_Y + (NAME_ROW_HEIGHT - 9) / 2;
    private static final int VERSION_Y = 32;
    private static final int UPDATE_TEXT_Y = 44;
    private static final int BANNER_Y = 60;
    private static final int FEATURED_BAR_Y = 160;
    private static final int FEATURED_BAR_HEIGHT = 18;
    private static final int FEATURED_BAR_INSET = 10;
    private static final int RANDOM_FEATURED_ITEM_COUNT = 40;
    private static final int SOCIAL_ICON_PADDING = 3;
    private List<ModEntry> mods;
    private List<VersionInfo> versionInfos = new ArrayList<>();
    private static int currentIndex = -1;
    private ThemedButton clientButton;
    private ThemedButton commonButton;
    private final List<SocialButton> socialButtons = new ArrayList<>();
    private FeaturedItemsBar featuredItemsBar;
    private final ConfettiEffect confetti = new ConfettiEffect();
    private int lastBannerX, lastBannerW, lastBannerH;
    private final Screen previousScreen;

    public ModListScreen(Screen previousScreen) {
        super(Component.empty());
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.mods = new ArrayList<>(ModRegistry.getAll());

        if (this.mods.isEmpty()) {
            currentIndex = 0;
        } else if (currentIndex >= 0) {
            currentIndex = Math.min(currentIndex, this.mods.size() - 1);
        } else {
            currentIndex = 0;

            for (int i = 0; i < this.mods.size(); i++) {
                if (this.mods.get(i).getModId().equals("vortylib")) {
                    currentIndex = i;
                    break;
                }
            }
        }

        this.versionInfos = new ArrayList<>();

        for (ModEntry entry : mods) {
            versionInfos.add(getVersionInfo(entry));
        }

        ThemedButton leftArrow = ThemedButton.builder(Component.literal("<"), b -> {
                    if (mods.isEmpty()) return;
                    currentIndex = (currentIndex - 1 + mods.size()) % mods.size();
                    refreshSelection();
                })
                .bounds(this.width / 2 - 110, NAME_ROW_Y, 20, NAME_ROW_HEIGHT)
                .build();

        ThemedButton rightArrow = ThemedButton.builder(Component.literal(">"), b -> {
                    if (mods.isEmpty()) return;
                    currentIndex = (currentIndex + 1) % mods.size();
                    refreshSelection();
                })
                .bounds(this.width / 2 + 90, NAME_ROW_Y, 20, NAME_ROW_HEIGHT)
                .build();

        this.clientButton = ThemedButton.builder(Component.literal("Client"), b -> openConfig(true))
                .bounds(this.width / 2 - 84, 130, 80, 20)
                .build();

        this.commonButton = ThemedButton.builder(Component.literal("Common"), b -> openConfig(false))
                .bounds(this.width / 2 + 4, 130, 80, 20)
                .build();

        this.addRenderableWidget(leftArrow);
        this.addRenderableWidget(rightArrow);
        this.addRenderableWidget(clientButton);
        this.addRenderableWidget(commonButton);

        this.addRenderableWidget(ThemedButton.builder(CommonComponents.GUI_DONE, b -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 26, 200, 20)
                .build());

        updateButtonStates();

        boolean hasMods = !mods.isEmpty();
        leftArrow.active = hasMods && mods.size() > 1;
        rightArrow.active = hasMods && mods.size() > 1;

        buildSocialButtons();
        buildFeaturedItemsBar();
    }

    private void buildSocialButtons() {
        for (SocialButton btn : socialButtons) {
            this.removeWidget(btn);
        }
        socialButtons.clear();

        if (mods.isEmpty()) return;

        ModEntry current = mods.get(currentIndex);
        List<SocialLink> socials = current.getSocials();

        for (int i = 0; i < socials.size(); i++) {
            SocialLink link = socials.get(i);
            int x = this.width - SOCIAL_SIZE - SOCIAL_PADDING;
            int y = this.height - SOCIAL_SIZE - SOCIAL_PADDING - (i * (SOCIAL_SIZE + SOCIAL_PADDING));
            SocialButton btn = new SocialButton(x, y, SOCIAL_SIZE, link, this);
            this.addRenderableWidget(btn);
            socialButtons.add(btn);
        }
    }

    private void buildFeaturedItemsBar() {
        if (featuredItemsBar != null) {
            this.removeWidget(featuredItemsBar);
            featuredItemsBar = null;
        }

        if (mods.isEmpty()) return;

        ModEntry current = mods.get(currentIndex);
        if (current.getFeaturedItemsMode() == FeaturedItemsMode.NONE) return;

        List<Item> items = resolveFeaturedItems(current);
        if (items.isEmpty()) return;

        int spanStart = clientButton.getX();
        int spanEnd = commonButton.getX() + commonButton.getWidth();

        int barX = spanStart + FEATURED_BAR_INSET;
        int barWidth = (spanEnd - spanStart) - FEATURED_BAR_INSET * 2;

        featuredItemsBar = new FeaturedItemsBar(barX, FEATURED_BAR_Y, barWidth, FEATURED_BAR_HEIGHT, items);
        this.addRenderableWidget(featuredItemsBar);
    }

    private List<Item> resolveFeaturedItems(ModEntry entry) {
        return switch (entry.getFeaturedItemsMode()) {
            case NONE -> List.of();
            case ALL_FROM_MODID -> BuiltInRegistries.ITEM.keySet().stream()
                    .filter(id -> id.getNamespace().equals(entry.getModId()))
                    .map(BuiltInRegistries.ITEM::get)
                    .collect(Collectors.toList());
            case CHOSEN -> entry.getChosenFeaturedItems().stream()
                    .map(BuiltInRegistries.ITEM::get)
                    .collect(Collectors.toList());
            case RANDOM -> {
                List<Item> all = new ArrayList<>(BuiltInRegistries.ITEM.stream().toList());
                Collections.shuffle(all, new Random());
                yield all.subList(0, Math.min(RANDOM_FEATURED_ITEM_COUNT, all.size()));
            }
        };
    }

    private void refreshSelection() {
        updateButtonStates();
        buildSocialButtons();
        buildFeaturedItemsBar();
    }

    private void updateButtonStates() {
        if (mods.isEmpty()) {
            clientButton.active = false;
            commonButton.active = false;
            return;
        }
        ModEntry current = mods.get(currentIndex);
        ConfigHolder client = current.getClientConfig();
        ConfigHolder common = current.getCommonConfig();
        clientButton.active = client != null;
        commonButton.active = common != null;
    }

    private void openConfig(boolean client) {
        if (mods.isEmpty()) return;
        ModEntry current = mods.get(currentIndex);
        ConfigHolder holder = client ? current.getClientConfig() : current.getCommonConfig();
        if (holder == null) return;
        this.minecraft.setScreen(new ModConfigScreen(this, current, holder));
    }

    private record VersionInfo(String current, String latest, boolean outdated) {}

    private VersionInfo getVersionInfo(ModEntry entry) {
        Optional<? extends ModContainer> containerOpt = ModList.get().getModContainerById(entry.getModId());
        if (containerOpt.isEmpty()) {
            return new VersionInfo("N/A", "N/A", false);
        }

        ModContainer container = containerOpt.get();
        String current = container.getModInfo().getVersion().toString();

        VersionChecker.CheckResult result = VersionChecker.getResult(container.getModInfo());

        if (result.status() == VersionChecker.Status.PENDING) {
            return new VersionInfo(current, "Checking...", false);
        }

        if (result.status() == VersionChecker.Status.FAILED) {
            return new VersionInfo(current, "Unavailable", false);
        }

        String latest = result.target() != null ? result.target().toString() : current;
        boolean outdated = result.status() == VersionChecker.Status.OUTDATED
                || result.status() == VersionChecker.Status.BETA_OUTDATED;

        return new VersionInfo(current, latest, outdated);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        VortyLibBackground.render(guiGraphics, this.width, this.height, partialTick,
                () -> super.renderBackground(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !mods.isEmpty() && isOverBanner(mouseX, mouseY)) {
            //confetti.trigger((int) mouseX, (int) mouseY);
            //TODO:Rework this

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isOverBanner(double mouseX, double mouseY) {
        return mouseX >= lastBannerX && mouseX <= lastBannerX + lastBannerW
                && mouseY >= BANNER_Y && mouseY <= BANNER_Y + lastBannerH;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        confetti.tick();

        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (!mods.isEmpty()) {
            ModEntry current = mods.get(currentIndex);

            guiGraphics.drawCenteredString(this.font, current.getDisplayName(), this.width / 2, NAME_TEXT_Y, 0xFFFFFF);

            VersionInfo info = versionInfos.get(currentIndex);

            MutableComponent versionLine = Component.literal("Version: ").withStyle(ChatFormatting.GRAY);
            versionLine.append(Component.literal(info.current()).withStyle(ChatFormatting.YELLOW));

            if (info.outdated()) {
                versionLine.append(Component.literal(" → ").withStyle(ChatFormatting.DARK_GRAY));
                versionLine.append(Component.literal(info.latest()).withStyle(ChatFormatting.GREEN));
            }

            guiGraphics.drawCenteredString(this.font, versionLine, this.width / 2, VERSION_Y, 0xFFFFFF);

            if (info.outdated()) {
                Component updateText = Component.literal("Update available!").withStyle(ChatFormatting.RED);
                guiGraphics.drawCenteredString(this.font, updateText, this.width / 2, UPDATE_TEXT_Y, 0xFF5555);
            }

            int bannerW = current.getBannerWidth();
            int bannerH = current.getBannerHeight();
            int bannerX = this.width / 2 - bannerW / 2;

            lastBannerX = bannerX;
            lastBannerW = bannerW;
            lastBannerH = bannerH;

            if (current.getBanner() != null) {
                guiGraphics.blit(
                        current.getBanner(),
                        bannerX, BANNER_Y,
                        0, 0,
                        bannerW, bannerH,
                        bannerW, bannerH
                );
            }

        } else {
            guiGraphics.drawCenteredString(this.font, Component.literal("No mods registered"),
                    this.width / 2, 60, 0xAAAAAA);
        }

        confetti.render(guiGraphics);

        renderHoveredSocialTooltip(guiGraphics);
    }

    private void renderHoveredSocialTooltip(GuiGraphics guiGraphics) {
        for (SocialButton btn : socialButtons) {
            if (!btn.isHovered()) continue;

            Component tooltip = btn.getLink().getTooltip();
            int textWidth = this.font.width(tooltip);
            int gap = 8;
            int tooltipX = btn.getX() - gap - textWidth;
            int tooltipY = btn.getY() + btn.getHeight() / 2;

            guiGraphics.renderTooltip(this.font, List.of(tooltip), Optional.empty(), tooltipX, tooltipY);
            return;
        }
    }

    private static class SocialButton extends ThemedButton {
        private final SocialLink link;
        private final Screen parent;

        SocialButton(int x, int y, int size, SocialLink link, Screen parent) {
            super(x, y, size, size, Component.empty(), b -> {});
            this.link = link;
            this.parent = parent;
        }

        @Override
        public void onPress() {
            this.parent.getMinecraft().setScreen(new ConfirmLinkScreen(
                    (confirmed) -> {
                        if (confirmed) {
                            Util.getPlatform().openUri(link.getUrl());
                        }
                        this.parent.getMinecraft().setScreen(parent);
                    },
                    link.getUrl(),
                    true
            ));
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            SpriteInfo sprite = isHovered() ? link.getHovered() : link.getNormal();

            int maxIconWidth = width - SOCIAL_ICON_PADDING * 2;
            int maxIconHeight = height - SOCIAL_ICON_PADDING * 2;

            float scaleX = (float) maxIconWidth / sprite.spriteWidth();
            float scaleY = (float) maxIconHeight / sprite.spriteHeight();
            float scale = Math.min(scaleX, scaleY);

            int drawWidth = Math.round(sprite.spriteWidth() * scale);
            int drawHeight = Math.round(sprite.spriteHeight() * scale);

            int offsetX = (width - drawWidth) / 2;
            int offsetY = (height - drawHeight) / 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(getX() + offsetX, getY() + offsetY, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);

            guiGraphics.blit(
                    sprite.texture(),
                    0, 0,
                    sprite.u(), sprite.v(),
                    sprite.spriteWidth(), sprite.spriteHeight(),
                    sprite.sheetWidth(), sprite.sheetHeight()
            );

            guiGraphics.pose().popPose();
        }

        SocialLink getLink() {
            return link;
        }
    }

    @Override
    public void onClose() {
        if (previousScreen != null && minecraft != null) {
            minecraft.setScreen(previousScreen);
        }
    }

    @Override
    public void tick() {
        super.tick();

        UiTheme.setCurrent(ClientConfig.UI_THEME.get());
        PanoramaTheme.setCurrent(ClientConfig.PANORAMA_THEME.get());
    }
}