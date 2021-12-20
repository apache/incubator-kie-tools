package org.kie.lienzo.client;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.GridLayer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.widget.panel.IsResizable;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

import static elemental2.dom.DomGlobal.document;

public class BaseLienzoExamples {

    HTMLDivElement panelDiv;

    LienzoPanel lienzo;

    private Example test;

    public void doLoad() {
        createTests(new BasicShapesExample("Shapes"),
                    new BasicWiresExample("Wires"),
                    new LineSpliceExample("Line Splice"),
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
                    new WiresDockingExample("Wires Docking"),
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

        JsCanvasExamples jsCanvasExamples = new JsCanvasExamples();
        jsCanvasExamples.examples = this;
        WindowJSCanvas.linkJSCanvasExamples(jsCanvasExamples);
    }

    private List<Example> exampleList = new ArrayList<>();

    public void createTests(Example... tests) {
        for (Example test : tests) {
            exampleList.add(test);
            createTest(test);
        }
    }

    public void goToTest(int index) {
        Example test = exampleList.get(index);
        displayTest(test);
    }

    public void createTest(Example test) {
        HTMLDivElement e1 = (HTMLDivElement) document.createElement("div");
        elemental2.dom.Text e1Text = document.createTextNode(test.getTitle());
        e1.appendChild(e1Text);
        e1.addEventListener("click", evt -> {
            displayTest(test);
        });
        Element links = document.getElementById("nav");
        links.appendChild(e1);
    }

    private void displayTest(Example test) {
        HTMLDivElement top = (HTMLDivElement) document.getElementById("top");
        top.style.display = Display.NONE.getCssName();
        createPanel(test);
        this.test = test;
        this.test.init(lienzo, top);
        this.test.run();
    }

    private void createPanel(Example test) {
        if (this.test != null) {
            this.test.destroy();
            this.test = null;
        }
        if (null != panelDiv) {
            panelDiv.remove();
            panelDiv = null;
        }

        panelDiv = (HTMLDivElement) document.createElement("div");
        panelDiv.style.display = "inline-block";
        HTMLDivElement main = (HTMLDivElement) document.getElementById("main");
        main.appendChild(panelDiv);

        lienzo = test.createPanel();

        panelDiv.appendChild(lienzo.getElement());

        if (lienzo instanceof IsResizable) {
            ((IsResizable) lienzo).onResize();
        }

        applyGrid(lienzo);
    }

    private void applyGrid(final LienzoPanel panel) {
        Line line1 = new Line(0, 0, 0, 0)
                .setStrokeColor("#0000FF")
                .setAlpha(0.2);
        Line line2 = new Line(0, 0, 0, 0)
                .setStrokeColor("#00FF00")
                .setAlpha(0.2);

        line2.setDashArray(2,
                           2);

        GridLayer gridLayer = new GridLayer(100, line1, 25, line2);

        panel.setBackgroundLayer(gridLayer);
    }
}
