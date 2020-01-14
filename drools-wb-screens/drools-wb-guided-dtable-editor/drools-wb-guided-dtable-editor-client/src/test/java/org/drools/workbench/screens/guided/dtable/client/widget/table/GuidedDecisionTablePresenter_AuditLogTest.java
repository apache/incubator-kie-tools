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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.auditlog.UpdateColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.DecisionTableAnalyzerProvider;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMenusEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.lockmanager.GuidedDecisionTableLockManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.GridWidgetColumnFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.EnumLoaderUtilities;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.verifier.reporting.client.panel.AnalysisReportScreen;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({Text.class, DateTimeFormat.class})
@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTablePresenter_AuditLogTest {

    @Mock
    private User identity;

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private ModelSynchronizer synchronizer;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private GuidedDecisionTableModellerPresenter modellerPresenter;

    @Mock
    private GuidedDecisionTableLockManager lockManager;

    @Mock
    private GuidedDecisionTableView view;

    @Mock
    private EventSourceMock<RefreshAttributesPanelEvent> refreshAttributesPanelEvent;

    @Mock
    private EventSourceMock<RefreshMetaDataPanelEvent> refreshMetaDataPanelEvent;

    @Mock
    private EventSourceMock<RefreshConditionsPanelEvent> refreshConditionsPanelEvent;

    @Mock
    private EventSourceMock<RefreshActionsPanelEvent> refreshActionsPanelEvent;

    @Mock
    private EventSourceMock<RefreshMenusEvent> refreshMenusEvent;

    @Mock
    private DecisionTableAnalyzerProvider decisionTableAnalyzerProvider;

    @Mock
    private EnumLoaderUtilities enumLoaderUtilities;

    @Mock
    private PluginHandler pluginHandler;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    private GridWidgetColumnFactory gridWidgetColumnFactory = new GridWidgetColumnFactoryImpl();
    private GuidedDecisionTablePresenter dtPresenter;
    private GuidedDecisionTableEditorContent dtContent;

    private GuidedDecisionTable52 model = spy(new GuidedDecisionTable52());
    private List<BaseColumnFieldDiff> diffs;

    @Before
    public void setup() throws VetoException {
        setupPresenter();

        for (Entry<String, Boolean> entry : model.getAuditLog().getAuditLogFilter().getAcceptedTypes().entrySet()) {
            entry.setValue(Boolean.TRUE);
        }
        Mockito.reset(model);

        diffs = new ArrayList();
        diffs.add(null);
        when(synchronizer.updateColumn(any(BaseColumn.class),
                                       any(BaseColumn.class))).thenReturn(diffs);
        when(synchronizer.updateColumn(any(Pattern52.class),
                                       any(ConditionCol52.class),
                                       any(Pattern52.class),
                                       any(ConditionCol52.class))).thenReturn(diffs);
    }

    private void setupPresenter() {
        dtPresenter = new GuidedDecisionTablePresenter(identity,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       refreshAttributesPanelEvent,
                                                       refreshMetaDataPanelEvent,
                                                       refreshConditionsPanelEvent,
                                                       refreshActionsPanelEvent,
                                                       refreshMenusEvent,
                                                       null,
                                                       null,
                                                       gridWidgetColumnFactory,
                                                       oracleFactory,
                                                       synchronizer,
                                                       beanManager,
                                                       lockManager,
                                                       null,
                                                       null,
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
            GuidedDecisionTableView makeView(final Set<PortableWorkDefinition> workItemDefinitions) {
                return view;
            }

            @Override
            void initialiseModels() {
                //Do nothing for tests
            }
        };

        final AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
        final PackageDataModelOracleBaselinePayload dmoBaseline = mock(PackageDataModelOracleBaselinePayload.class);
        final Set<PortableWorkDefinition> workItemDefinitions = Collections.emptySet();
        final Overview overview = mock(Overview.class);

        dtContent = new GuidedDecisionTableEditorContent(model,
                                                         workItemDefinitions,
                                                         overview,
                                                         dmoBaseline);

        when(oracleFactory.makeAsyncPackageDataModelOracle(any(Path.class),
                                                           any(GuidedDecisionTable52.class),
                                                           eq(dmoBaseline))).thenReturn(dmo);

        dtPresenter.setContent(null,
                               mock(PlaceRequest.class),
                               mock(AnalysisReportScreen.class),
                               dtContent,
                               modellerPresenter,
                               false);
        when(view.getLayer()).thenReturn(mock(Layer.class));
    }

    @Test
    public void updateColumnAddsToLog() throws VetoException {
        dtPresenter.updateColumn(new ActionCol52(),
                                 new ActionCol52());
        dtPresenter.updateColumn(new AttributeCol52(),
                                 new AttributeCol52());
        dtPresenter.updateColumn(new ConditionCol52(),
                                 new ConditionCol52());
        dtPresenter.updateColumn(new MetadataCol52(),
                                 new MetadataCol52());
        dtPresenter.updateColumn(new Pattern52(),
                                 new ConditionCol52(),
                                 new Pattern52(),
                                 new ConditionCol52());

        verify(synchronizer,
               times(4)).updateColumn(any(BaseColumn.class),
                                      any(BaseColumn.class));
        verify(synchronizer).updateColumn(any(Pattern52.class),
                                          any(ConditionCol52.class),
                                          any(Pattern52.class),
                                          any(ConditionCol52.class));
        verify(model,
               times(5)).getAuditLog();
        assertEquals(5,
                     model.getAuditLog().size());
        for (UpdateColumnAuditLogEntry entry : model.getAuditLog().toArray(new UpdateColumnAuditLogEntry[0])) {
            assertEquals(diffs,
                         entry.getDiffs());
        }
    }
}
