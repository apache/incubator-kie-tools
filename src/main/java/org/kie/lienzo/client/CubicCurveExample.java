package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.BezierCurve;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineCap;

public class CubicCurveExample extends BaseShapesExample<BezierCurve> implements Example {
	
	public CubicCurveExample(String title) {
		super(title);
		this.setPaddings(-0, 20, 50, 100);
		numberOfShapes = 30;
		shapes = new BezierCurve[numberOfShapes];
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
			shapes[i] = new BezierCurve(188, 130, 140, 10, 388, 10, 388, 170);  
			shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setDraggable(true).setLineCap(LineCap.ROUND);  
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
	
}
