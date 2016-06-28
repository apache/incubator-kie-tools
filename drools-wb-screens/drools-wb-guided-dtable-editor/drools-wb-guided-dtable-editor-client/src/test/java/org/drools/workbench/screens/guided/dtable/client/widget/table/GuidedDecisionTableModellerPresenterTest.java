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
import java.util.Set;
import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTableModellerPresenterTest {

    @Mock
    private GuidedDecisionTableModellerView view;

    private Event<RadarMenuBuilder.UpdateRadarEvent> updateRadarEvent = new EventSourceMock<>();

    private Event<DecisionTablePinnedEvent> pinnedEvent = new EventSourceMock<>();

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private SyncBeanDef<GuidedDecisionTableView.Presenter> dtableBeanDef;

    @Mock
    private GuidedDecisionTableView.Presenter dtablePresenter;

    @Mock
    private GuidedDecisionTableView dtableView;

    @Mock
    private GuidedDecisionTableModellerContextMenuSupport contextMenuSupport;

    private GuidedDecisionTableModellerPresenter presenter;

    @Before
    public void setup() {
        presenter = new GuidedDecisionTableModellerPresenter( view,
                                                              contextMenuSupport,
                                                              updateRadarEvent,
                                                              pinnedEvent,
                                                              beanManager );
        when( beanManager.lookupBean( GuidedDecisionTableView.Presenter.class ) ).thenReturn( dtableBeanDef );
        when( dtableBeanDef.getInstance() ).thenReturn( dtablePresenter );
        when( dtableBeanDef.newInstance() ).thenReturn( dtablePresenter );
        when( dtablePresenter.getView() ).thenReturn( dtableView );
    }

    @Test
    public void refreshingDecisionTableRetainsExistingLocation() {
        final GuidedDecisionTableView.Presenter dtPresenter = makeDecisionTable();
        final GuidedDecisionTableEditorContent dtContent = makeDecisionTableContent();
        final GuidedDecisionTableView dtView = dtPresenter.getView();
        final ObservablePath path = mock( ObservablePath.class );
        final PlaceRequest placeRequest = mock( PlaceRequest.class );
        final boolean isReadOnly = false;

        final Point2D dtLocation = new Point2D( 100,
                                                100 );

        when( dtView.getLocation() ).thenReturn( dtLocation );

        final ArgumentCaptor<Command> afterRemovalCommandCaptor = ArgumentCaptor.forClass( Command.class );

        presenter.refreshDecisionTable( dtPresenter,
                                        path,
                                        placeRequest,
                                        dtContent,
                                        isReadOnly );

        verify( dtView,
                times( 1 ) ).getLocation();
        verify( view,
                times( 1 ) ).removeDecisionTable( eq( dtView ),
                                                  afterRemovalCommandCaptor.capture() );
        final Command afterRemovalCommand = afterRemovalCommandCaptor.getValue();
        assertNotNull( afterRemovalCommand );

        afterRemovalCommand.execute();

        verify( dtView,
                times( 1 ) ).setLocation( eq( dtLocation ) );
    }

    private GuidedDecisionTableView.Presenter makeDecisionTable() {
        final GuidedDecisionTableView.Presenter dtPresenter = mock( GuidedDecisionTableView.Presenter.class );
        final GuidedDecisionTableView dtView = mock( GuidedDecisionTableView.class );

        when( dtPresenter.getView() ).thenReturn( dtView );
        when( dtPresenter.getAccess() ).thenReturn( mock( GuidedDecisionTablePresenter.Access.class ) );
        when( dtPresenter.getModel() ).thenReturn( mock( GuidedDecisionTable52.class ) );

        return dtPresenter;
    }

    private GuidedDecisionTableEditorContent makeDecisionTableContent() {
        final GuidedDecisionTable52 model = mock( GuidedDecisionTable52.class );
        final PackageDataModelOracleBaselinePayload dmoBaseline = mock( PackageDataModelOracleBaselinePayload.class );
        final Set<PortableWorkDefinition> workItemDefinitions = Collections.emptySet();
        final Overview overview = mock( Overview.class );

        final GuidedDecisionTableEditorContent dtContent = new GuidedDecisionTableEditorContent( model,
                                                                                                 workItemDefinitions,
                                                                                                 overview,
                                                                                                 dmoBaseline );
        return dtContent;
    }

}
