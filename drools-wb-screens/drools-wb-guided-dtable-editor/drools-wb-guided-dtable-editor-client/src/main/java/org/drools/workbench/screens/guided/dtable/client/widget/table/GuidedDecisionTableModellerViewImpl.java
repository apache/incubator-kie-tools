package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.accordion.GuidedDecisionTableAccordion;
import org.drools.workbench.screens.guided.dtable.client.widget.table.accordion.GuidedDecisionTableAccordionItem;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.AttributeColumnConfigRow;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;
import org.uberfire.mvp.ParameterizedCommand;

public class GuidedDecisionTableModellerViewImpl extends Composite implements GuidedDecisionTableModellerView {

    private static final double VP_SCALE = 1.0;

    private static GuidedDecisionTableModellerViewImplUiBinder uiBinder = GWT.create(GuidedDecisionTableModellerViewImplUiBinder.class);

    private final RuleSelector ruleSelector = new RuleSelector();

    private final GuidedDecisionTableModellerBoundsHelper boundsHelper = new GuidedDecisionTableModellerBoundsHelper();

    @UiField
    FlowPanel accordionContainer;

    @UiField
    Button addColumn;

    @UiField
    Button editColumns;

    @UiField(provided = true)
    GridLienzoPanel gridPanel = new GridLienzoPanel() {

        @Override
        public void onResize() {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    final int width = getParent().getOffsetWidth();
                    final int height = getParent().getOffsetHeight();
                    if ((width != 0) && (height != 0)) {
                        domElementContainer.setPixelSize(width,
                                                         height);
                        lienzoPanel.setPixelSize(width,
                                                 height);
                    }

                    final TransformMediator restriction = mousePanMediator.getTransformMediator();
                    final Transform transform = restriction.adjust(gridLayer.getViewport().getTransform(),
                                                                   gridLayer.getVisibleBounds());
                    gridLayer.getViewport().setTransform(transform);
                    gridLayer.draw();
                }
            });
        }
    };

    @Inject
    private GuidedDecisionTableAccordion guidedDecisionTableAccordion;

    private VerticalPanel attributeConfigWidget = makeDefaultPanel();

    private VerticalPanel metaDataConfigWidget = makeDefaultPanel();

    private VerticalPanel conditionsConfigWidget = makeDefaultPanel();

    private VerticalPanel actionsConfigWidget = makeDefaultPanel();

    private GuidedDecisionTableAccordion accordion;

    private TransformMediator defaultTransformMediator;

    private GuidedDecisionTableModellerView.Presenter presenter;

    private final DefaultGridLayer gridLayer = defaultGridLayer();

    private final RestrictedMousePanMediator mousePanMediator = restrictedMousePanMediator();

    public GuidedDecisionTableModellerViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    DefaultGridLayer defaultGridLayer() {
        return new DefaultGridLayer() {
            @Override
            public void enterPinnedMode(final GridWidget gridWidget,
                                        final Command onStartCommand) {
                super.enterPinnedMode(gridWidget,
                                      new Command() {
                                          @Override
                                          public void execute() {
                                              onStartCommand.execute();
                                              presenter.onViewPinned(true);
                                          }
                                      });
            }

            @Override
            public void exitPinnedMode(final Command onCompleteCommand) {
                super.exitPinnedMode(new Command() {
                    @Override
                    public void execute() {
                        onCompleteCommand.execute();
                        presenter.onViewPinned(false);
                    }
                });
            }

            @Override
            public TransformMediator getDefaultTransformMediator() {
                return defaultTransformMediator;
            }
        };
    }

    RestrictedMousePanMediator restrictedMousePanMediator() {
        return new RestrictedMousePanMediator(gridLayer) {
            @Override
            protected void onMouseMove(final NodeMouseMoveEvent event) {
                super.onMouseMove(event);
                presenter.updateRadar();
            }
        };
    }

    protected void initWidget(final Widget widget) {
        super.initWidget(widget);
    }

    @Override
    public void init(final GuidedDecisionTableModellerView.Presenter presenter) {
        this.presenter = presenter;

        setupAccordion(presenter);
    }

    @PostConstruct
    public void setup() {
        setupSubMenu();
        setupGridPanel();
    }

    void setupGridPanel() {
        //Lienzo stuff - Set default scale
        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getViewport().setTransform(transform);

        //Lienzo stuff - Add mouse pan support
        defaultTransformMediator = new BoundaryTransformMediator(GuidedDecisionTableModellerViewImpl.this);
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
        mousePanMediator.setBatchDraw(true);

        //Wire-up widgets
        gridPanel.add(gridLayer);

        //Set ID on GridLienzoPanel for Selenium tests.
        gridPanel.getElement().setId("dtable_container_" + Document.get().createUniqueId());
    }

    void setupSubMenu() {
        disableButtonMenu();

        getAddColumn().addClickHandler((e) -> addColumn());
        getEditColumns().addClickHandler((e) -> editColumns());
    }

    void addColumn() {
        getPresenter().openNewGuidedDecisionTableColumnWizard();
    }

    void editColumns() {
        toggleClassName(getAccordionContainer(),
                        GuidedDecisionTableResources.INSTANCE.css().openedAccordion());

        toggleClassName(getEditColumns(),
                        "active");
    }

    void toggleClassName(final Widget widget,
                         final String className) {
        widget.getElement().toggleClassName(className);
    }

    @Override
    public void onResize() {
        gridPanel.onResize();
        getPresenter().updateRadar();
    }

    @Override
    public HandlerRegistration addKeyDownHandler(final KeyDownHandler handler) {
        return gridPanel.addKeyDownHandler(handler);
    }

    void enableButtonMenu() {
        getAddColumn().setEnabled(true);
        getEditColumns().setEnabled(true);
    }

    void disableButtonMenu() {
        getAddColumn().setEnabled(false);
        getEditColumns().setEnabled(false);
    }

    @Override
    public HandlerRegistration addContextMenuHandler(final ContextMenuHandler handler) {
        return gridPanel.addDomHandler(handler,
                                       ContextMenuEvent.getType());
    }

    @Override
    public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
        return gridPanel.addMouseDownHandler(handler);
    }

    Widget ruleInheritanceWidget() {
        final FlowPanel result = makeFlowPanel();

        result.setStyleName(GuidedDecisionTableResources.INSTANCE.css().ruleInheritance());
        result.add(ruleInheritanceLabel());
        result.add(ruleSelector());

        return result;
    }

    FlowPanel makeFlowPanel() {
        return new FlowPanel();
    }

    Widget ruleSelector() {
        getRuleSelector().setEnabled(false);

        getRuleSelector().addValueChangeHandler(e -> {
            presenter.getActiveDecisionTable().setParentRuleName(e.getValue());
        });

        return getRuleSelector();
    }

    Label ruleInheritanceLabel() {
        final Label label = new Label(GuidedDecisionTableConstants.INSTANCE.AllTheRulesInherit());

        label.setStyleName(GuidedDecisionTableResources.INSTANCE.css().ruleInheritanceLabel());

        return label;
    }

    @Override
    public void clear() {
        gridLayer.removeAll();
    }

    @Override
    public void addDecisionTable(final GuidedDecisionTableView gridWidget) {
        //Ensure the first Decision Table is visible
        if (gridLayer.getGridWidgets().isEmpty()) {
            final Point2D translation = getTranslation(gridWidget);
            final Transform t = gridLayer.getViewport().getTransform();
            t.translate(translation.getX(),
                        translation.getY());
        }
        gridLayer.add(gridWidget);
        gridLayer.batch();
    }

    private Point2D getTranslation(final GuidedDecisionTableView gridWidget) {
        final double boundsPadding = GuidedDecisionTableModellerBoundsHelper.BOUNDS_PADDING;
        final Transform t = gridLayer.getViewport().getTransform();
        final double requiredTranslateX = boundsPadding - gridWidget.getX();
        final double requiredTranslateY = boundsPadding - gridWidget.getY();
        final double actualTranslateX = t.getTranslateX();
        final double actualTranslateY = t.getTranslateY();
        final double dx = requiredTranslateX - actualTranslateX;
        final double dy = requiredTranslateY - actualTranslateY;
        return new Point2D(dx,
                           dy);
    }

    @Override
    public void removeDecisionTable(final GuidedDecisionTableView gridWidget,
                                    final Command afterRemovalCommand) {
        if (gridWidget == null) {
            return;
        }
        final Command remove = () -> {
            gridLayer.remove(gridWidget);
            gridLayer.batch();

            disableButtonMenu();

            afterRemovalCommand.execute();
        };
        if (gridLayer.isGridPinned()) {
            final GridPinnedModeManager.PinnedContext context = gridLayer.getPinnedContext();
            if (gridWidget.equals(context.getGridWidget())) {
                gridLayer.exitPinnedMode(remove);
            }
        } else {
            remove.execute();
        }
    }

    @Override
    public void setEnableColumnCreation(final boolean enabled) {
        addColumn.setEnabled(enabled);
    }

    @Override
    public void refreshRuleInheritance(final String selectedParentRuleName,
                                       final Collection<String> availableParentRuleNames) {
        ruleSelector.setRuleName(selectedParentRuleName);
        ruleSelector.setRuleNames(availableParentRuleNames);
    }

    private VerticalPanel makeDefaultPanel() {
        return new VerticalPanel() {{
            add(blankSlate());
        }};
    }

    Label blankSlate() {
        final String disabledLabelStyle = "text-muted";
        final String noColumns = GuidedDecisionTableConstants.INSTANCE.NoColumnsAvailable();

        return new Label() {{
            setText(noColumns);
            setStyleName(disabledLabelStyle);
        }};
    }

    @Override
    public void refreshAttributeWidget(final List<AttributeCol52> attributeColumns) {
        getAttributeConfigWidget().clear();

        if (attributeColumns == null || attributeColumns.isEmpty()) {
            getAccordion().getItem(GuidedDecisionTableAccordionItem.Type.ATTRIBUTE).setOpen(false);
            getAttributeConfigWidget().add(blankSlate());
            return;
        }

        for (AttributeCol52 attributeColumn : attributeColumns) {
            AttributeColumnConfigRow attributeColumnConfigRow = new AttributeColumnConfigRow();
            attributeColumnConfigRow.init(attributeColumn,
                                          getPresenter());
            getAttributeConfigWidget().add(attributeColumnConfigRow.getView());
        }
    }

    @Override
    public void refreshMetaDataWidget(final List<MetadataCol52> metaDataColumns) {
        metaDataConfigWidget.clear();

        if (metaDataColumns == null || metaDataColumns.isEmpty()) {
            accordion.getItem(GuidedDecisionTableAccordionItem.Type.METADATA).setOpen(false);
            metaDataConfigWidget.add(blankSlate());
            return;
        }

        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for (MetadataCol52 metaDataColumn : metaDataColumns) {
            HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

            final SmallLabel label = makeColumnLabel(metaDataColumn);
            hp.add(label);

            final MetadataCol52 originalColumn = metaDataColumn;
            final CheckBox chkHideColumn = new CheckBox(new StringBuilder(GuidedDecisionTableConstants.INSTANCE.HideThisColumn()).append(GuidedDecisionTableConstants.COLON).toString());
            chkHideColumn.setValue(metaDataColumn.isHideColumn());
            chkHideColumn.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(final ClickEvent event) {
                    final MetadataCol52 editedColumn = originalColumn.cloneColumn();
                    editedColumn.setHideColumn(chkHideColumn.getValue());
                    presenter.getActiveDecisionTable().updateColumn(originalColumn,
                                                                    editedColumn);
                }
            });

            hp.add(chkHideColumn);

            if (isEditable) {
                hp.add(deleteAnchor((e) -> {
                    String ms = GuidedDecisionTableConstants.INSTANCE.DeleteActionColumnWarning(metaDataColumn.getMetadata());
                    if (Window.confirm(ms)) {
                        presenter.getActiveDecisionTable().deleteColumn(metaDataColumn);
                    }
                }));
            }

            metaDataConfigWidget.add(hp);
        }
    }

    private SmallLabel makeColumnLabel(final MetadataCol52 metaDataColumn) {
        SmallLabel label = new SmallLabel(metaDataColumn.getMetadata());
        ColumnUtilities.setColumnLabelStyleWhenHidden(label,
                                                      metaDataColumn.isHideColumn());
        return label;
    }

    @Override
    public void refreshConditionsWidget(final List<CompositeColumn<? extends BaseColumn>> conditionColumns) {
        conditionsConfigWidget.clear();

        if (conditionColumns == null || conditionColumns.isEmpty()) {
            accordion.getItem(GuidedDecisionTableAccordionItem.Type.CONDITION).setOpen(false);
            conditionsConfigWidget.add(blankSlate());
            return;
        }

        //Each Pattern is a row in a vertical panel
        final VerticalPanel patternsPanel = new VerticalPanel();
        conditionsConfigWidget.add(patternsPanel);

        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for (CompositeColumn<?> conditionColumn : conditionColumns) {
            if (conditionColumn instanceof Pattern52) {
                Pattern52 p = (Pattern52) conditionColumn;
                VerticalPanel patternPanel = new VerticalPanel();
                VerticalPanel conditionsPanel = new VerticalPanel();
                HorizontalPanel patternHeaderPanel = new HorizontalPanel();
                Label patternLabel = makePatternLabel(p);
                patternHeaderPanel.add(patternLabel);
                patternPanel.add(patternHeaderPanel);
                patternPanel.add(conditionsPanel);
                patternsPanel.add(patternPanel);

                List<ConditionCol52> conditions = p.getChildColumns();
                for (ConditionCol52 c : conditions) {
                    HorizontalPanel hp = new HorizontalPanel();

                    SmallLabel conditionLabel = makeColumnLabel(c);
                    hp.add(conditionLabel);

                    final FlowPanel buttons = new FlowPanel() {{
                        add(editCondition(p,
                                          c));

                        if (isEditable) {
                            add(removeCondition(c));
                        }
                    }};

                    hp.add(buttons);

                    conditionsPanel.add(hp);
                }
            } else if (conditionColumn instanceof BRLConditionColumn) {
                BRLConditionColumn brl = (BRLConditionColumn) conditionColumn;

                HorizontalPanel patternHeaderPanel = new HorizontalPanel();
                HorizontalPanel patternPanel = new HorizontalPanel();

                SmallLabel patternLabel = makePatternLabel(brl);
                patternPanel.add(patternLabel);
                patternHeaderPanel.add(patternPanel);

                final FlowPanel buttons = new FlowPanel() {{
                    add(editCondition(brl));

                    if (isEditable) {
                        add(removeCondition(brl));
                    }
                }};

                patternPanel.add(buttons);

                patternsPanel.add(patternHeaderPanel);
            }
        }
    }

    private Label makePatternLabel(final Pattern52 p) {
        StringBuilder patternLabel = new StringBuilder();
        String factType = p.getFactType();
        String boundName = p.getBoundName();
        if (factType != null && factType.length() > 0) {
            if (p.isNegated()) {
                patternLabel.append(GuidedDecisionTableConstants.INSTANCE.negatedPattern()).append(" ").append(factType);
            } else {
                patternLabel.append(factType).append(" [").append(boundName).append("]");
            }
        }
        return new Label(patternLabel.toString());
    }

    private SmallLabel makePatternLabel(final BRLConditionColumn brl) {
        StringBuilder sb = new StringBuilder();
        sb.append(brl.getHeader());
        return new SmallLabel(sb.toString());
    }

    private SmallLabel makeColumnLabel(final ConditionCol52 cc) {
        StringBuilder sb = new StringBuilder();
        if (cc.isBound()) {
            sb.append(cc.getBinding());
            sb.append(" : ");
        }
        sb.append(cc.getHeader());
        SmallLabel label = new SmallLabel(sb.toString());
        if (cc.isHideColumn()) {
            label.setStylePrimaryName(GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden());
        }
        return label;
    }

    private Widget editCondition(final Pattern52 origPattern,
                                 final ConditionCol52 origCol) {
        return makeEditColumnWidget(GuidedDecisionTableConstants.INSTANCE.EditThisColumnsConfiguration(),
                                    () -> presenter.getActiveDecisionTable().editCondition(origPattern,
                                                                                           origCol));
    }

    private Widget editCondition(final BRLConditionColumn origCol) {
        return makeEditColumnWidget(GuidedDecisionTableConstants.INSTANCE.EditThisColumnsConfiguration(),
                                    () -> presenter.getActiveDecisionTable().editCondition(origCol));
    }

    private Widget makeEditColumnWidget(final String caption,
                                        final Command command) {

        return editAnchor((e) -> command.execute());
    }

    private Widget editAnchor(final ClickHandler clickHandler) {
        return anchor(GuidedDecisionTableConstants.INSTANCE.Edit(),
                      clickHandler);
    }

    private Widget deleteAnchor(final ClickHandler clickHandler) {
        return anchor(GuidedDecisionTableConstants.INSTANCE.Delete(),
                      clickHandler);
    }

    private Anchor anchor(final String text,
                          final ClickHandler clickHandler) {
        return new Anchor() {{
            setText(text);
            addClickHandler(clickHandler);
        }};
    }

    private Widget removeCondition(final ConditionCol52 column) {
        if (column instanceof BRLConditionColumn) {
            return makeRemoveConditionWidget(column,
                                             (command) -> {
                                                 if (!presenter.getActiveDecisionTable().canConditionBeDeleted((BRLConditionColumn) column)) {
                                                     Window.alert(GuidedDecisionTableConstants.INSTANCE.UnableToDeleteConditionColumn0(column.getHeader()));
                                                     return;
                                                 }
                                                 command.execute();
                                             });
        }

        return makeRemoveConditionWidget(column,
                                         (command) -> {
                                             if (!presenter.getActiveDecisionTable().canConditionBeDeleted(column)) {
                                                 Window.alert(GuidedDecisionTableConstants.INSTANCE.UnableToDeleteConditionColumn0(column.getHeader()));
                                                 return;
                                             }
                                             command.execute();
                                         });
    }

    private Widget makeRemoveConditionWidget(final ConditionCol52 column,
                                             final ParameterizedCommand<Command> command) {

        final ClickHandler clickHandler = (e) -> command.execute(() -> {
            String cm = GuidedDecisionTableConstants.INSTANCE.DeleteConditionColumnWarning0(column.getHeader());
            if (Window.confirm(cm)) {
                presenter.getActiveDecisionTable().deleteColumn(column);
            }
        });

        return deleteAnchor(clickHandler);
    }

    @Override
    public void refreshActionsWidget(final List<ActionCol52> actionColumns) {
        actionsConfigWidget.clear();

        if (actionColumns == null || actionColumns.isEmpty()) {
            accordion.getItem(GuidedDecisionTableAccordionItem.Type.ACTION).setOpen(false);
            actionsConfigWidget.add(blankSlate());
            return;
        }

        //Each Action is a row in a vertical panel
        final VerticalPanel actionsPanel = new VerticalPanel();
        this.actionsConfigWidget.add(actionsPanel);

        //Add Actions to panel
        final boolean isEditable = presenter.isActiveDecisionTableEditable();
        for (ActionCol52 actionColumn : actionColumns) {
            HorizontalPanel hp = new HorizontalPanel();

            Label actionLabel = makeColumnLabel(actionColumn);
            hp.add(actionLabel);

            final FlowPanel buttons = new FlowPanel() {{
                add(editAction(actionColumn));

                if (isEditable) {
                    add(deleteAnchor((e) -> {
                        final String cm = GuidedDecisionTableConstants.INSTANCE.DeleteActionColumnWarning(actionColumn.getHeader());

                        if (Window.confirm(cm)) {
                            presenter.getActiveDecisionTable().deleteColumn(actionColumn);
                        }
                    }));
                }
            }};

            hp.add(buttons);

            actionsPanel.add(hp);
        }
    }

    private SmallLabel makeColumnLabel(final ActionCol52 actionColumn) {
        SmallLabel label = new SmallLabel(actionColumn.getHeader());
        if (actionColumn.isHideColumn()) {
            label.setStylePrimaryName(GuidedDecisionTableResources.INSTANCE.css().columnLabelHidden());
        }
        return label;
    }

    private Widget editAction(final ActionCol52 actionColumn) {
        return makeEditColumnWidget(GuidedDecisionTableConstants.INSTANCE.EditThisActionColumnConfiguration(),
                                    () -> presenter.getActiveDecisionTable().editAction(actionColumn));
    }

    @Override
    public void refreshColumnsNote(final boolean hasColumnDefinitions) {
        getGuidedDecisionTableAccordion().setColumnsNoteInfoHidden(hasColumnDefinitions);
    }

    @Override
    public void setZoom(final int zoom) {
        //Set zoom preserving translation
        final Transform transform = new Transform();
        final double tx = gridPanel.getViewport().getTransform().getTranslateX();
        final double ty = gridPanel.getViewport().getTransform().getTranslateY();
        transform.translate(tx,
                            ty);
        transform.scale(zoom / 100.0);

        //Ensure the change in zoom keeps the view in bounds. IGridLayer's visibleBounds depends
        //on the Viewport Transformation; so set it to the "proposed" transformation before checking.
        gridPanel.getViewport().setTransform(transform);
        final TransformMediator restriction = mousePanMediator.getTransformMediator();
        final Transform newTransform = restriction.adjust(transform,
                                                          gridLayer.getVisibleBounds());
        gridPanel.getViewport().setTransform(newTransform);
        gridPanel.getViewport().batch();
    }

    @Override
    public void onInsertColumn() {
        addColumn();
    }

    @Override
    public GridLayer getGridLayerView() {
        return gridLayer;
    }

    @Override
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public Bounds getBounds() {
        if (presenter == null) {
            return boundsHelper.getBounds(Collections.emptySet());
        } else {
            return boundsHelper.getBounds(presenter.getAvailableDecisionTables());
        }
    }

    @Override
    public void select(final GridWidget selectedGridWidget) {
        enableButtonMenu();
        getRuleSelector().setEnabled(true);
        getGridLayer().select(selectedGridWidget);
    }

    @Override
    public void selectLinkedColumn(final GridColumn<?> link) {
        gridLayer.selectLinkedColumn(link);
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return gridLayer.getGridWidgets();
    }

    GuidedDecisionTableAccordion getGuidedDecisionTableAccordion() {
        return guidedDecisionTableAccordion;
    }

    void setupAccordion(final Presenter presenter) {
        accordion = makeAccordion(presenter);

        final Widget widget = asWidget(accordion);

        getAccordionContainer().add(widget);
        getAccordionContainer().add(ruleInheritanceWidget());
    }

    FlowPanel getAccordionContainer() {
        return accordionContainer;
    }

    Widget asWidget(final GuidedDecisionTableAccordion accordion) {
        final GuidedDecisionTableAccordion.View accordionView = accordion.getView();

        return ElementWrapperWidget.getWidget(accordionView.getElement());
    }

    GuidedDecisionTableAccordion makeAccordion(final Presenter presenter) {
        final GuidedDecisionTableAccordion accordion = getGuidedDecisionTableAccordion();

        accordion.addItem(GuidedDecisionTableAccordionItem.Type.ATTRIBUTE,
                          getAttributeConfigWidget());
        accordion.addItem(GuidedDecisionTableAccordionItem.Type.METADATA,
                          getMetaDataConfigWidget());
        accordion.addItem(GuidedDecisionTableAccordionItem.Type.CONDITION,
                          getConditionsConfigWidget());
        accordion.addItem(GuidedDecisionTableAccordionItem.Type.ACTION,
                          getActionsConfigWidget());

        return accordion;
    }

    VerticalPanel getAttributeConfigWidget() {
        return attributeConfigWidget;
    }

    VerticalPanel getMetaDataConfigWidget() {
        return metaDataConfigWidget;
    }

    VerticalPanel getConditionsConfigWidget() {
        return conditionsConfigWidget;
    }

    VerticalPanel getActionsConfigWidget() {
        return actionsConfigWidget;
    }

    RuleSelector getRuleSelector() {
        return ruleSelector;
    }

    Button getAddColumn() {
        return addColumn;
    }

    Button getEditColumns() {
        return editColumns;
    }

    Presenter getPresenter() {
        return presenter;
    }

    GuidedDecisionTableAccordion getAccordion() {
        return accordion;
    }

    DefaultGridLayer getGridLayer() {
        return gridLayer;
    }

    interface GuidedDecisionTableModellerViewImplUiBinder extends UiBinder<Widget, GuidedDecisionTableModellerViewImpl> {

    }
}
