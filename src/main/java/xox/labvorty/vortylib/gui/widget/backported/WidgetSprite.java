package xox.labvorty.vortylib.gui.widget.backported;

public record WidgetSprite(
    NineSliceSprite enabled,
    NineSliceSprite disabled,
    NineSliceSprite enabledFocused,
    NineSliceSprite disabledFocused
) {}