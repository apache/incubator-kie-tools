package org.kie.lienzo.client;

import org.kie.lienzo.client.util.Util;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.shared.core.types.Color;

public class GroupsExample extends BaseShapesExample<Star> implements Example {
	
	public GroupsExample(String title) {
		super(title);
		this.setPaddings(20, 20, 30, 100);
		numberOfShapes = 5;
		shapes = new Star[numberOfShapes];
	}

	@Override
	public void run() {
		final Group group = new Group();  
        group.setDraggable(true);  
  
        for (int i = 0; i < 5; i++) {  
            final int strokeWidth = Util.randomNumber(2, 10);  
            shapes[i] = new Star((int) (Math.random() * 10), 25, 50);  
            shapes[i].setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor());  
            group.add(shapes[i]);  
        }  
        setLocation();
        layer.add(group);
	}
}
