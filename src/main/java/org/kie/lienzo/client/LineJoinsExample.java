package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineJoin;

public class LineJoinsExample extends BaseShapesExample<Star> implements Example {
	
	public LineJoinsExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 15;
		shapes = new Star[numberOfShapes];
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
			if (i % 3 == 0) {
				shapes[i] = new Star(5, 30, 80);  
				shapes[i].setStrokeColor(Color.getRandomHexColor()).setFillColor(Color.getRandomHexColor())  
                .setLineJoin(LineJoin.BEVEL).setStrokeWidth(15).setDraggable(true);  
			} else if (i % 3 == 1) {
				shapes[i] = new Star(10, 30, 80);  
				shapes[i].setStrokeColor(Color.getRandomHexColor()).setFillColor(Color.getRandomHexColor())  
                .setLineJoin(LineJoin.MITER).setStrokeWidth(15).setDraggable(true);  
			} else if (i % 3 == 2) {
				shapes[i] = new Star(7, 30, 80);  
				shapes[i].setStrokeColor(Color.getRandomHexColor()).setFillColor(Color.getRandomHexColor())  
                .setLineJoin(LineJoin.ROUND).setStrokeWidth(15).setDraggable(true);  
			}
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
}
