package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.shared.core.types.Color;
import org.kie.lienzo.client.BaseExample;

public class DragCirclesExample extends BaseExample implements Example
{
    private Circle[] circles;
    int total = 1000;

    public DragCirclesExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        this.circles = new Circle[total];

        for (int i = 0; i < total; i++) {

            final Circle circle = new Circle(10);
            circles[i] = circle;
            circle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            setRandomLocation(circle);

            layer.add(circle);

        }
    }

    @Override public void onResize()
    {
        super.onResize();

        for (int i = 0; i < total; i++) {

            final Circle circle = circles[i];
            setRandomLocation(circle);
        }

        layer.batch();
    }
}
