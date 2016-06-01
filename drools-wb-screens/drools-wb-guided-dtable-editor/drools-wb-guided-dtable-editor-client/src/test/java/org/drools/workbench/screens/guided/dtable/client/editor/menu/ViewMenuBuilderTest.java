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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ViewMenuBuilderTest {

    private ViewMenuBuilder builder;

    @Mock
    private ViewMenuView view;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter;
    private GuidedDecisionTablePresenter.Access access = new GuidedDecisionTablePresenter.Access();

    @Mock
    private GuidedDecisionTableView dtPresenterView;

    @Mock
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        builder = new ViewMenuBuilder( view );
        builder.setup();
        builder.setModeller( modeller );

        when( dtPresenter.getAccess() ).thenReturn( access );
    }

    @Test
    public void testInitialSetup() {
        verify( view,
                times( 1 ) ).setZoom125( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom100( eq( true ) );
        verify( view,
                times( 1 ) ).setZoom75( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom50( eq( false ) );
        verify( view,
                times( 1 ) ).enableToggleMergedStateMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableViewAuditLogMenuItem( eq( false ) );
    }

    @Test
    public void testOnDecisionTableSelectedEventWithNonOtherwiseColumnSelected() {
        builder.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableToggleMergedStateMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableViewAuditLogMenuItem( eq( true ) );
    }

    @Test
    public void testOnZoom125() {
        reset( view );

        builder.onZoom( 125 );

        verify( view,
                times( 1 ) ).setZoom125( eq( true ) );
        verify( view,
                times( 1 ) ).setZoom100( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom75( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom50( eq( false ) );
        verify( modeller,
                times( 1 ) ).setZoom( eq( 125 ) );
    }

    @Test
    public void testOnZoom100() {
        reset( view );

        builder.onZoom( 100 );

        verify( view,
                times( 1 ) ).setZoom125( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom100( eq( true ) );
        verify( view,
                times( 1 ) ).setZoom75( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom50( eq( false ) );
        verify( modeller,
                times( 1 ) ).setZoom( eq( 100 ) );
    }

    @Test
    public void testOnZoom75() {
        reset( view );

        builder.onZoom( 75 );

        verify( view,
                times( 1 ) ).setZoom125( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom100( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom75( eq( true ) );
        verify( view,
                times( 1 ) ).setZoom50( eq( false ) );
        verify( modeller,
                times( 1 ) ).setZoom( eq( 75 ) );
    }

    @Test
    public void testOnZoom50() {
        reset( view );

        builder.onZoom( 50 );

        verify( view,
                times( 1 ) ).setZoom125( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom100( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom75( eq( false ) );
        verify( view,
                times( 1 ) ).setZoom50( eq( true ) );
        verify( modeller,
                times( 1 ) ).setZoom( eq( 50 ) );
    }

    @Test
    public void testToggleMergeState() {
        when( dtPresenter.isMerged() ).thenReturn( false );

        builder.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        verify( view,
                times( 1 ) ).enableToggleMergedStateMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).enableViewAuditLogMenuItem( eq( true ) );
        verify( view,
                times( 1 ) ).setMerged( eq( false ) );

        builder.onToggleMergeState();

        verify( dtPresenter,
                times( 1 ) ).setMerged( eq( true ) );
        verify( view,
                times( 1 ) ).setMerged( eq( true ) );
    }

    @Test
    public void testViewAuditLog() {
        builder.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        builder.onViewAuditLog();

        verify( dtPresenter,
                times( 1 ) ).showAuditLog();
    }

    @Test
    public void testEnableZoom_Pinned() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( modeller,
                                                                          true ) );

        verify( view,
                times( 1 ) ).enableZoom( eq( false ) );
    }

    @Test
    public void testEnableZoom_Pinned_DifferentModeller() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( mock( GuidedDecisionTableModellerView.Presenter.class ),
                                                                          true ) );

        verify( view,
                never() ).enableZoom( any( Boolean.class ) );
    }

    @Test
    public void testEnableZoom_Unpinned() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( modeller,
                                                                          false ) );

        verify( view,
                times( 1 ) ).enableZoom( eq( true ) );
    }

    @Test
    public void testEnableZoom_Unpinned_DifferentModeller() {
        builder.onDecisionTablePinnedEvent( new DecisionTablePinnedEvent( mock( GuidedDecisionTableModellerView.Presenter.class ),
                                                                          false ) );

        verify( view,
                never() ).enableZoom( any( Boolean.class ) );
    }

    @Test
    public void testOnDecisionTableSelectedEventReadOnly() {
        //ViewMenuBuilder.setup() called in @Setup disables view by default
        verify( view,
                times( 1 ) ).enableToggleMergedStateMenuItem( eq( false ) );
        verify( view,
                times( 1 ) ).enableViewAuditLogMenuItem( eq( false ) );

        dtPresenter.getAccess().setReadOnly( true );
        builder.onDecisionTableSelectedEvent( new DecisionTableSelectedEvent( dtPresenter ) );

        //Verify selecting a read-only Decision Table also disables view
        verify( view,
                times( 2 ) ).enableToggleMergedStateMenuItem( eq( false ) );
        verify( view,
                times( 2 ) ).enableViewAuditLogMenuItem( eq( false ) );
    }

}
