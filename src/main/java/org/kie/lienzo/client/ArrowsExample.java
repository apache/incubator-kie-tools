package org.kie.lienzo.client;

import org.kie.lienzo.client.util.Util;

import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineJoin;

public class ArrowsExample extends BaseShapesExample<Arrow> implements Example {
	
	public ArrowsExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 40;
		shapes = new Arrow[numberOfShapes];
	}

	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
            
            double x1 = Util.randomDoubleBetween(0, width);  
            double y1 = Util.randomDoubleBetween(0, height);  
              
            double len = Util.randomDoubleBetween(20, 200);  
            double a = Util.randomDoubleBetween(0, 2 * Math.PI);  
              
            double x2 = x1 + len * Math.cos(a);  
            double y2 = y1 + len * Math.sin(a);  
                          
            double baseWidth = len * Util.randomDoubleBetween(0.05, 0.60);  
            double headWidth = baseWidth * Util.randomDoubleBetween(1.2, 1.5);  
            double arrowAngle = Util.randomDoubleBetween(25, 70);  
            double baseAngle = Util.randomDoubleBetween(15, 100 - arrowAngle);  
              
            shapes[i] = new Arrow(new Point2D(x1, y1), new Point2D(x2, y2),   
                    baseWidth, headWidth, arrowAngle, baseAngle,   
                    Util.randomValue(ArrowType.values()));  
              
            int strokeWidth = Util.randomIntBetween(1, 10);  
              
            shapes[i].setShadow(new Shadow("black", 6, 6, 6))  
                .setFillColor(Color.getRandomHexColor())  
                .setStrokeWidth(strokeWidth)  
                .setStrokeColor(Color.getRandomHexColor())  
                .setLineJoin(Util.randomValue(LineJoin.values()))  
                .setDraggable(true);  
              
            layer.add(shapes[i]);  
        }  
		setLocation();
		layer.draw();
	}
	
}
