package org.kie.lienzo.client;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.HTMLBRElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;

import static elemental2.dom.DomGlobal.document;

public class ArrowAttributesExample extends BaseExample implements Example {

    private Arrow[] arrows = new Arrow[2];

    private HTMLBRElement br = (HTMLBRElement) document.createElement("br");
    private HTMLSelectElement select = (HTMLSelectElement) document.createElement("select");
    private HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
    private HTMLLabelElement arrowTypeLabel = (HTMLLabelElement) document.createElement("label");
    private HTMLLabelElement baseWidthlabel = (HTMLLabelElement) document.createElement("label");
    private HTMLSelectElement baseWidthSelect = (HTMLSelectElement) document.createElement("select");
    private HTMLLabelElement headWidthlabel = (HTMLLabelElement) document.createElement("label");
    private HTMLSelectElement headWidthSelect = (HTMLSelectElement) document.createElement("select");
    private HTMLLabelElement arrowAnglelabel = (HTMLLabelElement) document.createElement("label");
    private HTMLSelectElement arrowAngleSelect = (HTMLSelectElement) document.createElement("select");
    private HTMLLabelElement baseAnglelabel = (HTMLLabelElement) document.createElement("label");
    private HTMLSelectElement baseAngleSelect = (HTMLSelectElement) document.createElement("select");

    public ArrowAttributesExample(String title) {
        super(title);
    }

    @Override
    public void destroy() {
        super.destroy();
        detach();
    }

    @Override
    public void onResize() {
        super.onResize();
        console.log("ReDrawing Arrow Example on Resize N/A -->>");
        setLocation();
        layer.batch();
    }

    private void setLocation() {
        // No need
    }

    public void detach() {
        layer.clear();
        br.remove();
        arrowTypeLabel.remove();
        select.remove();
        baseWidthlabel.remove();
        baseWidthSelect.remove();
        headWidthlabel.remove();
        headWidthSelect.remove();
        arrowAnglelabel.remove();
        arrowAngleSelect.remove();
        baseAnglelabel.remove();
        baseAngleSelect.remove();
        layer.batch();
        console.log("Destroying Arrow Attributes --->>");
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);

        topDiv.style.display = Display.INLINE_BLOCK.getCssName();
        detach();
        option.label = "at-end";
        option.value = "at-end";
        select.add(option);

        option = (HTMLOptionElement) document.createElement("option");
        option.label = "at-start";
        option.value = "at-start";
        select.add(option);

        option = (HTMLOptionElement) document.createElement("option");
        option.label = "at-both-ends";
        option.value = "at-both-ends";
        select.add(option);

        option = (HTMLOptionElement) document.createElement("option");
        option.label = "at-end-tapered";
        option.value = "at-end-tapered";
        select.add(option);

        option = (HTMLOptionElement) document.createElement("option");
        option.label = "at-start-tapered";
        option.value = "at-start-tapered";
        select.add(option);

        arrowTypeLabel.textContent = "Arrow Type:";
        baseWidthlabel.textContent = "Base Width:";
        headWidthlabel.textContent = "Head Width:";
        arrowAnglelabel.textContent = "Arrow Angle:";
        baseAnglelabel.textContent = "Base Angle:";

        for (int i = 0; i < 200; i += 5) {
            addOption(i, baseWidthSelect, i == 30);
            addOption(i, headWidthSelect, i == 50);
            addOption(i, arrowAngleSelect, i == 45);
            addOption(i, baseAngleSelect, i == 45);
        }

        topDiv.appendChild(br);
        topDiv.appendChild(arrowTypeLabel);
        topDiv.appendChild(select);
        topDiv.appendChild(baseWidthlabel);
        topDiv.appendChild(baseWidthSelect);
        topDiv.appendChild(headWidthlabel);
        topDiv.appendChild(headWidthSelect);
        topDiv.appendChild(arrowAnglelabel);
        topDiv.appendChild(arrowAngleSelect);
        topDiv.appendChild(baseAnglelabel);
        topDiv.appendChild(baseAngleSelect);

        select.onchange = (e) -> {
            switch (select.value) {
                case "at-end":
                    arrows[0].setArrowType(ArrowType.AT_END);
                    arrows[1].setArrowType(ArrowType.AT_END);
                    break;
                case "at-start":
                    arrows[0].setArrowType(ArrowType.AT_START);
                    arrows[1].setArrowType(ArrowType.AT_START);
                    break;
                case "at-both-ends":
                    arrows[0].setArrowType(ArrowType.AT_BOTH_ENDS);
                    arrows[1].setArrowType(ArrowType.AT_BOTH_ENDS);
                    break;
                case "at-end-tapered":
                    arrows[0].setArrowType(ArrowType.AT_END_TAPERED);
                    arrows[1].setArrowType(ArrowType.AT_END_TAPERED);
                    break;
                case "at-start-tapered":
                    arrows[0].setArrowType(ArrowType.AT_START_TAPERED);
                    arrows[1].setArrowType(ArrowType.AT_START_TAPERED);
                    break;
            }
            arrows[0].getScene().draw();
            arrows[1].getScene().draw();
            layer.batch();
            return null;
        };

        baseWidthSelect.onchange = (e) -> {
            double baseWidth = Double.parseDouble(baseWidthSelect.value);

            arrows[0].setBaseWidth(baseWidth);
            arrows[1].setBaseWidth(baseWidth);

            arrows[0].getScene().draw();
            arrows[1].getScene().draw();
            layer.batch();
            return null;
        };

        headWidthSelect.onchange = (e) -> {
            double headWidth = Double.parseDouble(headWidthSelect.value);

            arrows[0].setHeadWidth(headWidth);
            arrows[1].setHeadWidth(headWidth);

            arrows[0].getScene().draw();
            arrows[1].getScene().draw();
            layer.batch();
            return null;
        };

        arrowAngleSelect.onchange = (e) -> {
            double arrowAngle = Double.parseDouble(arrowAngleSelect.value);

            arrows[0].setArrowAngle(arrowAngle);
            arrows[1].setArrowAngle(arrowAngle);

            arrows[0].getScene().draw();
            arrows[1].getScene().draw();
            layer.batch();
            return null;
        };

        baseAngleSelect.onchange = (e) -> {
            double baseAngle = Double.parseDouble(baseAngleSelect.value);

            arrows[0].setBaseAngle(baseAngle);
            arrows[1].setBaseAngle(baseAngle);

            arrows[0].getScene().draw();
            arrows[1].getScene().draw();
            layer.batch();
            return null;
        };
    }

    private void addOption(int value, HTMLSelectElement select, boolean selected) {
        HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
        option.label = "" + value;
        option.value = "" + value;
        option.selected = selected;
        select.add(option);
    }

    @Override
    public void run() {

        Point2D start = new Point2D(100, 100);
        Point2D end = new Point2D(200, 200);

        Arrow arrow = new Arrow(start, end, 30, 50, 45, 45, ArrowType.AT_END);
        arrow.setStrokeColor(ColorName.BLACK);
        arrow.setStrokeWidth(1);
        arrow.setFillColor(ColorName.YELLOW);
        layer.add(arrow);
        arrows[0] = arrow;

        Arrow dragArrow = new Arrow(start, end, 30, 50, 45, 45, ArrowType.AT_END);
        dragArrow.setStrokeColor(ColorName.GREY);
        dragArrow.setStrokeWidth(1);
        dragArrow.setVisible(false);
        arrows[1] = dragArrow;

        DragHandle startHandle = new DragHandle("start", true, arrow, dragArrow);
        startHandle.setLocation(start);
        layer.add(startHandle);

        DragHandle endHandle = new DragHandle("end", false, arrow, dragArrow);
        endHandle.setLocation(end);
        layer.add(endHandle);
    }

    public static class DragHandle extends Group implements NodeDragStartHandler,
                                                            NodeDragMoveHandler,
                                                            NodeDragEndHandler {

        private Arrow arrow, dragArrow;
        private boolean start;

        public DragHandle(String text, boolean start, Arrow arrow, Arrow dragArrow) {
            Circle circle = new Circle(3);
            circle.setFillColor(ColorName.BLACK.getColor().setA(0.5));
            add(circle);

            Text txt = new Text(text, "Arial, sans-serif", 10);
            txt.setX(-10).setY(15);
            txt.setFillColor(ColorName.BLACK);
            add(txt);

            this.arrow = arrow;
            this.dragArrow = dragArrow;
            this.start = start;

            setDraggable(true);
            addNodeDragStartHandler(this);
            addNodeDragMoveHandler(this);
            addNodeDragEndHandler(this);
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {

            if (dragArrow.getParent() == null) {
                getViewport().getDragLayer().add(dragArrow);
                dragArrow.moveToBottom();
            }

            if (start) {
                dragArrow.setStart(new Point2D(event.getX(), event.getY()));
            } else {
                dragArrow.setEnd(new Point2D(event.getX(), event.getY()));
            }

            dragArrow.setVisible(true);
            arrow.getScene().draw();
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event) {
            if (start) {
                dragArrow.setStart(new Point2D(event.getX(), event.getY()));
            } else {
                dragArrow.setEnd(new Point2D(event.getX(), event.getY()));
            }

            arrow.setStrokeColor(ColorName.GRAY);
            arrow.setFillColor(ColorName.TRANSPARENT);
            arrow.setStart(dragArrow.getStart());
            arrow.setEnd(dragArrow.getEnd());
            arrow.getScene().draw();
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            if (start) {
                dragArrow.setStart(new Point2D(event.getX(), event.getY()));
            } else {
                dragArrow.setEnd(new Point2D(event.getX(), event.getY()));
            }

            dragArrow.setVisible(false);

            arrow.setStart(dragArrow.getStart());
            arrow.setEnd(dragArrow.getEnd());
            arrow.setVisible(true);
            arrow.setFillColor(ColorName.YELLOW);
            arrow.setStrokeColor(ColorName.BLACK);

            arrow.getScene().draw();
        }
    }

    public void update(int baseWidth, int headWidth, int arrowAngle, int baseAngle, ArrowType arrowType) {
        for (int i = 0; i < 2; i++) {
            Arrow a = arrows[i];
            a.setBaseWidth(baseWidth);
            a.setHeadWidth(headWidth);
            a.setArrowAngle(arrowAngle);
            a.setBaseAngle(baseAngle);
            a.setArrowType(arrowType);
        }
        layer.draw();
    }
}
