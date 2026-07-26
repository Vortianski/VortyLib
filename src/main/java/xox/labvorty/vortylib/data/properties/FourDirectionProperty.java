package xox.labvorty.vortylib.data.properties;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum FourDirectionProperty implements StringRepresentable {
    N("0"),
    E("90"),
    S("180"),
    W("270");

    private final String name;

    private FourDirectionProperty(String name) {
        this.name = name;
    }

    public FourDirectionProperty increment() {
        return switch (this.name) {
            case "0" -> E;
            case "90" -> S;
            case "180" -> W;
            case "270" -> N;

            default -> N;
        };
    }

    public String toString() {
        return this.name;
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}
