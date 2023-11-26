package com.mcajben.animation_skipper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ClientTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(name = "Animation Skipper")
public class AnimationSkipperPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    @Inject
    private AnimationSkipperConfig config;
    @Inject
    private AnimationSkipperOverlay overlay;
    @Inject
    private AnimationCache cache;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        final Player player = client.getLocalPlayer();
        if (player == null) return;

        final int playerAnimation = player.getAnimation();
        final boolean visible = cache.onClientTick(playerAnimation);
        this.overlay.setVisible(visible);
    }

    @Provides
    AnimationSkipperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimationSkipperConfig.class);
    }
}
