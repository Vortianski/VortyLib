package xox.labvorty.vortylib.gui.widget;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import xox.labvorty.vortylib.gui.theme.UiTheme;

public class ThemedEditBox extends EditBox {
private static final Field F_DISPLAY_POS;
    private static final Field F_HIGHLIGHT_POS;
    private static final Field F_TEXT_COLOR;
    private static final Field F_TEXT_COLOR_UNEDITABLE;
    private static final Field F_IS_EDITABLE;
    private static final Field F_SUGGESTION;
    private static final Field F_FORMATTER;
    private static final Field F_HINT;
    private static final Field F_FOCUSED_TIME;
    private static final Field F_MAX_LENGTH;
    private static final boolean REFLECTION_OK;

    static {
        Field displayPos = null, highlightPos = null, textColor = null, textColorUneditable = null,
                isEditable = null, suggestion = null, formatter = null, hint = null,
                focusedTime = null, maxLength = null;
        boolean ok;
        try {
            displayPos = EditBox.class.getDeclaredField("displayPos");
            highlightPos = EditBox.class.getDeclaredField("highlightPos");
            textColor = EditBox.class.getDeclaredField("textColor");
            textColorUneditable = EditBox.class.getDeclaredField("textColorUneditable");
            isEditable = EditBox.class.getDeclaredField("isEditable");
            suggestion = EditBox.class.getDeclaredField("suggestion");
            formatter = EditBox.class.getDeclaredField("formatter");
            hint = EditBox.class.getDeclaredField("hint");
            focusedTime = EditBox.class.getDeclaredField("focusedTime");
            maxLength = EditBox.class.getDeclaredField("maxLength");

            for (Field f : new Field[]{displayPos, highlightPos, textColor, textColorUneditable,
                    isEditable, suggestion, formatter, hint, focusedTime, maxLength}) {
                f.setAccessible(true);
            }
            ok = true;
        } catch (ReflectiveOperationException e) {
            ok = false;
            System.err.println("[VortyLib] ThemedEditBox: failed to hook EditBox internals, "
                    + "falling back to framed rendering: " + e);
        }
        F_DISPLAY_POS = displayPos;
        F_HIGHLIGHT_POS = highlightPos;
        F_TEXT_COLOR = textColor;
        F_TEXT_COLOR_UNEDITABLE = textColorUneditable;
        F_IS_EDITABLE = isEditable;
        F_SUGGESTION = suggestion;
        F_FORMATTER = formatter;
        F_HINT = hint;
        F_FOCUSED_TIME = focusedTime;
        F_MAX_LENGTH = maxLength;
        REFLECTION_OK = ok;
    }

    private final Font themeFont;

    public ThemedEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.themeFont = font;
        this.setBordered(true); // required for EditBox's own correct text centering/padding
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!isVisible()) {
            return;
        }

        if (!REFLECTION_OK) {
            renderFramedFallback(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        try {
            renderThemed(guiGraphics);
        } catch (Exception e) {
            // Belt-and-suspenders: don't let a reflection edge case break input rendering.
            renderFramedFallback(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderFramedFallback(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(UiTheme.current().panelSprite(), getX(), getY(), getWidth(), getHeight());
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderThemed(GuiGraphics guiGraphics) throws ReflectiveOperationException {
        // Replaces vanilla's SPRITES.get(active, focused) blit with the theme panel.
        guiGraphics.blitSprite(UiTheme.current().panelSprite(), getX(), getY(), getWidth(), getHeight());

        String value = getValue();
        int cursorPos = getCursorPosition();
        int displayPos = F_DISPLAY_POS.getInt(this);
        int highlightPos = F_HIGHLIGHT_POS.getInt(this);
        int textColor = F_TEXT_COLOR.getInt(this);
        int textColorUneditable = F_TEXT_COLOR_UNEDITABLE.getInt(this);
        boolean isEditable = F_IS_EDITABLE.getBoolean(this);
        String suggestion = (String) F_SUGGESTION.get(this);
        @SuppressWarnings("unchecked")
        BiFunction<String, Integer, FormattedCharSequence> formatter =
                (BiFunction<String, Integer, FormattedCharSequence>) F_FORMATTER.get(this);
        Component hint = (Component) F_HINT.get(this);
        long focusedTime = F_FOCUSED_TIME.getLong(this);
        int maxLength = F_MAX_LENGTH.getInt(this);
        boolean textShadow = getTextShadow();

        int color = isEditable ? textColor : textColorUneditable;
        int i = cursorPos - displayPos;
        String s = themeFont.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());
        boolean cursorInView = i >= 0 && i <= s.length();
        boolean blinkOn = isFocused() && (Util.getMillis() - focusedTime) / 300L % 2L == 0L && cursorInView;

        int j = isBordered() ? getX() + 4 : getX();
        int k = isBordered() ? getY() + (getHeight() - 8) / 2 : getY();
        int l = j;
        int highlightViewPos = Mth.clamp(highlightPos - displayPos, 0, s.length());

        if (!s.isEmpty()) {
            String beforeCursor = cursorInView ? s.substring(0, i) : s;
            l = guiGraphics.drawString(themeFont, formatter.apply(beforeCursor, displayPos), j, k, color, textShadow);
        }

        boolean atEndOrFull = cursorPos < value.length() || value.length() >= maxLength;
        int cursorX = l;
        if (!cursorInView) {
            cursorX = i > 0 ? j + getWidth() : j;
        } else if (atEndOrFull) {
            cursorX = l - 1;
            --l;
        }

        if (!s.isEmpty() && cursorInView && i < s.length()) {
            guiGraphics.drawString(themeFont, formatter.apply(s.substring(i), cursorPos), l, k, color, textShadow);
        }

        if (hint != null && s.isEmpty() && !isFocused()) {
            guiGraphics.drawString(themeFont, hint, l, k, color, textShadow);
        }

        if (!atEndOrFull && suggestion != null) {
            guiGraphics.drawString(themeFont, suggestion, cursorX - 1, k, -8355712, textShadow);
        }

        if (blinkOn) {
            if (atEndOrFull) {
                guiGraphics.fill(RenderType.guiOverlay(), cursorX, k - 1, cursorX + 1, k + 1 + 9, -3092272);
            } else {
                guiGraphics.drawString(themeFont, "_", cursorX, k, color, textShadow);
            }
        }

        if (highlightViewPos != i) {
            int highlightPixelX = j + themeFont.width(s.substring(0, highlightViewPos));
            renderHighlightThemed(guiGraphics, cursorX, k - 1, highlightPixelX - 1, k + 1 + 9);
        }
    }

    private void renderHighlightThemed(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
        if (minX < maxX) {
            int t = minX;
            minX = maxX;
            maxX = t;
        }
        if (minY < maxY) {
            int t = minY;
            minY = maxY;
            maxY = t;
        }
        if (maxX > getX() + getWidth()) {
            maxX = getX() + getWidth();
        }
        if (minX > getX() + getWidth()) {
            minX = getX() + getWidth();
        }
        guiGraphics.fill(RenderType.guiTextHighlight(), minX, minY, maxX, maxY, -16776961);
    }
}