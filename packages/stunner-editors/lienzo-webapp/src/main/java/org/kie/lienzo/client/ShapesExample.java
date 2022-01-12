package org.kie.lienzo.client;

import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element.OnclickFn;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;

public class ShapesExample extends BaseExample implements Example {

    private HTMLButtonElement rectangleButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement roundedCornersRectangleButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement linesButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement dashedLinesButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement lineCapsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement lineJoinsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement arcsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement arrowsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement arrowAttributesButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement circleButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement ellipseButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement quadraticCurveButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement cubicCurveButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement polygonsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement starsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement groupsButton = (HTMLButtonElement) DomGlobal.document.createElement("button");
    private HTMLButtonElement sliceGroupButton = (HTMLButtonElement) DomGlobal.document.createElement("button");

    private RectangleExample rectangleExample = new RectangleExample("Rectangle Example");
    private RoundedCornersExample roundedCornersExample = new RoundedCornersExample("Rounded Corners Example");
    private LinesExample linesExample = new LinesExample("Lines Example");
    private DashedLinesExample dashedLinesExample = new DashedLinesExample("Dashed Lines Example");
    private LinesCapExample linesCapExample = new LinesCapExample("Lines Cap Example");
    private LineJoinsExample lineJoinsExample = new LineJoinsExample("Lines Joins Example");
    private ArcsExample arcsExample = new ArcsExample("Arcs Example");
    private ArrowsExample arrowsExample = new ArrowsExample("Arrows Example");
    private ArrowAttributesExample arrowAttributesExample = new ArrowAttributesExample("Arrow Attributes Example");
    private CircleExample circleExample = new CircleExample("Circle Example");
    private EllipseExample ellipseExample = new EllipseExample("Ellipse Example");
    private QuadraticCurveExample quadraticCurveExample = new QuadraticCurveExample("Quadratic Curve Example");
    private CubicCurveExample cubicCurveExample = new CubicCurveExample("Cubic Curve Example");
    private PolygonsExample polygonsExample = new PolygonsExample("Polygons Example");
    private StarsExample starsExample = new StarsExample("Stars Example");
    private GroupsExample groupsExample = new GroupsExample("Groups Example");
    private SliceGroupExample sliceGroupExample = new SliceGroupExample("Slice Group Example");

    private Example currentExample = null;

    public ShapesExample(final String title) {
        super(title);
        heightOffset = 0;
    }

    @Override
    public void destroy() {
        super.destroy();
        rectangleButton.remove();
        roundedCornersRectangleButton.remove();
        linesButton.remove();
        dashedLinesButton.remove();
        lineCapsButton.remove();
        lineJoinsButton.remove();
        arcsButton.remove();
        arrowsButton.remove();
        arrowAttributesButton.remove();
        circleButton.remove();
        ellipseButton.remove();
        quadraticCurveButton.remove();
        cubicCurveButton.remove();
        polygonsButton.remove();
        starsButton.remove();
        groupsButton.remove();
        sliceGroupButton.remove();

        console.log("Destroying Shapes Demo 1-->>#");

        if (currentExample != null && currentExample == arrowAttributesExample) {
            arrowAttributesExample.detach();
        }

        console.log("Destroying Shapes Demo -->>>#");
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Display.INLINE_BLOCK.getCssName();

        rectangleButton.textContent = "Rectangle";
        roundedCornersRectangleButton.textContent = "Rounded Rectangle";
        linesButton.textContent = "Lines";
        dashedLinesButton.textContent = "Dashed Lines";
        lineCapsButton.textContent = "Lines Cap";
        lineJoinsButton.textContent = "Line Joins";
        arcsButton.textContent = "Arcs";
        arrowsButton.textContent = "Arrows";
        arrowAttributesButton.textContent = "Arrow Attributes";
        circleButton.textContent = "Circle";
        ellipseButton.textContent = "Ellipse";
        quadraticCurveButton.textContent = "Quadratic Curve";
        cubicCurveButton.textContent = "Cubic Curve";
        polygonsButton.textContent = "Polygons";
        starsButton.textContent = "Stars";
        groupsButton.textContent = "Groups";
        sliceGroupButton.textContent = "Slice group";

        topDiv.appendChild(rectangleButton);
        topDiv.appendChild(roundedCornersRectangleButton);
        topDiv.appendChild(linesButton);
        topDiv.appendChild(dashedLinesButton);
        topDiv.appendChild(lineCapsButton);
        topDiv.appendChild(lineJoinsButton);
        topDiv.appendChild(arcsButton);
        topDiv.appendChild(arrowsButton);
        topDiv.appendChild(arrowAttributesButton);
        topDiv.appendChild(circleButton);
        topDiv.appendChild(ellipseButton);
        topDiv.appendChild(quadraticCurveButton);
        topDiv.appendChild(cubicCurveButton);
        topDiv.appendChild(polygonsButton);
        topDiv.appendChild(starsButton);
        topDiv.appendChild(groupsButton);
        topDiv.appendChild(sliceGroupButton);
    }

    @Override
    public void run() {

        rectangleButton.onclick = (e) -> {
            return addSubExample(rectangleExample);
        };
        roundedCornersRectangleButton.onclick = (e) -> {
            return addSubExample(roundedCornersExample);
        };
        linesButton.onclick = (e) -> {
            return addSubExample(linesExample);
        };
        dashedLinesButton.onclick = (e) -> {
            return addSubExample(dashedLinesExample);
        };
        lineCapsButton.onclick = (e) -> {
            return addSubExample(linesCapExample);
        };
        lineJoinsButton.onclick = (e) -> {
            return addSubExample(lineJoinsExample);
        };
        arcsButton.onclick = (e) -> {
            return addSubExample(arcsExample);
        };
        arrowsButton.onclick = (e) -> {
            return addSubExample(arrowsExample);
        };
        arrowAttributesButton.onclick = (e) -> {
            return addSubExample(arrowAttributesExample);
        };
        circleButton.onclick = (e) -> {
            return addSubExample(circleExample);
        };
        ellipseButton.onclick = (e) -> {
            return addSubExample(ellipseExample);
        };
        quadraticCurveButton.onclick = (e) -> {
            return addSubExample(quadraticCurveExample);
        };
        cubicCurveButton.onclick = (e) -> {
            return addSubExample(cubicCurveExample);
        };
        polygonsButton.onclick = (e) -> {
            return addSubExample(polygonsExample);
        };
        starsButton.onclick = (e) -> {
            return addSubExample(starsExample);
        };
        groupsButton.onclick = (e) -> {
            return addSubExample(groupsExample);
        };
        sliceGroupButton.onclick = (e) -> {
            return addSubExample(sliceGroupExample);
        };
    }

    private OnclickFn addSubExample(Example example) {
        panel.destroy();
        example.init(panel, topDiv);
        example.run();

        if (currentExample != null && currentExample == arrowAttributesExample) {
            console.log("Destroying Previous Shapes Example--->>##");
            arrowAttributesExample.detach();
            currentExample = null;
        }

        currentExample = example;
        return null;
    }

    @Override
    public void onResize() {
        console.log("Resizing Call");
        super.onResize();

        if (currentExample != null) {
            currentExample.onResize();
        }

        layer.batch();
    }
}

