/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.search.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.typeahead.client.base.StringDataset;
import org.gwtbootstrap3.extras.typeahead.client.ui.Typeahead;
import org.kie.workbench.common.screens.search.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.search.client.widgets.SearchResultTable;
import org.kie.workbench.common.screens.search.model.QueryMetadataPageRequest;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

@Dependent
@WorkbenchScreen( identifier = "FindForm" )
public class FindForm
        extends Composite {

    interface FindFormBinder
            extends
            UiBinder<Widget, FindForm> {

    }

    private static FindFormBinder uiBinder = GWT.create( FindFormBinder.class );

    @Inject
    private ClientTypeRegistry clientTypeRegistry;

    @UiField
    SimplePanel errorPanel;

    @UiField
    FormGroup formGroup;

    @UiField
    Form form;

    @UiField
    TextBox sourceTextBox;

    @UiField
    TextBox createdByTextBox;

    @UiField
    TextBox descriptionByTextBox;

    @UiField
    Typeahead formatTypeahead;

    @UiField
    TextBox subjectTextBox;

    @UiField
    TextBox typeTextBox;

    @UiField
    TextBox lastModifiedByTextBox;

    @UiField
    TextBox externalLinkTextBox;

    @UiField
    TextBox checkinCommentTextBox;

    @UiField
    DatePicker createdAfter;

    @UiField
    DatePicker createdBefore;

    @UiField
    DatePicker lastModifiedAfter;

    @UiField
    DatePicker lastModifiedBefore;

    @UiField
    PanelGroup accordion;

    @UiField
    PanelHeader formAccordionHeader;

    @UiField
    PanelCollapse formAccordionCollapse;

    @UiField
    PanelHeader resultAccordionHeader;

    @UiField
    PanelCollapse resultAccordionCollapse;

    @UiField
    Column simplePanel;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        accordion.setId( DOM.createUniqueId() );
        formAccordionHeader.setDataParent( accordion.getId() );
        formAccordionHeader.setDataTargetWidget( formAccordionCollapse );
        resultAccordionHeader.setDataParent( accordion.getId() );
        resultAccordionHeader.setDataTargetWidget( resultAccordionCollapse );

        //TODO {porcelli} due a bug on bootstrap we can't use custom date formats
        createdAfter.setFormat( ApplicationPreferences.getDroolsDateFormat() );
        createdBefore.setFormat( ApplicationPreferences.getDroolsDateFormat() );
        lastModifiedAfter.setFormat( ApplicationPreferences.getDroolsDateFormat() );
        lastModifiedBefore.setFormat( ApplicationPreferences.getDroolsDateFormat() );

        formGroup.setStyleName( null );

        formatTypeahead.setDatasets( new StringDataset( new ArrayList<String>() {{
            for ( final ClientResourceType resourceType : clientTypeRegistry.getRegisteredTypes() ) {
                add( resourceType.getShortName() );
            }
        }} ) );
    }

    @UiHandler( "clear" )
    public void onClearClick( final ClickEvent e ) {
        form.reset();
    }

    @UiHandler( "search" )
    public void onSearchClick( final ClickEvent e ) {
        errorPanel.clear();
        formGroup.setValidationState( ValidationState.NONE );
        final Map<String, Object> metadata = new HashMap<String, Object>();
        if ( !sourceTextBox.getText().trim().isEmpty() ) {
            metadata.put( "dcore.source[0]", sourceTextBox.getText().trim() );
        }

        if ( !createdByTextBox.getText().trim().isEmpty() ) {
            metadata.put( "createdBy", createdByTextBox.getText().trim() );
        }

        if ( !descriptionByTextBox.getText().trim().isEmpty() ) {
            metadata.put( "dcore.description[0]", descriptionByTextBox.getText().trim() );
        }

        if ( !formatTypeahead.getText().trim().isEmpty() ) {
            final String pattern = clientTypeRegistry.resolveWildcardPattern( formatTypeahead.getText().trim() );
            metadata.put( "filename", pattern );
        }

        if ( !subjectTextBox.getText().trim().isEmpty() ) {
            metadata.put( "dcore.subject[0]", subjectTextBox.getText().trim() );
        }

        if ( !typeTextBox.getText().trim().isEmpty() ) {
            metadata.put( "dcore.type[0]", typeTextBox.getText().trim() );
        }

        if ( !lastModifiedByTextBox.getText().trim().isEmpty() ) {
            metadata.put( "lastModifiedBy", lastModifiedByTextBox.getText().trim() );
        }

        if ( !externalLinkTextBox.getText().trim().isEmpty() ) {
            metadata.put( "dcore.relation[0]", externalLinkTextBox.getText().trim() );
        }

        if ( !checkinCommentTextBox.getText().trim().isEmpty() ) {
            metadata.put( "checkinComment", checkinCommentTextBox.getText().trim() );
        }

        boolean hasSomeDateValue = false;

        if ( createdAfter.getValue() != null ) {
            hasSomeDateValue = true;
        }

        if ( createdBefore.getValue() != null ) {
            hasSomeDateValue = true;
        }

        if ( lastModifiedAfter.getValue() != null ) {
            hasSomeDateValue = true;
        }

        if ( lastModifiedBefore.getValue() != null ) {
            hasSomeDateValue = true;
        }

        if ( metadata.size() == 0 && !hasSomeDateValue ) {
            formGroup.setValidationState( ValidationState.ERROR );
            Alert alert = new Alert( Constants.INSTANCE.AtLeastOneFieldMustBeSet(), AlertType.DANGER );
            alert.setVisible( true );
            alert.setDismissable( true );
            errorPanel.add( alert );
            return;
        }

        final SearchResultTable queryTable = new SearchResultTable( new QueryMetadataPageRequest( metadata,
                createdAfter.getValue(), createdBefore.getValue(),
                lastModifiedAfter.getValue(), lastModifiedBefore.getValue(),
                0, null ) );
        simplePanel.clear();

        simplePanel.add( queryTable );

        formAccordionCollapse.setIn( false );
        resultAccordionCollapse.setIn( true );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.FindTitle();
    }

}
