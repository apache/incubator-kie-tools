package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.lienzo.client.BaseExample;

public class PanAndZoomExample extends BaseExample implements Example
{
    private Shape[]   shapes;

    private int      total = 50;

    public PanAndZoomExample(final String title)
    {
        super(title);
        topPadding = 100;
    }

    @Override
    public void run()
    {

        shapes = new Shape[total];
        for (int i = 0; i < total; i++) {

            final int strokeWidth = 1;

            final Rectangle rectangle = new Rectangle(Math.random() * 220, Math.random() * 220);
            rectangle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            layer.add(rectangle);
            shapes[i] = rectangle;

            Text text = new Text("Press Shift and use your mouse wheel to zoom in and out!");
            text.setX(5);
            text.setY(40);
            text.setFontFamily("Verdana");
            text.setFontSize(24);
            text.setFillColor(ColorName.BLACK);
            layer.add(text);

            text = new Text("Press Cmd(Meta) and use your mouse button to pan!");
            text.setX(5);
            text.setY(75);
            text.setFontFamily("Verdana");
            text.setFontSize(24);
            text.setFillColor(ColorName.BLACK);
            layer.add(text);

        }

        setLocation();

        layer.draw();
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
        for (int i = 0; i < shapes.length; i++) {
            final Shape shape =  shapes[i];
            setRandomLocation(shape);
        }
    }
}
