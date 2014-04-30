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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Typeahead;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.datepicker.client.ui.DateBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.search.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.search.client.widgets.SearchResultTable;
import org.kie.workbench.common.screens.search.model.QueryMetadataPageRequest;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

@Dependent
@WorkbenchScreen(identifier = "FindForm")
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
    ControlGroup form;

    @UiField
    TextBox sourceTextBox;

    @UiField
    TextBox createdByTextBox;

    @UiField
    TextBox descriptionByTextBox;

    @UiField
    Typeahead formatTypeahead;

    @UiField
    TextBox formatTextBox;

    @UiField
    RadioButton statusAny;
    @UiField
    RadioButton statusEnabled;
    @UiField
    RadioButton statusDisabled;

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

    @UiField(provided = true)
    DateBox createdAfter;
    @UiField(provided = true)
    DateBox createdBefore;

    @UiField(provided = true)
    DateBox lastModifiedAfter;
    @UiField(provided = true)
    DateBox lastModifiedBefore;

    @UiField
    AccordionGroup formAccordion;
    @UiField
    AccordionGroup resultAccordion;

    @UiField
    SimplePanel simplePanel;

    @PostConstruct
    public void init() {
        createdAfter = new DateBox();
        createdAfter.setValue( null );
        createdAfter.setAutoClose( true );

        createdBefore = new DateBox();
        createdBefore.setValue( null );
        createdBefore.setAutoClose( true );

        lastModifiedAfter = new DateBox();
        lastModifiedAfter.setValue( null );
        lastModifiedAfter.setAutoClose( true );

        lastModifiedBefore = new DateBox();
        lastModifiedBefore.setValue( null );
        lastModifiedBefore.setAutoClose( true );

        //TODO {porcelli} due a bug on bootstrap we can't use custom date formats
//        createdAfter.setFormat( ApplicationPreferences.getDroolsDateFormat() );
//        createdBefore.setFormat( ApplicationPreferences.getDroolsDateFormat() );
//        lastModifiedAfter.setFormat( ApplicationPreferences.getDroolsDateFormat() );
//        lastModifiedBefore.setFormat( ApplicationPreferences.getDroolsDateFormat() );

        initWidget( uiBinder.createAndBindUi( this ) );

        statusEnabled.setFormValue( "enabled" );
        statusDisabled.setFormValue( "disabled" );

        final MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) formatTypeahead.getSuggestOracle();

        for ( final ClientResourceType resourceType : clientTypeRegistry.getRegisteredTypes() ) {
            oracle.add( resourceType.getShortName() );
        }
    }

    @UiHandler("search")
    public void onSearchClick( final ClickEvent e ) {
        errorPanel.clear();
        form.setType( ControlGroupType.NONE );
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

        if ( !formatTextBox.getText().trim().isEmpty() ) {
            final String pattern = clientTypeRegistry.resolveWildcardPattern( formatTextBox.getText().trim() );
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

        if ( statusDisabled.getValue() ) {
            metadata.put( "othermeta.mode", statusDisabled.getFormValue() );
        }

        if ( statusEnabled.getValue() ) {
            metadata.put( "othermeta.mode", statusEnabled.getFormValue() );
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
            form.setType( ControlGroupType.ERROR );
            Alert alert = new Alert( Constants.INSTANCE.AtLeastOneFieldMustBeSet(), AlertType.ERROR );
            alert.setVisible( true );
            alert.setClose( true );
            errorPanel.add( alert );
            return;
        }

        final SearchResultTable queryTable = new SearchResultTable( new QueryMetadataPageRequest( metadata,
                                                                                                  createdAfter.getValue(), createdBefore.getValue(),
                                                                                                  lastModifiedAfter.getValue(), lastModifiedBefore.getValue(),
                                                                                                  0, null ) );
        simplePanel.clear();

        simplePanel.add( queryTable );

        formAccordion.hide();
        resultAccordion.show();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.FindTitle();
    }

}
