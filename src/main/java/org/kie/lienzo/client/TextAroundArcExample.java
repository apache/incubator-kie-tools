package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.lienzo.client.BaseExample;

public class TextAroundArcExample extends BaseExample implements Example
{


    public TextAroundArcExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        final int x = (int) (width / 2);
        final int y = (int) (height /2);
        final int radius = 200;
        final double startAngle = Math.PI;
        final double endAngle = 0;
        double angle = startAngle;
        final String string = "Lienzo is so cooool!";
        final double angleDecrement = (startAngle - endAngle)/(string.length()-1);

        layer.add(new Circle(5).setFillColor(ColorName.CORNFLOWERBLUE.getValue()).setStrokeColor(ColorName.GRAY.getValue()).setX(x).setY(y).setShadow(new Shadow(ColorName.BLACK.getValue(), 20, 5, 5)));

        /**
         * Draw Text
         */
        for (int i=0; i<string.length(); i++) {
            Text text = new Text(string.substring(i, i+1));
            text.setX(x + Math.cos(angle) * radius);
            text.setY(y - Math.sin(angle) * radius);
            text.setRotation(Math.PI/2 - angle);

            text.setFontFamily("Lucida Sans");
            text.setFontSize(64);
            text.setFillColor(ColorName.CORNFLOWERBLUE.getValue());
            text.setStrokeColor(ColorName.GRAY.getValue());
            text.setStrokeWidth(2);
            text.setShadow(new Shadow("black", 20, 5, 5));

            layer.add(text);
            angle -= angleDecrement;
        }
        layer.draw();
    }

}
