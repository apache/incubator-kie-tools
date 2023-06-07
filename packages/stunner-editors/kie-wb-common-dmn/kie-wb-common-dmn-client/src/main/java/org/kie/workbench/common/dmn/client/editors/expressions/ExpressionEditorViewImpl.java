/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.editors.expressions;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.ClearExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillContextExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillDecisionTableExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillFunctionExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillInvocationExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillListExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillLiteralExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.FillRelationExpressionCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.commands.UpdateCanvasNodeNameCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DataTypeProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.EntryInfo;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ModelsFromDocument;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PMMLParam;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionEditorService;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionPropsFiller;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.js.DMNLoader;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BoundaryTransformMediator;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationEditCell;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationEscapeGridCell;
import org.kie.workbench.common.dmn.client.widgets.grid.keyboard.KeyboardOperationInvokeContextMenuForSelectedCell;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelContainer;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveDown;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveLeft;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveRight;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveUp;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static java.util.stream.Stream.concat;
import static org.kie.workbench.common.dmn.api.definition.HasName.NOP;
import static org.kie.workbench.common.dmn.api.definition.model.ItemDefinition.ITEM_DEFINITION_COMPARATOR;
import static org.kie.workbench.common.dmn.api.definition.model.common.DomainObjectSearcherHelper.matches;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BUILT_IN_TYPE_COMPARATOR;

@Templated
@Dependent
public class ExpressionEditorViewImpl implements ExpressionEditorView {

    static final double VP_SCALE = 1.0;

    private ExpressionEditorView.Presenter presenter;

    @DataField("returnToDRGLink")
    private HTMLButtonElement returnToDRGLink;

    @DataField("returnToDRGLabel")
    private Span returnToDRGLabel;

    @DataField("expressionName")
    private Span expressionName;

    @DataField("expressionType")
    private Span expressionType;

    @DataField("dmn-table")
    private DMNGridPanelContainer gridPanelContainer;

    private TranslationService translationService;
    private ListSelectorView.Presenter listSelector;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private DefaultCanvasCommandFactory canvasCommandFactory;
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private Event<DomainObjectSelectionEvent> domainObjectSelectionEvent;
    private Event<ExpressionEditorChanged> editorSelectedEvent;
    private Event<DataTypePageTabActiveEvent> dataTypePageActiveEvent;
    private PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;
    private ItemDefinitionUtils itemDefinitionUtils;

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private CellEditorControlsView.Presenter cellEditorControls;
    private RestrictedMousePanMediator mousePanMediator;
    private ExpressionContainerGrid expressionContainerGrid;
    private String nodeUUID;
    private HasExpression hasExpression;
    private Optional<HasName> hasName;
    private boolean isOnlyVisualChangeAllowed;
    private UpdateCanvasNodeNameCommand updateCanvasNodeNameCommand;
    private String selectedUUID;

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final HTMLButtonElement returnToDRGLink,
                                    final Span returnToDRGLabel,
                                    final Span expressionName,
                                    final Span expressionType,
                                    final @DMNEditor DMNGridPanelContainer gridPanelContainer,
                                    final TranslationService translationService,
                                    final ListSelectorView.Presenter listSelector,
                                    final SessionManager sessionManager,
                                    final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                    final @DMNEditor DefaultCanvasCommandFactory canvasCommandFactory,
                                    final @DMNEditor Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                    final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                    final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent,
                                    final Event<ExpressionEditorChanged> editorSelectedEvent,
                                    final Event<DataTypePageTabActiveEvent> dataTypePageActiveEvent,
                                    final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider,
                                    final DefinitionUtils definitionUtils,
                                    final ItemDefinitionUtils itemDefinitionUtils) {
        this.returnToDRGLink = returnToDRGLink;
        this.returnToDRGLabel = returnToDRGLabel;
        this.expressionName = expressionName;
        this.expressionType = expressionType;
        this.gridPanelContainer = gridPanelContainer;
        this.translationService = translationService;
        this.listSelector = listSelector;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.domainObjectSelectionEvent = domainObjectSelectionEvent;
        this.editorSelectedEvent = editorSelectedEvent;
        this.dataTypePageActiveEvent = dataTypePageActiveEvent;
        this.pmmlDocumentMetadataProvider = pmmlDocumentMetadataProvider;
        this.itemDefinitionUtils = itemDefinitionUtils;
        this.updateCanvasNodeNameCommand = new UpdateCanvasNodeNameCommand(sessionManager,
                                                                           definitionUtils,
                                                                           canvasCommandFactory);
    }

    @Override
    public void init(final ExpressionEditorView.Presenter presenter) {
        this.presenter = presenter;
        ExpressionEditorService.registerExpressionEditorView(this);
    }

    @Override
    public void bind(final DMNSession session) {
        this.gridPanel = session.getGridPanel();
        this.gridLayer = session.getGridLayer();
        this.cellEditorControls = session.getCellEditorControls();
        this.mousePanMediator = session.getMousePanMediator();

        setupGridPanel();
        setupGridWidget();
        setupGridWidgetPanControl();
    }

    protected void setupGridPanel() {
        final Transform transform = new Transform().scale(VP_SCALE);
        gridPanel.getElement().setId("dmn_container_" + com.google.gwt.dom.client.Document.get().createUniqueId());
        gridPanel.getViewport().setTransform(transform);

        final BaseGridWidgetKeyboardHandler handler = new BaseGridWidgetKeyboardHandler(gridLayer);
        addKeyboardOperation(handler, new KeyboardOperationEditCell(gridLayer));
        addKeyboardOperation(handler, new KeyboardOperationEscapeGridCell(gridLayer));
        addKeyboardOperation(handler, new KeyboardOperationMoveLeft(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationMoveRight(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationMoveUp(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationMoveDown(gridLayer, gridPanel));
        addKeyboardOperation(handler, new KeyboardOperationInvokeContextMenuForSelectedCell(gridLayer));
        gridPanel.addKeyDownHandler(handler);

        gridPanelContainer.clear();
        gridPanelContainer.setWidget(gridPanel);
    }

    void addKeyboardOperation(final BaseGridWidgetKeyboardHandler handler,
                              final KeyboardOperation operation) {
        handler.addOperation(operation);
    }

    protected void setupGridWidget() {
        expressionContainerGrid = new ExpressionContainerGrid(gridLayer,
                                                              cellEditorControls,
                                                              translationService,
                                                              listSelector,
                                                              sessionManager,
                                                              sessionCommandManager,
                                                              canvasCommandFactory,
                                                              expressionEditorDefinitionsSupplier,
                                                              getExpressionGridCacheSupplier(),
                                                              this::setExpressionTypeText,
                                                              this::setExpressionNameText,
                                                              refreshFormPropertiesEvent,
                                                              domainObjectSelectionEvent);
        gridLayer.removeAll();
        gridLayer.add(expressionContainerGrid);
        gridLayer.select(expressionContainerGrid);
        gridLayer.enterPinnedMode(expressionContainerGrid,
                                  () -> {/*Nothing*/});
    }

    // This class (ExpressionEditorViewImpl) is instantiated when injected into SessionDiagramEditorScreen
    // which is before a Session has been created and the ExpressionGridCache CanvasControl has been registered.
    // Therefore we need to defer instance access to a Supplier.
    protected Supplier<ExpressionGridCache> getExpressionGridCacheSupplier() {
        return () -> ((DMNSession) sessionManager.getCurrentSession()).getExpressionGridCache();
    }

    protected void setupGridWidgetPanControl() {
        final TransformMediator defaultTransformMediator = new BoundaryTransformMediator(expressionContainerGrid);
        mousePanMediator.setTransformMediator(defaultTransformMediator);
        mousePanMediator.setBatchDraw(true);
        gridLayer.setDefaultTransformMediator(defaultTransformMediator);
        gridPanel.getViewport().getMediators().push(mousePanMediator);
    }

    @Override
    public void setReturnToLinkText(final String text) {
        returnToDRGLabel.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ReturnToLink, text));
    }

    @Override
    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName,
                              final boolean isOnlyVisualChangeAllowed) {
        this.nodeUUID = nodeUUID;
        this.hasExpression = hasExpression;
        this.hasName = hasName;
        this.isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed;
        this.selectedUUID = nodeUUID;
        expressionContainerGrid.setExpression(nodeUUID,
                                              hasExpression,
                                              hasName,
                                              isOnlyVisualChangeAllowed);
        setExpressionNameText(hasName);
        setExpressionTypeText(Optional.ofNullable(hasExpression.getExpression()));
    }

    public ExpressionContainerGrid getExpressionContainerGrid() {
        return expressionContainerGrid;
    }

    @Override
    public void setExpressionNameText(final Optional<HasName> hasName) {
        hasName.ifPresent(name -> expressionName.setTextContent(name.getName().getValue()));
    }

    @Override
    public void setExpressionTypeText(final Optional<Expression> expression) {
        final String expressionTypeText = expressionEditorDefinitionsSupplier.get().getExpressionEditorDefinition(expression).get().getName();
        expressionType.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ExpressionTypeText,
                                                                expression.isPresent() ?
                                                                        expressionTypeText :
                                                                        "<" + expressionTypeText + ">"));
    }

    @SuppressWarnings("unused")
    public void onEditorSelectedPageEvent(@Observes MultiPageEditorSelectedPageEvent editorSelectedPageEvent) {
        reloadEditor();
    }

    void loadNewBoxedExpressionEditor() {
        ExpressionProps expression = ExpressionPropsFiller.buildAndFillJsInteropProp(hasExpression.getExpression(), getExpressionName(), getTypeRef());
        String decisionNodeId = null;
        if (hasExpression instanceof Decision) {
            decisionNodeId = ((Decision) hasExpression).getId().getValue();
        } else if (hasExpression.getExpression() instanceof FunctionDefinition) {
            decisionNodeId = getBusinessKnowledgeModel().getId().getValue();
        }
        DMNLoader.renderBoxedExpressionEditor(
                ".kie-dmn-new-expression-editor",
                decisionNodeId,
                expression,
                concat(retrieveDefaultDataTypeProps(), retrieveCustomDataTypeProps()).toArray(DataTypeProps[]::new),
                hasExpression.isClearSupported(),
                buildPmmlParams()
        );
    }

    @Override
    public void selectDomainObject(final String uuid) {
        this.selectedUUID = uuid;
        fireDomainObjectSelectionEvent(findDomainObject(uuid));
    }

    DomainObject findDomainObject(final String uuid) {

        if (currentDomainObjectMatches(uuid)) {
            return (DomainObject) getHasExpression();
        } else if (businessKnowledgeModelMatches(uuid)) {
            return getBusinessKnowledgeModel();
        } else {
            return findDomainObjectInCurrentExpression(uuid);
        }
    }

    DomainObject findDomainObjectInCurrentExpression(final String uuid) {

        if (!Objects.isNull(getHasExpression().getExpression())) {
            final Optional<DomainObject> domainObject = getHasExpression().getExpression().findDomainObject(uuid);
            if (domainObject.isPresent()) {
                return domainObject.get();
            } else if (innerExpressionMatches(uuid)) {
                return (DomainObject) getHasExpression();
            }
        }
        return new NOPDomainObject();
    }

    BusinessKnowledgeModel getBusinessKnowledgeModel() {
        return ((BusinessKnowledgeModel) hasExpression.getExpression().asDMNModelInstrumentedBase().getParent());
    }

    boolean currentDomainObjectMatches(final String uuid) {
        return getHasExpression() instanceof DomainObject
                && matches(((DomainObject) getHasExpression()), uuid);
    }

    boolean innerExpressionMatches(final String uuid) {

        return getHasExpression() instanceof DomainObject
                && !Objects.isNull(getHasExpression().getExpression())
                && Objects.equals(getHasExpression().getExpression().getId().getValue(), uuid);
    }

    boolean businessKnowledgeModelMatches(final String uuid) {

        return getHasExpression().getExpression() instanceof FunctionDefinition
                && getHasExpression().getExpression().asDMNModelInstrumentedBase().getParent() instanceof BusinessKnowledgeModel
                && (matches(getBusinessKnowledgeModel(), uuid) || encapsulatedLogicMatches(uuid));
    }

    boolean encapsulatedLogicMatches(final String uuid) {

        final BusinessKnowledgeModel bkm = getBusinessKnowledgeModel();
        return !Objects.isNull(bkm)
                && Objects.equals(bkm.getEncapsulatedLogic().getId().getValue(), uuid);
    }

    void fireDomainObjectSelectionEvent(final DomainObject domainObject) {
        final Optional<CanvasHandler> canvasHandler = getCanvasHandler();
        if (!canvasHandler.isPresent()) {
            return;
        }

        final Optional<Node> domainObjectNode = findDomainObjectNodeByDomainObject(domainObject);

        if (domainObjectNode.isPresent()) {
            refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(getCurrentSession(), domainObjectNode.get().getUUID()));
        } else {
            domainObjectSelectionEvent.fire(new DomainObjectSelectionEvent(canvasHandler.get(), domainObject));
        }
    }

    private Optional<Node> findDomainObjectNodeByDomainObject(final DomainObject domainObject) {
        return getCanvasHandler()
                .map(canvasHandler -> {
                    final Graph<?, Node> graph = canvasHandler.getDiagram().getGraph();
                    return StreamSupport
                            .stream(graph.nodes().spliterator(), false)
                            .filter(node -> node.getContent() instanceof Definition)
                            .filter(node -> Objects.equals(domainObject, ((Definition) node.getContent()).getDefinition()))
                            .findFirst();
                })
                .orElse(Optional.empty());
    }

    private Optional<CanvasHandler> getCanvasHandler() {
        final Optional<ClientSession> session = Optional.ofNullable(sessionManager.getCurrentSession());
        return session.map(ClientSession::getCanvasHandler);
    }

    private ClientSession getCurrentSession() {
        return sessionManager.getCurrentSession();
    }

    public void updateExpression(final ExpressionProps expressionProps) {
        ExpressionType logicType = ExpressionType.getTypeByText(expressionProps.logicType);
        switch (logicType) {
            case CONTEXT:
                executeUndoableExpressionCommand(new FillContextExpressionCommand(getHasExpression(),
                                                                                  (ContextProps) expressionProps,
                                                                                  getEditorSelectedEvent(),
                                                                                  getNodeUUID(),
                                                                                  itemDefinitionUtils,
                                                                                  getHasName()));
                break;
            case DECISION_TABLE:
                executeUndoableExpressionCommand(new FillDecisionTableExpressionCommand(getHasExpression(),
                                                                                        (DecisionTableProps) expressionProps,
                                                                                        getEditorSelectedEvent(),
                                                                                        getNodeUUID(),
                                                                                        itemDefinitionUtils,
                                                                                        getHasName()));
                break;
            case FUNCTION:
                executeUndoableExpressionCommand(new FillFunctionExpressionCommand(getHasExpression(),
                                                                                   (FunctionProps) expressionProps,
                                                                                   getEditorSelectedEvent(),
                                                                                   getNodeUUID(),
                                                                                   itemDefinitionUtils,
                                                                                   getHasName()));
                break;
            case INVOCATION:
                executeUndoableExpressionCommand(new FillInvocationExpressionCommand(getHasExpression(),
                                                                                     (InvocationProps) expressionProps,
                                                                                     getEditorSelectedEvent(),
                                                                                     getNodeUUID(),
                                                                                     itemDefinitionUtils,
                                                                                     getHasName()));
                break;
            case LIST:
                executeUndoableExpressionCommand(new FillListExpressionCommand(getHasExpression(),
                                                                               (ListProps) expressionProps,
                                                                               getEditorSelectedEvent(),
                                                                               getNodeUUID(),
                                                                               itemDefinitionUtils,
                                                                               getHasName()));
                break;
            case LITERAL_EXPRESSION:
                executeUndoableExpressionCommand(new FillLiteralExpressionCommand(getHasExpression(),
                                                                                  (LiteralProps) expressionProps,
                                                                                  getEditorSelectedEvent(),
                                                                                  getNodeUUID(),
                                                                                  itemDefinitionUtils,
                                                                                  getHasName()));
                break;
            case RELATION:
                executeUndoableExpressionCommand(new FillRelationExpressionCommand(getHasExpression(),
                                                                                   (RelationProps) expressionProps,
                                                                                   getEditorSelectedEvent(),
                                                                                   getNodeUUID(),
                                                                                   itemDefinitionUtils,
                                                                                   getHasName()));
                break;
            case UNDEFINED:
                executeUndoableExpressionCommand(new ClearExpressionCommand(getHasExpression(),
                                                                            expressionProps,
                                                                            getEditorSelectedEvent(),
                                                                            getNodeUUID(),
                                                                            itemDefinitionUtils,
                                                                            getHasName()));
                getExpressionContainerGrid().clearExpression(getNodeUUID());
                break;
            default:
                throw new UnsupportedOperationException("Logic type: " + logicType + " is currently unsupported");
        }

        if (Objects.nonNull(selectedUUID)) {
            refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(getCurrentSession(), null));
            fireDomainObjectSelectionEvent(findDomainObject(selectedUUID));
        }

        syncExpressionWithOlderEditor();
    }

    public void openDataTypePage() {
        dataTypePageActiveEvent.fire(new DataTypePageTabActiveEvent());
    }

    public ExpressionProps getDefaultExpressionDefinition(String logicType, String dataType) {
        return expressionEditorDefinitionsSupplier
                .get()
                .getExpressionEditorDefinition(ExpressionType.getTypeByText(logicType))
                .map(expressionExpressionEditorDefinition -> generateExpressionProps(expressionExpressionEditorDefinition, dataType))
                .orElseThrow(IllegalStateException::new);
    }

    private ExpressionProps generateExpressionProps(final ExpressionEditorDefinition<Expression> expressionExpressionEditorDefinition,
                                                    final String dataType) {
        final Optional<Expression> modelExpression = expressionExpressionEditorDefinition.getModelClass();
        expressionExpressionEditorDefinition.enrichRootExpression(getNodeUUID(),
                                                                  hasExpression,
                                                                  modelExpression.orElse(null),
                                                                  dataType);
        return modelExpression
                .map(expression -> ExpressionPropsFiller.buildAndFillJsInteropProp(expression, getExpressionName(), getTypeRef()))
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * It executes a given expression command. Before executing it, it creates and UNDO command with the current model
     * status. Statement ordering matters: the UNDO command MUST be called before executing the expression command change.
     *
     * @param expressionCommand
     */
    void executeUndoableExpressionCommand(final FillExpressionCommand expressionCommand) {

        createUndoCommand();
        if (!expressionCommand.isCurrentExpressionOfTheSameType()) {
            getHasExpression().setExpression(null);
            getExpressionContainerGrid().clearExpression(getNodeUUID());
        }

        boolean isSameExpressionName =
                getHasName().orElse(NOP).getName().getValue().equals(expressionCommand.getExpressionProps().name);

        expressionCommand.execute();

        if (!isSameExpressionName) {
            getUpdateCanvasNodeNameCommand().execute(getNodeUUID(), getHasName().orElse(NOP));
        }
    }

    Stream<DataTypeProps> retrieveDefaultDataTypeProps() {
        return Stream.of(BuiltInType.values())
                .sorted(BUILT_IN_TYPE_COMPARATOR)
                .map(builtInType -> new DataTypeProps(builtInType.name(), builtInType.getName(), false));
    }

    Stream<DataTypeProps> retrieveCustomDataTypeProps() {
        return itemDefinitionUtils
                .all().stream()
                .filter(itemDefinition -> itemDefinition.getName() != null)
                .sorted(ITEM_DEFINITION_COMPARATOR)
                .map(itemDefinition -> {
                    final String itemDefinitionName = itemDefinition.getName().getValue();
                    return new DataTypeProps(itemDefinitionName, itemDefinitionName, true);
                });
    }

    private String getExpressionName() {
        final HasName fallbackHasName = hasExpression instanceof HasName ? (HasName) hasExpression : HasName.NOP;
        return hasName.orElse(fallbackHasName).getValue().getValue();
    }

    @SuppressWarnings("unchecked")
    private String getTypeRef() {
        QName qName = BuiltInType.UNDEFINED.asQName();
        if (hasExpression instanceof HasVariable) {
            qName = ((HasVariable<InformationItemPrimary>) hasExpression).getVariable().getTypeRef();
        } else if (hasExpression.getExpression() != null && hasExpression.getExpression().asDMNModelInstrumentedBase().getParent() instanceof HasVariable) {
            final HasVariable<InformationItemPrimary> parent = (HasVariable<InformationItemPrimary>) hasExpression.getExpression().asDMNModelInstrumentedBase().getParent();
            qName = parent != null && parent.getVariable() != null ? parent.getVariable().getTypeRef() : BuiltInType.UNDEFINED.asQName();
        }
        return qName.getLocalPart();
    }

    private PMMLParam[] buildPmmlParams() {
        return pmmlDocumentMetadataProvider.getPMMLDocumentNames()
                .stream()
                .map(documentToPMMLParamMapper())
                .toArray(PMMLParam[]::new);
    }

    private Function<String, PMMLParam> documentToPMMLParamMapper() {
        return documentName -> {
            final ModelsFromDocument[] modelsFromDocuments = pmmlDocumentMetadataProvider
                    .getPMMLDocumentModels(documentName)
                    .stream()
                    .map(modelToEntryInfoMapper(documentName))
                    .toArray(ModelsFromDocument[]::new);
            return new PMMLParam(documentName, modelsFromDocuments);
        };
    }

    private Function<String, ModelsFromDocument> modelToEntryInfoMapper(String documentName) {
        return modelName -> {
            final EntryInfo[] parametersFromModel = pmmlDocumentMetadataProvider.getPMMLDocumentModelParameterNames(documentName, modelName)
                    .stream()
                    .map(parameter -> new EntryInfo(new Id().getValue(), parameter, BuiltInType.ANY.getName(), null))
                    .toArray(EntryInfo[]::new);
            return new ModelsFromDocument(modelName, parametersFromModel);
        };
    }

    @EventHandler("returnToDRGLink")
    void onClickReturnToDRGLink(final ClickEvent event) {
        presenter.exit();
    }

    @Override
    public void onResize() {
        gridPanelContainer.onResize();
    }

    @Override
    public void refresh() {
        gridLayer.batch();
    }

    @Override
    public void reloadEditor() {
        if (isReactBoxedExpressionVisible()) {
            loadNewBoxedExpressionEditor();
            syncExpressionWithOlderEditor();
        }
    }

    boolean isReactBoxedExpressionVisible() {
        return DomGlobal.document.getElementsByClassName("kie-dmn-new-expression-editor").length > 0;
    }

    void syncExpressionWithOlderEditor() {
        getExpressionGridCacheSupplier().get().removeExpressionGrid(getNodeUUID());
        getExpressionContainerGrid().setExpression(getNodeUUID(),
                                                   getHasExpression(),
                                                   getHasName(),
                                                   isOnlyVisualChangeAllowed);
    }

    @Override
    public void setFocus() {
        loadNewBoxedExpressionEditor();
    }

    HasExpression getHasExpression() {
        return hasExpression;
    }

    public UpdateCanvasNodeNameCommand getUpdateCanvasNodeNameCommand() {
        return updateCanvasNodeNameCommand;
    }

    Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    String getNodeUUID() {
        return nodeUUID;
    }

    /**
     * It creates an UNDO command with the current expression status
     */
    protected void createUndoCommand() {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = createCommandBuilder();
        final SaveCurrentStateCommand expressionCommand = new SaveCurrentStateCommand(getHasExpression(),
                                                                                      getEditorSelectedEvent(),
                                                                                      getNodeUUID(),
                                                                                      getHasName(),
                                                                                      getUpdateCanvasNodeNameCommand());
        addExpressionCommand(expressionCommand, commandBuilder);

        execute(commandBuilder);
    }

    void execute(final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        sessionCommandManager.execute((AbstractCanvasHandler) getCurrentSession().getCanvasHandler(),
                                      commandBuilder.build());
    }

    void addExpressionCommand(final SaveCurrentStateCommand expressionCommand,
                              final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        commandBuilder.addCommand(expressionCommand);
    }

    Optional<HasName> getHasName() {
        return hasName;
    }

    CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> createCommandBuilder() {
        return new CompositeCommand.Builder<>();
    }
}
