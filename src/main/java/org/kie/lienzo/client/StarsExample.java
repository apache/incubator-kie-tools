package org.kie.lienzo.client;

import org.kie.lienzo.client.util.Util;

import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.shared.core.types.Color;

public class StarsExample extends BaseShapesExample<Star> implements Example {
	
	public StarsExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 40;
		shapes = new Star[numberOfShapes];
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
            final int strokeWidth = Util.randomNumber(2, 10);  
            shapes[i] = new Star((int) (Math.random() * 10), 25, 50);  
            shapes[i].setStrokeColor(Color.getRandomHexColor())  
                    .setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);  
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
}
