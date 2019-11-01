package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.widget.panel.IsResizable;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.BoundsProviderFactory;
import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

import static elemental2.dom.DomGlobal.document;

public class BaseLienzoExamples {
    HTMLDivElement panelDiv;

    LienzoPanel lienzo;

    private Example test;

    public void doLoad() {
        createTests(new BasicExample("Basic"),
                    new ToolboxExample("Toolbox"),
                    new PerformanceTests("Performance tests"),
                    new StrokeAndFillingExample("Stroke and Filling"),
                    new GradientsAndShadowsExample("Gradients and Shadows"),
                    new ColorsAndTransparencyExample("Colors and Transparency"),
                    new HorizontalTextAlignmentExample("Horizontal Text Alignment"),
                    new VerticalTextAlignmentExample("Vertical Text Alignment"),
                    new MeasureTextExample("Measure Text"),
                    new ScaledTextExample("Scaled Text"),
                    new TextAroundArcExample("Text around Arc"),
                    new PanAndZoomExample("Pan and Zoom"),
                    new TweeningExample("Tweening"),
                    new TimersExample("Timers"),
                    new DragCirclesExample("Drag Circles"),
                    new FixedDragConstraintsExample("Fixed Drag Constraints"),
                    new CustomDragConstraintsExample("Custom Drag Constraints"),
                    new AnimatedCirclesExample("Animated Circles"),
                    new EventExample("Events"),
                    new SVGTigerExample("SVG Paths Tiger"),
                    new Animate("Animations"),
                    new LionExample("Polygon Lion with Clipping"),
                    new WiresExample("Wires General"),
                    new WiresDockingExample("Wires Docking" ),
                    new CardinalIntersectsExample("Cardinal Intersects"),
                    new CornerRadiusExample("Corner Radius"),
                    new ShapesExample("Shapes Example"),
                    new MovieExample("Video"),
                    new DrawImageExample("Draw Image"),
                    new Transform3PointsExample("Transform 3Points"),
                    new SpriteExample("Sprite Example"),
                    new ImageStripExample("Image Strip Example"),
                    new AsteroidsGameExample("Asteroids Game")
        );

    }

    public void createTests(Example... tests)
    {
        for ( Example test : tests)
        {
            createTest(test);
        }
    }

    public void createTest(Example test)
    {
        HTMLDivElement e1 = (HTMLDivElement) document.createElement("div");
        HTMLDivElement top = (HTMLDivElement) document.getElementById("top");
        elemental2.dom.Text e1Text = document.createTextNode(test.getTitle());
        e1.appendChild(e1Text);
        e1.addEventListener("click", evt -> {
            top.style.display = Display.NONE.getCssName();
            createPanel(test);
            this.test = test;
            this.test.init(lienzo, top);
            this.test.run();

        });
        Element links = document.getElementById("nav");
        links.appendChild(e1);
    }

    private void createPanel(Example test)
    {
        if (this.test != null)
        {
            this.test.destroy();
            this.test = null;
        }

        panelDiv = (HTMLDivElement) document.createElement("div");
        panelDiv.style.display = "inline-block";
        HTMLDivElement main = (HTMLDivElement) document.getElementById("main");
        main.appendChild(panelDiv);

        // lienzo = LienzoFixedPanel.newPanel(600, 600);
        // lienzo = LienzoResizablePanel.newPanel();
        lienzo = ScrollablePanel.newPanel(new BoundsProviderFactory.PrimitivesBoundsProvider());

        panelDiv.appendChild(lienzo.getElement());

        if (lienzo instanceof IsResizable) {
            ((IsResizable) lienzo).onResize();
        }

        // TODO: REMOVE ALL BELOW EVENT LISTENERS
        /*((ScrollablePanel) lienzo).addBoundsChangedEventListener(evt -> {
            DomGlobal.console.log("BOUNDS CHANGED!!! YEAH!");
            LienzoPanelEventDetail detail = LienzoPanelEventDetail.getDetail(evt);
            DomGlobal.console.log("DETAIL = " + detail.toString());
        });

        ((ScrollablePanel) lienzo).addResizeEventListener(evt -> {
            DomGlobal.console.log("RESIZE!!! YEAH!");
            LienzoPanelEventDetail detail = LienzoPanelEventDetail.getDetail(evt);
            int widePx = detail.getLienzoPanel().getWidePx();
            int highPx = detail.getLienzoPanel().getHighPx();
            DomGlobal.console.log("DETAIL = " + detail.toString());
            DomGlobal.console.log("W/H = " + widePx + ", " + highPx);
        });

        ((ScrollablePanel) lienzo).addScrollEventListener(evt -> {
            DomGlobal.console.log("SCROLL!!! YEAH!");
            LienzoPanelScrollEventDetail detail = LienzoPanelScrollEventDetail.getScrollDetail(evt);
            DomGlobal.console.log("DETAIL = " + detail.toString());
            DomGlobal.console.log("Px/Py = " + detail.getPx() + ", " + detail.getPy());
        });*/


        applyGrid(lienzo);

        DomGlobal.window.addEventListener("resize", (e) ->
        {
            test.onResize();
        });
    }

    private void applyGrid( final LienzoPanel panel) {
        // Grid.
        Line line1 = new Line(0, 0, 0, 0 )
                .setStrokeColor( "#0000FF" )
                .setAlpha( 0.2 );
        Line line2 = new Line( 0, 0, 0, 0 )
                .setStrokeColor( "#00FF00"  )
                .setAlpha( 0.2 );

        line2.setDashArray( 2,
                2 );

        GridLayer gridLayer = new GridLayer(100, line1, 25, line2 );

        panel.setBackgroundLayer( gridLayer );
    }
}
