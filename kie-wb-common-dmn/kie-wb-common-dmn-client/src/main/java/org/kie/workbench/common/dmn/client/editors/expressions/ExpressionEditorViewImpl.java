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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
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
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
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
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionModelFiller;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util.ExpressionPropsFiller;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml.PMMLDocumentMetadataProvider;
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
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetKeyboardHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveDown;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveLeft;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveRight;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperationMoveUp;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;

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
    private PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider;

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private CellEditorControlsView.Presenter cellEditorControls;
    private RestrictedMousePanMediator mousePanMediator;
    private ExpressionContainerGrid expressionContainerGrid;
    private String nodeUUID;
    private HasExpression hasExpression;
    private Optional<HasName> hasName;
    private boolean isOnlyVisualChangeAllowed;

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
                                    final PMMLDocumentMetadataProvider pmmlDocumentMetadataProvider,
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
        this.pmmlDocumentMetadataProvider = pmmlDocumentMetadataProvider;

        this.tryIt = tryIt;
        this.switchBack = switchBack;
        this.betaBoxedExpressionToggle = betaBoxedExpressionToggle;
        this.newBoxedExpression = newBoxedExpression;
        this.dmnExpressionType = dmnExpressionType;
        this.dmnExpressionEditor = dmnExpressionEditor;
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
    public void activate() {
        DMNLoader.renderBoxedExpressionEditor(".kie-dmn-new-expression-editor",
                                              ExpressionPropsFiller.buildAndFillJsInteropProp(hasExpression.getExpression(), getExpressionName(), getTypeRef()),
                                              buildPmmlParams());
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

    @EventHandler("try-it")
    public void onTryIt(final ClickEvent event) {
        activate();
        renderNewBoxedExpression();
        toggleBoxedExpression(true);
        preventDefault(event);
    }

    @EventHandler("switch-back")
    public void onSwitchBack(final ClickEvent event) {
        getExpressionGridCacheSupplier()
                .get()
                .removeExpressionGrid(nodeUUID);
        setExpression(nodeUUID, hasExpression, hasName, isOnlyVisualChangeAllowed);
        renderOldBoxedExpression();
        toggleBoxedExpression(false);
        preventDefault(event);
    }

    public void resetExpressionDefinition(final ExpressionProps expressionProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        setExpressionNameAndDataType(expressionProps);
        hasExpression.setExpression(null);
    }

    public void broadcastLiteralExpressionDefinition(final LiteralProps literalProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        setExpressionNameAndDataType(literalProps);
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new LiteralExpression());
        }
        ExpressionModelFiller.fillLiteralExpression((LiteralExpression) hasExpression.getExpression(), literalProps);
    }

    public void broadcastContextExpressionDefinition(final ContextProps contextProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        setExpressionNameAndDataType(contextProps);
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new Context());
        }
        ExpressionModelFiller.fillContextExpression((Context) hasExpression.getExpression(), contextProps);
    }

    public void broadcastRelationExpressionDefinition(final RelationProps relationProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new Relation());
        }
        ExpressionModelFiller.fillRelationExpression((Relation) hasExpression.getExpression(), relationProps);
    }

    public void broadcastListExpressionDefinition(final ListProps listProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new List());
        }
        ExpressionModelFiller.fillListExpression((List) hasExpression.getExpression(), listProps);
    }

    public void broadcastInvocationExpressionDefinition(final InvocationProps invocationProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        setExpressionNameAndDataType(invocationProps);
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new Invocation());
        }
        ExpressionModelFiller.fillInvocationExpression((Invocation) hasExpression.getExpression(), invocationProps);
    }

    public void broadcastFunctionExpressionDefinition(final FunctionProps functionProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        setExpressionNameAndDataType(functionProps);
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new FunctionDefinition());
        }
        ExpressionModelFiller.fillFunctionExpression((FunctionDefinition) hasExpression.getExpression(), functionProps);
    }

    public void broadcastDecisionTableExpressionDefinition(final DecisionTableProps decisionTableProps) {
        editorSelectedEvent.fire(new ExpressionEditorChanged(nodeUUID));
        setExpressionNameAndDataType(decisionTableProps);
        if (hasExpression.getExpression() == null) {
            hasExpression.setExpression(new DecisionTable());
        }
        ExpressionModelFiller.fillDecisionTableExpression((DecisionTable) hasExpression.getExpression(), decisionTableProps);
    }

    void renderNewBoxedExpression() {
        toggleEditorsVisibility();
    }

    void renderOldBoxedExpression() {
        toggleEditorsVisibility();
    }

    void toggleBoxedExpression(final boolean enabled) {
        betaBoxedExpressionToggle.classList.toggle(ENABLED_BETA_CSS_CLASS, enabled);
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

    private void setExpressionNameAndDataType(final ExpressionProps expressionProps) {
        setExpressionName(expressionProps.name);
        setTypeRef(expressionProps.dataType);
    }

    private void setExpressionName(final String name) {
        final HasName fallbackHasName = hasExpression instanceof HasName ? (HasName) hasExpression : HasName.NOP;
        hasName.orElse(fallbackHasName).setName(new Name(name));
    }

    @SuppressWarnings("unchecked")
    private void setTypeRef(final String dataType) {
        final QName typeRef = BuiltInTypeUtils
                .findBuiltInTypeByName(dataType)
                .orElse(BuiltInType.UNDEFINED)
                .asQName();
        if (hasExpression instanceof HasVariable) {
            ((HasVariable<InformationItemPrimary>) hasExpression).getVariable().setTypeRef(typeRef);
        } else if (hasExpression.getExpression() != null && hasExpression.getExpression().asDMNModelInstrumentedBase().getParent() instanceof HasVariable) {
            ((HasVariable<InformationItemPrimary>) hasExpression.getExpression().asDMNModelInstrumentedBase().getParent()).getVariable().setTypeRef(typeRef);
        }
    }

    private void toggleEditorsVisibility() {
        dmnExpressionType.classList.toggle("hidden");
        dmnExpressionEditor.classList.toggle("hidden");
        newBoxedExpression.classList.toggle("hidden");
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
                    .map(parameter -> new EntryInfo(parameter, UNDEFINED.getText()))
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
    public void setFocus() {
        gridPanel.setFocus(true);
    }
}
