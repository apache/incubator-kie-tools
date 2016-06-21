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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.linkmanager.impl;

import java.util.Collections;
import java.util.HashSet;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGuidedDecisionTableLinkManagerTest {

    @Captor
    private ArgumentCaptor<GridData> sourceUiModelArgumentCaptor;

    @Captor
    private ArgumentCaptor<GridData> targetUiModelArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> sourceColumnIndexArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> targetColumnIndexArgumentCaptor;

    private DefaultGuidedDecisionTableLinkManager manager;

    @Before
    public void setup() {
        final DefaultGuidedDecisionTableLinkManager wrapped = new DefaultGuidedDecisionTableLinkManager() {
            @Override
            void linkColumns( final GridData sourceUiModel,
                              final GridData targetUiModel,
                              final int sourceColumnIndex,
                              final int targetColumnIndex ) {
                //Do nothing for Unit Tests
            }
        };
        manager = spy( wrapped );
    }

    private GuidedDecisionTableView.Presenter makeGuidedDecisionTablePresenter() {
        final GuidedDecisionTableView.Presenter dtPresenter = mock( GuidedDecisionTableView.Presenter.class );
        final GuidedDecisionTableView dtPresenterView = mock( GuidedDecisionTableView.class );
        final GridData uiModel = mock( GridData.class );
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();

        when( dtPresenter.getView() ).thenReturn( dtPresenterView );
        when( dtPresenterView.getModel() ).thenReturn( uiModel );
        when( dtPresenter.getModel() ).thenReturn( model );

        return dtPresenter;
    }

    @Test
    public void onlyOneDecisionTableThereforeNoLinks() {
        manager.link( makeGuidedDecisionTablePresenter(),
                      Collections.<GuidedDecisionTableView.Presenter>emptySet() );

        verify( manager,
                never() ).linkColumns( any( GridData.class ),
                                       any( GridData.class ),
                                       any( Integer.class ),
                                       any( Integer.class ) );
    }

    @Test
    public void fieldConstraintLinksToActionUpdateField() {
        //Columns: Row#[0], Description[1], Condition[2], Action[3]
        final GuidedDecisionTableView.Presenter dtPresenter1 = makeGuidedDecisionTablePresenter();
        final Pattern52 p1 = new Pattern52();
        p1.setBoundName( "$f" );
        p1.setFactType( "Fact" );
        final ConditionCol52 p1c1 = new ConditionCol52();
        p1c1.setFactField( "field" );
        p1.getChildColumns().add( p1c1 );
        dtPresenter1.getModel().getConditions().add( p1 );
        final ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "$f" );
        asf.setFactField( "field" );
        dtPresenter1.getModel().getActionCols().add( asf );

        //Columns: Row#[0], Description[1], Condition[2]
        final GuidedDecisionTableView.Presenter dtPresenter2 = makeGuidedDecisionTablePresenter();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName( "$f" );
        p2.setFactType( "Fact" );
        final ConditionCol52 p2c1 = new ConditionCol52();
        p2c1.setFactField( "field" );
        p2.getChildColumns().add( p2c1 );
        dtPresenter2.getModel().getConditions().add( p2 );

        final GridData dtPresenter1UiModel = dtPresenter1.getView().getModel();
        final GridData dtPresenter2UiModel = dtPresenter2.getView().getModel();

        manager.link( dtPresenter1,
                      new HashSet<GuidedDecisionTableView.Presenter>() {{
                          add( dtPresenter2 );
                      }} );

        verify( manager,
                times( 1 ) ).linkColumns( sourceUiModelArgumentCaptor.capture(),
                                          targetUiModelArgumentCaptor.capture(),
                                          sourceColumnIndexArgumentCaptor.capture(),
                                          targetColumnIndexArgumentCaptor.capture() );

        final GridData sourceUiModel = sourceUiModelArgumentCaptor.getValue();
        final GridData targetUiModel = targetUiModelArgumentCaptor.getValue();
        final int sourceColumnIndex = sourceColumnIndexArgumentCaptor.getValue();
        final int targetColumnIndex = targetColumnIndexArgumentCaptor.getValue();

        //Source (dtPresenter1) Action column [3] is linked to Target (dtPresenter2) Condition column [2]
        assertEquals( dtPresenter1UiModel,
                      sourceUiModel );
        assertEquals( dtPresenter2UiModel,
                      targetUiModel );
        assertEquals( 3,
                      sourceColumnIndex );
        assertEquals( 2,
                      targetColumnIndex );
    }

    @Test
    public void fieldConstraintLinksToActionInseertFactField() {
        //Columns: Row#[0], Description[1], Action[2]
        final GuidedDecisionTableView.Presenter dtPresenter1 = makeGuidedDecisionTablePresenter();
        final ActionInsertFactCol52 aif = new ActionInsertFactCol52();
        aif.setFactType( "Fact" );
        aif.setFactField( "field" );
        dtPresenter1.getModel().getActionCols().add( aif );

        //Columns: Row#[0], Description[1], Condition[2]
        final GuidedDecisionTableView.Presenter dtPresenter2 = makeGuidedDecisionTablePresenter();
        final Pattern52 p2 = new Pattern52();
        p2.setBoundName( "$f" );
        p2.setFactType( "Fact" );
        final ConditionCol52 p2c1 = new ConditionCol52();
        p2c1.setFactField( "field" );
        p2.getChildColumns().add( p2c1 );
        dtPresenter2.getModel().getConditions().add( p2 );

        final GridData dtPresenter1UiModel = dtPresenter1.getView().getModel();
        final GridData dtPresenter2UiModel = dtPresenter2.getView().getModel();

        manager.link( dtPresenter1,
                      new HashSet<GuidedDecisionTableView.Presenter>() {{
                          add( dtPresenter2 );
                      }} );

        verify( manager,
                times( 1 ) ).linkColumns( sourceUiModelArgumentCaptor.capture(),
                                          targetUiModelArgumentCaptor.capture(),
                                          sourceColumnIndexArgumentCaptor.capture(),
                                          targetColumnIndexArgumentCaptor.capture() );

        final GridData sourceUiModel = sourceUiModelArgumentCaptor.getValue();
        final GridData targetUiModel = targetUiModelArgumentCaptor.getValue();
        final int sourceColumnIndex = sourceColumnIndexArgumentCaptor.getValue();
        final int targetColumnIndex = targetColumnIndexArgumentCaptor.getValue();

        //Source (dtPresenter1) Action column [2] is linked to Target (dtPresenter2) Condition column [2]
        assertEquals( dtPresenter1UiModel,
                      sourceUiModel );
        assertEquals( dtPresenter2UiModel,
                      targetUiModel );
        assertEquals( 2,
                      sourceColumnIndex );
        assertEquals( 2,
                      targetColumnIndex );
    }

}
