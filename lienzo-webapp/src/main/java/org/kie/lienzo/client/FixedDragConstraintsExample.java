package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Arc;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Ellipse;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.google.gwt.dom.client.Style.Display;
import elemental2.core.JsArray;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.kie.lienzo.client.util.Util;

import static elemental2.dom.DomGlobal.document;

public class FixedDragConstraintsExample extends BaseExample implements Example {

    private HTMLSelectElement select;
    JsArray<Shape> shapes;

    public FixedDragConstraintsExample(final String title) {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Display.INLINE_BLOCK.getCssName();

        select = (HTMLSelectElement) document.createElement("select");
        addOption("Horizontal", select);
        addOption("Vertical", select);
        HTMLOptionElement option = addOption("None", select);
        option.selected = true;

        topDiv.appendChild(select);

        select.onchange = (e) -> {
            setDragConstraint(DragConstraint.lookup(select.value.toLowerCase()));
            layer.batch();
            return null;
        };

        heightOffset = 30;
    }

    private HTMLOptionElement addOption(String value, HTMLSelectElement select) {
        HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.label = value;
        option.value = value;
        select.add(option);

        return option;
    }

    @Override
    public void run() {
        shapes = new JsArray<Shape>();

        for (int i = 0; i < 5; i++) {

            final int strokeWidth = Util.randomNumber(2, 10);

            final Circle circle = new Circle(Math.random() * 40);
            circle.setX(Util.generateValueWithinBoundary(width, 125)).setY(Util.generateValueWithinBoundary(height, 125))
                    .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            shapes.push(circle);
            layer.add(circle);

            final Rectangle rectangle = new Rectangle(Math.random() * 120, Math.random() * 60);
            rectangle.setX(Util.generateValueWithinBoundary(width, 125)).setY(Util.generateValueWithinBoundary(height, 125))
                    .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            shapes.push(rectangle);
            layer.add(rectangle);

            final Star star = new Star((int) (Math.random() * 10), 25, 50);
            star.setX(Util.generateValueWithinBoundary(width, 125)).setY(Util.generateValueWithinBoundary(height, 125))
                    .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            shapes.push(star);
            layer.add(star);

            final Arc arc = new Arc((int) (Math.random() * 80), 0, (Math.PI * 2) / 2);
            arc.setX(Util.generateValueWithinBoundary(width, 125)).setY(Util.generateValueWithinBoundary(height, 125))
                    .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            shapes.push(arc);
            layer.add(arc);

            final Ellipse ellipse = new Ellipse(Math.random() * 120, Math.random() * 60);
            ellipse.setX(Util.generateValueWithinBoundary(width, 125)).setY(Util.generateValueWithinBoundary(height, 125))
                    .setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(strokeWidth).setFillColor(Color.getRandomHexColor()).setDraggable(true);
            shapes.push(ellipse);
            layer.add(ellipse);
        }
    }

    public void setDragConstraint(DragConstraint dragConstraint) {

        for (Shape<?> shape : shapes.asList()) {
            shape.setDragConstraint(dragConstraint);
        }
        layer.draw();
    }

    @Override
    public void destroy() {
        super.destroy();
        select.remove();
    }
}
