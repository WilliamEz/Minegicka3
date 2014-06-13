package com.williameze.minegicka3.main.models.hat;

import java.awt.Color;

import com.williameze.api.math.Vector;
import com.williameze.api.models.Cylinder;
import com.williameze.api.models.Ring;
import com.williameze.minegicka3.main.Values;

public class ModelHatDefault extends ModelHat
{
    @Override
    public void addComponents()
    {
	Color c1 = new Color(10, 10, 10);

	components.add(Cylinder.create(new Vector(0, 0.40, 0), new Vector(0, 0.51, 0), 0.8, 0.3, 16, 0).setRenderCaps(false).setColor(c1));
	components.add(Cylinder.create(new Vector(0, 0.51, 0), new Vector(0, 0.8, 0), 0.3, 0.2, 16, 0).setRenderCaps(false).setColor(c1));
	components.add(Cylinder.create(new Vector(0, 0.8, 0), new Vector(0.1, 1.21, -0.1), 0.2, 0.08, 16, 1).setRenderCaps(false).setColor(c1));
	components.add(Cylinder.create(new Vector(0.1, 1.21, -0.1), new Vector(0.2, 1.38, -0.2), 0.08, 0, 16, 1).setRenderCaps(false).setColor(c1));

	components.add(Cylinder.create(new Vector(0, 0.5105, 0), new Vector(0, 0.5005, 0), 0.3, 0.34545, 16, 0).setRenderCaps(false)
		.setColor(Values.yellow));
	components.add(Cylinder.create(new Vector(0, 0.4195, 0), new Vector(0, 0.3995, 0), 0.71, 0.8, 16, 0).setRenderCaps(false)
		.setColor(Values.yellow));
    }

    @Override
    public void render(Object obj, float f)
    {
	super.render(obj, f);
    }
}
