package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.toolbox.ToolboxVisibilityExecutors;
import com.ait.lienzo.client.core.shape.toolbox.grid.AutoGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.client.core.shape.toolbox.items.LayerToolbox;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ToolboxFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.WiresShapeToolbox;
import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.ILocationAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import com.google.gwt.dom.client.Style;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;

public class ToolboxExample extends BaseExample implements Example {

    private HTMLButtonElement showToolboxesButton;
    private HTMLButtonElement hideToolboxesButton;

    private WiresShape rectangleRed;
    private JsArray<LayerToolbox> toolboxes;
    private HandlerRegistrationManager handlerRegistrationManager;

    public ToolboxExample(final String title) {
        super(title);
    }

    @Override
    public void init(LienzoPanel panel, HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Style.Display.INLINE_BLOCK.getCssName();

        showToolboxesButton = createButton("Show Toolbox", this::showToolboxes);
        topDiv.appendChild(showToolboxesButton);
        hideToolboxesButton = createButton("Hide Toolbox", this::hideToolboxes);
        topDiv.appendChild(hideToolboxesButton);
    }

    @Override
    public void destroy() {
        super.destroy();
        showToolboxesButton.remove();
        hideToolboxesButton.remove();
        handlerRegistrationManager.removeHandler();
    }

    @Override
    public void run() {

        handlerRegistrationManager = new HandlerRegistrationManager();

        // Wires Manager.
        WiresManager wires_manager = WiresManager.get(layer);
        wires_manager.setContainmentAcceptor(IContainmentAcceptor.ALL);
        wires_manager.setConnectionAcceptor(IConnectionAcceptor.ALL.ALL);
        wires_manager.setDockingAcceptor(IDockingAcceptor.ALL);
        wires_manager.setLocationAcceptor(ILocationAcceptor.ALL);
        wires_manager.setControlPointsAcceptor(IControlPointsAcceptor.ALL);

        // Red rectangle.
        rectangleRed =
                new WiresShape(new MultiPath()
                                       .rect(0, 0, 100, 100)
                                       .setStrokeColor("#FF0000")
                                       .setFillColor("#FF0000"))
                        .setDraggable(true);
        rectangleRed.setLocation(new Point2D(350, 150));
        wires_manager.register(rectangleRed);
        wires_manager.getMagnetManager().createMagnets(rectangleRed);
        WiresShapeToolbox rectangleRedToolbox = appendToolbox(rectangleRed);
        addToolboxButton(rectangleRedToolbox, ColorName.GREEN.getColorString());
        addToolboxButton(rectangleRedToolbox, ColorName.YELLOW.getColorString());

        toolboxes = new JsArray<>(rectangleRedToolbox);

        HandlerRegistration layerClickReg = layer.addNodeMouseClickHandler(event -> onLayerClick());
        handlerRegistrationManager.register(layerClickReg);
    }

    private static final double BUTTON_SIZE = 15;
    private static final double BUTTON_PADDING = 5;
    private static final Direction TOOLBOX_AT = Direction.NORTH_WEST;
    private static final Direction GRID_TOWARDS = Direction.SOUTH_WEST;

    private WiresShapeToolbox appendToolbox(WiresShape shape) {
        final Layer topLayer = getTopLayer();
        final WiresShapeToolbox toolbox = new WiresShapeToolbox(shape)
                .attachTo(topLayer)
                .at(TOOLBOX_AT)
                .grid(new AutoGrid.Builder()
                              .forBoundingBox(shape.getGroup().getBoundingBox())
                              .withPadding(BUTTON_PADDING)
                              .withIconSize(BUTTON_SIZE)
                              .towards(GRID_TOWARDS)
                              .build())
                .useShowExecutor(ToolboxVisibilityExecutors.upScaleX())
                .useHideExecutor(ToolboxVisibilityExecutors.downScaleX());
        HandlerRegistration rectangleRedClickReg = shape.getGroup().addNodeMouseClickHandler(event -> {
            if (toolbox.isVisible()) {
                toolbox.hide();
            } else {
                toolbox.show();
            }
        });
        handlerRegistrationManager.register(rectangleRedClickReg);
        return toolbox;
    }

    private static void addToolboxButton(WiresShapeToolbox toolbox,
                                         String color) {
        final ButtonItem button =
                ToolboxFactory.INSTANCE.buttons()
                        .button(new Rectangle(BUTTON_SIZE, BUTTON_SIZE)
                                        .setFillColor(color))
                        // .decorate(createDecorator())
                        // .tooltip(tooltip.createItem(title))
                        // .onMouseEnter(event -> onMouseEnter())
                        // .onMouseExit(event -> onMouseExit())
                        .onClick(event -> DomGlobal.console.log("onToolboxButtonClick [" + color + "]"));
        toolbox.add(button);
    }

    private void onLayerClick() {
        DomGlobal.console.log("Clicking on layer!");
        hideToolboxes();
    }

    private void showToolboxes() {
        DomGlobal.console.log("[Toolbox] Showing all");
        for (int i = 0; i < toolboxes.length; i++) {
            LayerToolbox toolbox = toolboxes.getAt(i);
            toolbox.show();
        }
        draw();
    }

    private void hideToolboxes() {
        DomGlobal.console.log("[Toolbox] Hiding all");
        for (int i = 0; i < toolboxes.length; i++) {
            LayerToolbox toolbox = toolboxes.getAt(i);
            toolbox.hide();
        }
        draw();
    }

    private Layer getTopLayer() {
        return layer.getScene().getTopLayer();
    }

    private void draw() {
        layer.draw();
        getTopLayer().draw();
    }
}
