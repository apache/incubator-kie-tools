/*
 * Copyright 2014 JBoss Inc
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

package org.uberfire.ext.widgets.common.client.tables.popup;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.resources.CommonImages;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class NewTabFilterPopup extends BaseModal {
    interface Binder
            extends
            UiBinder<Widget, NewTabFilterPopup> {

    }

    public static String FILTER_TAB_NAME_PARAM = "filterTabName";
    public static String FILTER_TAB_DESC_PARAM = "filterTabDesc";

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Form filterForm;

    @UiField
    public FlowPanel basicTabPanel;


    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;

    @Inject
    private Event<NotificationEvent> notification;

    HashMap formValues = new HashMap();

    private final List<ControlGroup> filterControlGroups = new ArrayList<ControlGroup>();

    private CommonImages images = GWT.create( CommonImages.class );

    private MultiGridPreferencesStore multiGridPreferenceStore;
    Command addfilterCommand;

    protected AsyncDataProvider<DataGridFilterSummary> dataProvider;

    PagedTable<DataGridFilterSummary> existingFiltersGrid = new PagedTable<DataGridFilterSummary>(5);


    private static Binder uiBinder = GWT.create( Binder.class );

    public NewTabFilterPopup() {
        setTitle( CommonConstants.INSTANCE.Filter_Management() );

        add( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.Add_New_Filter(),
                new Command() {
                    @Override
                    public void execute() {
                        okButton();
                    }
                }, null,
                ButtonType.PRIMARY );

        add( footer );

    }

    public void show(Command addfilterCommand,
                     MultiGridPreferencesStore multiGridPreferencesStore) {
        cleanFormValues(filterControlGroups);
        this.addfilterCommand=addfilterCommand;
        this.multiGridPreferenceStore = multiGridPreferencesStore;
        super.show();
    }


    private void okButton() {
        getFormValues( filterControlGroups );
        if ( validateForm() ) {
            addfilterCommand.execute();
            cleanFormValues(filterControlGroups);
        }
        closePopup();

    }

    public void init() {
        basicTabPanel.clear();
        filterControlGroups.clear();
        filterForm.clear();

        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( CommonConstants.INSTANCE.Filter_Name() );
        HTML lab = new HTML( "<span style=\"color:red\"> * </span>" + "<span style=\"margin-right:10px\">" + CommonConstants.INSTANCE.Filter_Name() + "</span>" );
        controlLabel.add( lab );

        TextBox fieldTextBox = new TextBox();
        fieldTextBox.setName( FILTER_TAB_NAME_PARAM );

        controlGroup.add( controlLabel );
        controlGroup.add( fieldTextBox );

        filterControlGroups.add( controlGroup );
        basicTabPanel.add( controlGroup );


        controlGroup = new ControlGroup();

        controlLabel = new ControlLabel();
        controlLabel.setTitle( "Filter description" );
        lab = new HTML( "<span style=\"color:red\"> * </span>" + "<span style=\"margin-right:10px\">" + "Filter description" + "</span>" );
        controlLabel.add( lab );

        fieldTextBox = new TextBox();
        fieldTextBox.setName( FILTER_TAB_DESC_PARAM );

        controlGroup.add( controlLabel );
        controlGroup.add( fieldTextBox );

        filterControlGroups.add( controlGroup );

        basicTabPanel.add( controlGroup );

    }

    public void cleanFormValues( List<ControlGroup> controlGroups ) {
        formValues = new HashMap();
        clearErrorMessages();
        for ( ControlGroup groupControl : controlGroups ) {
            if ( groupControl.getWidget( 1 ) instanceof TextBox ) {
                (( TextBox ) groupControl.getWidget( 1 ) ).setText( "" );
            } else if ( groupControl.getWidget( 1 ) instanceof ListBox ) {
                ListBox listBox = ( ListBox ) groupControl.getWidget( 1 );
                listBox.setSelectedIndex( -1 );


            }
        }
    }


    public void closePopup() {
        cleanFormValues( filterControlGroups );
        hide();
        super.hide();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();
        String filterName = ( String ) formValues.get( FILTER_TAB_NAME_PARAM );
        if ( filterName == null || filterName.trim().length() == 0  ) {
            errorMessages.setText( CommonConstants.INSTANCE.Filter_Must_Have_A_Name() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
            valid = false;
        } else {
            errorMessages.setText( "" );
            errorMessagesGroup.setType( ControlGroupType.NONE );
        }
        return valid;
    }

    public void getFormValues( List<ControlGroup> controlGroups ) {
        formValues = new HashMap();

        for ( ControlGroup groupControl : controlGroups ) {
            if ( groupControl.getWidget( 1 ) instanceof TextBox ) {
                formValues.put( ( ( TextBox ) groupControl.getWidget( 1 ) ).getName(),
                        ( ( TextBox ) groupControl.getWidget( 1 ) ).getValue() );
            } else if ( groupControl.getWidget( 1 ) instanceof ListBox ) {
                ListBox listBox = ( ListBox ) groupControl.getWidget( 1 );

                List<String> selectedValues = new ArrayList<String>();
                for ( int i = 0; i < listBox.getItemCount(); i++ ) {
                    if ( listBox.isItemSelected( i ) ) {
                        selectedValues.add( listBox.getValue( i ) );
                    }
                }

                formValues.put( listBox.getName(), selectedValues );
            }
        }
    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );
    }

    public HashMap getFormValues() {
        return formValues;
    }

    public void addListBoxToFilter( String label, String fieldName, boolean multiselect, HashMap<String, String> listBoxInfo ) {
        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">" + label + "</span>" );
        controlLabel.add( lab );

        ListBox listBox = new ListBox( multiselect );
        if ( listBoxInfo != null ) {
            Set listBoxKeys = listBoxInfo.keySet();
            Iterator it = listBoxKeys.iterator();
            String key;
            while ( it.hasNext() ) {
                key = ( String ) it.next();
                listBox.addItem( listBoxInfo.get( key ), key );
            }
        }
        listBox.setName( fieldName );

        controlGroup.add( controlLabel );
        controlGroup.add( listBox );

        filterControlGroups.add( controlGroup );
        filterForm.add( controlGroup );
    }

    public void addTextBoxToFilter( String label, String fieldName ) {
        addTextBoxToFilter( label, fieldName, "" );
    }

    public void addTextBoxToFilter( String label, String fieldName, String defaultValue ) {
        ControlGroup controlGroup = new ControlGroup();

        ControlLabel controlLabel = new ControlLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">" + label + "</span>" );
        controlLabel.add( lab );

        TextBox textBox = new TextBox();
        textBox.setName( fieldName );
        if ( defaultValue != null && defaultValue.trim().length() > 0 ) {
            textBox.setText( defaultValue );
        }

        controlGroup.add( controlLabel );
        controlGroup.add( textBox );

        filterControlGroups.add( controlGroup );
        filterForm.add( controlGroup );
    }


}
