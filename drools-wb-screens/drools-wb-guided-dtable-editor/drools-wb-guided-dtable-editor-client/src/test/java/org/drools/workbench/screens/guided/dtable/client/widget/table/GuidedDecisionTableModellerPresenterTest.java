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

import javax.enterprise.event.Event;

import org.drools.workbench.screens.guided.dtable.client.editor.menu.CellContextMenu;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RadarMenuBuilder;
import org.drools.workbench.screens.guided.dtable.client.editor.menu.RowContextMenu;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UpdatedLockStatusEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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
    private CellContextMenu cellContextMenu;

    @Mock
    private RowContextMenu rowContextMenu;

    private GuidedDecisionTableModellerPresenter presenter;

    @Before
    public void setup() {
        presenter = new GuidedDecisionTableModellerPresenter( view,
                                                              updateRadarEvent,
                                                              pinnedEvent,
                                                              beanManager,
                                                              cellContextMenu,
                                                              rowContextMenu );
        when( beanManager.lookupBean( GuidedDecisionTableView.Presenter.class ) ).thenReturn( dtableBeanDef );
        when( dtableBeanDef.getInstance() ).thenReturn( dtablePresenter );
        when( dtableBeanDef.newInstance() ).thenReturn( dtablePresenter );
        when( dtablePresenter.getView() ).thenReturn( dtableView );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NonNullFile() {
        final ObservablePath dtPath = mock( ObservablePath.class );
        final PlaceRequest dtPlaceRequest = mock( PlaceRequest.class );
        final GuidedDecisionTableEditorContent dtContent = mock( GuidedDecisionTableEditorContent.class );

        final GuidedDecisionTableView.Presenter dtPresenter = presenter.addDecisionTable( dtPath,
                                                                                          dtPlaceRequest,
                                                                                          dtContent,
                                                                                          false );

        when( dtPresenter.getCurrentPath() ).thenReturn( dtPath );

        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        when( event.getFile() ).thenReturn( mock( Path.class ) );
        presenter.onUpdatedLockStatusEvent( event );
    }

    @Test
    public void testOnUpdatedLockStatusEvent_NullFile() {
        final ObservablePath dtPath = mock( ObservablePath.class );
        final PlaceRequest dtPlaceRequest = mock( PlaceRequest.class );
        final GuidedDecisionTableEditorContent dtContent = mock( GuidedDecisionTableEditorContent.class );

        final GuidedDecisionTableView.Presenter dtPresenter = presenter.addDecisionTable( dtPath,
                                                                                          dtPlaceRequest,
                                                                                          dtContent,
                                                                                          false );

        when( dtPresenter.getCurrentPath() ).thenReturn( dtPath );

        final UpdatedLockStatusEvent event = mock( UpdatedLockStatusEvent.class );
        presenter.onUpdatedLockStatusEvent( event );
    }

}
