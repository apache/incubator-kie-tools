package org.kie.lienzo.client;

import org.kie.lienzo.client.util.Util;

import com.ait.lienzo.client.core.shape.QuadraticCurve;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineCap;

public class QuadraticCurveExample extends BaseShapesExample<QuadraticCurve> implements Example {
	
	public QuadraticCurveExample(String title) {
		super(title);
		this.setPaddings(-50, 20, 120, 100);
		numberOfShapes = 30;
		shapes = new QuadraticCurve[numberOfShapes];
	}
	
	@Override
	public void run() {
		final int strokeWidth = Util.randomNumber(2, 10);  
		for (int i = 0; i < numberOfShapes; i++) {  
			shapes[i] = new QuadraticCurve(130, 130, 200, 0, 230, 130);  
			shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setDraggable(true).setLineCap(LineCap.ROUND);  
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
}
