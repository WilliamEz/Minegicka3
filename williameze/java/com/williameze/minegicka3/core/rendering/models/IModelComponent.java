package com.williameze.minegicka3.core.rendering.models;

import net.minecraft.client.renderer.Tessellator;

public interface IModelComponent
{
    public static Tessellator tess = Tessellator.instance;

    public void render();
}
