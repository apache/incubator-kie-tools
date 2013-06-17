/*
 * Copyright 2011 JBoss Inc
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.kie.workbench.common.widgets.client.resources.WizardResources;
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
    HorizontalPanel messages;

    @UiField
    TextBox txtBaseFileName;

    @UiField
    HorizontalPanel baseFileNameContainer;

    @UiField
    Label lblContextPath;

    @UiField
    Label lblTableFormat;

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
        txtBaseFileName.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                baseFileName = txtBaseFileName.getText();
                presenter.stateChanged();
                validateBaseFileName();
            }

        } );
    }

    private void validateBaseFileName() {
        if ( baseFileName != null && !baseFileName.equals( "" ) ) {
            baseFileNameContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerValid() );
        } else {
            baseFileNameContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerInvalid() );
        }
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
        validateBaseFileName();
    }

    @Override
    public void setContextPath( final Path contextPath ) {
        lblContextPath.setText( contextPath.toURI() );
    }

    @Override
    public void setTableFormat( GuidedDecisionTable52.TableFormat tableFormat ) {
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
    public void setHasInvalidAssetName( final boolean isInvalid ) {
        messages.setVisible( isInvalid );
    }

}
