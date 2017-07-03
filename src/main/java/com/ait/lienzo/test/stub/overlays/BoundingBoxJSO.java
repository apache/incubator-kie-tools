package com.ait.lienzo.test.stub.overlays;

import com.ait.lienzo.test.annotation.StubClass;
import com.google.gwt.core.client.JavaScriptObject;

@StubClass("com.ait.lienzo.client.core.types.BoundingBox$BoundingBoxJSO")
public class BoundingBoxJSO extends JavaScriptObject
{
	private double minx;

    private double miny;
    
    private double maxx;

    private double maxy;
	
	protected BoundingBoxJSO()
    {
    }

    protected BoundingBoxJSO(double minx, double miny, double maxx, double maxy)
    {
    	this.minx = minx;
    	this.miny = miny;
    	this.maxx = maxx;
    	this.maxy = maxy;
    	
    }

    public static BoundingBoxJSO make(double minx, double miny, double maxx, double maxy)
    {
        return new BoundingBoxJSO(minx, miny, maxx, maxy);
    }
    
    public double getMinX()
    {
		return this.minx;
    }

    public double getMinY()
    {
		return this.miny;
    }

    public double getMaxX()
    {
		return this.maxx;
    }

    public double getMaxY()
    {
		return this.maxy;
    }

    public void addX(double x)
    {
		if (x < this.minx) 
		{
			this.minx = x;
		}
		if (x > this.maxx) 
		{
			this.maxx = x;
		}
    }

    public void addY(double y)
    {
    	if (y < this.miny) 
    	{
    		this.miny = y;
    	}
		if (y > this.maxy) 
		{
			this.maxy = y;
		}
    }
}