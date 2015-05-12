/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.CascadeType;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.FetchMode;
import org.kie.workbench.common.screens.datamodeller.client.util.RelationType;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import static org.kie.workbench.common.screens.datamodeller.client.util.RelationshipAnnotationValueHandler.*;


public class RelationshipEditionDialog
        extends BaseModal implements PropertyEditionPopup {

    @UiField
    ListBox relationType;

    @UiField
    ListBox cascadeType;

    @UiField
    ListBox fetchMode;

    @UiField
    CheckBox optional;

    @UiField
    Label optionalLabel;

    @UiField
    Label mappedByLabel;

    @UiField
    TextBox mappedBy;

    @UiField
    Label orphanRemovalLabel;

    @UiField
    CheckBox orphanRemoval;

    @UiField
    ControlGroup relationControlGroup;

    @UiField
    HelpInline relationGroupInline;

    private Boolean revertChanges = Boolean.TRUE;

    PropertyEditorFieldInfo property;

    Command okCommand;

    interface Binder
            extends
            UiBinder<Widget, RelationshipEditionDialog> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public RelationshipEditionDialog() {
        setTitle( "Relationship configuration" );
        setMaxHeigth( "450px" );
        add( uiBinder.createAndBindUi( this ) );

        add( new ModalFooterOKCancelButtons(
                        new Command() {
                            @Override
                            public void execute() {
                                okButton();
                            }
                        },
                        new Command() {
                            @Override
                            public void execute() {
                                cancelButton();
                            }
                        }
                )
        );

        relationType.addItem( "Not set", DataModelerUtils.NOT_SELECTED );
        relationType.addItem( "One to One", RelationType.ONE_TO_ONE.name() );
        relationType.addItem( "One to Many", RelationType.ONE_TO_MANY.name() );
        relationType.addItem( "Many to One", RelationType.MANY_TO_ONE.name() );
        relationType.addItem( "Many to Many", RelationType.MANY_TO_MANY.name() );

        relationType.addChangeHandler( new ChangeHandler() {
            @Override public void onChange( ChangeEvent event ) {
                relationTypeChanged();
            }
        } );

        cascadeType.addItem( "ALL", CascadeType.ALL.name() );
        cascadeType.addItem( "PERSIST", CascadeType.PERSIST.name() );
        cascadeType.addItem( "MERGE", CascadeType.MERGE.name() );
        cascadeType.addItem( "REMOVE", CascadeType.REMOVE.name() );
        cascadeType.addItem( "REFRESH", CascadeType.REFRESH.name() );
        cascadeType.addItem( "DETACH", CascadeType.DETACH.name() );

        fetchMode.addItem( "EAGER", FetchMode.EAGER.name() );
        fetchMode.addItem( "LAZY", FetchMode.LAZY.name() );

    }

    private void relationTypeChanged() {
        String strValue = relationType.getValue();
        if ( DataModelerUtils.NOT_SELECTED.equals( strValue ) ) {
            //clean();
        } else {
            RelationType type = RelationType.valueOf( relationType.getValue() );
            enableRelationDependentFields( type );
        }
    }

    private void enableRelationDependentFields( RelationType relationType ) {
        if ( relationType != null ) {
            switch ( relationType ) {
                case ONE_TO_ONE:
                    enableOptional( true );
                    enableMappedBy( true );
                    enableOrphanRemoval( true );
                    break;
                case ONE_TO_MANY:
                    enableOptional( false );
                    enableMappedBy( true );
                    enableOrphanRemoval( true );
                    break;
                case MANY_TO_ONE:
                    enableOptional( true );
                    enableMappedBy( false );
                    enableOrphanRemoval( false );
                    break;
                case MANY_TO_MANY:
                    enableOptional( false );
                    enableMappedBy( true );
                    enableOrphanRemoval( false );
            }
        }
    }

    private void enableOptional( boolean value ) {
        optionalLabel.setVisible( value );
        optional.setVisible( value );
    }

    private void enableOrphanRemoval( boolean value ) {
        orphanRemoval.setVisible( value );
        orphanRemovalLabel.setVisible( value );
    }

    private void enableMappedBy( boolean value ) {
        mappedBy.setVisible( value );
        mappedByLabel.setVisible( value );
    }

    private void addHiddlenHandler() {
        addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent hiddenEvent ) {
                if ( userPressCloseOrCancel() ) {
                    revertChanges();
                }
            }
        } );
    }

    private void revertChanges() {

    }

    private boolean userPressCloseOrCancel() {
        return revertChanges;
    }

    public void show() {

        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;

        RelationType relationTypeValue = (RelationType) fieldInfo.getCurrentValue( RELATION_TYPE );

        if ( relationTypeValue != null ) {
            relationType.setSelectedValue( relationTypeValue.name( ) );
        } else {
            relationType.setSelectedValue( DataModelerUtils.NOT_SELECTED );
        }

        enableRelationDependentFields( relationTypeValue );

        CascadeType cascadeTypeValue = (CascadeType) fieldInfo.getCurrentValue( CASCADE );

        if ( cascadeTypeValue != null ) {
            cascadeType.setSelectedValue( cascadeTypeValue.name() );
        } else {
            cascadeType.setSelectedValue( DataModelerUtils.NOT_SELECTED );
        }

        FetchMode fetchModeValue = (FetchMode) fieldInfo.getCurrentValue( FETCH );

        if ( fetchModeValue != null ) {
            fetchMode.setSelectedValue( fetchModeValue.name() );
        } else {
            fetchMode.setSelectedValue( DataModelerUtils.NOT_SELECTED );
        }

        String mappedBy = (String) fieldInfo.getCurrentValue( MAPPED_BY );
        this.mappedBy.setText( mappedBy );

        Boolean orphanRemovalValue =  (Boolean) fieldInfo.getCurrentValue( ORPHAN_REMOVAL );
        if ( orphanRemovalValue != null) orphanRemoval.setValue( orphanRemovalValue );

        super.show();
    }

    public void setOkCommand( Command okCommand ) {
        this.okCommand = okCommand;
    }

    public void setProperty( PropertyEditorFieldInfo property ) {
        this.property = property;
    }

    void okButton() {

        //TODO add validation in order to establish if the ok operation can be performed. If validation is ok,
        // then new current values can be set.

        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;

        String relationTypeValueStr = relationType.getValue();

        fieldInfo.removeCurrentValue( RELATION_TYPE );
        fieldInfo.removeCurrentValue( CASCADE );
        fieldInfo.removeCurrentValue( FETCH );
        fieldInfo.removeCurrentValue( OPTIONAL );
        fieldInfo.removeCurrentValue( MAPPED_BY );
        fieldInfo.removeCurrentValue( ORPHAN_REMOVAL );

        if ( !relationTypeValueStr.equals( DataModelerUtils.NOT_SELECTED )) {
            fieldInfo.setCurrentValue( RELATION_TYPE, RelationType.valueOf( relationType.getValue() ) );
            fieldInfo.setCurrentValue( CASCADE, CascadeType.valueOf( cascadeType.getValue() ) );
            fieldInfo.setCurrentValue( FETCH, FetchMode.valueOf( fetchMode.getValue() ) );

            if ( relationType.getValue().equals( RelationType.ONE_TO_ONE.name() ) ||
                    relationType.getValue().equals( RelationType.MANY_TO_ONE.name() ) ) {
                fieldInfo.setCurrentValue( OPTIONAL, optional.getValue() );
            }

            if ( relationType.getValue().equals( RelationType.ONE_TO_ONE.name() ) ||
                    relationType.getValue().equals( RelationType.ONE_TO_MANY.name() ) ||
                    relationType.getValue().equals( RelationType.MANY_TO_MANY.name() ) ) {
                fieldInfo.setCurrentValue( MAPPED_BY, mappedBy.getText() );
            }

            if ( relationType.getValue().equals( RelationType.ONE_TO_ONE.name() ) ||
                    relationType.getValue().equals( RelationType.ONE_TO_MANY.name() ) ) {
                fieldInfo.setCurrentValue( ORPHAN_REMOVAL, orphanRemoval.getValue() );
            }
        } else {
            fieldInfo.setCurrentValue( RELATION_TYPE, null );
        }

        super.hide();
        revertChanges = Boolean.FALSE;
        if ( okCommand != null ) {
            okCommand.execute();
        }
    }

    void cancelButton() {
        super.hide();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public String getStringValue() {
        //return the value to show in the property editor simple text field.
        String value = relationType.getValue();
        if ( value == null || "".equals( value ) ) value = "NOT_SET";
        return value;
    }

    @Override
    public void setStringValue( String value ) {
        //do nothing
    }
}
