package com.ait.lienzo.client.core.shape.wires.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.IDestroyable;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.event.WiresConnectorPointsChangedHandler;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;

public class WiresConnectorLabel implements IDestroyable {

    public Rectangle rectangle = new Rectangle(0, 0);
    private final WiresConnector connector;
    private final HandlerRegistrationManager m_registrationManager;
    private final Text text;
    private final BiConsumer<WiresConnector, Text> executor;
    private int maxWidth = 100;
    private String fullText = "";
    private String minText = "";
    private boolean isMouseOver = false;
    private boolean needsWrapping = false;
    private boolean isWrapped = false;
    private String rectangleColor = "";
    private int rectangleOverOffsetX = 6;
    private int rectangleOverOffsetY = 6;
    private boolean stopAtNCharacters = false;
    private int maxChars = 15;
    private HandlerRegistration rectangleMouseEnterHandler;
    private HandlerRegistration rectangleMouseExitHandler;
    private List<Point2D> orthogonalPoints;
    private static final String THREE_DOTS = "...";

    private final WiresConnectorPointsChangedHandler pointsUpdatedHandler = event -> {
        if (isVisible()) {
            refresh();
        }
    };

    WiresConnectorLabel(final String text,
                        final WiresConnector connector,
                        final BiConsumer<WiresConnector, Text> executor) {
        this(new Text(text), connector, executor, new HandlerRegistrationManager());
    }

    WiresConnectorLabel(final Text text,
                        final WiresConnector connector,
                        final BiConsumer<WiresConnector, Text> executor,
                        final HandlerRegistrationManager registrationManager) {
        this.connector = connector;
        this.executor = executor;
        this.m_registrationManager = registrationManager;
        this.text = text;
        this.rectangle.setCornerRadius(3);
        init();
    }

    public WiresConnectorLabel configure(Consumer<Text> consumer) {
        consumer.accept(text);
        refresh();
        batch();
        return this;
    }

    public void setOrthogonalPoints(List<Point2D> orthogonalPoints) {
        this.orthogonalPoints = orthogonalPoints;
        batch();
    }

    public void setRectangleColor(String color) {
        this.rectangleColor = color;
        paintRectangle(color);
    }

    private void paintRectangle(String color) {
        rectangle.setStrokeColor(color);
        rectangle.setFillColor(color);
    }

    public WiresConnectorLabel show() {
        text.setAlpha(1);
        refresh();
        return this;
    }

    public WiresConnectorLabel hide() {
        text.setAlpha(0);
        batch();
        return this;
    }

    public Text getText() {
        return text;
    }

    public boolean isVisible() {
        return text.getAlpha() > 0;
    }

    @Override
    public void destroy() {
        m_registrationManager.destroy();
        rectangleMouseEnterHandler.removeHandler();
        rectangleMouseExitHandler.removeHandler();
        text.removeFromParent();
    }

    private void init() {
        text.setListening(false);
        text.setDraggable(false);
        connector.getGroup().add(text);
        connector.getGroup().add(rectangle);
        rectangleMouseEnterHandler = rectangle.addNodeMouseEnterHandler(event -> {
                                                                            rectangle.setFillColor("blue");
                                                                            rectangle.setStrokeColor("blue");
                                                                            if (needsWrapping) {
                                                                                text.setText(fullText);
                                                                            }
                                                                            isMouseOver = true;
                                                                            batch();
                                                                        }
        );

        rectangleMouseExitHandler = rectangle.addNodeMouseExitHandler(event -> {
                                                                          paintRectangle(rectangleColor);
                                                                          if (needsWrapping) {
                                                                              text.setText(minText);
                                                                          }
                                                                          isMouseOver = false;
                                                                          batch();
                                                                      }
        );

        refresh();
        m_registrationManager.register(connector.addWiresConnectorPointsChangedHandler(pointsUpdatedHandler));
    }

    private void refresh() {
        executor.accept(connector, text);
    }

    private void batch() {
        final Layer layer = connector.getGroup().getLayer();
        if (layer != null) {

            if (text != null && text.getText() != null && !text.getText().isEmpty()) {
                BoundingBox bb = text.getBoundingBox();
                needsWrapping = bb.getWidth() > maxWidth;
                if (!isMouseOver && needsWrapping) { // calculate minimum string
                    isWrapped = true;
                    fullText = text.getText();
                    if (stopAtNCharacters && fullText.length() > maxChars) { // aprox 150 pixels
                        fullText = fullText.substring(0, maxChars);
                    }
                    minText = calculateMinimumText(fullText, text, maxWidth);
                    minText = minText.substring(0, minText.length() - 3) + THREE_DOTS;
                    text.setText(minText);
                }

                WiresConnectorLabelFactory.Segment segment = null != orthogonalPoints ?
                        getLargestSegment(orthogonalPoints) : getLargestSegment(connector);
                Point2D point = new Point2D((segment.getStart().getX() + segment.getEnd().getX()) / 2,
                                            (segment.getStart().getY() + segment.getEnd().getY()) / 2);

                double relativeWidth = text.getBoundingBox().getWidth();
                double relativeHeight = text.getBoundingBox().getHeight();

                // final positioning of text and rectangle
                if (isMouseOver) {
                    BoundingBox boundingBoxForString = setTextAndRectangleDimension(isWrapped, text, fullText);

                    relativeWidth = boundingBoxForString.getWidth();
                    relativeHeight = boundingBoxForString.getHeight();
                    if (relativeHeight > 11) {
                        text.setTextAlign(TextAlign.CENTER);
                    } else {
                        text.setTextAlign(TextAlign.LEFT);
                    }
                } else {
                    text.setTextAlign(TextAlign.LEFT);
                }

                rectangle.setHeight(relativeHeight + rectangleOverOffsetX);
                rectangle.setWidth(relativeWidth + rectangleOverOffsetY);

                // center Text and Rectangle
                double finalXPos = point.getX() - (relativeWidth / 2);
                double finalYPos = point.getY() - (relativeHeight / 2);

                text.setX(finalXPos);
                text.setY(finalYPos);

                rectangle.setX(finalXPos - (rectangleOverOffsetX / 2));
                rectangle.setY(finalYPos - (rectangleOverOffsetX / 2));
            }

            layer.batch();
        }
    }

    protected static String calculateMinimumText(String fullText, Text text, int maxWidth) {
        for (int i = fullText.length(); i > 0; i--) {
            String substring = fullText.substring(0, i);
            text.setText(substring);
            BoundingBox bb2 = text.getBoundingBox();

            if (bb2.getWidth() <= maxWidth) {
                return substring;
            }
        }
        return null;
    }

    protected static BoundingBox setTextAndRectangleDimension(boolean isWrapped, Text text, String fullText) {
        BoundingBox boundingBoxForString;
        if (!isWrapped) {
            // this works for long text
            boundingBoxForString = text.getWrapper().getBoundingBox();
        } else {
            boundingBoxForString = text.getBoundingBoxForString(fullText);
            text.setText(fullText);
            if (fullText.contains(" ")) {
                //this works for space within text
                boundingBoxForString = text.getWrapper().getBoundingBox();
            }
        }
        return boundingBoxForString;
    }

    protected static WiresConnectorLabelFactory.Segment getLargestSegment(WiresConnector connector) {
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < connector.getPointHandles().size() - 1; i++) {
            IControlHandle control1 = connector.getPointHandles().getHandle(i);
            IControlHandle control2 = connector.getPointHandles().getHandle(i + 1);
            points.add(new Point2D(control1.getControl().getX(), control1.getControl().getY()));
            points.add(new Point2D(control2.getControl().getX(), control2.getControl().getY()));
        }

        return getLargestSegment(points);
    }

    protected static WiresConnectorLabelFactory.Segment getLargestSegment(final List<Point2D> points) {
        double maxLength = 0;

        WiresConnectorLabelFactory.Segment segment = null;
        for (int i = 0; i < points.size() - 1; i++) {

            Point2D point1 = points.get(i);
            Point2D point2 = points.get(i + 1);

            double distance = point1.distance(point2);
            if (distance > maxLength) {
                maxLength = distance;
                segment = new WiresConnectorLabelFactory.Segment(0, point1, point2);
            }
        }
        return segment;
    }
}
