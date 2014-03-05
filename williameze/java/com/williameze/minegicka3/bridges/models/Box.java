package com.williameze.minegicka3.bridges.models;

import java.util.ArrayList;
import java.util.List;

public class Box extends ModelObject
{
    public List<Quad> faces = new ArrayList();
    
    public Box(double minx, double miny, double minz, double maxx, double maxy, double maxz)
    {
	
    }

    @Override
    public void render()
    {
    }

}
