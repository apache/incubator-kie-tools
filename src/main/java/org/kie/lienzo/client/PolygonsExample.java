package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.RegularPolygon;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.LineJoin;
import org.kie.lienzo.client.util.Util;

public class PolygonsExample extends BaseShapesExample<RegularPolygon> implements Example {
	
	public PolygonsExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 40;
		shapes = new RegularPolygon[numberOfShapes];
	}
	
	@Override
	public void run() {
		for (int i = 0; i < numberOfShapes; i++) {  
			final int strokeWidth = Util.randomNumber(2, 10);
	        
			shapes[i] = new RegularPolygon(8, 60);  
			shapes[i].setShadow(new Shadow("black", 6, 6, 6)).setFillColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth)  
                    .setStrokeColor(Color.getRandomHexColor()).setLineJoin(LineJoin.ROUND)
                    .setDraggable(true);  
            layer.add(shapes[i]);  
        }  
		setLocation();
	}
	
}
