package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;

public class BasicShapesExample extends BaseExample implements Example {

    public BasicShapesExample(final String title) {
        super(title);
    }

    @Override
    public void run() {

        Rectangle rectangle = new Rectangle(100, 100)
                .setX(300)
                .setY(100)
                .setFillColor(ColorName.LIGHTGREY)
                .setStrokeColor(ColorName.BLACK)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        layer.add(rectangle);

        Circle circle = new Circle(50)
                .setX(600)
                .setY(150)
                .setFillColor(ColorName.LIGHTBLUE)
                .setStrokeColor(ColorName.DARKBLUE)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        layer.add(circle);

        layer.draw();
    }
}
