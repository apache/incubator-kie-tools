/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.impl.DefaultClipboard;
import org.drools.workbench.screens.guided.dtable.client.type.GuidedDTableResourceType;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableColumnSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectionsChangedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshActionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshAttributesPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshConditionsPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.RefreshMetaDataPanelEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.lockmanager.GuidedDecisionTableLockManager;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.GridWidgetCellFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.cell.impl.GridWidgetCellFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.GridWidgetColumnFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl.GridWidgetColumnFactoryImpl;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter.Access.LockedBy.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTablePresenterTest {

    @Mock
    private User identity;

    @Mock
    private GuidedDTableResourceType resourceType;

    @Mock
    private RuleNamesService ruleNameService;
    private Caller<RuleNamesService> ruleNameServiceCaller;

    @Mock
    private EnumDropdownService enumDropdownService;
    private Caller<EnumDropdownService> enumDropdownServiceCaller;

    @Mock
    private AsyncPackageDataModelOracleFactory oracleFactory;

    @Mock
    private ModelSynchronizer synchronizer;

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private ObservablePath dtPath;

    @Mock
    private PlaceRequest dtPlaceRequest;

    @Mock
    private GuidedDecisionTableModellerPresenter modellerPresenter;

    @Mock
    private GuidedDecisionTableLockManager lockManager;

    private Event<DecisionTableSelectedEvent> decisionTableSelectedEvent = new EventSourceMock<>();
    private Event<DecisionTableColumnSelectedEvent> decisionTableColumnSelectedEvent = new EventSourceMock<>();
    private Event<DecisionTableSelectionsChangedEvent> decisionTableSelectionsChangedEvent = new EventSourceMock<>();
    private Event<RefreshAttributesPanelEvent> refreshAttributesPanelEvent = new EventSourceMock<>();
    private Event<RefreshMetaDataPanelEvent> refreshMetaDataPanelEvent = new EventSourceMock<>();
    private Event<RefreshConditionsPanelEvent> refreshConditionsPanelEvent = new EventSourceMock<>();
    private Event<RefreshActionsPanelEvent> refreshActionsPanelEvent = new EventSourceMock<>();
    private Event<NotificationEvent> notificationEvent = new EventSourceMock<>();
    private GridWidgetCellFactory gridWidgetCellFactory = new GridWidgetCellFactoryImpl();
    private GridWidgetColumnFactory gridWidgetColumnFactory = new GridWidgetColumnFactoryImpl();
    private Clipboard clipboard = new DefaultClipboard();

    @Mock
    private GuidedDecisionTableView view;

    private GuidedDecisionTablePresenter dtPresenter;
    private GuidedDecisionTableEditorContent dtContent;

    @Before
    public void setup() {
        setupPreferences();
        setupServices();
        setupPresenter();
    }

    private void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put( ApplicationPreferences.DATE_FORMAT,
                 "dd/mm/yyyy" );
        }};
        ApplicationPreferences.setUp( preferences );
    }

    private void setupServices() {
        ruleNameServiceCaller = new CallerMock<>( ruleNameService );
        enumDropdownServiceCaller = new CallerMock<>( enumDropdownService );
    }

    private void setupPresenter() {
        final GuidedDecisionTablePresenter wrapped = new GuidedDecisionTablePresenter( identity,
                                                                                       resourceType,
                                                                                       ruleNameServiceCaller,
                                                                                       enumDropdownServiceCaller,
                                                                                       decisionTableSelectedEvent,
                                                                                       decisionTableColumnSelectedEvent,
                                                                                       decisionTableSelectionsChangedEvent,
                                                                                       refreshAttributesPanelEvent,
                                                                                       refreshMetaDataPanelEvent,
                                                                                       refreshConditionsPanelEvent,
                                                                                       refreshActionsPanelEvent,
                                                                                       notificationEvent,
                                                                                       gridWidgetCellFactory,
                                                                                       gridWidgetColumnFactory,
                                                                                       oracleFactory,
                                                                                       synchronizer,
                                                                                       beanManager,
                                                                                       lockManager,
                                                                                       clipboard ) {
            @Override
            void initialiseLockManager() {
                //Do nothing for tests
            }

            @Override
            GuidedDecisionTableView makeView( final Set<PortableWorkDefinition> workItemDefinitions ) {
                return view;
            }

            @Override
            void initialiseModels() {
                //Do nothing for tests
            }

            @Override
            void initialiseAuditLog() {
                //Do nothing for tests
            }
        };
        dtPresenter = spy( wrapped );

        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final AsyncPackageDataModelOracle dmo = mock( AsyncPackageDataModelOracle.class );
        final PackageDataModelOracleBaselinePayload dmoBaseline = mock( PackageDataModelOracleBaselinePayload.class );
        final Set<PortableWorkDefinition> workItemDefinitions = Collections.emptySet();
        final Overview overview = mock( Overview.class );

        dtContent = new GuidedDecisionTableEditorContent( model,
                                                          workItemDefinitions,
                                                          overview,
                                                          dmoBaseline );

        when( oracleFactory.makeAsyncPackageDataModelOracle( any( Path.class ),
                                                             any( GuidedDecisionTable52.class ),
                                                             eq( dmoBaseline ) ) ).thenReturn( dmo );

        dtPresenter.setContent( dtPath,
                                dtPlaceRequest,
                                dtContent,
                                modellerPresenter,
                                false );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByCurrentUser() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( dtPath );
        when( event.isLockedByCurrentUser() ).thenReturn( true );
        when( event.isLocked() ).thenReturn( true );

        dtPresenter.onUpdatedLockStatusEvent( event );

        verify( modellerPresenter,
                times( 1 ) ).onLockStatusUpdated( eq( dtPresenter ) );
        assertEquals( CURRENT_USER,
                      dtPresenter.getAccess().getLock() );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_LockedByOtherUser() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( dtPath );
        when( event.isLockedByCurrentUser() ).thenReturn( false );
        when( event.isLocked() ).thenReturn( true );

        dtPresenter.onUpdatedLockStatusEvent( event );

        verify( modellerPresenter,
                times( 1 ) ).onLockStatusUpdated( eq( dtPresenter ) );
        assertEquals( OTHER_USER,
                      dtPresenter.getAccess().getLock() );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NotLocked() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( dtPath );
        dtPresenter.onUpdatedLockStatusEvent( event );

        verify( modellerPresenter,
                times( 1 ) ).onLockStatusUpdated( eq( dtPresenter ) );
        assertEquals( NOBODY,
                      dtPresenter.getAccess().getLock() );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NullFile() {
        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        dtPresenter.onUpdatedLockStatusEvent( event );
    }

    @Test
    public void testActivate() {
        dtPresenter.activate();
        verify( lockManager,
                times( 1 ) ).fireChangeTitleEvent();
    }

}
