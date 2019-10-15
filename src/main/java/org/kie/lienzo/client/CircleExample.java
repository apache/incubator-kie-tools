package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.shared.core.types.Color;
import org.kie.lienzo.client.util.Util;

public class CircleExample extends BaseShapesExample<Circle> implements Example {
	
	public CircleExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 40;
		shapes = new Circle[numberOfShapes];
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
			shapes[i] = new Circle(Util.randomNumber(8, 10));
			shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor()).setDraggable(true);  
            layer.add(shapes[i]);  
        }  
		setLocation();
		layer.draw();
	}
}
