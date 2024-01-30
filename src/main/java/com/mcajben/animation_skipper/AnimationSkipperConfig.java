package com.mcajben.animation_skipper;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("animationskipper")
public interface AnimationSkipperConfig extends Config {

    @Alpha
    @ConfigItem(
            keyName = "overlayColor",
            name = "Overlay color",
            description = "Color of the full screen overlay"
    )
    default Color overlayColor() {
        return new Color(0, 0, 0, 160);
    }

    @ConfigItem(
            keyName = "fadeDuration",
            name = "Fade duration",
            description = "Time for the overlay to fade in and out"
    )
    @Units(Units.MILLISECONDS)
    default int fadeDuration() {
        return 500;
    }

    @ConfigItem(
            keyName = "textSize",
            name = "Text Size",
            description = "Text Size"
    )
    @Range(min = 8, max = 96)
    default int textSize() {
        return 36;
    }

    @ConfigItem(
            keyName = "debounceDuration",
            name = "Debounce duration",
            description = "Time between animation ending and the overlay fading out. Increasing the debounce duration can prevent flickering."
    )
    @Units(Units.MILLISECONDS)
    default int debounceDuration() {
        return 1800;
    }
}
