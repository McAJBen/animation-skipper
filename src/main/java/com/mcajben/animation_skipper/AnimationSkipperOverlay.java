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
    private Instant lastFadeStart = Instant.MIN;
    private Duration lastFadeDuration = Duration.ZERO;
    private String displayText = "";

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
        final Instant now = Instant.now();
        float fadeProgress = getFadeProgress(now, lastFadeStart, lastFadeDuration);
        final float opacity = getOpacity(isVisible, fadeProgress);

        if (opacity > 0.01f) {
            final Color overlayColor = config.overlayColor();
            final Color textColor = config.textColor();
            final int textSize = config.textSize();
            graphics.setColor(new Color(overlayColor.getRed(), overlayColor.getGreen(), overlayColor.getBlue(), (int) (overlayColor.getAlpha() * opacity)));
            graphics.fill(new Rectangle(client.getCanvas().getSize()));

            graphics.setFont(new Font("Times New Roman", Font.BOLD, textSize));

            if (textColor.getAlpha() == 0) {
                // Use transparent text
                graphics.setComposite(AlphaComposite.DstOut);
                graphics.setColor(Color.white);
            } else {
                graphics.setColor(textColor);
            }

            drawStringCentered(graphics, client.getCanvasWidth(), client.getCanvasHeight(), displayText);
        }

        return null;
    }

    public void updateVisibility(boolean visible) {
        if (visible == this.isVisible) {
            // No change to visibility
            return;
        }

        final Instant now = Instant.now();

        final Duration fadeDuration = Duration.ofMillis(config.fadeDuration());
        final Duration debounceDuration = Duration.ofMillis(config.debounceDuration());

        final float previousProgressMade = getFadeProgress(now, lastFadeStart, lastFadeDuration);

        final Duration startingFadeDuration = Duration.ofMillis((long) (fadeDuration.toMillis() * (1.0f - previousProgressMade)));

        this.isVisible = visible;
        this.lastFadeDuration = fadeDuration;
        if (!this.isVisible && previousProgressMade >= 1.0f) {
            // add debounceDuration when fading away from 100% visible
            this.lastFadeStart = now.minus(startingFadeDuration).plus(debounceDuration);
        } else {
            this.lastFadeStart = now.minus(startingFadeDuration);
        }
        if (this.isVisible && previousProgressMade >= 1.0f) {
            // reset text only when text was just invisible
            this.displayText = DisplayTextFactory.getRandomText(this.displayText);
        }
    }

    /**
     * @param fadeStart    The {@link Instant} that the fade started or should start.
     * @param fadeDuration The {@link Duration} that the fade lasts for.
     * @return Double between 0.0 inclusive and 1.0 inclusive representing the fade's progress.
     * * 0.0 means the fade either hasn't started, or has just started.
     * * 1.0 means the fade is complete.
     */
    private static float getFadeProgress(Instant now, Instant fadeStart, Duration fadeDuration) {
        final Duration difference = Duration.between(fadeStart, now);
        if (difference.isNegative()) {
            // Fade has not started yet
            return 0.0f;
        }
        if (difference.compareTo(fadeDuration) >= 0) {
            // Fade is complete
            return 1.0f;
        }
        // Fade is happening, so compute what percent of time has passed
        return ((float) difference.toMillis()) / fadeDuration.toMillis();
    }

    /**
     * @param isVisible    true if the overlay is visible
     * @param fadeProgress {@link AnimationSkipperOverlay#getFadeProgress}
     * @return Double between 0.0 inclusive and 1.0 inclusive representing the overlay's opacity.
     */
    private static float getOpacity(boolean isVisible, float fadeProgress) {
        if (isVisible) {
            return fadeProgress;
        } else {
            return 1.0f - fadeProgress;
        }
    }


    private static void drawStringCentered(Graphics2D graphics, int canvasWidth, int canvasHeight, String text) {
        final FontMetrics metrics = graphics.getFontMetrics();
        final int width = metrics.stringWidth(text);
        final int height = metrics.getHeight();
        final int x = (canvasWidth - width) / 2;
        final int y = (canvasHeight + height) / 2;
        graphics.drawString(text, x, y);
    }
}
