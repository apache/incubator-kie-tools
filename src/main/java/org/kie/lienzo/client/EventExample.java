package org.kie.lienzo.client;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.event.NodeMouseOutEvent;
import com.ait.lienzo.client.core.event.NodeMouseOutHandler;
import com.ait.lienzo.client.core.event.NodeMouseOverEvent;
import com.ait.lienzo.client.core.event.NodeMouseOverHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.gwtlienzo.event.shared.EventHandler;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.lienzo.client.util.Console;

public class EventExample extends BaseExample implements Example
{
    public EventExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        createDragTests();

        createMouseEnterExitTests();

        createMouseUpDownTests();

        createMouseClickedTests();

        createAllTests();

        //createMouseOverOutTests();

    }

    private void createDragTests()
    {
        Rectangle rect = new Rectangle(500, 200);
        rect.setX(50).setY(50);
        rect.setFillColor(ColorName.PALEVIOLETRED);
        rect.setStrokeColor(ColorName.PALEVIOLETRED);
        rect.setDraggable(true);
        layer.add(rect);

        addHandlers(rect, NodeDragStartHandler.class, NodeDragMoveHandler.class, NodeDragEndHandler.class);

        rect = new Rectangle(100, 100);
        rect.setX(250).setY(100);
        rect.setFillColor(ColorName.DARKGRAY);
        rect.setStrokeColor(ColorName.DARKGRAY);
        rect.setDraggable(true);
        layer.add(rect);

        addHandlers(rect, NodeDragStartHandler.class, NodeDragMoveHandler.class, NodeDragEndHandler.class);
    }

    private void createMouseEnterExitTests()
    {
        Circle circ = new Circle(50);
        circ.setX(150).setY(150);
        circ.setFillColor(ColorName.YELLOWGREEN );
        circ.setStrokeColor(ColorName.YELLOWGREEN);
        circ.setDraggable(true);
        layer.add(circ);

        addHandlers(circ, NodeMouseEnterHandler.class, NodeMouseExitHandler.class);

        circ = new Circle(50);
        circ.setX(150).setY(350);
        circ.setFillColor(ColorName.BLUEVIOLET );
        circ.setStrokeColor(ColorName.BLUEVIOLET);
        circ.setDraggable(true);
        layer.add(circ);

        addHandlers(circ, NodeMouseEnterHandler.class, NodeMouseExitHandler.class);
    }

    private void createMouseUpDownTests()
    {
        Star star = new Star(5, 25, 75);
        star.setX(300).setY(350);
        star.setFillColor(ColorName.CHOCOLATE );
        star.setStrokeColor(ColorName.CHOCOLATE);
        star.setDraggable(true);
        layer.add(star);

        addHandlers(star, NodeMouseUpHandler.class, NodeMouseDownHandler.class);

//            circ = new Circle(50);
//            circ.setX(150).setY(350);
//            circ.setFillColor(ColorName.ALICEBLUE );
//            circ.setStrokeColor(ColorName.ALICEBLUE);
//            circ.setDraggable(true);
//            layer.add(circ);
//
//            addHandlers(circ, NodeMouseEnterHandler.class, NodeMouseExitHandler.class);
    }

    private void createMouseClickedTests()
    {
        Triangle star = new Triangle(new Point2D(0, 100), new Point2D(100, 100), new Point2D(50, 0));
        star.setX(400).setY(300);
        star.setFillColor(ColorName.MAROON );
        star.setStrokeColor(ColorName.MAROON);
        star.setDraggable(true);
        layer.add(star);

        addHandlers(star, NodeMouseClickHandler.class, NodeMouseDoubleClickHandler.class);
    }

    public void createAllTests()
    {
        final String    svg  = "M 0 100 L 65 115 L 65 105 L 120 125 L 120 115 L 200 180 L 140 160 L 140 170 L 85 150 L 85 160 L 0 140 Z";
        final MultiPath path = new MultiPath(svg).setStrokeColor(ColorName.GREENYELLOW).setFillColor(ColorName.GREENYELLOW);
        path.setLocation(new Point2D(100, 350)).setDraggable(true);
        layer.add(path);

        addHandlers(path,
                    NodeDragStartHandler.class, NodeDragMoveHandler.class, NodeDragEndHandler.class,
                    NodeMouseEnterHandler.class, NodeMouseExitHandler.class,
                    NodeMouseUpHandler.class, NodeMouseDownHandler.class,
                    NodeMouseClickHandler.class, NodeMouseDoubleClickHandler.class);
    }

    public void addHandlers(Node node, Class<? extends EventHandler>... handlers)
    {
        CompositeEventHandler composite = new CompositeEventHandler(console);
        for (Class<? extends EventHandler> handlerClass : handlers )
        {
            // drag handlers
            if ( handlerClass == NodeDragStartHandler.class)
            {
                node.addNodeDragStartHandler(composite);
            }
            if ( handlerClass == NodeDragMoveHandler.class)
            {
                node.addNodeDragMoveHandler(composite);
            }
            if ( handlerClass == NodeDragEndHandler.class)
            {
                node.addNodeDragEndHandler(composite);
            }


            // enter/exit handlers
            if ( handlerClass == NodeMouseEnterHandler.class)
            {
                node.addNodeMouseEnterHandler(composite);
            }
            if ( handlerClass == NodeMouseExitHandler.class)
            {
                node.addNodeMouseExitHandler(composite);
            }

            // over/out handlers
            if ( handlerClass == NodeMouseOverHandler.class)
            {
                node.addNodeMouseOverHandler(composite);
            }
            if ( handlerClass == NodeMouseOutHandler.class)
            {
                node.addNodeMouseOutHandler(composite);
            }

            // up/dow  handlers
            if ( handlerClass == NodeMouseDownHandler.class)
            {
                node.addNodeMouseDownHandler(composite);
            }
            if ( handlerClass == NodeMouseUpHandler.class)
            {
                node.addNodeMouseUpHandler(composite);
            }

            // click/doubleclick  handlers
            if ( handlerClass == NodeMouseClickHandler.class)
            {
                node.addNodeMouseClickHandler(composite);
            }

            if ( handlerClass == NodeMouseDoubleClickHandler.class)
            {
                node.addNodeMouseDoubleClickHandler(composite);
            }
        }
    }

    public static class CompositeEventHandler implements NodeDragStartHandler,
                                                         NodeDragMoveHandler,
                                                         NodeDragEndHandler,
                                                         NodeMouseEnterHandler,
                                                         NodeMouseExitHandler,
                                                         NodeMouseDownHandler,
                                                         NodeMouseUpHandler,
                                                         NodeMouseClickHandler,
                                                         NodeMouseDoubleClickHandler,
                                                         NodeMouseOverHandler,
                                                         NodeMouseOutHandler
    {
        Console console;

        public CompositeEventHandler(final Console console)
        {
            this.console = console;
        }

        @Override
        public void onNodeDragStart(final NodeDragStartEvent event)
        {
            console.log("drag start");
        }

        @Override
        public void onNodeDragMove(final NodeDragMoveEvent event)
        {
            console.log("drag move");
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event)
        {
            console.log("drag end");
        }

        @Override
        public void onNodeMouseEnter(final NodeMouseEnterEvent event)
        {
            console.log("mouse enter");
        }

        @Override
        public void onNodeMouseExit(final NodeMouseExitEvent event)
        {
            console.log("mouse exit");
        }

        @Override
        public void onNodeMouseUp(final NodeMouseUpEvent event)
        {
            console.log("mouse up");
        }

        @Override
        public void onNodeMouseDown(final NodeMouseDownEvent event)
        {
            console.log("mouse down");
        }

        @Override
        public void onNodeMouseClick(final NodeMouseClickEvent event)
        {
            console.log("mouse click");
        }

        @Override
        public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event)
        {
            console.log("mouse double click");
        }

        @Override
        public void onNodeMouseOver(final NodeMouseOverEvent event)
        {
            console.log("mouse over");
        }

        @Override
        public void onNodeMouseOut(final NodeMouseOutEvent event)
        {
            console.log("mouse out");
        }
    }
}
