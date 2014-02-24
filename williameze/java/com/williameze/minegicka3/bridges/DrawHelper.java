package com.williameze.minegicka3.bridges;

import net.minecraft.client.renderer.Tessellator;

public class DrawHelper
{
	public static void tessAddQuad(Tessellator tess, double x1, double y1, double x2, double y2, double u1, double v1, double u2, double v2)
	{
		if(x1>x2)
		{
			double x3 = x2;
			x2 = x1;
			x1 = x3;
		}
		if(y1>y2)
		{
			double y3 = y2;
			y2 = y1;
			y1 = y3;
		}
		if(u1>u2)
		{
			double u3 = u2;
			u2 = u1;
			u1 = u3;
		}
		if(v1>v2)
		{
			double v3 = v2;
			v2 = v1;
			v1 = v3;
		}
		tess.addVertexWithUV(x1, y2, 0, u1, v2);
		tess.addVertexWithUV(x2, y2, 0, u2, v2);
		tess.addVertexWithUV(x2, y1, 0, u2, v1);
		tess.addVertexWithUV(x1, y1, 0, u1, v1);
	}
}
