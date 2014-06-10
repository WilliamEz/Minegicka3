package com.williameze.api.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.williameze.api.lib.NoiseGen2D;
import com.williameze.api.math.IntVector;
import com.williameze.api.math.Vector;

public class Sphere extends ModelObject
{
    public double orgX, orgY, orgZ;
    public double radius;
    public int stacks;
    public int slices;
    public List<Triangle> faces = new ArrayList();
    public List<Triangle> distortedFaces = new ArrayList();
    public Map<Vector, IntVector> vectorFlatmap = new HashMap();
    public NoiseGen2D currentNoiseMap;

    public Sphere(double orX, double orY, double orZ, double r, int stack, int slice)
    {
	orgX = orX;
	orgY = orY;
	orgZ = orZ;
	radius = r;
	stacks = stack;
	slices = slice;
	calculateVectexes();
    }

    public void calculateVectexes()
    {
	vectorFlatmap.clear();
	double vertRotAngle = Math.PI / stacks;
	double horzRotAngle = Math.PI * 2 / slices;
	Vector vert = new Vector(0, radius, 0);
	for (int a = 0; a < stacks; a++)
	{
	    for (int b = 0; b < slices; b++)
	    {
		Vector v1 = vert.rotateAround(Vector.unitX, a * vertRotAngle).rotateAround(Vector.unitY, b * horzRotAngle);
		Vector v2 = vert.rotateAround(Vector.unitX, (a + 1) * vertRotAngle).rotateAround(Vector.unitY, b * horzRotAngle);
		Vector v3 = vert.rotateAround(Vector.unitX, a * vertRotAngle).rotateAround(Vector.unitY, (b + 1) * horzRotAngle);
		Vector v4 = vert.rotateAround(Vector.unitX, (a + 1) * vertRotAngle).rotateAround(Vector.unitY, (b + 1) * horzRotAngle);

		if (a < stacks - 1)
		{
		    Triangle tri1 = new Triangle(v1, v2, v4);
		    faces.add(tri1);
		}
		if (a > 0)
		{
		    Triangle tri2 = new Triangle(v1, v3, v4);
		    faces.add(tri2);
		}
		if (!vectorFlatmap.containsKey(v1)) vectorFlatmap.put(v1, new IntVector(b, a, 0));
		if (!vectorFlatmap.containsKey(v2)) vectorFlatmap.put(v2, new IntVector(b, a + 1, 0));
		if (!vectorFlatmap.containsKey(v3)) vectorFlatmap.put(v3, new IntVector(b + 1, a, 0));
		if (!vectorFlatmap.containsKey(v4)) vectorFlatmap.put(v4, new IntVector(b + 1, a + 1, 0));
	    }
	}
	distortedFaces.addAll(faces);
    }

    public void applyNoiseMap(NoiseGen2D noise)
    {
	currentNoiseMap = noise;
	distortedFaces.clear();
	int noiseRangeX = currentNoiseMap.rangeX;
	int noiseRangeY = currentNoiseMap.rangeY;
	for (Triangle tri : faces)
	{
	    Vector v1 = tri.v1.copy();
	    IntVector iv1 = vectorFlatmap.get(tri.v1);
	    double value1 = noise.noises[iv1.x % noiseRangeX][iv1.y % noiseRangeY];
	    v1.setToLength(v1.lengthVector() + value1);

	    Vector v2 = tri.v2.copy();
	    IntVector iv2 = vectorFlatmap.get(tri.v2);
	    double value2 = noise.noises[iv2.x % noiseRangeX][iv2.y % noiseRangeY];
	    v2.setToLength(v2.lengthVector() + value2);

	    Vector v3 = tri.v3.copy();
	    IntVector iv3 = vectorFlatmap.get(tri.v3);
	    double value3 = noise.noises[iv3.x % noiseRangeX][iv3.y % noiseRangeY];
	    v3.setToLength(v3.lengthVector() + value3);

	    Triangle newTri = new Triangle(v1, v2, v3, tri.normal, false);
	    distortedFaces.add(newTri);
	}
    }

    @Override
    public void render()
    {
	GL11.glPushMatrix();
	GL11.glTranslated(orgX, orgY, orgZ);
	begin(GL11.GL_TRIANGLES);
	glSetColor();
	for (Triangle t : distortedFaces)
	{
	    t.addTriangleToGL();
	}
	end();
	glResetColor();
	GL11.glPopMatrix();
    }

}
