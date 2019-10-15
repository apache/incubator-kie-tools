package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import org.kie.lienzo.client.BaseExample;

public class VerticalTextAlignmentExample extends BaseExample implements Example
{
    private Rectangle[]   markers;
    private Text[]   texts;

    private int      total = TextBaseLine.values().length;

    public VerticalTextAlignmentExample(final String title)
    {
        super(title);
        topPadding = 100;
    }

    @Override
    public void run()
    {

        markers = new Rectangle[total];
        texts = new Text[total];
        final int fontSize = 24;

        int i = 0;
        for (TextBaseLine align : TextBaseLine.values()) {

            Rectangle marker = new Rectangle(7, 7);
            marker.setFillColor(ColorName.RED.getValue());
            markers[i] = marker;

            Text text = new Text(align.getValue().toUpperCase(), "oblique normal bold", fontSize);
            text.setFillColor(ColorName.CORNFLOWERBLUE.getValue()).setTextBaseLine(align);
            texts[i] = text;

            layer.add(marker);
            layer.add(text);

            i++;
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
        int x = width / 2;
        int y = height / 4;
        int fontSize = 24;

        for (int i = 0; i < total; i++) {
            Rectangle marker = markers[i];
            Text text = texts[i];

            marker.setX(x).setY(y);
            text.setX(x).setY(y) ;

            y += fontSize * 3;
        }
    }
}
