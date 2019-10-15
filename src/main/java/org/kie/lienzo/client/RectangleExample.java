package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.Color;

public class RectangleExample extends BaseShapesExample<Rectangle> implements Example {
	
	public RectangleExample(String title) {
		super(title);
		this.numberOfShapes = 30;
		this.setPaddings(5, 20, 5, 100);
		shapes = new Rectangle[numberOfShapes];
	}
	
	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
            final int strokeWidth = 1;  
            shapes[i] = new Rectangle(Math.random() * 220, Math.random() * 160) 
            .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);  
            layer.add(shapes[i]);  
        }
		 setLocation();
		 layer.draw();
	}
}
