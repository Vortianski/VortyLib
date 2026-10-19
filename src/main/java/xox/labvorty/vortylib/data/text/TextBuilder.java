package xox.labvorty.vortylib.data.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class TextBuilder {
    public static final class Params {
        private final StringBuilder out = new StringBuilder();

        public Params a(float value) {
            return set("a", value);
        }

        public Params f(float value) {
            return set("f", value);
        }

        public Params w(float value) {
            return set("w", value);
        }

        public Params set(String key, float value) {
            return raw(key, Float.toString(value));
        }

        public Params set(String key, boolean value) {
            return raw(key, Boolean.toString(value));
        }

        public Params color(String key, int rgb) {
            return raw(key, String.format("#%06X", rgb & 0xFFFFFF));
        }

        public Params raw(String key, String value) {
            out.append(' ').append(key).append('=').append(value);
            return this;
        }

        String serialize() {
            return out.toString();
        }
    }

    private static final class Segment {
        private final MutableComponent content;
        private final List<TextEffect> effects = new ArrayList<>();
        private final List<UnaryOperator<Style>> styles = new ArrayList<>();

        private Segment(MutableComponent content) {
            this.content = content;
        }
    }

    private final List<Segment> segments = new ArrayList<>();
    private final List<TextEffect> rootEffects = new ArrayList<>();

    private TextBuilder() {}

    public static TextBuilder create() {
        return new TextBuilder();
    }

    public static TextBuilder of(String text) {
        return create().text(text);
    }

    public static TextBuilder of(Component component) {
        return create().component(component);
    }

    public TextBuilder text(String text) {
        return segment(Component.literal(text));
    }

    public TextBuilder translatable(String key, Object... args) {
        return segment(Component.translatable(key, args));
    }

    public TextBuilder component(Component component) {
        return segment(component.copy());
    }

    public TextBuilder effect(String spec) {
        return effect(parseOrThrow(spec));
    }

    public TextBuilder effect(String type, Consumer<Params> params) {
        Params p = new Params();
        params.accept(p);
        return effect(type + p.serialize());
    }

    public TextBuilder effect(TextEffect effect) {
        put(current().effects, effect);
        return this;
    }

    public TextBuilder all(String... specs) {
        for (String spec : specs) put(rootEffects, parseOrThrow(spec));
        return this;
    }

    public TextBuilder all(TextEffect... effects) {
        for (TextEffect effect : effects) put(rootEffects, effect);
        return this;
    }

    public TextBuilder wave() {
        return effect("wave");
    }

    public TextBuilder wave(Consumer<Params> params) {
        return effect("wave", params);
    }

    public TextBuilder rainbow() {
        return effect("rainbow");
    }

    public TextBuilder rainbow(Consumer<Params> params) {
        return effect("rainbow", params);
    }

    public TextBuilder shake() {
        return effect("shake");
    }

    public TextBuilder shake(Consumer<Params> params) {
        return effect("shake", params);
    }

    public TextBuilder spin() {
        return effect("spin");
    }

    public TextBuilder spin(Consumer<Params> params) {
        return effect("spin", params);
    }

    public TextBuilder swing() {
        return effect("swing");
    }

    public TextBuilder swing(Consumer<Params> params) {
        return effect("swing", params);
    }

    public TextBuilder bounce() {
        return effect("bounce");
    }

    public TextBuilder bounce(Consumer<Params> params) {
        return effect("bounce", params);
    }

    public TextBuilder pulse() {
        return effect("pulse");
    }

    public TextBuilder pulse(Consumer<Params> params) {
        return effect("pulse", params);
    }

    public TextBuilder orbit() {
        return effect("orbit");
    }

    public TextBuilder orbit(Consumer<Params> params) {
        return effect("orbit", params);
    }

    public TextBuilder twitch() {
        return effect("twitch");
    }

    public TextBuilder twitch(Consumer<Params> params) {
        return effect("twitch", params);
    }

    public TextBuilder glitch() {
        return effect("glitch");
    }

    public TextBuilder glitch(Consumer<Params> params) {
        return effect("glitch", params);
    }

    public TextBuilder fire() {
        return effect("fire");
    }

    public TextBuilder fire(Consumer<Params> params) {
        return effect("fire", params);
    }

    public TextBuilder fade() {
        return effect("fade");
    }

    public TextBuilder fade(float minOpacity) {
        return effect("fade", p -> p.a(minOpacity));
    }

    public TextBuilder fade(Consumer<Params> params) {
        return effect("fade", params);
    }

    public TextBuilder grad() {
        return effect("grad");
    }

    public TextBuilder grad(int from, int to) {
        return effect("grad", p -> p.color("from", from).color("to", to));
    }

    public TextBuilder grad(Consumer<Params> params) {
        return effect("grad", params);
    }

    public TextBuilder style(UnaryOperator<Style> operator) {
        current().styles.add(operator);
        return this;
    }

    public TextBuilder color(int rgb) {
        return style(s -> s.withColor(rgb));
    }

    public TextBuilder format(ChatFormatting... formats) {
        return style(s -> s.applyFormats(formats));
    }

    public TextBuilder bold() {
        return style(s -> s.withBold(true));
    }

    public TextBuilder italic() {
        return style(s -> s.withItalic(true));
    }

    public TextBuilder underlined() {
        return style(s -> s.withUnderlined(true));
    }

    public TextBuilder strikethrough() {
        return style(s -> s.withStrikethrough(true));
    }

    public MutableComponent build() {
        MutableComponent root = Component.empty();
        for (Segment segment : segments) root.append(finish(segment));
        if (!rootEffects.isEmpty()) {
            Style style = root.getStyle();
            root.setStyle(StyleEffects.withEffects(style, TextEffects.merge(StyleEffects.get(style), rootEffects)));
        }
        return root;
    }

    private static MutableComponent finish(Segment segment) {
        MutableComponent component = segment.content.copy();
        Style style = component.getStyle();
        for (UnaryOperator<Style> operator : segment.styles) style = operator.apply(style);
        if (!segment.effects.isEmpty()) {
            style = StyleEffects.withEffects(style, TextEffects.merge(StyleEffects.get(style), segment.effects));
        }
        return component.setStyle(style);
    }

    private TextBuilder segment(MutableComponent content) {
        segments.add(new Segment(content));
        return this;
    }

    private Segment current() {
        if (segments.isEmpty()) throw new IllegalStateException("Add text before applying effects or styles");
        return segments.get(segments.size() - 1);
    }

    private static TextEffect parseOrThrow(String spec) {
        TextEffect effect = TextEffects.parse(spec);
        if (effect == null) throw new IllegalArgumentException("Unknown text effect: " + spec);
        return effect;
    }

    private static void put(List<TextEffect> list, TextEffect effect) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).type().equals(effect.type())) {
                list.set(i, effect);
                return;
            }
        }
        list.add(effect);
    }
}