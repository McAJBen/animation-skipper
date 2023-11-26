package com.mcajben.animation_skipper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AnimationSkipperPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AnimationSkipperPlugin.class);
		RuneLite.main(args);
	}
}