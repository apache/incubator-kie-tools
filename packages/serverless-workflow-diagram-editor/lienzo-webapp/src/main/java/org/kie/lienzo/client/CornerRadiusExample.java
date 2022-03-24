package org.kie.lienzo.client;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Polygon;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.RegularPolygon;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.shape.guides.ToolTip;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import static elemental2.dom.DomGlobal.document;

public class CornerRadiusExample extends BaseExample implements Example {

    private double m_corner = 0;

    private HTMLSelectElement select;

    public CornerRadiusExample(final String title) {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Display.INLINE_BLOCK.getCssName();

        select = (HTMLSelectElement) document.createElement("select");
        for (int i = 0; i < 45; i += 5) {
            addOption(i, select);
        }
        topDiv.appendChild(select);

        select.onchange = (e) -> {
            m_corner = Double.parseDouble(select.value);

            for (final IPrimitive<?> prim : layer.getChildNodes().toList()) {
                JsPropertyMap map = Js.asPropertyMap(prim);
                if (map.has("cornerRadius")) {
                    map.set("cornerRadius", m_corner);
                }
                prim.refresh();
            }
            layer.batch();
            return null;
        };

        heightOffset = 30;
    }

    private void addOption(int radius, HTMLSelectElement select) {
        HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.label = "" + radius;
        option.value = "" + radius;
        select.add(option);
    }

    public void run() {
        make(layer);

        final ToolTip tool = new ToolTip().setAutoHideTime(1000);

        panel.getViewport().getOverLayer().add(tool);

        for (final IPrimitive<?> prim : layer.getChildNodes().toList()) {
            final Shape<?> shape = prim.asShape();

            if (null != shape) {
                shape.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                    @Override
                    public void onNodeMouseEnter(final NodeMouseEnterEvent event) {
                        tool.setValues("Corner(" + m_corner + ")", shape.getShapeType().getValue());

                        final BoundingBox bb = shape.getBoundingBox();

                        tool.show(shape.getX() + bb.getX() + (bb.getWidth() / 2), shape.getY() + bb.getY() + (bb.getHeight() / 2));
                    }
                });
                shape.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                    @Override
                    public void onNodeMouseExit(final NodeMouseExitEvent event) {
                        tool.hide();
                    }
                });
                shape.addNodeDragStartHandler(new NodeDragStartHandler() {
                    @Override
                    public void onNodeDragStart(final NodeDragStartEvent event) {
                        tool.hide();
                    }
                });
                shape.addNodeDragEndHandler(new NodeDragEndHandler() {
                    @Override
                    public void onNodeDragEnd(final NodeDragEndEvent event) {
                        tool.setValues("Corner(" + m_corner + ")", shape.getShapeType().getValue());

                        final BoundingBox bb = shape.getBoundingBox();

                        tool.show(shape.getX() + bb.getX() + (bb.getWidth() / 2), shape.getY() + bb.getY() + (bb.getHeight() / 2));
                    }
                });
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        select.remove();
    }

    private void make(final Layer layer) {
        final Star star = new Star(5, 50, 100);
        star.setDraggable(true);
        star.setX(115);
        star.setY(130);
        star.setStrokeWidth(3);
        star.setFillColor(ColorName.DARKORCHID);
        star.setFillAlpha(0.50);
        star.setStrokeColor(ColorName.BLACK);
        layer.add(star);

        final RegularPolygon regp = new RegularPolygon(5, 100);
        regp.setDraggable(true);
        regp.setX(300);
        regp.setY(195);
        regp.setStrokeWidth(3);
        regp.setFillColor(ColorName.AQUAMARINE);
        regp.setFillAlpha(0.50);
        regp.setStrokeColor(ColorName.BLACK);
        layer.add(regp);

        final Point2DArray points = Point2DArray.fromArrayOfDouble(0, 0, 60, 0, 60, 60, 100, 125, 0, 125);

        final PolyLine line = new PolyLine(points);
        line.setDraggable(true);
        line.setX(50);
        line.setY(275);
        line.setStrokeWidth(3);
        line.setStrokeColor(ColorName.BLACK);
        layer.add(line);

        final Polygon poly = new Polygon(points);
        poly.setDraggable(true);
        poly.setX(200);
        poly.setY(315);
        poly.setFillColor(ColorName.YELLOW);
        poly.setFillAlpha(0.50);
        poly.setStrokeWidth(3);
        poly.setStrokeColor(ColorName.BLACK);
        layer.add(poly);

        final Triangle tria = new Triangle(new Point2D(0, 0), new Point2D(200, 0), new Point2D(100, 150));
        tria.setDraggable(true);
        tria.setX(370);
        tria.setY(50);
        tria.setFillColor(ColorName.RED);
        tria.setFillAlpha(0.50);
        tria.setStrokeWidth(3);
        tria.setStrokeColor(ColorName.BLACK);
        layer.add(tria);

        final Rectangle rect = new Rectangle(200, 200);
        rect.setDraggable(true);
        rect.setX(400);
        rect.setY(230);
        rect.setFillColor(ColorName.BLUE);
        rect.setFillAlpha(0.50);
        rect.setStrokeWidth(3);
        rect.setStrokeColor(ColorName.BLACK);
        layer.add(rect);
    }
}
