/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.impl.DefaultClipboard;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DecisionTableAnalyzerProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableColumnSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMenusEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.lockmanager.GuidedDecisionTableLockManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.impl.GridWidgetCellFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.BaseColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionInsertFactColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionRetractFactColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionSetFieldColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionWorkItemExecuteColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionWorkItemInsertFactColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ActionWorkItemSetFieldColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.AttributeColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.BRLActionVariableColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.BRLConditionVariableColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.ConditionColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.DescriptionColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.GridWidgetColumnFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.LimitedEntryColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.MetaDataColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.RowNumberColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.RuleNameColumnConverter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.Synchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionInsertFactColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionRetractFactColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionSetFieldColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionWorkItemExecuteColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionWorkItemInsertFactColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ActionWorkItemSetFieldColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.AttributeColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BRLActionColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.BRLConditionColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ConditionColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.LimitedEntryBRLActionColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.LimitedEntryBRLConditionColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.MetaDataColumnSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.ModelSynchronizerImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl.RowSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.themes.GuidedDecisionTableRenderer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.EnumLoaderUtilities;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager;
import org.drools.workbench.screens.guided.dtable.shared.DefaultGuidedDecisionTableLinkManager;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.services.verifier.reporting.client.controller.AnalyzerController;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseGuidedDecisionTablePresenterTest {

    @Mock
    protected User identity;

    @Mock
    protected GuidedDTableResourceType resourceType;

    @Mock
    protected RuleNamesService ruleNameService;
    protected Caller<RuleNamesService> ruleNameServiceCaller;

    @Mock
    protected EnumDropdownService enumDropdownService;
    protected Caller<EnumDropdownService> enumDropdownServiceCaller;
    protected EnumLoaderUtilities enumLoaderUtilities;

    @Mock
    protected AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    protected AsyncPackageDataModelOracle oracle;

    @Mock
    protected SyncBeanManager beanManager;

    @Mock
    protected ObservablePath dtPath;

    @Mock
    protected PlaceRequest dtPlaceRequest;

    @Mock
    protected GuidedDecisionTableModellerPresenter modellerPresenter;

    @Mock
    protected GuidedDecisionTableModellerView modellerView;

    @Mock
    protected GuidedDecisionTableLockManager lockManager;

    @Mock
    protected AuthorizationManager authorizationManager;

    @Mock
    protected SessionInfo sessionInfo;

    protected GuidedDecisionTableLinkManager linkManager;

    protected Event<DecisionTableSelectedEvent> decisionTableSelectedEvent = spy(new EventSourceMock<DecisionTableSelectedEvent>() {
        @Override
        public void fire(final DecisionTableSelectedEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<DecisionTableColumnSelectedEvent> decisionTableColumnSelectedEvent = spy(new EventSourceMock<DecisionTableColumnSelectedEvent>() {
        @Override
        public void fire(final DecisionTableColumnSelectedEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<DecisionTableSelectionsChangedEvent> decisionTableSelectionsChangedEvent = spy(new EventSourceMock<DecisionTableSelectionsChangedEvent>() {
        @Override
        public void fire(final DecisionTableSelectionsChangedEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<RefreshAttributesPanelEvent> refreshAttributesPanelEvent = spy(new EventSourceMock<RefreshAttributesPanelEvent>() {
        @Override
        public void fire(final RefreshAttributesPanelEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<RefreshMetaDataPanelEvent> refreshMetaDataPanelEvent = spy(new EventSourceMock<RefreshMetaDataPanelEvent>() {
        @Override
        public void fire(final RefreshMetaDataPanelEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<RefreshConditionsPanelEvent> refreshConditionsPanelEvent = spy(new EventSourceMock<RefreshConditionsPanelEvent>() {
        @Override
        public void fire(final RefreshConditionsPanelEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<RefreshActionsPanelEvent> refreshActionsPanelEvent = spy(new EventSourceMock<RefreshActionsPanelEvent>() {
        @Override
        public void fire(final RefreshActionsPanelEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<RefreshMenusEvent> refreshMenusEvent = spy(new EventSourceMock<RefreshMenusEvent>() {
        @Override
        public void fire(final RefreshMenusEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected Event<NotificationEvent> notificationEvent = spy(new EventSourceMock<NotificationEvent>() {
        @Override
        public void fire(final NotificationEvent event) {
            //Do nothing. Default implementation throws an UnsupportedOperationException
        }
    });
    protected GridWidgetCellFactory gridWidgetCellFactory = new GridWidgetCellFactoryImpl();
    protected GridWidgetColumnFactory gridWidgetColumnFactory = new GridWidgetColumnFactoryImpl();
    protected ModelSynchronizer synchronizer = spy(new ModelSynchronizerImpl() {

        @Override
        protected void fireAfterColumnInsertedEvent(final BaseColumn column) {
            //Do nothing; we're not testing V&V integration in these tests.
        }

        @Override
        protected void fireAfterColumnDeletedEvent(final int columnIndex) {
            //Do nothing; we're not testing V&V integration in these tests.
        }

        @Override
        protected void fireAppendRowEvent() {
            //Do nothing; we're not testing V&V integration in these tests.
        }

        @Override
        protected void fireDeleteRowEvent(final int rowIndex) {
            //Do nothing; we're not testing V&V integration in these tests.
        }

        @Override
        protected void fireInsertRowEvent(final int rowIndex) {
            //Do nothing; we're not testing V&V integration in these tests.
        }

        @Override
        protected void fireValidateEvent(final GridData.Range rowRange,
                                         final Set<Integer> columnRange) {
            //Do nothing; we're not testing V&V integration in these tests.
        }

        @Override
        protected void fireUpdateColumnDataEvent() {
            //Do nothing; we're not testing V&V integration in these tests.
        }
    });
    protected Clipboard clipboard = spy(new DefaultClipboard());

    @Mock
    protected GuidedDecisionTableRenderer renderer;

    @Mock
    protected GuidedDecisionTableView view;

    @Mock
    protected DefaultGridLayer gridLayer;
    @Mock
    protected AnalyzerController analyzerController;
    protected GuidedDecisionTable52 model;
    protected GuidedDecisionTablePresenter dtPresenter;
    protected GuidedDecisionTableEditorContent dtContent;
    @Mock
    private DecisionTableAnalyzerProvider decisionTableAnalyzerProvider;

    @Mock
    private PluginHandler pluginHandler;

    @Before
    public void setup() {
        setupPreferences();
        setupServices();
        setupProviders();
        dtPresenter = setupPresenter();
    }

    private void setupProviders() {
        when(decisionTableAnalyzerProvider.newAnalyzer(any(),
                                                       eq(dtPlaceRequest),
                                                       eq(oracle),
                                                       any(GuidedDecisionTable52.class),
                                                       any(EventBus.class))).thenReturn(analyzerController);
    }

    private void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yyyy");
        }};
        ApplicationPreferences.setUp(preferences);
    }

    private void setupServices() {
        ruleNameServiceCaller = new CallerMock<>(ruleNameService);
        enumDropdownServiceCaller = new CallerMock<>(enumDropdownService);
        enumLoaderUtilities = new EnumLoaderUtilities(enumDropdownServiceCaller);
        linkManager = spy(new DefaultGuidedDecisionTableLinkManager());
    }

    protected GuidedDecisionTablePresenter setupPresenter() {
        final GuidedDecisionTablePresenter wrapped = new GuidedDecisionTablePresenter(identity,
                                                                                      resourceType,
                                                                                      ruleNameServiceCaller,
                                                                                      decisionTableSelectedEvent,
                                                                                      decisionTableColumnSelectedEvent,
                                                                                      decisionTableSelectionsChangedEvent,
                                                                                      refreshAttributesPanelEvent,
                                                                                      refreshMetaDataPanelEvent,
                                                                                      refreshConditionsPanelEvent,
                                                                                      refreshActionsPanelEvent,
                                                                                      refreshMenusEvent,
                                                                                      notificationEvent,
                                                                                      gridWidgetCellFactory,
                                                                                      gridWidgetColumnFactory,
                                                                                      oracleFactory,
                                                                                      synchronizer,
                                                                                      beanManager,
                                                                                      lockManager,
                                                                                      linkManager,
                                                                                      clipboard,
                                                                                      decisionTableAnalyzerProvider,
                                                                                      enumLoaderUtilities,
                                                                                      pluginHandler,
                                                                                      authorizationManager,
                                                                                      sessionInfo) {
            @Override
            void initialiseLockManager() {
                //Do nothing for tests
            }

            @Override
            GuidedDecisionTableRenderer makeViewRenderer() {
                return renderer;
            }

            @Override
            GuidedDecisionTableView makeView(final Set<PortableWorkDefinition> workItemDefinitions) {
                return view;
            }

            @Override
            void initialiseAuditLog() {
                //Do nothing for tests
            }

            @Override
            List<BaseColumnConverter> getConverters() {
                final List<BaseColumnConverter> converters = new ArrayList<BaseColumnConverter>();
                converters.add(new ActionInsertFactColumnConverter());
                converters.add(new ActionRetractFactColumnConverter());
                converters.add(new ActionSetFieldColumnConverter());
                converters.add(new ActionWorkItemExecuteColumnConverter());
                converters.add(new ActionWorkItemInsertFactColumnConverter());
                converters.add(new ActionWorkItemSetFieldColumnConverter());
                converters.add(new AttributeColumnConverter());
                converters.add(new BRLActionVariableColumnConverter());
                converters.add(new BRLConditionVariableColumnConverter());
                converters.add(new ConditionColumnConverter());
                converters.add(new DescriptionColumnConverter());
                converters.add(new LimitedEntryColumnConverter());
                converters.add(new MetaDataColumnConverter());
                converters.add(new RuleNameColumnConverter());
                converters.add(new RowNumberColumnConverter());
                return converters;
            }

            @Override
            List<Synchronizer<? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData>> getSynchronizers() {
                final List<Synchronizer<? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData, ? extends Synchronizer.MetaData>> synchronizers = new ArrayList<>();
                synchronizers.add(new ActionColumnSynchronizer());
                synchronizers.add(new ActionInsertFactColumnSynchronizer());
                synchronizers.add(new ActionRetractFactColumnSynchronizer());
                synchronizers.add(new ActionSetFieldColumnSynchronizer());
                synchronizers.add(new ActionWorkItemExecuteColumnSynchronizer());
                synchronizers.add(new ActionWorkItemInsertFactColumnSynchronizer());
                synchronizers.add(new ActionWorkItemSetFieldColumnSynchronizer());
                synchronizers.add(new AttributeColumnSynchronizer());
                synchronizers.add(new BRLActionColumnSynchronizer());
                synchronizers.add(new BRLConditionColumnSynchronizer());
                synchronizers.add(new ConditionColumnSynchronizer());
                synchronizers.add(new LimitedEntryBRLActionColumnSynchronizer());
                synchronizers.add(new LimitedEntryBRLConditionColumnSynchronizer());
                synchronizers.add(new MetaDataColumnSynchronizer());
                synchronizers.add(new RowSynchronizer());
                return synchronizers;
            }
        };
        GuidedDecisionTablePresenter presenter = spy(wrapped);

        model = new GuidedDecisionTable52();
        final PackageDataModelOracleBaselinePayload dmoBaseline = mock(PackageDataModelOracleBaselinePayload.class);
        final Set<PortableWorkDefinition> workItemDefinitions = Collections.emptySet();
        final Overview overview = mock(Overview.class);

        dtContent = new GuidedDecisionTableEditorContent(model,
                                                         workItemDefinitions,
                                                         overview,
                                                         dmoBaseline);

        when(oracleFactory.makeAsyncPackageDataModelOracle(any(Path.class),
                                                           any(GuidedDecisionTable52.class),
                                                           eq(dmoBaseline))).thenReturn(oracle);

        when(view.getLayer()).thenReturn(gridLayer);
        when(modellerPresenter.getView()).thenReturn(modellerView);
        when(modellerView.getGridLayerView()).thenReturn(gridLayer);
        when(presenter.getModellerPresenter()).thenReturn(modellerPresenter);

        presenter.setContent(dtPath,
                             dtPlaceRequest,
                             mock(AnalysisReportScreen.class),
                             dtContent,
                             modellerPresenter,
                             false);

        return presenter;
    }
}
