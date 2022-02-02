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

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
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
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.BoxedExpressionService;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionPropsFiller;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
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
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
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
import static org.kie.workbench.common.dmn.api.definition.model.ItemDefinition.ITEM_DEFINITION_COMPARATOR;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BUILT_IN_TYPE_COMPARATOR;

@Templated
@Dependent
public class ExpressionEditorViewImpl implements ExpressionEditorView {

    static final double VP_SCALE = 1.0;

    static final String ENABLED_BETA_CSS_CLASS = "kie-beta-boxed-expression-editor--enabled";

    private ExpressionEditorView.Presenter presenter;

    @DataField("returnToLink")
    private Anchor returnToLink;

    @DataField("expressionName")
    private Span expressionName;

    @DataField("expressionType")
    private Span expressionType;

    @DataField("dmn-table")
    private DMNGridPanelContainer gridPanelContainer;

    @DataField("try-it")
    private HTMLAnchorElement tryIt;

    @DataField("switch-back")
    private HTMLAnchorElement switchBack;

    @DataField("beta-boxed-expression-toggle")
    private HTMLDivElement betaBoxedExpressionToggle;

    @DataField("dmn-new-expression-editor")
    private HTMLDivElement newBoxedExpression;

    @DataField("dmn-expression-type")
    private HTMLDivElement dmnExpressionType;

    @DataField("dmn-expression-editor")
    private HTMLDivElement dmnExpressionEditor;

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

    public ExpressionEditorViewImpl() {
        //CDI proxy
    }

    @Inject
    public ExpressionEditorViewImpl(final Anchor returnToLink,
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
                                    final ItemDefinitionUtils itemDefinitionUtils,
                                    final HTMLAnchorElement tryIt,
                                    final HTMLAnchorElement switchBack,
                                    final HTMLDivElement betaBoxedExpressionToggle,
                                    final HTMLDivElement newBoxedExpression,
                                    final HTMLDivElement dmnExpressionType,
                                    final HTMLDivElement dmnExpressionEditor) {
        this.returnToLink = returnToLink;
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

        this.tryIt = tryIt;
        this.switchBack = switchBack;
        this.betaBoxedExpressionToggle = betaBoxedExpressionToggle;
        this.newBoxedExpression = newBoxedExpression;
        this.dmnExpressionType = dmnExpressionType;
        this.dmnExpressionEditor = dmnExpressionEditor;
        this.updateCanvasNodeNameCommand = new UpdateCanvasNodeNameCommand(sessionManager,
                                                                           definitionUtils,
                                                                           canvasCommandFactory);
    }

    @Override
    public void init(final ExpressionEditorView.Presenter presenter) {
        this.presenter = presenter;
        BoxedExpressionService.registerBroadcastForExpression(this);
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

        expressionContainerGrid.setOnUndoClear(Optional.of(o -> reloadIfIsNewEditor()));
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
        returnToLink.setTextContent(translationService.format(DMNEditorConstants.ExpressionEditor_ReturnToLink, text));
    }

    @Override
    public void setExpression(final String nodeUUID,
                              final HasExpression hasExpression,
                              final Optional<HasName> hasName,
                              final boolean isOnlyVisualChangeAllowed) {
        toggleBetaBoxedExpressionEditor(false);
        toggleLegacyExpressionEditor(true);
        this.nodeUUID = nodeUUID;
        this.hasExpression = hasExpression;
        this.hasName = hasName;
        this.isOnlyVisualChangeAllowed = isOnlyVisualChangeAllowed;
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

    public void onEditorSelectedPageEvent(@Observes MultiPageEditorSelectedPageEvent editorSelectedPageEvent) {
        toggleBetaBoxedExpressionEditor(false);
        toggleLegacyExpressionEditor(true);
    }

    @EventHandler("try-it")
    public void onTryIt(final ClickEvent event) {
        loadNewBoxedExpressionEditor();
        toggleLegacyExpressionEditor(false);
        toggleBetaBoxedExpressionEditor(true);
        preventDefault(event);
    }

    void loadNewBoxedExpressionEditor() {
        String decisionNodeId = null;
        if (hasExpression instanceof Decision) {
            decisionNodeId = ((Decision) hasExpression).getId().getValue();
        } else if (hasExpression.getExpression() instanceof FunctionDefinition) {
            decisionNodeId = ((BusinessKnowledgeModel) hasExpression.getExpression().asDMNModelInstrumentedBase().getParent()).getId().getValue();
        }
        DMNLoader.renderBoxedExpressionEditor(
                ".kie-dmn-new-expression-editor",
                decisionNodeId,
                ExpressionPropsFiller.buildAndFillJsInteropProp(hasExpression.getExpression(), getExpressionName(), getTypeRef()),
                concat(retrieveDefaultDataTypeProps(), retrieveCustomDataTypeProps()).toArray(DataTypeProps[]::new),
                hasExpression.isClearSupported(),
                buildPmmlParams()
        );
    }

    @EventHandler("switch-back")
    public void onSwitchBack(final ClickEvent event) {
        getExpressionGridCacheSupplier()
                .get()
                .removeExpressionGrid(nodeUUID);
        setExpression(nodeUUID, hasExpression, hasName, isOnlyVisualChangeAllowed);
        toggleLegacyExpressionEditor(true);
        toggleBetaBoxedExpressionEditor(false);
        preventDefault(event);
    }

    @Override
    public void clear() {
        getExpressionContainerGrid().clearExpressionType();
    }

    public void resetExpressionDefinition(final ExpressionProps expressionProps) {
        executeExpressionCommand(new ClearExpressionCommand(getHasExpression(),
                                                            expressionProps,
                                                            getEditorSelectedEvent(),
                                                            getNodeUUID(),
                                                            this,
                                                            itemDefinitionUtils,
                                                            getHasName()));
    }

    public void broadcastLiteralExpressionDefinition(final LiteralProps literalProps) {
        executeExpressionCommand(new FillLiteralExpressionCommand(getHasExpression(),
                                                                  literalProps,
                                                                  getEditorSelectedEvent(),
                                                                  getNodeUUID(),
                                                                  this,
                                                                  itemDefinitionUtils,
                                                                  getHasName()));
    }

    public void broadcastContextExpressionDefinition(final ContextProps contextProps) {
        executeExpressionCommand(new FillContextExpressionCommand(getHasExpression(),
                                                                  contextProps,
                                                                  getEditorSelectedEvent(),
                                                                  getNodeUUID(),
                                                                  this,
                                                                  itemDefinitionUtils,
                                                                  getHasName()));
    }

    public void broadcastRelationExpressionDefinition(final RelationProps relationProps) {
        executeExpressionCommand(new FillRelationExpressionCommand(getHasExpression(),
                                                                   relationProps,
                                                                   getEditorSelectedEvent(),
                                                                   getNodeUUID(),
                                                                   this,
                                                                   itemDefinitionUtils,
                                                                   getHasName()));
    }

    public void broadcastListExpressionDefinition(final ListProps listProps) {
        executeExpressionCommand(new FillListExpressionCommand(getHasExpression(),
                                                               listProps,
                                                               getEditorSelectedEvent(),
                                                               getNodeUUID(),
                                                               this,
                                                               itemDefinitionUtils,
                                                               getHasName()));
    }

    public void broadcastInvocationExpressionDefinition(final InvocationProps invocationProps) {
        executeExpressionCommand(new FillInvocationExpressionCommand(getHasExpression(),
                                                                     invocationProps,
                                                                     getEditorSelectedEvent(),
                                                                     getNodeUUID(),
                                                                     this,
                                                                     itemDefinitionUtils,
                                                                     getHasName()));
    }

    public void broadcastFunctionExpressionDefinition(final FunctionProps functionProps) {
        executeExpressionCommand(new FillFunctionExpressionCommand(getHasExpression(),
                                                                   functionProps,
                                                                   getEditorSelectedEvent(),
                                                                   getNodeUUID(),
                                                                   this,
                                                                   itemDefinitionUtils,
                                                                   getHasName()));
    }

    public void broadcastDecisionTableExpressionDefinition(final DecisionTableProps decisionTableProps) {
        executeExpressionCommand(new FillDecisionTableExpressionCommand(getHasExpression(),
                                                                        decisionTableProps,
                                                                        getEditorSelectedEvent(),
                                                                        getNodeUUID(),
                                                                        this,
                                                                        itemDefinitionUtils,
                                                                        getHasName()));
    }

    public void openManageDataType() {
        dataTypePageActiveEvent.fire(new DataTypePageTabActiveEvent());
    }

    void executeExpressionCommand(final FillExpressionCommand expressionCommand) {
        expressionCommand.execute();
        updateCanvasNodeNameCommand.execute(getNodeUUID(), getHasName().orElse(null));
    }

    void toggleBetaBoxedExpressionEditor(final boolean enabled) {
        betaBoxedExpressionToggle.classList.toggle(ENABLED_BETA_CSS_CLASS, enabled);
        newBoxedExpression.classList.toggle("hidden", !enabled);
    }

    void toggleLegacyExpressionEditor(final boolean enabled) {
        dmnExpressionType.classList.toggle("hidden", !enabled);
        dmnExpressionEditor.classList.toggle("hidden", !enabled);
    }

    boolean isNewEditorEnabled() {
        return !HiddenHelper.isHidden(newBoxedExpression);
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

    private void preventDefault(final ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
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
                    .map(parameter -> new EntryInfo(new Id().getValue(), parameter, BuiltInType.ANY.getName()))
                    .toArray(EntryInfo[]::new);
            return new ModelsFromDocument(modelName, parametersFromModel);
        };
    }

    @EventHandler("returnToLink")
    void onClickReturnToLink(final ClickEvent event) {
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
        loadNewBoxedExpressionEditor();

        // This should be removed when the older editor is removed.
        syncExpressionWithOlderEditor();
    }

    void syncExpressionWithOlderEditor() {
        getExpressionGridCacheSupplier().get().removeExpressionGrid(getNodeUUID());
        getExpressionContainerGrid().setExpression(getNodeUUID(),
                                                   getHasExpression(),
                                                   getHasName(),
                                                   isOnlyVisualChangeAllowed);
    }

    void reloadIfIsNewEditor() {
        if (isNewEditorEnabled()) {
            reloadEditor();
        }
    }

    @Override
    public void setFocus() {
        gridPanel.setFocus(true);
    }

    HasExpression getHasExpression() {
        return hasExpression;
    }

    Event<ExpressionEditorChanged> getEditorSelectedEvent() {
        return editorSelectedEvent;
    }

    String getNodeUUID() {
        return nodeUUID;
    }

    public void notifyUserAction() {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder = createCommandBuilder();
        final SaveCurrentStateCommand expressionCommand = new SaveCurrentStateCommand(getHasExpression(),
                                                                                      getEditorSelectedEvent(),
                                                                                      this,
                                                                                      getNodeUUID(),
                                                                                      getHasName(),
                                                                                      updateCanvasNodeNameCommand);
        addExpressionCommand(expressionCommand, commandBuilder);

        execute(commandBuilder);
    }

    void execute(final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> commandBuilder) {
        sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
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
