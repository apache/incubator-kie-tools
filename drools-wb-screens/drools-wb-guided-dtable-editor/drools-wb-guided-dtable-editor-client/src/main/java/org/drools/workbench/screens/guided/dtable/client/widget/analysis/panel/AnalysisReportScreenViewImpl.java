/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.reporting.Issue;

public class AnalysisReportScreenViewImpl
        extends Composite
        implements AnalysisReportScreenView,
                   RequiresResize {

    private AnalysisReportScreen presenter;

    interface Binder
            extends
            UiBinder<Widget, AnalysisReportScreenViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField(provided = true)
    CellList<Issue> issuesList;

    @UiField(provided = true)
    IssuePresenter issueDetails;

    @UiField
    ScrollPanel issuesListContainer;

    @Inject
    public AnalysisReportScreenViewImpl( final IssuePresenter issueDetails ) {

        makeIssuesList();
        this.issueDetails = issueDetails;

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void makeIssuesList() {
        issuesList = new CellList<Issue>( new AnalysisLineCell() );
        issuesList.setKeyboardPagingPolicy( HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE );
        issuesList.setKeyboardSelectionPolicy( HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.BOUND_TO_SELECTION );
        issuesList.setSelectionModel( getSelectionModel() );
    }

    private SingleSelectionModel<Issue> getSelectionModel() {
        final SingleSelectionModel<Issue> selectionModel = new SingleSelectionModel<Issue>();

        selectionModel.addSelectionChangeHandler(
                new SelectionChangeEvent.Handler() {
                    public void onSelectionChange( SelectionChangeEvent event ) {
                        presenter.onSelect( selectionModel.getSelectedObject() );
                    }
                } );

        return selectionModel;
    }

    @Override
    public void setUpDataProvider( ListDataProvider<Issue> dataProvider ) {
        dataProvider.addDataDisplay( issuesList );
    }

    @Override
    public void setPresenter( final AnalysisReportScreen presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void show( Issue issue ) {
        issueDetails.show( issue );
    }

    @Override
    public void clearIssue() {
        issueDetails.clear();
    }

    @Override
    public void onResize() {
        setHeight( getParent().getOffsetHeight() + "px" );
        setWidth( ( getParent().getOffsetWidth() - 15 ) + "px" );

//        issuesListContainer.setWidth( (getParent().getOffsetWidth() - 1) + "px" );
    }

}
