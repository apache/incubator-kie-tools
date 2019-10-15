package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Arc;
import com.ait.lienzo.shared.core.types.Color;
import org.kie.lienzo.client.util.Util;

public class ArcsExample extends BaseShapesExample<Arc> implements Example {

	public ArcsExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 30;
		shapes = new Arc[numberOfShapes];
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
			shapes[i] = new Arc((int) (Util.randomNumber(10, 10)), 0, (Math.PI * 2) / 2);
			shapes[i] .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor()).setDraggable(true)  
                    .setRotationDegrees(Util.randomNumber(3, 10));  
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
}
