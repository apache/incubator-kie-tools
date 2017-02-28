/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.HitPolicyInternationalizer;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.backend.vfs.Path;

/**
 * An implementation of the Summary page
 */
@Dependent
public class SummaryPageViewImpl extends Composite
        implements
        SummaryPageView {

    private Presenter presenter;

    @UiField
    TextBox txtBaseFileName;

    @UiField
    HelpBlock baseFileNameHelp;

    @UiField
    FormGroup baseFileNameContainer;

    @UiField
    FormControlStatic lblContextPath;

    @UiField
    FormControlStatic lblTableFormat;

    @UiField
    FormControlStatic lblHitPolicy;

    private String baseFileName;

    interface SummaryPageWidgetBinder
            extends
            UiBinder<Widget, SummaryPageViewImpl> {

    }

    private static SummaryPageWidgetBinder uiBinder = GWT.create( SummaryPageWidgetBinder.class );

    public SummaryPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseBaseFileName();
    }

    private void initialiseBaseFileName() {
        txtBaseFileName.addKeyUpHandler( new KeyUpHandler() {

            @Override
            public void onKeyUp( KeyUpEvent event ) {
                handleFileNameInputKeyUp();
            }
        } );
    }

    void handleFileNameInputKeyUp() {
        baseFileName = txtBaseFileName.getText();
        presenter.stateChanged();
    }

    @Override
    public void init( final SummaryPageView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getBaseFileName() {
        return this.baseFileName;
    }

    @Override
    public void setBaseFileName( final String baseFileName ) {
        this.baseFileName = baseFileName;
        txtBaseFileName.setText( baseFileName );
    }

    @Override
    public void setValidBaseFileName( final boolean isValid ) {
        if ( isValid ) {
            baseFileNameHelp.setVisible( false );
            baseFileNameContainer.removeStyleName( ValidationState.ERROR.getCssName() );
        } else {
            baseFileNameHelp.setVisible( true );
            baseFileNameContainer.addStyleName( ValidationState.ERROR.getCssName() );
        }
    }

    @Override
    public void setContextPath( final Path contextPath ) {
        lblContextPath.setText( contextPath.toURI() );
    }

    @Override
    public void setTableFormat( final GuidedDecisionTable52.TableFormat tableFormat ) {
        switch ( tableFormat ) {
            case EXTENDED_ENTRY:
                lblTableFormat.setText( GuidedDecisionTableConstants.INSTANCE.TableFormatExtendedEntry() );
                break;
            case LIMITED_ENTRY:
                lblTableFormat.setText( GuidedDecisionTableConstants.INSTANCE.TableFormatLimitedEntry() );
                break;
        }
    }

    @Override
    public void setHitPolicy( final GuidedDecisionTable52.HitPolicy hitPolicy ) {
        lblHitPolicy.setText( HitPolicyInternationalizer.internationalize( hitPolicy ) );
    }

}
