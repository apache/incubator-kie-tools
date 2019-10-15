package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.tools.client.Timer;
import elemental2.dom.TextMetrics;
import org.kie.lienzo.client.BaseExample;

public class MeasureTextExample extends BaseExample implements Example
{
    private final String       APPEND_TO_TEXT = ". Cursor at the end of the line ";

    private       TextSample[] samples = new TextSample[5];

    private Timer timer;

    public MeasureTextExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        samples[0] = new TextSample("Regular text" + APPEND_TO_TEXT, ColorName.DARKMAGENTA.getValue(), 100, 100);
        samples[1] = new TextSample("Scaled down text" + APPEND_TO_TEXT, ColorName.CHOCOLATE.getValue(), 100, 120);
        samples[2] = new TextSample("Rotated text" + APPEND_TO_TEXT, ColorName.DARKBLUE.getValue(), 100, 150);
        samples[3] = new TextSample("Sheared text" + APPEND_TO_TEXT, ColorName.RED.getValue(), 100, 200);
        samples[4] = new TextSample("Inversed text" + APPEND_TO_TEXT, ColorName.FORESTGREEN.getValue(), -800, 250);

        samples[1].setScale(0.5);
        samples[2].setRotation(Math.PI / 6);
        samples[3].setShear(1.2, 0);
        samples[4].setScale(-1, 1);

        layer.add(samples[0]);
        layer.add(samples[1]);
        layer.add(samples[2]);
        layer.add(samples[3]);
        layer.add(samples[4]);

        timer = new Timer() {
            @Override
            public void run() {
                for (TextSample s : samples) {
                    s.blink();
                }
                layer.batch();
            }
        };
        timer.scheduleRepeating(500);

        layer.draw();
    }

    public static class TextSample extends Group
    {
        private        String           color;
        private Text text      = null;
        private Line line      = null;
        private        int              m_x, m_y;
        private boolean show;

        public TextSample(String textString, String color, int x, int y) {
            m_x = x;
            m_y = y;
            this.color = color;
            text = new Text(textString, "oblique normal bold", 24);
            text.setX(x).setY(y).setTextAlign(TextAlign.LEFT).setFillColor(color);
            add(text);
        }

        public void blink() {
            if (line == null) {

                Text temp = new Text("M", "oblique normal bold", 24);
                TextMetrics tempMetrics = temp.measure(getLayer().getContext());
                double height = tempMetrics.width;

                TextMetrics textMetric = text.measure(getLayer().getContext());
                final int textXOffSet = (int) (m_x + textMetric.width);

                line = new Line(textXOffSet, m_y, textXOffSet, m_y - height);
                line.setFillColor(color).setStrokeColor(color);
                add(line);
            }

            show = !show;
            line.setVisible(show);
        }
    }
}
