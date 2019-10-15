package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Ellipse;
import com.ait.lienzo.shared.core.types.Color;

public class EllipseExample extends BaseShapesExample<Ellipse> implements Example {

	public EllipseExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 40;
		shapes = new Ellipse[numberOfShapes];
	}

	@Override
	public void run() {
		final int strokeWidth = 1;  
		for (int i = 0; i < numberOfShapes; i++) {  
			shapes[i] = new Ellipse(Math.random() * 160, Math.random() * 80);  
			shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);  
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
}
