package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.shared.core.types.Color;
import org.kie.lienzo.client.BaseExample;

public class ColorsAndTransparencyExample extends BaseExample implements Example
{
    private Shape[] shapes;

    private int     total = 10;

    public ColorsAndTransparencyExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        shapes = new Shape[total];

        // create the rectangles
        for (int i = 0; i < total; i++) {
            final Rectangle rectangle = new Rectangle(120, 60);
            rectangle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(1).setFillColor(Color.getRandomHexColor())
                     .setAlpha(Math.random() * 1).setDraggable(true);
            layer.add(rectangle);
            shapes[i] = rectangle;
        }

        setLocation();
    }

    @Override
    public void onResize()
    {
        super.onResize();

        setLocation();

        layer.batch();
    }

    private void setLocation()
    {
        int xOffSet = 40;
        int yOffSet = 40;

        int x = width  / 2 - (xOffSet * total/2);
        int y = height / 2 - (yOffSet * total/2);

        for (int j = 0; j < shapes.length; j++) {
            final Shape shape = shapes[j];
            shape.setX(x).setY(y);
            x += xOffSet;
            y += yOffSet;
        }
    }
}
