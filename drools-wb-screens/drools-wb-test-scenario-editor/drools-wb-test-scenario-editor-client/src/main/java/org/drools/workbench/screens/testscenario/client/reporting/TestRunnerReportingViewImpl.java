/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.testscenario.client.reporting;

import java.util.Date;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.messageconsole.client.console.widget.MessageTableWidget;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;

public class TestRunnerReportingViewImpl
        extends Composite
        implements TestRunnerReportingView {

    private static Binder uiBinder = GWT.create( Binder.class );
    private Presenter presenter;

    interface Binder extends UiBinder<Widget, TestRunnerReportingViewImpl> {

    }

    @UiField
    Row dataGridHost;

    @UiField
    Label successPanel;

    @UiField
    Label failurePanel;

    @UiField
    InlineLabel stats;

    protected final MessageTableWidget<Failure> dataGrid = new MessageTableWidget<Failure>() {{
        setToolBarVisible( false );
    }};

    @Inject
    public TestRunnerReportingViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        dataGridHost.add( dataGrid );

        addSuccessColumn();
        addTextColumn();

        dataGrid.addStyleName( ColumnSize.MD_12.getCssName() );
    }

    private void addSuccessColumn() {
        dataGrid.addLevelColumn( 10, new MessageTableWidget.ColumnExtractor<Level>() {
            @Override
            public Level getValue( final Object row ) {
                presenter.onAddingFailure( (Failure) row );
                return Level.ERROR;
            }
        } );
    }

    private void addTextColumn() {
        dataGrid.addTextColumn( 90, new MessageTableWidget.ColumnExtractor<String>() {
            @Override
            public String getValue( final Object row ) {

                return makeMessage( (Failure) row );
            }
        } );
    }

    private String makeMessage( Failure failure ) {
        final String displayName = failure.getDisplayName();
        final String message = failure.getMessage();
        return displayName + ( !( message == null || message.isEmpty() ) ? " : " + message : "" );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void bindDataGridToService( TestRuntimeReportingService testRuntimeReportingService ) {
        testRuntimeReportingService.addDataDisplay( dataGrid );
    }

    @Override
    public void showSuccess() {
        successPanel.setVisible( true );
        failurePanel.setVisible( false );
    }

    @Override
    public void showFailure() {
        failurePanel.setVisible( true );
        successPanel.setVisible( false );
    }

    @Override
    public void setExplanation( String explanation ) {
    }

    @Override
    public void setRunStatus( int runCount,
                              long runTime ) {
        Date date = new Date( runTime );
        DateTimeFormat minutesFormat = DateTimeFormat.getFormat( "m" );
        DateTimeFormat secondsFormat = DateTimeFormat.getFormat( "s" );

        stats.setText( TestScenarioConstants.INSTANCE.XTestsRanInYMinutesZSeconds( runCount, minutesFormat.format( date ), secondsFormat.format( date ) ) );
    }
}
