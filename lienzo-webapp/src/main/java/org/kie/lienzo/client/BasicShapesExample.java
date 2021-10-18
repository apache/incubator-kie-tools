package org.kie.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOverEvent;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import elemental2.dom.DomGlobal;

public class BasicShapesExample extends BaseExample implements Example {

    public static final String RECTANGLE = "rectangle";
    public static final ColorName COLOR = ColorName.LIGHTGREY;
    public static final String PATH = "path";
    public static final ColorName OVER_COLOR = ColorName.RED;

    private Rectangle rectangle;

    public BasicShapesExample(final String title) {
        super(title);
    }

    @Override
    public void run() {

        rectangle = new Rectangle(100, 100)
                .setID(RECTANGLE)
                .setX(300)
                .setY(100)
                .setFillColor(COLOR)
                .setStrokeColor(ColorName.DARKGRAY)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        rectangle.addNodeMouseClickHandler(this::onRectangleMouseClick);
        rectangle.addNodeMouseDoubleClickHandler(this::onRectangleMouseDoubleClick);
        rectangle.addNodeMouseOverHandler(this::onRectangleMouseOver);
        rectangle.addNodeMouseOutHandler(this::onRectangleMouseOut);
        rectangle.addNodeMouseEnterHandler(this::onNodeMouseEnter);
        rectangle.addNodeMouseExitHandler(this::onNodeMouseExit);

        layer.add(rectangle);

        Circle circle = new Circle(50)
                .setX(600)
                .setY(150)
                .setFillColor(ColorName.LIGHTBLUE)
                .setStrokeColor(ColorName.DARKBLUE)
                .setStrokeWidth(1.5)
                .setDraggable(true);

        layer.add(circle);

        MultiPath path = new MultiPath().rect(0, 0, 100, 100);
        path
                .setID(PATH)
                .setX(400)
                .setY(100)
                .setFillColor(COLOR)
                .setStrokeColor(COLOR)
                .setStrokeWidth(1)
                .setAlpha(1)
                .setDraggable(true);

        layer.add(path);

        layer.draw();
    }

    private void onRectangleMouseClick(NodeMouseClickEvent e) {
        log("CLICK!");
        rectangle.setWidth(rectangle.getWidth() * 2);
        rectangle.setHeight(rectangle.getHeight() * 2);
        layer.draw();
    }

    private void onRectangleMouseDoubleClick(NodeMouseDoubleClickEvent e) {
        log("DOUBLE CLICK!");
        rectangle.setWidth(rectangle.getWidth() / 2);
        rectangle.setHeight(rectangle.getHeight() / 2);
        layer.draw();
    }

    private void onRectangleMouseOver(NodeMouseOverEvent e) {
        log("OVER!");
        rectangle.setFillColor(OVER_COLOR);
        layer.draw();
    }

    private void onRectangleMouseOut(NodeMouseOutEvent e) {
        log("OUT!");
        rectangle.setFillColor(COLOR);
        layer.draw();
    }

    private void onNodeMouseEnter(NodeMouseEnterEvent event) {
        log("ENTER!");
        rectangle.setFillColor(OVER_COLOR);
        layer.draw();
    }

    private void onNodeMouseExit(NodeMouseExitEvent event) {
        log("EXIT!");
        rectangle.setFillColor(COLOR);
        layer.draw();
    }

    private void log(String s) {
        console.log(s);
        DomGlobal.console.log(s);
    }
}
