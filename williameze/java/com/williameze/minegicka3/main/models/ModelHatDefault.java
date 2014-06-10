package com.williameze.minegicka3.main.models;

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
	components.add(new Ring(new Vector(0, 0.609, 0), new Vector(0, 51, 0), 0.77, 0.8, 12).setColor(new Color(50, 50, 50)));
	components.add(new Ring(new Vector(0, 0.6095, 0), new Vector(0, 51, 0), 0.68, 0.8, 12).setColor(new Color(20, 0, 20)));
	components.add(new Ring(new Vector(0, 0.61, 0), new Vector(0, 51, 0), 0.28, 0.8, 12).setColor(Color.black));
	components.add(new Ring(new Vector(0, 0.6105, 0), new Vector(0, 51, 0), 0.28, 0.38, 12).setColor(Values.yellow));

	components.add(Cylinder.create(new Vector(0, 0.51, 0), new Vector(0, 0.8, 0), 0.3, 0.2, 12, 0).setColor(new Color(10, 10, 10)));
	components.add(Cylinder.create(new Vector(0, 0.8, 0), new Vector(0.1, 1.21, -0.1), 0.2, 0.08, 12, 1).setColor(new Color(10, 10, 10)));
	components.add(Cylinder.create(new Vector(0.1, 1.21, -0.1), new Vector(0.2, 1.38, -0.2), 0.08, 0, 12, 1).setColor(new Color(10, 10, 10)));
    }

    @Override
    public void render(Object obj, float f)
    {
	super.render(obj, f);
    }
}
