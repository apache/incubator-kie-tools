package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Arc;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Ellipse;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.RadialGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.Color;
import org.kie.lienzo.client.BaseExample;
import org.kie.lienzo.client.util.Util;

public class GradientsAndShadowsExample extends BaseExample implements Example
{
    private Shape[] shapes;
    private int     total = 4;

    public GradientsAndShadowsExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {

        shapes = new Shape[total * 5];

        int j = 0;
        for (int i = 0; i < total; i++) {

            final int strokeWidth = 1;

            final RadialGradient radialGradient = new RadialGradient(0, 0, 0, 0, 0, 40);
            radialGradient.addColorStop(0.0, Color.getRandomHexColor());
            radialGradient.addColorStop(1.0, Color.getRandomHexColor());

            final Circle circle = new Circle(Util.randomNumber(8, 10));
            circle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillGradient(radialGradient)
                  .setShadow(new Shadow(Color.getRandomHexColor(), 50, 0, 0)).setDraggable(true);
            setRandomLocation(circle);
            layer.add(circle);
            shapes[j++] = circle;

            final LinearGradient linearGradient = new LinearGradient(0, -50, 0, 50);
            linearGradient.addColorStop(0, Color.getRandomHexColor());
            linearGradient.addColorStop(0.30, Color.getRandomHexColor());
            linearGradient.addColorStop(1, Color.getRandomHexColor());

            final Rectangle rectangle = new Rectangle(Math.random() * 160, Math.random() * 100);
            rectangle.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillGradient(linearGradient)
                     .setShadow(new Shadow(Color.getRandomHexColor(), 50, 0, 0)).setDraggable(true);
            setRandomLocation(rectangle);
            layer.add(rectangle);
            shapes[j++] = rectangle;

            final Star star = new Star((int) (Math.random() * 10), 25, 50);
            star.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillGradient(linearGradient)
                .setShadow(new Shadow(Color.getRandomHexColor(), 20, 10, 10)).setDraggable(true);
            setRandomLocation(star);
            layer.add(star);
            shapes[j++] = star;

            final Arc arc = new Arc((int) (Math.random() * 80), 0, (Math.PI * 2) / 2);
            arc.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillGradient(radialGradient)
               .setShadow(new Shadow(Color.getRandomHexColor(), 50, 0, 0)).setDraggable(true);
            setRandomLocation(arc);
            layer.add(arc);
            shapes[j++] = arc;

            final Ellipse ellipse = new Ellipse(Math.random() * 120, Math.random() * 60);
            ellipse.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillGradient(linearGradient)
                   .setShadow(new Shadow(Color.getRandomHexColor(), 50, 0, 0)).setDraggable(true);
            setRandomLocation(ellipse);
            layer.add(ellipse);
            shapes[j++] = ellipse;

            layer.draw();
        }
    }

    @Override
    public void onResize()
    {
        super.onResize();

        for (int j = 0; j < shapes.length; j++) {
            final Shape shape = shapes[j];
            setRandomLocation(shape);
        }

        layer.batch();
    }
}
