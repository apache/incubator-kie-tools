package org.kie.lienzo.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.AbstractLayoutService;
import com.ait.lienzo.client.core.layout.Edge;
import com.ait.lienzo.client.core.layout.Layout;
import com.ait.lienzo.client.core.layout.VertexPosition;
import com.ait.lienzo.client.core.layout.graph.OutgoingEdge;
import com.ait.lienzo.client.core.layout.graph.Vertex;
import com.ait.lienzo.client.core.layout.sugiyama.SugiyamaLayoutService;
import com.ait.lienzo.client.core.layout.sugiyama.step01.ReverseEdgesCycleBreaker;
import com.ait.lienzo.client.core.layout.sugiyama.step02.LongestPathVertexLayerer;
import com.ait.lienzo.client.core.layout.sugiyama.step02.VertexLayerer;
import com.ait.lienzo.client.core.layout.sugiyama.step03.DefaultVertexOrdering;
import com.ait.lienzo.client.core.layout.sugiyama.step03.LayerCrossingCount;
import com.ait.lienzo.client.core.layout.sugiyama.step03.MedianVertexLayerPositioning;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VertexLayerPositioning;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VertexOrdering;
import com.ait.lienzo.client.core.layout.sugiyama.step03.VerticesTransposer;
import com.ait.lienzo.client.core.layout.sugiyama.step04.DefaultVertexPositioning;
import com.ait.lienzo.client.core.layout.sugiyama.step04.VertexPositioning;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.dom.client.Style;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.kie.lienzo.client.util.WiresUtils;

import static com.ait.lienzo.tools.common.api.java.util.UUID.uuid;

public class AutomaticLayoutExample extends BaseExample implements Example {

    private HTMLButtonElement performAutomaticLayoutButton;
    private final Map<String, Vertex> vertices;
    private final Map<String, WiresShape> createdShapes;
    private final int SHAPE_WIDTH = 100;
    private final int SHAPE_HEIGHT = 50;
    private final String SHAPE_COLOR = "#000000";

    public AutomaticLayoutExample(String title) {
        super(title);
        this.vertices = new HashMap<>();
        this.createdShapes = new HashMap<>();
    }

    @Override
    public void run() {
        this.vertices.clear();
        this.createdShapes.clear();

        addShape("Start");
        addShape("Create Initial Data");
        addShape("Inject data");
        addShape("Handle Error1");
        addShape("Handle Error2");
        addShape("Compensate");
        addShape("Wait for Events");
        addShape("Run Operations");
        addShape("Switch State");

        addShape("Event2 State");
        addShape("Event1 State");
        addShape("Default State");
        addShape("Handle Error3");
        addShape("End");

        addConnection("Start", "Create Initial Data");

        addConnection("Create Initial Data", "Inject data");

        addConnection("Inject data", "Handle Error1");
        addConnection("Inject data", "Compensate");
        addConnection("Inject data", "Wait for Events");
        addConnection("Inject data", "Handle Error2");

        addConnection("Wait for Events", "Run Operations");
        addConnection("Run Operations", "Switch State");

        addConnection("Switch State", "Event2 State");
        addConnection("Switch State", "Event1 State");
        addConnection("Switch State", "Default State");
        addConnection("Switch State", "Handle Error3");

        addConnection("Event2 State", "End");
        addConnection("Event1 State", "End");
        addConnection("Default State", "End");
        addConnection("Handle Error3", "End");
        addConnection("Handle Error2", "End");
        addConnection("Handle Error1", "End");
    }

    private void addConnection(final String source,
                               final String target) {

        final Vertex sourceVertex = vertices.get(source);
        final Vertex targetVertex = vertices.get(target);

        sourceVertex.getOutgoingEdges().add(new OutgoingEdge(uuid(), targetVertex));
    }

    private void connectShapes(final String source,
                               final String target,
                               final List<Point2D> bendingPoints) {
        final WiresShape sourceShape = createdShapes.get(source);
        final WiresShape targetShape = createdShapes.get(target);

        final WiresManager manager = WiresManager.get(layer);

        WiresUtils.connect(sourceShape.getMagnets(),
                           0,
                           targetShape.getMagnets(),
                           0,
                           manager,
                           bendingPoints);
    }

    private void addShape(final String shapeText) {

        final WiresShape shape = new WiresShape(new MultiPath().rect(0,
                                                                     0,
                                                                     SHAPE_WIDTH,
                                                                     SHAPE_HEIGHT)
                                                        .setStrokeColor(SHAPE_COLOR))
                .setDraggable(true);
        shape.setLocation(new Point2D(0, 0));

        final Text text = getText(shapeText);
        shape.addChild(text);

        createdShapes.put(shapeText, shape);

        final WiresManager manager = WiresManager.get(layer);
        manager.register(shape);

        manager.getMagnetManager().createMagnets(shape);

        final Vertex vertex = new Vertex();
        vertex.setHeight(SHAPE_HEIGHT);
        vertex.setWidth(SHAPE_WIDTH);
        vertex.setId(shapeText);
        vertices.put(shapeText, vertex);
    }

    private Text getText(String shapeText) {
        final Text text = new Text(shapeText, null, 8);
        text.setFillColor("#000000");
        text.setX(SHAPE_WIDTH / 2);
        text.setY(SHAPE_HEIGHT / 2);
        text.setTextAlign(TextAlign.CENTER);
        text.setTextBaseLine(TextBaseLine.MIDDLE);
        return text;
    }

    @Override
    public void init(LienzoPanel panel, HTMLDivElement topDiv) {
        super.init(panel, topDiv);

        topDiv.style.display = Style.Display.INLINE_BLOCK.getCssName();

        performAutomaticLayoutButton = createButton("Perform Automatic Layout", this::performAutomaticLayout);
        topDiv.appendChild(performAutomaticLayoutButton);
    }

    private void performAutomaticLayout() {

        final AbstractLayoutService layoutService = createLayoutService();
        final Layout layout = layoutService.createLayout(vertices.values().stream().collect(Collectors.toList()), "Start", "End");

        final List<VertexPosition> positions = layout.getVerticesPositions();

        for (final VertexPosition position : positions) {

            final WiresShape shape = createdShapes.get(position.getId());
            final Point2D location = new Point2D(position.getX(), position.getY());
            shape.setLocation(location);

            for (final Edge outgoingEdge : position.getOutgoingEdges()) {
                connectShapes(outgoingEdge.getSource(), outgoingEdge.getTarget(), outgoingEdge.getBendingPoints());
            }

            vertices.get(position.getId()).setPosition(location);
        }

        layer.refresh();
        layer.batch();
    }

    private AbstractLayoutService createLayoutService() {

        final ReverseEdgesCycleBreaker cycleBreaker = new ReverseEdgesCycleBreaker();
        final VertexLayerer vertexLayerer = new LongestPathVertexLayerer();
        final VertexOrdering vertexOrdering = createVertexOrdering();
        final VertexPositioning vertexPositioning = new DefaultVertexPositioning();

        return new SugiyamaLayoutService(cycleBreaker,
                                         vertexLayerer,
                                         vertexOrdering,
                                         vertexPositioning);
    }

    private VertexOrdering createVertexOrdering() {

        final VertexLayerPositioning vertexLayerPositioning = new MedianVertexLayerPositioning();
        final LayerCrossingCount crossingCount = new LayerCrossingCount();
        final VerticesTransposer verticesTranspose = new VerticesTransposer(crossingCount);
        return new DefaultVertexOrdering(vertexLayerPositioning,
                                         crossingCount,
                                         verticesTranspose);
    }
}

