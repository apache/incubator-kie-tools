package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Shape;

public abstract class BaseShapesExample<T extends Shape<T>> extends BaseExample implements Example {

	protected Shape<T>[] shapes;
	
	protected int numberOfShapes = 10; // Default
	protected boolean ignoreLocation = false;
	
	public BaseShapesExample(String title) {
		super(title);
	}
	
	protected void setPaddings(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
		this.leftPadding = leftPadding;
		this.topPadding = topPadding;
		this.rightPadding = rightPadding;
		this.bottomPadding = bottomPadding;
	}
	
	protected void setLocation() {
    	console.log("Random Location for " + this.getClass().getName() +  "--->");
        for (int i = 0; i < shapes.length; i++) {
            setRandomLocation(shapes[i]);
        }
    }
	
	public void destroy() {
        super.destroy();
    }
	
	@Override
    public void onResize() {
        super.onResize();
        if (!ignoreLocation) {
        	setLocation();
        }
        layer.batch();
    }

}
