/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import org.drools.verifier.api.reporting.Issue;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DeleteRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.InsertRowAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.auditlog.UpdateColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RuleNameColumn;
import org.drools.workbench.models.guided.dtable.shared.validation.HitPolicyValidation;
import org.drools.workbench.screens.guided.dtable.client.GuidedDecisionTable;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.impl.DefaultClipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.search.GuidedDecisionTableSearchableElement;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DecisionTableAnalyzerProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.auditlog.AuditLog;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.BooleanUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableColumnSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMenusEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.lockmanager.GuidedDecisionTableLockManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiCell;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.themes.GuidedDecisionTableRenderer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.DependentEnumsUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.EnumLoaderUtilities;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager;
import org.drools.workbench.screens.guided.rule.client.util.GWTDateConverter;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.soup.project.datamodel.oracle.DateConverter;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.services.verifier.reporting.client.controller.AnalyzerController;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.services.verifier.reporting.client.panel.IssueSelectedEvent;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.search.common.SearchPerformedEvent;
import org.kie.workbench.common.widgets.client.search.common.Searchable;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetColumnVisibilityEvent;
import org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.LockTarget;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.CURRENT_USER;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.NOBODY;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.OTHER_USER;
import static org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer.MetaData;

@Dependent
public class GuidedDecisionTablePresenter implements GuidedDecisionTableView.Presenter {

    private final User identity;
    private final GuidedDTableResourceType resourceType;
    private final Caller<RuleNamesService> ruleNameService;
    private final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent;
    private final Event<DecisionTableColumnSelectedEvent> decisionTableColumnSelectedEvent;
    private final Event<DecisionTableSelectionsChangedEvent> decisionTableSelectionsChangedEvent;
    private final Event<RefreshAttributesPanelEvent> refreshAttributesPanelEvent;
    private final Event<RefreshMetaDataPanelEvent> refreshMetaDataPanelEvent;
    private final Event<RefreshConditionsPanelEvent> refreshConditionsPanelEvent;
    private final Event<RefreshActionsPanelEvent> refreshActionsPanelEvent;
    private final Event<RefreshMenusEvent> refreshMenusEvent;
    private final Event<NotificationEvent> notificationEvent;
    private final GridWidgetCellFactory gridWidgetCellFactory;
    private final GridWidgetColumnFactory gridWidgetColumnFactory;
    private final AsyncPackageDataModelOracleFactory oracleFactory;
    private final ModelSynchronizer synchronizer;
    private final SyncBeanManager beanManager;
    private final GuidedDecisionTableLockManager lockManager;
    private final GuidedDecisionTableLinkManager linkManager;
    private final Clipboard clipboard;
    private final DecisionTableAnalyzerProvider decisionTableAnalyzerProvider;
    private final EnumLoaderUtilities enumLoaderUtilities;

    private final Access access = new Access();
    private final PluginHandler pluginHandler;
    private final AuthorizationManager authorizationManager;
    private final SessionInfo sessionInfo;

    protected CellUtilities cellUtilities;
    protected ColumnUtilities columnUtilities;

    private DependentEnumsUtilities dependentEnumsUtilities;
    private AnalyzerController analyzerController;
    private GuidedDecisionTable52 model;
    private Overview overview;
    private AsyncPackageDataModelOracle oracle;
    private GuidedDecisionTableModellerView.Presenter parent;
    private BRLRuleModel rm;
    private GuidedDecisionTableUiModel uiModel;
    private GuidedDecisionTableView view;
    private GuidedDecisionTableRenderer renderer;
    private AuditLog auditLog;
    private String version = null;
    private ObservablePath latestPath = null;
    private ObservablePath currentPath = null;
    private PlaceRequest placeRequest = null;
    private Integer originalHashCode = null;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    //This EventBus is local to the screen and should be used for local operations, set data, add rows etc
    private EventBus eventBus = new SimpleEventBus();
    private Set<PortableWorkDefinition> workItemDefinitions;
    private AnalysisReportScreen analysisReportScreen;

    @Inject
    public GuidedDecisionTablePresenter(final User identity,
                                        final GuidedDTableResourceType resourceType,
                                        final Caller<RuleNamesService> ruleNameService,
                                        final Event<DecisionTableSelectedEvent> decisionTableSelectedEvent,
                                        final Event<DecisionTableColumnSelectedEvent> decisionTableColumnSelectedEvent,
                                        final Event<DecisionTableSelectionsChangedEvent> decisionTableSelectionsChangedEvent,
                                        final Event<RefreshAttributesPanelEvent> refreshAttributesPanelEvent,
                                        final Event<RefreshMetaDataPanelEvent> refreshMetaDataPanelEvent,
                                        final Event<RefreshConditionsPanelEvent> refreshConditionsPanelEvent,
                                        final Event<RefreshActionsPanelEvent> refreshActionsPanelEvent,
                                        final Event<RefreshMenusEvent> refreshMenusEvent,
                                        final Event<NotificationEvent> notificationEvent,
                                        final GridWidgetCellFactory gridWidgetCellFactory,
                                        final GridWidgetColumnFactory gridWidgetColumnFactory,
                                        final AsyncPackageDataModelOracleFactory oracleFactory,
                                        final ModelSynchronizer synchronizer,
                                        final SyncBeanManager beanManager,
                                        final @GuidedDecisionTable GuidedDecisionTableLockManager lockManager,
                                        final GuidedDecisionTableLinkManager linkManager,
                                        final Clipboard clipboard,
                                        final DecisionTableAnalyzerProvider decisionTableAnalyzerProvider,
                                        final EnumLoaderUtilities enumLoaderUtilities,
                                        final PluginHandler pluginHandler,
                                        final AuthorizationManager authorizationManager,
                                        final SessionInfo sessionInfo) {

        this.identity = identity;
        this.resourceType = resourceType;
        this.ruleNameService = ruleNameService;
        this.decisionTableSelectedEvent = decisionTableSelectedEvent;
        this.decisionTableColumnSelectedEvent = decisionTableColumnSelectedEvent;
        this.decisionTableSelectionsChangedEvent = decisionTableSelectionsChangedEvent;
        this.refreshAttributesPanelEvent = refreshAttributesPanelEvent;
        this.refreshMetaDataPanelEvent = refreshMetaDataPanelEvent;
        this.refreshConditionsPanelEvent = refreshConditionsPanelEvent;
        this.refreshActionsPanelEvent = refreshActionsPanelEvent;
        this.refreshMenusEvent = refreshMenusEvent;
        this.notificationEvent = notificationEvent;
        this.gridWidgetCellFactory = gridWidgetCellFactory;
        this.gridWidgetColumnFactory = gridWidgetColumnFactory;
        this.oracleFactory = oracleFactory;
        this.synchronizer = synchronizer;
        this.beanManager = beanManager;
        this.lockManager = lockManager;
        this.linkManager = linkManager;
        this.clipboard = clipboard;
        this.decisionTableAnalyzerProvider = decisionTableAnalyzerProvider;
        this.enumLoaderUtilities = enumLoaderUtilities;
        this.pluginHandler = pluginHandler;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;

        CellUtilities.injectDateConvertor(getDateConverter());

        pluginHandler.init(this);
    }

    @Override
    public Set<PortableWorkDefinition> getWorkItemDefinitions() {
        return workItemDefinitions;
    }

    DateConverter getDateConverter() {
        return GWTDateConverter.getInstance();
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public void activate() {
        lockManager.fireChangeTitleEvent();
    }

    @Override
    public GuidedDecisionTable52 getModel() {
        return this.model;
    }

    GridData getUiModel() {
        return this.uiModel;
    }

    @Override
    public AsyncPackageDataModelOracle getDataModelOracle() {
        return this.oracle;
    }

    @Override
    public Overview getOverview() {
        return this.overview;
    }

    @Override
    public GuidedDecisionTableView getView() {
        return view;
    }

    @Override
    public GuidedDecisionTableModellerView.Presenter getModellerPresenter() {
        return parent;
    }

    @Override
    public void setContent(final ObservablePath path,
                           final PlaceRequest placeRequest,
                           final AnalysisReportScreen analysisReportScreen,
                           final GuidedDecisionTableEditorContent content,
                           final GuidedDecisionTableModellerView.Presenter parent,
                           final boolean isReadOnly) {
        this.parent = parent;
        this.latestPath = path;
        this.analysisReportScreen = analysisReportScreen;

        initialiseContent(path,
                          placeRequest,
                          content,
                          isReadOnly);
    }

    @Override
    public void refreshContent(final ObservablePath path,
                               final PlaceRequest placeRequest,
                               final GuidedDecisionTableEditorContent content,
                               final boolean isReadOnly) {
        onClose();

        initialiseContent(path,
                          placeRequest,
                          content,
                          isReadOnly);

        if (!isReadOnly()) {
            analyzerController.initialiseAnalysis();
        }
    }

    void initialiseContent(final ObservablePath path,
                           final PlaceRequest placeRequest,
                           final GuidedDecisionTableEditorContent content,
                           final boolean isReadOnly) {

        final GuidedDecisionTable52 model = content.getModel();
        final PackageDataModelOracleBaselinePayload dataModel = content.getDataModel();

        this.workItemDefinitions = content.getWorkItemDefinitions();
        this.currentPath = path;
        this.placeRequest = placeRequest;
        this.model = model;
        this.overview = content.getOverview();
        this.oracle = oracleFactory.makeAsyncPackageDataModelOracle(path,
                                                                    model,
                                                                    dataModel);
        this.rm = new BRLRuleModel(model);

        this.uiModel = makeUiModel();
        this.renderer = makeViewRenderer();
        this.view = makeView(workItemDefinitions);

        initialiseAccess(isReadOnly);
        initialiseLockManager();
        initialiseUtilities();
        initialiseModels();
        initialiseValidationAndVerification();
        initialiseEventHandlers();
        initialiseAuditLog();
    }

    void initialiseAccess(final boolean isReadOnly) {
        getAccess().setReadOnly(isReadOnly);
        getAccess().setHasEditableColumns(canEditColumns());
    }

    boolean canEditColumns() {

        final String permission = WorkbenchFeatures.GUIDED_DECISION_TABLE_EDIT_COLUMNS;
        final User user = sessionInfo.getIdentity();

        return authorizationManager.authorize(permission, user);
    }

    //Setup LockManager
    void initialiseLockManager() {
        lockManager.init(new LockTarget(currentPath,
                                        parent.getView().asWidget(),
                                        placeRequest,
                                        () -> currentPath.getFileName() + " - " + resourceType.getDescription(),
                                        () -> {/*Nothing*/}),
                         parent);
    }

    //Instantiate UiModel overriding cell selection to inform MenuItems about changes to selected cells.
    GuidedDecisionTableUiModel makeUiModel() {
        return new GuidedDecisionTableUiModel(synchronizer) {

            @Override
            public Range selectCell(final int rowIndex,
                                    final int columnIndex) {
                final Range rows = super.selectCell(rowIndex,
                                                    columnIndex);
                decisionTableSelectionsChangedEvent.fire(new DecisionTableSelectionsChangedEvent(GuidedDecisionTablePresenter.this));
                lockManager.acquireLock();
                return rows;
            }

            @Override
            public Range selectCells(final int rowIndex,
                                     final int columnIndex,
                                     final int width,
                                     final int height) {
                final Range rows = super.selectCells(rowIndex,
                                                     columnIndex,
                                                     width,
                                                     height);
                decisionTableSelectionsChangedEvent.fire(new DecisionTableSelectionsChangedEvent(GuidedDecisionTablePresenter.this));
                lockManager.acquireLock();
                return rows;
            }

            @Override
            public boolean isRowDraggingEnabled() {
                return access.isEditable();
            }

            @Override
            public boolean isColumnDraggingEnabled() {
                return access.isEditable();
            }

            @Override
            public Range deleteCell(int rowIndex,
                                    int columnIndex) {

                Range cellRange = super.deleteCell(rowIndex,
                                                   columnIndex);
                decisionTableSelectionsChangedEvent.fire(new DecisionTableSelectionsChangedEvent(GuidedDecisionTablePresenter.this));
                return cellRange;
            }

            @Override
            public void deleteColumn(GridColumn<?> column) {
                super.deleteColumn(column);
                decisionTableSelectionsChangedEvent.fire(new DecisionTableSelectionsChangedEvent(GuidedDecisionTablePresenter.this));
            }

            @Override
            public Range deleteRow(int rowIndex) {
                Range rowRange = super.deleteRow(rowIndex);
                decisionTableSelectionsChangedEvent.fire(new DecisionTableSelectionsChangedEvent(GuidedDecisionTablePresenter.this));
                return rowRange;
            }
        };
    }

    GuidedDecisionTableRenderer makeViewRenderer() {
        return new GuidedDecisionTableRenderer(uiModel,
                                               model);
    }

    GuidedDecisionTableView makeView(final Set<PortableWorkDefinition> workItemDefinitions) {
        return new GuidedDecisionTableViewImpl(uiModel,
                                               renderer,
                                               this,
                                               model,
                                               notificationEvent);
    }

    void initialiseUtilities() {
        this.cellUtilities = new CellUtilities();
        this.columnUtilities = new ColumnUtilities(model,
                                                   oracle);

        //Setup the DropDownManager that requires the Model and UI data to determine drop-down lists
        //for dependent enumerations. This needs to be called before the columns are created.
        this.dependentEnumsUtilities = new DependentEnumsUtilities(model,
                                                                   oracle);

        //Setup Factories for new Columns and Cells
        gridWidgetColumnFactory.setConverters(getConverters());
        gridWidgetColumnFactory.initialise(model,
                                           oracle,
                                           columnUtilities,
                                           this);

        //Setup synchronizers to update the Model when the UiModel changes.
        synchronizer.setSynchronizers(getSynchronizers());
        synchronizer.initialise(model,
                                uiModel,
                                cellUtilities,
                                columnUtilities,
                                dependentEnumsUtilities,
                                gridWidgetCellFactory,
                                gridWidgetColumnFactory,
                                view,
                                rm,
                                eventBus,
                                access);
    }

    //Copy Model data to UiModel.
    void initialiseModels() {
        initialiseLegacyColumnDataTypes();
        final List<BaseColumn> modelColumns = model.getExpandedColumns();
        for (BaseColumn column : modelColumns) {
            initialiseColumn(column);
        }
        for (List<DTCellValue52> row : model.getData()) {
            initialiseRow(modelColumns,
                          row);
        }
        setOriginalHashCode(model.hashCode());
    }

    //Ensure field data-type is set (field did not exist before 5.2)
    void initialiseLegacyColumnDataTypes() {
        for (CompositeColumn<?> column : model.getConditions()) {
            if (column instanceof Pattern52) {
                final Pattern52 pattern = (Pattern52) column;
                for (ConditionCol52 condition : pattern.getChildColumns()) {
                    condition.setFieldType(oracle.getFieldType(pattern.getFactType(),
                                                               condition.getFactField()));
                }
            }
        }
    }

    //Setup the Validation & Verification analyzer
    void initialiseValidationAndVerification() {
        this.analyzerController = decisionTableAnalyzerProvider.newAnalyzer(analysisReportScreen,
                                                                            placeRequest,
                                                                            oracle,
                                                                            model,
                                                                            eventBus);
    }

    void initialiseEventHandlers() {
        view.registerNodeDragMoveHandler((event) -> getModellerPresenter().updateRadar());
        view.registerNodeMouseDoubleClickHandler((event) -> {
            if (view.isNodeMouseEventOverCaption(event)) {
                if (isGridPinned()) {
                    exitPinnedMode(() -> {/*Nothing*/});
                } else {
                    enterPinnedMode(view,
                                    () -> {/*Nothing*/});
                }
            }
        });
    }

    //Setup Audit Log
    void initialiseAuditLog() {
        this.auditLog = new AuditLog(model,
                                     identity);
    }

    @Override
    public void link(final Set<GuidedDecisionTableView.Presenter> dtPresenters) {
        final Set<GuidedDecisionTableView.Presenter> otherDecisionTables = new HashSet<>();
        otherDecisionTables.addAll(dtPresenters);
        otherDecisionTables.remove(this);
        otherDecisionTables.stream().forEach((e) -> linkManager.link(this.getModel(),
                                                                     e.getModel(),
                                                                     (final int sourceColumnIndex,
                                                                      final int targetColumnIndex) -> {
                                                                         final GridData sourceUiModel = GuidedDecisionTablePresenter.this.getView().getModel();
                                                                         final GridData targetUiModel = e.getView().getModel();
                                                                         targetUiModel.getColumns().get(targetColumnIndex).setLink(sourceUiModel.getColumns().get(sourceColumnIndex));
                                                                     }));
    }

    List<BaseColumnConverter> getConverters() {
        final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();
        for (SyncBeanDef<BaseColumnConverter> bean : beanManager.lookupBeans(BaseColumnConverter.class)) {
            converters.add(bean.getInstance());
        }
        Collections.sort(converters,
                         new Comparator<BaseColumnConverter>() {

                             @Override
                             public int compare(final BaseColumnConverter o1,
                                                final BaseColumnConverter o2) {
                                 return o2.priority() - o1.priority();
                             }
                         });
        return converters;
    }

    @SuppressWarnings("unchecked")
    List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> getSynchronizers() {
        final List<Synchronizer<? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData, ? extends MetaData>> synchronizers = new ArrayList<>();
        for (SyncBeanDef<Synchronizer> bean : beanManager.lookupBeans(Synchronizer.class)) {
            synchronizers.add(bean.getInstance());
        }
        return synchronizers;
    }

    @Override
    public Access getAccess() {
        return this.access;
    }

    @Override
    public void onClose() {
        terminateAnalysis();

        if (uiModel != null) {
            for (GridColumn<?> column : uiModel.getColumns()) {
                if (column.getColumnRenderer() instanceof HasDOMElementResources) {
                    ((HasDOMElementResources) column.getColumnRenderer()).destroyResources();
                }
            }
        }

        lockManager.releaseLock();
        oracleFactory.destroy(oracle);
    }

    @Override
    public void initialiseAnalysis() {
        if (analyzerController == null) {
            initialiseValidationAndVerification();
        }
        analyzerController.initialiseAnalysis();
    }

    @Override
    public void terminateAnalysis() {
        if (analyzerController != null) {
            analyzerController.terminateAnalysis();
        }
    }

    @Override
    @SuppressWarnings("unused")
    public void select(final GridWidget selectedGridWidget) {
        decisionTableSelectedEvent.fire(new DecisionTableSelectedEvent(this));
        if (!isReadOnly()) {
            lockManager.acquireLock();
        }
    }

    void onSearchPerformed(final @Observes SearchPerformedEvent event) {
        if (event.hasElement()) {
            final Searchable element = event.getCurrentElement();
            if (element instanceof GuidedDecisionTableSearchableElement) {
                final GuidedDecisionTableSearchableElement gdtElement = ((GuidedDecisionTableSearchableElement) element);
                if (!Objects.equals(model, gdtElement.getModel())) {
                    renderer.clearCellHighlight();
                }
            }
        }
        getView().draw();
    }

    void onUpdatedLockStatusEvent(final @Observes UpdatedLockStatusEvent event) {
        if (currentPath == null) {
            return;
        }
        if (currentPath.equals(event.getFile())) {
            if (event.isLocked()) {
                access.setLock(event.isLockedByCurrentUser() ? CURRENT_USER : OTHER_USER);
            } else {
                access.setLock(NOBODY);
            }
            refreshColumnsPage();
            refreshMenus();
        }
    }

    void refreshColumnsPage() {
        refreshAttributesPanelEvent.fire(new RefreshAttributesPanelEvent(this, model.getAttributeCols()));
        refreshMetaDataPanelEvent.fire(new RefreshMetaDataPanelEvent(this, model.getMetadataCols()));
        refreshConditionsPanelEvent.fire(new RefreshConditionsPanelEvent(this, model.getConditions()));
        refreshActionsPanelEvent.fire(new RefreshActionsPanelEvent(this, model.getActionCols()));
    }

    void refreshMenus() {
        refreshMenusEvent.fire(new RefreshMenusEvent());
    }

    void onIssueSelectedEvent(final @Observes IssueSelectedEvent event) {
        if (event == null) {
            return;
        }
        final PlaceRequest placeRequest = event.getPlaceRequest();
        final Issue issue = event.getIssue();

        if (placeRequest == null || issue == null) {
            renderer.clearHighlights();
        } else if (!placeRequest.equals(this.getPlaceRequest())) {
            renderer.clearHighlights();
        } else {
            renderer.highlightRows(event.getIssue().getSeverity(),
                                   event.getIssue().getRowNumbers());
        }
        getView().draw();
    }

    @Override
    public void selectLinkedColumn(final GridColumn<?> column) {
        decisionTableColumnSelectedEvent.fire(new DecisionTableColumnSelectedEvent(column));
    }

    @Override
    public Set<GridWidget> getGridWidgets() {
        return parent.getView().getGridWidgets();
    }

    @Override
    public void enterPinnedMode(final GridWidget gridWidget,
                                final Command onStartCommand) {
        parent.enterPinnedMode(gridWidget,
                               onStartCommand);
    }

    @Override
    public void exitPinnedMode(final Command onCompleteCommand) {
        parent.exitPinnedMode(onCompleteCommand);
    }

    @Override
    public void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException {
        parent.updatePinnedContext(gridWidget);
    }

    @Override
    public PinnedContext getPinnedContext() {
        return parent.getPinnedContext();
    }

    @Override
    public boolean isGridPinned() {
        return parent.isGridPinned();
    }

    @Override
    public TransformMediator getDefaultTransformMediator() {
        return parent.getDefaultTransformMediator();
    }

    @Override
    public void addOnEnterPinnedModeCommand(final Command command) {
        getParent().addOnEnterPinnedModeCommand(command);
    }

    @Override
    public void addOnExitPinnedModeCommand(final Command command) {
        getParent().addOnExitPinnedModeCommand(command);
    }

    GuidedDecisionTableModellerView.Presenter getParent() {
        return parent;
    }

    @Override
    public void getPackageParentRuleNames(final ParameterizedCommand<Collection<String>> command) {
        ruleNameService.call(new RemoteCallback<Collection<String>>() {

            @Override
            public void callback(final Collection<String> ruleNames) {
                command.execute(ruleNames);
            }
        }).getRuleNames(getCurrentPath(),
                        model.getPackageName());
    }

    @Override
    public void setParentRuleName(final String parentName) {
        model.setParentName(parentName);
    }

    @Override
    public boolean hasColumnDefinitions() {

        final boolean hasAttributeCols = model.getAttributeCols().size() > 0;
        final boolean hasMetadataCols = model.getMetadataCols().size() > 0;
        final boolean hasConditionCols = model.getConditionsCount() > 0;
        final boolean hasActionCols = model.getActionCols().size() > 0;

        return hasAttributeCols || hasConditionCols || hasActionCols || hasMetadataCols;
    }

    @Override
    public Set<String> getBindings(final String className) {
        //For some reason, Fact Pattern data-types use the leaf name of the fully qualified Class Name
        //whereas Fields use the fully qualified Class Name. We don't use the generic fieldType (see
        //SuggestionCompletionEngine.TYPE) as we can't distinguish between different numeric types
        String simpleClassName = className;
        if (simpleClassName != null && simpleClassName.lastIndexOf(".") > 0) {
            simpleClassName = simpleClassName.substring(simpleClassName.lastIndexOf(".") + 1);
        }
        Set<String> bindings = new HashSet<String>();
        for (Pattern52 p : model.getPatterns()) {
            if (className == null || p.getFactType().equals(simpleClassName)) {
                String binding = p.getBoundName();
                if (!(binding == null || "".equals(binding))) {
                    bindings.add(binding);
                }
            }
            for (ConditionCol52 c : p.getChildColumns()) {
                if (c.isBound()) {
                    String fieldDataType = oracle.getFieldClassName(p.getFactType(),
                                                                    c.getFactField());
                    if (fieldDataType.equals(className)) {
                        bindings.add(c.getBinding());
                    }
                }
            }
        }
        return bindings;
    }

    @Override
    public List<String> getLHSBoundFacts() {
        return rm.getLHSBoundFacts();
    }

    @Override
    public Map<String, String> getValueListLookups(final BaseColumn column) {
        final String[] dropDownItems = columnUtilities.getValueList(column);
        return enumLoaderUtilities.convertDropDownData(dropDownItems);
    }

    @Override
    public void getEnumLookups(final String factType,
                               final String factField,
                               final DependentEnumsUtilities.Context context,
                               final Callback<Map<String, String>> callback) {
        final DropDownData enumDefinition = oracle.getEnums(factType,
                                                            factField,
                                                            this.dependentEnumsUtilities.getCurrentValueMap(context));
        enumLoaderUtilities.getEnums(enumDefinition,
                                     callback,
                                     this,
                                     () -> view.showBusyIndicator(CommonConstants.INSTANCE.RefreshingList()),
                                     () -> view.hideBusyIndicator());
    }

    @Override
    public Set<String> getReservedAttributeNames() {
        final Set<String> result = new HashSet<>();

        result.addAll(getExistingAttributeNames());
        result.addAll(HitPolicyValidation.getReservedAttributes(model.getHitPolicy()));

        return result;
    }

    private Set<String> getExistingAttributeNames() {
        final Set<String> existingAttributeNames = new HashSet<>();
        for (AttributeCol52 attributeCol : model.getAttributeCols()) {
            existingAttributeNames.add(attributeCol.getAttribute());
        }
        return existingAttributeNames;
    }

    @Override
    public boolean isMetaDataUnique(final String metaDataName) {
        for (MetadataCol52 mc : model.getMetadataCols()) {
            if (metaDataName.equals(mc.getMetadata())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void editCondition(final Pattern52 pattern,
                              final ConditionCol52 column) {
        pluginHandler.edit(pattern,
                           column);
    }

    @Override
    public void editCondition(final BRLConditionColumn column) {
        pluginHandler.edit(column);
    }

    @Override
    public void editAction(final ActionCol52 column) {
        pluginHandler.edit(column);
    }

    @Override
    public void appendColumn(final AttributeCol52 column) {
        doAppendColumn(column,
                       () -> synchronizer.appendColumn(column),
                       () -> refreshAttributesPanelEvent.fire(new RefreshAttributesPanelEvent(this,
                                                                                              model.getAttributeCols())));
    }

    @Override
    public void appendColumn(final MetadataCol52 column) {
        doAppendColumn(column,
                       () -> synchronizer.appendColumn(column),
                       () -> refreshMetaDataPanelEvent.fire(new RefreshMetaDataPanelEvent(this,
                                                                                          model.getMetadataCols())));
    }

    @Override
    public void appendColumn(final Pattern52 pattern,
                             final ConditionCol52 column) {
        doAppendColumn(column,
                       () -> synchronizer.appendColumn(pattern,
                                                       column),
                       () -> refreshConditionsPanelEvent.fire(new RefreshConditionsPanelEvent(this,
                                                                                              model.getConditions())));
    }

    @Override
    public void appendColumn(final ConditionCol52 column) {
        doAppendColumn(column,
                       () -> synchronizer.appendColumn(column),
                       () -> refreshConditionsPanelEvent.fire(new RefreshConditionsPanelEvent(this,
                                                                                              model.getConditions())));
    }

    @Override
    public void appendColumn(final ActionCol52 column) {
        doAppendColumn(column,
                       () -> synchronizer.appendColumn(column),
                       () -> refreshActionsPanelEvent.fire(new RefreshActionsPanelEvent(this,
                                                                                        model.getActionCols())));
    }

    private void doAppendColumn(final BaseColumn column,
                                final VetoableColumnCommand append,
                                final Command callback) {
        if (isReadOnly()) {
            return;
        }
        try {
            append.execute();

            refreshView();

            //Log addition of column
            model.getAuditLog().add(new InsertColumnAuditLogEntry(identity.getIdentifier(),
                                                                  column));
            callback.execute();
        } catch (VetoException e) {
            getModellerPresenter().getView().showGenericVetoMessage();
        }
    }

    @Override
    public void onAppendRow() {
        if (isReadOnly()) {
            return;
        }
        try {
            synchronizer.appendRow();
            lockManager.acquireLock();

            refreshView();

            //Log insertion of row
            model.getAuditLog().add(new InsertRowAuditLogEntry(identity.getIdentifier(),
                                                               model.getData().size() - 1));
        } catch (VetoException e) {
            getModellerPresenter().getView().showGenericVetoMessage();
        }
    }

    void refreshView() {
        getParent().updateLinks();
        getParent().refreshScrollPosition();

        view.getLayer().draw();
    }

    @Override
    public void deleteColumn(final AttributeCol52 column) throws VetoException {
        doDeleteColumn(column,
                       () -> refreshAttributesPanelEvent.fire(new RefreshAttributesPanelEvent(this,
                                                                                              model.getAttributeCols())));
    }

    @Override
    public void deleteColumn(final MetadataCol52 column) throws VetoException {
        doDeleteColumn(column,
                       () -> refreshMetaDataPanelEvent.fire(new RefreshMetaDataPanelEvent(this,
                                                                                          model.getMetadataCols())));
    }

    @Override
    public void deleteColumn(final ConditionCol52 column) throws VetoException {
        doDeleteColumn(column,
                       () -> refreshConditionsPanelEvent.fire(new RefreshConditionsPanelEvent(this,
                                                                                              model.getConditions())));
    }

    @Override
    public void deleteColumn(final ActionCol52 column) throws VetoException {
        doDeleteColumn(column,
                       () -> refreshActionsPanelEvent.fire(new RefreshActionsPanelEvent(this,
                                                                                        model.getActionCols())));
    }

    private void doDeleteColumn(final BaseColumn column,
                                final Command callback) throws VetoException {
        if (isReadOnly()) {
            return;
        }
        synchronizer.deleteColumn(column);

        refreshView();

        //Log deletion of column
        model.getAuditLog().add(new DeleteColumnAuditLogEntry(identity.getIdentifier(),
                                                              column));
        callback.execute();
    }


    @Override
    public void onSort(final GridColumn gridColumn) {
        try {
           final List<Integer> sort = uiModel.sort(gridColumn);
            refreshView();
            analyzerController.sort(sort);
        } catch (VetoException veto) {
            getModellerPresenter().getView().showGenericVetoMessage();
        }
    }

    @Override
    public void setShowRuleName(final boolean show) {
        model.getRuleNameColumn().setHideColumn(!show);
        eventBus.fireEvent(new SetColumnVisibilityEvent(1, show));
        final int iModelColumn = model.getExpandedColumns().indexOf(model.getRuleNameColumn());
        uiModel.getColumns().get(iModelColumn).setVisible(show);

        refreshView();
    }

    @Override
    public void updateColumn(final AttributeCol52 originalColumn,
                             final AttributeCol52 editedColumn) throws VetoException {
        doUpdateColumn(originalColumn,
                       editedColumn,
                       () -> synchronizer.updateColumn(originalColumn,
                                                       editedColumn),
                       () -> refreshAttributesPanelEvent.fire(new RefreshAttributesPanelEvent(this,
                                                                                              model.getAttributeCols())));
    }

    @Override
    public void updateColumn(final MetadataCol52 originalColumn,
                             final MetadataCol52 editedColumn) throws VetoException {
        doUpdateColumn(originalColumn,
                       editedColumn,
                       () -> synchronizer.updateColumn(originalColumn,
                                                       editedColumn),
                       () -> refreshMetaDataPanelEvent.fire(new RefreshMetaDataPanelEvent(this,
                                                                                          model.getMetadataCols())));
    }

    @Override
    public void updateColumn(final Pattern52 originalPattern,
                             final ConditionCol52 originalColumn,
                             final Pattern52 editedPattern,
                             final ConditionCol52 editedColumn) throws VetoException {
        doUpdateColumn(originalColumn,
                       editedColumn,
                       () -> synchronizer.updateColumn(originalPattern,
                                                       originalColumn,
                                                       editedPattern,
                                                       editedColumn),
                       () -> refreshConditionsPanelEvent.fire(new RefreshConditionsPanelEvent(this,
                                                                                              model.getConditions())));
    }

    @Override
    public void updateColumn(final ConditionCol52 originalColumn,
                             final ConditionCol52 editedColumn) throws VetoException {
        doUpdateColumn(originalColumn,
                       editedColumn,
                       () -> synchronizer.updateColumn(originalColumn,
                                                       editedColumn),
                       () -> refreshConditionsPanelEvent.fire(new RefreshConditionsPanelEvent(this,
                                                                                              model.getConditions())));
    }

    @Override
    public void updateColumn(final ActionCol52 originalColumn,
                             final ActionCol52 editedColumn) throws VetoException {
        doUpdateColumn(originalColumn,
                       editedColumn,
                       () -> synchronizer.updateColumn(originalColumn,
                                                       editedColumn),
                       () -> refreshActionsPanelEvent.fire(new RefreshActionsPanelEvent(this,
                                                                                        model.getActionCols())));
    }

    private void doUpdateColumn(final BaseColumn originalColumn,
                                final BaseColumn editedColumn,
                                final VetoableUpdateColumnCommand update,
                                final Command callback) throws VetoException {
        if (isReadOnly()) {
            return;
        }
        final List<BaseColumnFieldDiff> diffs = update.execute();

        parent.updateLinks();

        //Log change to column definition
        if (!(diffs == null || diffs.isEmpty())) {
            view.getLayer().draw();
            model.getAuditLog().add(new UpdateColumnAuditLogEntry(identity.getIdentifier(),
                                                                  originalColumn,
                                                                  editedColumn,
                                                                  diffs));
            callback.execute();
        }
    }

    private void initialiseColumn(final BaseColumn column) {
        final GridColumn<?> gridColumn = gridWidgetColumnFactory.convertColumn(column,
                                                                               access,
                                                                               getView());
        uiModel.appendColumn(gridColumn);
    }

    private void initialiseRow(final List<BaseColumn> columns,
                               final List<DTCellValue52> row) {
        final GridRow uiModelRow = new BaseGridRow(GuidedDecisionTableView.ROW_HEIGHT);
        final int rowIndex = uiModel.getRowCount();
        uiModel.appendRow(uiModelRow);

        for (int iModelColumn = 0; iModelColumn < row.size(); iModelColumn++) {
            final DTCellValue52 modelCell = row.get(iModelColumn);
            final BaseColumn modelColumn = columns.get(iModelColumn);

            // We cannot rely upon the values in the existing data as legacy tables aren't guaranteed to be sorted
            if (modelColumn instanceof RowNumberCol52) {
                modelCell.setNumericValue(uiModel.getRowCount());
            }

            //BaseGridData is sparsely populated; only add values if needed.
            if (modelCell.hasValue()) {
                uiModel.setCellValueInternal(rowIndex,
                                             iModelColumn,
                                             gridWidgetCellFactory.convertCell(modelCell,
                                                                               modelColumn,
                                                                               cellUtilities,
                                                                               columnUtilities));

                //Set-up SelectionManager for Row Number column, to select entire row.
                if (modelColumn instanceof RowNumberCol52) {
                    uiModel.getCell(rowIndex,
                                    iModelColumn).setSelectionStrategy(RowSelectionStrategy.INSTANCE);
                }
            }
        }
    }

    @Override
    public void onCut() {
        if (isSelectionEmpty()) {
            return;
        }
        if (isReadOnly()) {
            return;
        }
        copyCellsToClipboard();
        onDeleteSelectedCells();
        view.showDataCutNotificationEvent();
    }

    @Override
    public void onCopy() {
        if (isSelectionEmpty()) {
            return;
        }
        if (isReadOnly()) {
            return;
        }
        copyCellsToClipboard();
        view.showDataCopiedNotificationEvent();
    }

    private void copyCellsToClipboard() {
        final List<GridData.SelectedCell> selections = uiModel.getSelectedCells();
        if (selections == null || selections.isEmpty()) {
            return;
        }
        int originRowIndex = Integer.MAX_VALUE;
        int originColumnIndex = Integer.MAX_VALUE;
        final Set<Clipboard.ClipboardData> data = new HashSet<>();

        for (GridData.SelectedCell sc : selections) {
            final int rowIndex = sc.getRowIndex();
            final int columnIndex = findUiColumnIndex(sc.getColumnIndex());
            originRowIndex = Math.min(rowIndex,
                                      originRowIndex);
            originColumnIndex = Math.min(columnIndex,
                                         originColumnIndex);
        }
        for (GridData.SelectedCell sc : selections) {
            final int rowIndex = sc.getRowIndex();
            final int columnIndex = findUiColumnIndex(sc.getColumnIndex());
            final DTCellValue52 value = model.getData().get(rowIndex).get(columnIndex);
            data.add(new DefaultClipboard.ClipboardDataImpl(rowIndex - originRowIndex,
                                                            columnIndex - originColumnIndex,
                                                            new DTCellValue52(value)));
        }
        clipboard.setData(data);
    }

    @Override
    public void onPaste() {
        if (!clipboard.hasData()) {
            return;
        }
        if (isSelectionEmpty()) {
            return;
        }
        if (isReadOnly()) {
            return;
        }
        final Set<Clipboard.ClipboardData> data = clipboard.getData();
        final int currentOriginRowIndex = uiModel.getSelectedCellsOrigin().getRowIndex();
        final int currentOriginColumnIndex = findUiColumnIndex(uiModel.getSelectedCellsOrigin().getColumnIndex());

        boolean updateSystemControlledValues = false;
        for (Clipboard.ClipboardData cd : data) {
            final int targetRowIndex = currentOriginRowIndex + cd.getRowIndex();
            final int targetColumnIndex = currentOriginColumnIndex + cd.getColumnIndex();
            if (targetRowIndex < 0 || targetRowIndex > uiModel.getRowCount() - 1) {
                continue;
            }
            if (targetColumnIndex < 0 || targetColumnIndex > uiModel.getColumns().size() - 1) {
                continue;
            }

            final DTCellValue52 modelCell = cd.getValue();
            final BaseColumn modelColumn = model.getExpandedColumns().get(targetColumnIndex);
            if (modelCell.hasValue()) {
                uiModel.setCellValue(targetRowIndex,
                                     targetColumnIndex,
                                     gridWidgetCellFactory.convertCell(modelCell,
                                                                       modelColumn,
                                                                       cellUtilities,
                                                                       columnUtilities));
            } else {
                uiModel.deleteCell(targetRowIndex,
                                   targetColumnIndex);
            }

            if (modelColumn instanceof RowNumberCol52) {
                updateSystemControlledValues = true;
            }
        }
        if (updateSystemControlledValues) {
            synchronizer.updateSystemControlledColumnValues();
        }
        view.batch();
    }

    boolean isSelectionEmpty() {
        return uiModel.getSelectedCells().isEmpty();
    }

    @Override
    public void onDeleteSelectedCells() {
        if (isReadOnly()) {
            return;
        }
        final List<GridData.SelectedCell> selections = uiModel.getSelectedCells();
        if (selections == null || selections.isEmpty()) {
            return;
        }
        for (GridData.SelectedCell sc : selections) {
            final int rowIndex = sc.getRowIndex();
            final int columnIndex = findUiColumnIndex(sc.getColumnIndex());
            final BaseColumn column = model.getExpandedColumns().get(columnIndex);
            final GridColumn<?> uiColumn = uiModel.getColumns().get(columnIndex);
            if (column instanceof RowNumberCol52) {
                continue;
            }
            if (uiColumn instanceof BooleanUiColumn) {
                uiModel.setCellValue(rowIndex,
                                     columnIndex,
                                     new GuidedDecisionTableUiCell<>(false));
            } else {
                uiModel.deleteCell(rowIndex,
                                   columnIndex);
            }
        }
        view.getLayer().draw();
    }

    @Override
    public void onDeleteSelectedColumns() {
        if (isReadOnly()) {
            return;
        }
        final Set<Integer> selectedColumnIndexes = getSelectedColumnIndexes();
        final Set<BaseColumn> columnsToDelete = new HashSet<>();
        for (int selectedColumnIndex : selectedColumnIndexes) {
            final int columnIndex = findUiColumnIndex(selectedColumnIndex);
            final BaseColumn column = model.getExpandedColumns().get(columnIndex);
            if (!(column instanceof RowNumberCol52 || column instanceof DescriptionCol52 || column instanceof RuleNameColumn)) {
                columnsToDelete.add(column);
            }
        }
        for (BaseColumn columnToDelete : columnsToDelete) {
            if (columnToDelete instanceof AttributeCol52) {
                try {
                    deleteColumn((AttributeCol52) columnToDelete);
                } catch (VetoException veto) {
                    getModellerPresenter().getView().showGenericVetoMessage();
                }
            } else if (columnToDelete instanceof MetadataCol52) {
                try {
                    deleteColumn((MetadataCol52) columnToDelete);
                } catch (VetoException veto) {
                    getModellerPresenter().getView().showGenericVetoMessage();
                }
            } else if (columnToDelete instanceof ConditionCol52) {
                try {
                    deleteColumn((ConditionCol52) columnToDelete);
                } catch (VetoException veto) {
                    getModellerPresenter().getView().showUnableToDeleteColumnMessage((ConditionCol52) columnsToDelete);
                }
            } else if (columnToDelete instanceof ActionCol52) {
                try {
                    deleteColumn((ActionCol52) columnToDelete);
                } catch (VetoException veto) {
                    getModellerPresenter().getView().showUnableToDeleteColumnMessage((ActionCol52) columnsToDelete);
                }
            }
        }
    }

    private Set<Integer> getSelectedColumnIndexes() {
        final Set<Integer> columnUsage = new HashSet<>();
        for (GridData.SelectedCell sc : uiModel.getSelectedCells()) {
            columnUsage.add(sc.getColumnIndex());
        }
        return columnUsage;
    }

    private int findUiColumnIndex(final int modelColumnIndex) {
        final List<GridColumn<?>> columns = uiModel.getColumns();
        for (int uiColumnIndex = 0; uiColumnIndex < columns.size(); uiColumnIndex++) {
            final GridColumn<?> c = columns.get(uiColumnIndex);
            if (c.getIndex() == modelColumnIndex) {
                return uiColumnIndex;
            }
        }
        throw new IllegalStateException("Column was not found!");
    }

    @Override
    public void onDeleteSelectedRows() {
        if (isReadOnly()) {
            return;
        }
        Set<Integer> selectedRowIndexes;
        while (!(selectedRowIndexes = getSelectedRowIndexes()).isEmpty()) {
            final int rowIndex = selectedRowIndexes.iterator().next();
            deleteRow(rowIndex);
        }
    }

    private void deleteRow(final int rowIndex) {
        try {
            synchronizer.deleteRow(rowIndex);

            refreshView();

            //Log deletion of column
            model.getAuditLog().add(new DeleteRowAuditLogEntry(identity.getIdentifier(),
                                                               rowIndex));
        } catch (VetoException e) {
            getModellerPresenter().getView().showGenericVetoMessage();
        }
    }

    @Override
    public boolean isMerged() {
        return uiModel.isMerged();
    }

    @Override
    public void setMerged(final boolean merged) {
        uiModel.setMerged(merged);
        view.getLayer().draw();
    }

    @Override
    public void showAuditLog() {
        auditLog.show();
    }

    @Override
    public void onInsertRowAbove() {
        doInsertRow(this::insertRow);
    }

    @Override
    public void onInsertRowBelow() {
        doInsertRow((index) -> insertRow(index + 1));
    }

    private void doInsertRow(final ParameterizedCommand<Integer> callback) {
        if (isReadOnly()) {
            return;
        }
        final Set<Integer> selectedRowIndexes = getSelectedRowIndexes();
        if (selectedRowIndexes.size() != 1) {
            return;
        }
        callback.execute(selectedRowIndexes.iterator().next());
    }

    private Set<Integer> getSelectedRowIndexes() {
        final Set<Integer> rowUsage = new HashSet<>();
        for (GridData.SelectedCell sc : uiModel.getSelectedCells()) {
            rowUsage.add(sc.getRowIndex());
        }
        return rowUsage;
    }

    private void insertRow(final int rowIndex) {
        try {
            synchronizer.insertRow(rowIndex);

            refreshView();

            //Log insertion of row
            model.getAuditLog().add(new InsertRowAuditLogEntry(identity.getIdentifier(),
                                                               rowIndex));
        } catch (VetoException e) {
            getModellerPresenter().getView().showGenericVetoMessage();
        }
    }

    @Override
    public void onOtherwiseCell() {
        if (isReadOnly()) {
            return;
        }
        final List<GridData.SelectedCell> selections = uiModel.getSelectedCells();
        if (selections.size() != 1) {
            return;
        }
        final GridData.SelectedCell selection = selections.get(0);
        final int columnIndex = findUiColumnIndex(selection.getColumnIndex());
        synchronizer.setCellOtherwiseState(selection.getRowIndex(),
                                           columnIndex);
        view.getLayer().draw();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public ObservablePath getLatestPath() {
        return latestPath;
    }

    @Override
    public void setLatestPath(final ObservablePath latestPath) {
        this.latestPath = latestPath;
    }

    @Override
    public ObservablePath getCurrentPath() {
        return currentPath;
    }

    @Override
    public void setCurrentPath(final ObservablePath currentPath) {
        this.currentPath = currentPath;
    }

    @Override
    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    @Override
    public boolean isReadOnly() {
        return !this.access.isEditable();
    }

    @Override
    public void setReadOnly(final boolean isReadOnly) {
        this.access.setReadOnly(isReadOnly);
    }

    @Override
    public boolean hasEditableColumns() {
        return getAccess().hasEditableColumns();
    }

    @Override
    public Integer getOriginalHashCode() {
        return originalHashCode;
    }

    @Override
    public void setOriginalHashCode(final Integer originalHashCode) {
        this.originalHashCode = originalHashCode;
    }

    @Override
    public ObservablePath.OnConcurrentUpdateEvent getConcurrentUpdateSessionInfo() {
        return concurrentUpdateSessionInfo;
    }

    @Override
    public void setConcurrentUpdateSessionInfo(final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo) {
        this.concurrentUpdateSessionInfo = concurrentUpdateSessionInfo;
    }

    private interface VetoableColumnCommand {

        void execute() throws VetoException;
    }

    private interface VetoableUpdateColumnCommand {

        List<BaseColumnFieldDiff> execute() throws VetoException;
    }

    public static class Access {

        private LockedBy lock = NOBODY;
        private boolean isReadOnly = false;
        private boolean hasEditableColumns = false;

        public LockedBy getLock() {
            return lock;
        }

        public void setLock(final LockedBy lock) {
            this.lock = lock;
        }

        public boolean isReadOnly() {
            return isReadOnly;
        }

        public void setReadOnly(final boolean isReadOnly) {
            this.isReadOnly = isReadOnly;
        }

        public boolean hasEditableColumns() {
            return hasEditableColumns;
        }

        public void setHasEditableColumns(final boolean hasEditableColumns) {
            this.hasEditableColumns = hasEditableColumns;
        }

        public boolean isEditable() {
            return !(lock == OTHER_USER || isReadOnly);
        }

        public enum LockedBy {
            CURRENT_USER,
            OTHER_USER,
            NOBODY
        }
    }
}
