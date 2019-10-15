package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.tools.client.Timer;
import org.kie.lienzo.client.BaseExample;

public class TimersExample extends BaseExample implements Example
{
    private Shape[] shapes;
    public TimersExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        shapes = new Shape[5];

        int x = width / 2;

        Text text1 = new Text("Click to hide and show once.", "oblique normal", 16)
                .setTextAlign(TextAlign.CENTER).setStrokeColor(ColorName.BLACK).setStrokeWidth(2)
                .setX(x).setY(80);
        layer.add(text1);
        shapes[0] = text1;

        final Circle circ1 = new Circle(50);
        circ1.setX(x).setY(150);
        circ1.setFillColor(ColorName.YELLOWGREEN);
        circ1.setStrokeColor(ColorName.YELLOWGREEN);
        circ1.setDraggable(true);
        layer.add(circ1);
        shapes[1] = circ1;

        final Timer scheduledTimer1 = new Timer() {
            @Override
            public void run() {
                circ1.setVisible(false);
                layer.batch();
                Timer scheduledTimer2 = new Timer() {
                    @Override
                    public void run() {
                        circ1.setVisible(true);
                        layer.batch();

                    }
                };
                scheduledTimer2.schedule(1000);
            }
        };

        circ1.addNodeMouseClickHandler((e) ->{
            scheduledTimer1.schedule(1000);
        });

        Text text2 = new Text("Click to repeat hide and show.", "oblique normal", 16)
                .setTextAlign(TextAlign.CENTER).setStrokeColor(ColorName.BLACK).setStrokeWidth(2)
                .setX(x).setY(250);
        layer.add(text2);
        shapes[2] = text2;

        Text text3 = new Text("Click again to stop.", "oblique normal", 16)
                .setTextAlign(TextAlign.CENTER).setStrokeColor(ColorName.BLACK).setStrokeWidth(2)
                .setX(x).setY(280);
        layer.add(text3);
        shapes[3] = text3;


        final Circle circ2 = new Circle(50);
        circ2.setX(x).setY(350);
        circ2.setFillColor(ColorName.BLUEVIOLET );
        circ2.setStrokeColor(ColorName.BLUEVIOLET);
        circ2.setDraggable(true);
        layer.add(circ2);
        shapes[4] = circ2;

        final Timer intervalTimer1 = new Timer() {
            @Override
            public void run()
            {
                circ2.setVisible(!circ2.isVisible());
                layer.batch();
            }
        };
        circ2.addNodeMouseClickHandler((e) ->{
            if (intervalTimer1.isRunning())
            {
                intervalTimer1.cancel();
            }
            else
            {
                intervalTimer1.scheduleRepeating(1000);
            }

        });

        layer.batch();

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
        int x = width  / 2;

        for (int j = 0; j < shapes.length; j++) {
            final Shape shape = shapes[j];
            shape.setX(x);
        }
    }
}
