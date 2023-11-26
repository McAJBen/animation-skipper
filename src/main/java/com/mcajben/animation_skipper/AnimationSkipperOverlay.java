package com.mcajben.animation_skipper;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class AnimationSkipperOverlay extends Overlay {
    private final Client client;
    private final AnimationSkipperConfig config;
    private boolean isVisible = false;
    private Instant lastVisibilityChange = Instant.MIN;

    @Inject
    private AnimationSkipperOverlay(AnimationSkipperPlugin plugin, Client client, AnimationSkipperConfig config) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        final Duration fadeDuration = Duration.ofMillis(config.fadeDuration());
        float fadeProgress = getFadeProgress(fadeDuration, lastVisibilityChange);
        final float opacity = getOpacity(isVisible, fadeProgress);

        if (opacity > 0.05f) {
            final Color overlayColor = config.overlayColor();
            graphics.setColor(new Color(
                    overlayColor.getRed(),
                    overlayColor.getGreen(),
                    overlayColor.getBlue(),
                    (int) (overlayColor.getAlpha() * opacity)
            ));
            graphics.fill(new Rectangle(client.getCanvas().getSize()));

            graphics.setFont(new Font("Times New Roman", Font.BOLD, 48));
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
            drawStringCentered(graphics, client.getCanvas(), "ONE HOUR LATER");
        }

        return null;
    }

    public void setVisible(boolean visible) {
        if (visible == this.isVisible) return;
        this.isVisible = visible;

        final Instant now = Instant.now();
        final Duration fadeDuration = Duration.ofMillis(config.fadeDuration());

        final Duration sinceLastChange = Duration.between(lastVisibilityChange, now);
        final Duration progressMade = fadeDuration.minus(sinceLastChange);
        if (progressMade.isNegative()) {
            this.lastVisibilityChange = now;
        } else {
            this.lastVisibilityChange = now.minus(progressMade);
        }
    }

    private static float getFadeProgress(Duration fadeDuration, Instant lastVisibilityChange) {
        if (fadeDuration.isZero()) return 1.0f;

        final Instant now = Instant.now();
        final Duration difference = Duration.between(lastVisibilityChange, now);
        if (difference.isNegative()) return 0.0f;
        if (difference.compareTo(fadeDuration) >= 0) return 1.0f;
        return ((float) difference.toMillis()) / fadeDuration.toMillis();
    }

    private static float getOpacity(boolean isVisible, float fadeProgress) {
        if (isVisible) {
            return fadeProgress;
        } else {
            return 1.0f - fadeProgress;
        }
    }

    private static void drawStringCentered(Graphics2D graphics, Canvas canvas, String text) {
        final FontMetrics metrics = graphics.getFontMetrics();
        final int width = metrics.stringWidth(text);
        final int height = metrics.getHeight();
        final int x = (canvas.getWidth() - width) / 2;
        final int y = canvas.getHeight() / 2 + height / 2;
        graphics.drawString(text, x, y);
    }
}
