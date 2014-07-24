package com.williameze.minegicka3.main.entities.monsters.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.DrawHelper;
import com.williameze.minegicka3.main.Values;
import com.williameze.minegicka3.main.entities.monsters.model.ModelEntity888;

public class RenderEntity888 extends RenderLiving
{
    public ModelEntity888 model = new ModelEntity888();

    public RenderEntity888()
    {
	super(null, 0);
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z, float yaw, float partialTick)
    {
	if (var1 instanceof IBossDisplayData) BossStatus.setBossStatus((IBossDisplayData) var1, false);
	GL11.glPushMatrix();
	GL11.glTranslated(x, y, z);

	model.render(var1, partialTick);

	GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1)
    {
	return null;
    }

}
