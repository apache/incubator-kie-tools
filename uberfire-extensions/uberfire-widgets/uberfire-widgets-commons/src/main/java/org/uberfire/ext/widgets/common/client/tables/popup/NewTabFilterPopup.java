/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.common.client.tables.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
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
    Form filterForm;

    @UiField
    FlowPanel basicTabPanel;

    @UiField
    HelpBlock errorMessages;

    @UiField
    FormGroup errorMessagesGroup;

    @UiField
    TabListItem tabBasic;

    @UiField
    TabListItem tabFilter;

    @UiField
    TabPane tab1;

    @UiField
    TabPane tab2;

    @Inject
    private Event<NotificationEvent> notification;

    HashMap formValues = new HashMap();

    private final List<FormGroup> filterControlGroups = new ArrayList<FormGroup>();

    private CommonImages images = GWT.create( CommonImages.class );

    private MultiGridPreferencesStore multiGridPreferenceStore;
    Command addfilterCommand;

    protected AsyncDataProvider<DataGridFilterSummary> dataProvider;

    PagedTable<DataGridFilterSummary> existingFiltersGrid = new PagedTable<DataGridFilterSummary>(5);


    private static Binder uiBinder = GWT.create( Binder.class );

    public NewTabFilterPopup() {
        setTitle( CommonConstants.INSTANCE.Filter_Management() );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( NewTabFilterPopup.this ) );
        }} );

        tabBasic.setDataTargetWidget( tab1 );
        tabFilter.setDataTargetWidget( tab2 );


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

        FormGroup controlGroup = new FormGroup();

        FormLabel controlLabel = new FormLabel();
        controlLabel.setTitle( CommonConstants.INSTANCE.Filter_Name() );
        HTML lab = new HTML( "<span style=\"color:red\"> * </span>" + "<span style=\"margin-right:10px\">" + CommonConstants.INSTANCE.Filter_Name() + "</span>" );
        controlLabel.setHTML( lab.getHTML() );

        TextBox fieldTextBox = new TextBox();
        fieldTextBox.setName( FILTER_TAB_NAME_PARAM );

        controlGroup.add( controlLabel );
        controlGroup.add( fieldTextBox );

        filterControlGroups.add( controlGroup );
        basicTabPanel.add( controlGroup );


        controlGroup = new FormGroup();

        controlLabel = new FormLabel();
        controlLabel.setTitle( "Filter description" );
        lab = new HTML( "<span style=\"color:red\"> * </span>" + "<span style=\"margin-right:10px\">" + "Filter description" + "</span>" );
        controlLabel.setHTML( lab.getHTML() );

        fieldTextBox = new TextBox();
        fieldTextBox.setName( FILTER_TAB_DESC_PARAM );

        controlGroup.add( controlLabel );
        controlGroup.add( fieldTextBox );

        filterControlGroups.add( controlGroup );

        basicTabPanel.add( controlGroup );

    }

    public void cleanFormValues( List<FormGroup> controlGroups ) {
        formValues = new HashMap();
        clearErrorMessages();
        for ( FormGroup groupControl : controlGroups ) {
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
            errorMessagesGroup.setValidationState( ValidationState.ERROR );
            valid = false;
        } else {
            errorMessages.setText( "" );
            errorMessagesGroup.setValidationState( ValidationState.NONE );
        }
        return valid;
    }

    public void getFormValues( List<FormGroup> controlGroups ) {
        formValues = new HashMap();

        for ( FormGroup groupControl : controlGroups ) {
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
        FormGroup controlGroup = new FormGroup();

        FormLabel controlLabel = new FormLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">" + label + "</span>" );
        controlLabel.setHTML( lab.getHTML() );

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
        FormGroup controlGroup = new FormGroup();

        FormLabel controlLabel = new FormLabel();
        controlLabel.setTitle( label );
        HTML lab = new HTML( "<span style=\"margin-right:10px\">" + label + "</span>" );
        controlLabel.setHTML( lab.getHTML() );

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
