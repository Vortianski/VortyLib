package xox.labvorty.vortylib.data.text;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public final class TextEffects {
    public static final String NAMESPACE = "vortylib";
    public static final String EFFECTS_KEY = "vl_effects";

    public enum Mode {
        ALL,
        NO_COLOR_CYCLING,
        NONE
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, Function<EffectParams, TextEffect>> FACTORIES =
            new ConcurrentHashMap<>();
    private static final long START_MILLIS = Util.getMillis();
    private static volatile Mode mode = Mode.ALL;

    static {
        BuiltinEffects.registerAll();
    }

    private TextEffects() {}

    public static void init() {}

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    public static ResourceLocation register(ResourceLocation type, Function<EffectParams, TextEffect> factory) {
        if (FACTORIES.putIfAbsent(type, factory) != null) {
            throw new IllegalStateException("Text effect already registered: " + type);
        }
        return type;
    }

    public static ResourceLocation register(ResourceLocation type, boolean colorful, Function<EffectParams, Consumer<GlyphState>> perGlyphFactory) {
        return register(type, params -> BaseTextEffect.of(type, params, colorful, perGlyphFactory.apply(params)));
    }

    public static Mode mode() {
        return mode;
    }

    public static void setMode(Mode newMode) {
        mode = newMode;
    }

    public static boolean shouldApply(TextEffect effect) {
        return switch (mode) {
            case ALL -> true;
            case NO_COLOR_CYCLING -> !effect.isColorful();
            case NONE -> false;
        };
    }

    public static float time() {
        return (Util.getMillis() - START_MILLIS) / 1000f;
    }

    @Nullable
    public static TextEffect parse(String spec) {
        String[] parts = spec.trim().split("\\s+", 2);
        if (parts.length == 0 || parts[0].isEmpty()) return null;
        ResourceLocation type = resolve(parts[0]);
        if (type == null) return null;
        Function<EffectParams, TextEffect> factory = FACTORIES.get(type);
        if (factory == null) return null;
        return factory.apply(parts.length > 1 ? EffectParams.parse(parts[1]) : EffectParams.EMPTY);
    }

    @Nullable
    static ResourceLocation resolve(String name) {
        return ResourceLocation.tryParse(name.contains(":") ? name : NAMESPACE + ":" + name);
    }

    public static List<TextEffect> parseAll(List<String> specs) {
        List<TextEffect> out = new ArrayList<>(specs.size());
        for (String spec : specs) {
            TextEffect effect = parse(spec);
            if (effect == null) {
                LOGGER.warn("Ignoring unknown text effect '{}'", spec);
            } else {
                out.add(effect);
            }
        }
        return out;
    }

    public static List<String> serializeAll(List<TextEffect> effects) {
        List<String> out = new ArrayList<>(effects.size());
        for (TextEffect effect : effects) out.add(effect.serialize());
        return out;
    }

    public static List<TextEffect> merge(List<TextEffect> parent, List<TextEffect> child) {
        if (child.isEmpty()) return parent;
        if (parent.isEmpty()) return child;
        List<TextEffect> out = new ArrayList<>(parent);
        for (TextEffect effect : child) {
            boolean replaced = false;
            for (int i = 0; i < out.size(); i++) {
                if (out.get(i).type().equals(effect.type())) {
                    out.set(i, effect);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) out.add(effect);
        }
        return List.copyOf(out);
    }

    public static MutableComponent apply(Component component, String... specs) {
        TextEffect[] effects = new TextEffect[specs.length];
        for (int i = 0; i < specs.length; i++) {
            TextEffect effect = parse(specs[i]);
            if (effect == null) throw new IllegalArgumentException("Unknown text effect: " + specs[i]);
            effects[i] = effect;
        }
        return apply(component, effects);
    }

    public static MutableComponent apply(Component component, TextEffect... effects) {
        MutableComponent copy = component.copy();
        Style style = copy.getStyle();
        List<TextEffect> merged = merge(StyleEffects.get(style), List.of(effects));
        return copy.setStyle(StyleEffects.withEffects(style, merged));
    }

    public static MutableComponent parseMarkup(String markup) {
        MutableComponent root = Component.empty();
        List<TextEffect> active = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        int i = 0;
        while (i < markup.length()) {
            char c = markup.charAt(i);
            if (c == '<') {
                int end = markup.indexOf('>', i + 1);
                if (end > i + 1 && handleTag(markup.substring(i + 1, end).trim(), active, root, text)) {
                    i = end + 1;
                    continue;
                }
            }
            text.append(c);
            i++;
        }
        flush(root, text, active);
        return root;
    }

    private static boolean handleTag(String tag, List<TextEffect> active, MutableComponent root, StringBuilder text) {
        if (tag.startsWith("/")) {
            ResourceLocation type = resolve(tag.substring(1).trim());
            if (type == null) return false;
            for (int k = active.size() - 1; k >= 0; k--) {
                if (active.get(k).type().equals(type)) {
                    flush(root, text, active);
                    active.remove(k);
                    return true;
                }
            }
            return false;
        }
        TextEffect effect = parse(tag);
        if (effect == null) return false;
        flush(root, text, active);
        active.add(effect);
        return true;
    }

    private static void flush(MutableComponent root, StringBuilder text, List<TextEffect> active) {
        if (text.isEmpty()) return;
        MutableComponent part = Component.literal(text.toString());
        if (!active.isEmpty()) {
            part.setStyle(StyleEffects.withEffects(Style.EMPTY, List.copyOf(active)));
        }
        root.append(part);
        text.setLength(0);
    }
}