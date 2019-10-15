package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.TextAlign;
import org.kie.lienzo.client.BaseExample;

public class StrokeAndFillingExample extends BaseExample implements Example
{
    private Circle[] circles;
    private Text[]   texts;


    private int      total = 3;

    public StrokeAndFillingExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        final String strokeColor = Color.getRandomHexColor();
        final String fillColor = Color.getRandomHexColor();
        Text text;

        circles = new Circle[total];
        texts = new Text[total];

        for (int i = 0; i < total; i++) {

            final Circle circle = new Circle(60);
            circles[i] = circle;

            if (i == 0) {
                text = new Text("Stroke", "oblique normal bold", 24).setTextAlign(TextAlign.CENTER).setStrokeColor(strokeColor)
                                                                    .setStrokeWidth(2);
                texts[i] = text;
                circle.setStrokeColor(strokeColor).setStrokeWidth(2);
            } else if (i == 1) {
                text = new Text("Fill", "oblique normal bold", 24).setTextAlign(TextAlign.CENTER).setFillColor(fillColor);
                texts[i] = text;
                circle.setFillColor(fillColor);
            } else if (i == 2) {
                text = new Text("Stroke & Fill", "oblique normal bold", 24).setTextAlign(TextAlign.CENTER)
                                                                           .setStrokeColor(strokeColor).setStrokeWidth(2).setFillColor(fillColor);
                texts[i] = text;
                circle.setFillColor(fillColor).setStrokeColor(strokeColor).setStrokeWidth(2);
            } else {
                throw new RuntimeException();
            }
            layer.add(circle);
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
        int          x = (int) (width * .25);
        int          y = (int) (height * .50);

        for (int i = 0; i < circles.length; i++) {
            int   xOffSet = x * (i+1);
            Shape shape   = circles[i];
            shape.setX(xOffSet).setY(y);

            final Text text   = texts[i];
            text.setX(xOffSet).setY(y - 70);
        }
    }
}
