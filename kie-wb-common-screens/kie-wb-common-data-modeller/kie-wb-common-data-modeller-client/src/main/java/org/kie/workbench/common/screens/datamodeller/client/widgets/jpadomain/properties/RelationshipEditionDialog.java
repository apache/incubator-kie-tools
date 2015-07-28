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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import static org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler.*;
import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

public class RelationshipEditionDialog
        extends BaseModal implements PropertyEditionPopup {

    @UiField
    Select relationType;

    @UiField
    Select fetchMode;

    @UiField
    CheckBox optional;

    @UiField
    FormLabel optionalLabel;

    @UiField
    FormLabel mappedByLabel;

    @UiField
    TextBox mappedBy;

    @UiField
    FormLabel orphanRemovalLabel;

    @UiField
    CheckBox orphanRemoval;

    @UiField
    CheckBox cascadeAll;

    @UiField
    CheckBox cascadePersist;

    @UiField
    CheckBox cascadeMerge;

    @UiField
    CheckBox cascadeRemove;

    @UiField
    CheckBox cascadeRefresh;

    @UiField
    CheckBox cascadeDetach;

    private Boolean revertChanges = Boolean.TRUE;

    PropertyEditorFieldInfo property;

    Command okCommand;

    private boolean cascadeAllWasClicked = false;

    interface Binder
            extends
            UiBinder<Widget, RelationshipEditionDialog> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public RelationshipEditionDialog() {
        setTitle( "Relationship configuration" );

        setBody( uiBinder.createAndBindUi( RelationshipEditionDialog.this ) );

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

        relationType.add( newOption( "Not set", DataModelerUtils.NOT_SELECTED ) );
        relationType.add( newOption( "One to One", RelationType.ONE_TO_ONE.name() ) );
        relationType.add( newOption( "One to Many", RelationType.ONE_TO_MANY.name() ) );
        relationType.add( newOption( "Many to One", RelationType.MANY_TO_ONE.name() ) );
        relationType.add( newOption( "Many to Many", RelationType.MANY_TO_MANY.name() ) );

        relationType.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                relationTypeChanged();
            }
        } );

        fetchMode.add( newOption( "EAGER", FetchMode.EAGER.name() ) );
        fetchMode.add( newOption( "LAZY", FetchMode.LAZY.name() ) );

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
        orphanRemovalLabel.setVisible( value );
        orphanRemoval.setVisible( value );
    }

    private void enableMappedBy( boolean value ) {
        mappedByLabel.setVisible( value );
        mappedBy.setVisible( value );
    }

    private void addHiddlenHandler() {
        addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( ModalHiddenEvent hiddenEvent ) {
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
            setSelectedValue( relationType, relationTypeValue.name() );
        } else {
            setSelectedValue( relationType, DataModelerUtils.NOT_SELECTED );
        }

        enableRelationDependentFields( relationTypeValue );

        cascadeAllWasClicked = false;
        setCascadeTypes( (List<CascadeType>) fieldInfo.getCurrentValue( CASCADE ) );
        enableCascadeTypes( true, true );

        FetchMode fetchModeValue = (FetchMode) fieldInfo.getCurrentValue( FETCH );

        if ( fetchModeValue != null ) {
            setSelectedValue( fetchMode, fetchModeValue.name() );
        } else {
            setSelectedValue( fetchMode, DataModelerUtils.NOT_SELECTED );
        }

        String mappedBy = (String) fieldInfo.getCurrentValue( MAPPED_BY );
        this.mappedBy.setText( mappedBy );

        Boolean orphanRemovalValue = (Boolean) fieldInfo.getCurrentValue( ORPHAN_REMOVAL );
        if ( orphanRemovalValue != null ) {
            orphanRemoval.setValue( orphanRemovalValue );
        }

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

        if ( !relationTypeValueStr.equals( DataModelerUtils.NOT_SELECTED ) ) {
            fieldInfo.setCurrentValue( RELATION_TYPE, RelationType.valueOf( relationType.getValue() ) );
            fieldInfo.setCurrentValue( CASCADE, getCascadeTypes() );
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
        if ( value == null || "".equals( value ) ) {
            value = "NOT_SET";
        }
        return value;
    }

    @Override
    public void setStringValue( String value ) {
        //do nothing
    }

    private void setCascadeTypes( List<CascadeType> cascadeTypes ) {
        cascadeAll.setValue( cascadeTypes != null && cascadeTypes.contains( CascadeType.ALL ) );
        cascadePersist.setValue( cascadeTypes != null && cascadeTypes.contains( CascadeType.PERSIST ) );
        cascadeMerge.setValue( cascadeTypes != null && cascadeTypes.contains( CascadeType.MERGE ) );
        cascadeRemove.setValue( cascadeTypes != null && cascadeTypes.contains( CascadeType.REMOVE ) );
        cascadeRefresh.setValue( cascadeTypes != null && cascadeTypes.contains( CascadeType.REFRESH ) );
        cascadeDetach.setValue( cascadeTypes != null && cascadeTypes.contains( CascadeType.DETACH ) );
    }

    private List<CascadeType> getCascadeTypes() {
        List<CascadeType> cascadeTypes = new ArrayList<CascadeType>();
        if ( cascadeAll.getValue() ) {
            cascadeTypes.add( CascadeType.ALL );
            if ( cascadeAllWasClicked ) {
                //when cascade ALL was selected in the UI by intention, then it's the only option that we will
                //configure since it include the other available ones.
                return cascadeTypes;
            }
        }
        if ( cascadePersist.getValue() ) {
            cascadeTypes.add( CascadeType.PERSIST );
        }
        if ( cascadeMerge.getValue() ) {
            cascadeTypes.add( CascadeType.MERGE );
        }
        if ( cascadeRemove.getValue() ) {
            cascadeTypes.add( CascadeType.REMOVE );
        }
        if ( cascadeRefresh.getValue() ) {
            cascadeTypes.add( CascadeType.REFRESH );
        }
        if ( cascadeDetach.getValue() ) {
            cascadeTypes.add( CascadeType.DETACH );
        }
        return cascadeTypes;
    }

    @UiHandler("cascadeAll")
    void onCascadeAllChanged( ClickEvent clickEvent ) {
        if ( cascadeAll.getValue() ) {
            enableCascadeTypes( true, false );
            cascadePersist.setValue( true );
            cascadeMerge.setValue( true );
            cascadeRemove.setValue( true );
            cascadeRefresh.setValue( true );
            cascadeDetach.setValue( true );
        } else {
            enableCascadeTypes( true, true );
            if ( cascadeAllWasClicked ) {
                //if cascade is clicked for second time then we can enable the auto disabling mode
                cascadePersist.setValue( false );
                cascadeMerge.setValue( false );
                cascadeRemove.setValue( false );
                cascadeRefresh.setValue( false );
                cascadeDetach.setValue( false );
            }
        }
        cascadeAllWasClicked = true;
    }

    private void enableCascadeTypes( boolean enableCascadeAll,
                                     boolean enableTheRest ) {
        cascadeAll.setEnabled( enableCascadeAll );
        cascadePersist.setEnabled( enableTheRest );
        cascadeMerge.setEnabled( enableTheRest );
        cascadeRemove.setEnabled( enableTheRest );
        cascadeRefresh.setEnabled( enableTheRest );
        cascadeDetach.setEnabled( enableTheRest );
    }

}