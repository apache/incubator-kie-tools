package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.AbstractMultiPointShape;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Color;

public class LinesExample extends BaseShapesExample<Line> implements Example {
	
	public LinesExample(String title) {
		super(title);
		numberOfShapes = 10;
		shapes = new Line[numberOfShapes];
		ignoreLocation = true;
	}
	
	@Override
	public void run() {
		final double x1 = width * 0.25;  
        double y1 = height * 0.15;  
          
        final double x2 = width * 0.75;  
        double y2 = height * 0.15;  
  
        for (int i = 0; i < numberOfShapes; i++) {  
            shapes[i] = new Line(x1,y1, x2, y2);  
            shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(1+i).setFillColor(Color.getRandomHexColor());  
            layer.add(shapes[i]);  
            y1 += 50;  
            y2 += 50;  
        }  
	}
	
	    @Override
	    public void onResize() {
	        super.onResize();
	    
	        console.log("ReDrawing Lines on Resize...>>");
	    
	        final double x1 = width * 0.25;  
            double y1 = height * 0.15;  
              
            final double x2 = width * 0.75;  
            double y2 = height * 0.15;  
	        
	        for (Shape<Line> line: shapes) {
	        	Point2D p1 = new Point2D(x1, y1);
	        	Point2D p2 = new Point2D(x2, y2);
	        	((AbstractMultiPointShape<Line>) line).setPoint2DArray(Point2DArray.fromArrayOfPoint2D(p1, p2));
	        	y1 += 50;  
	            y2 += 50;  
	        }
	        layer.batch();
	    }
}
