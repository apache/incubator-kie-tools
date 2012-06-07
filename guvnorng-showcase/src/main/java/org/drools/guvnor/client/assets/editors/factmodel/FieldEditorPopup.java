/*
 * Copyright 2010 JBoss Inc
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
package org.drools.guvnor.client.assets.editors.factmodel;

import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FieldMetaModel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class FieldEditorPopup {

    // A valid Fact, Field or Annotation name
    private static final RegExp        VALID_NAME     = RegExp.compile( "^[a-zA-Z][a-zA-Z\\d_$]*$" );

    // A valid (fully qualified) data-type name
    private static final RegExp        VALID_DATATYPE = RegExp.compile( "^([a-zA-Z][a-zA-Z\\d_$]*\\.)*[a-zA-Z][a-zA-Z\\d_$]*$" );

    private final FieldMetaModel       field;
    private final List<FieldMetaModel> fields;

    private final ModelNameHelper      modelNameHelper;

    private Command                    okCommand;

    public FieldEditorPopup(List<FieldMetaModel> fields,
                            ModelNameHelper modelNameHelper) {
        this( new FieldMetaModel(),
              fields,
              modelNameHelper );
    }

    public FieldEditorPopup(FieldMetaModel field,
                            List<FieldMetaModel> fields,
                            ModelNameHelper modelNameHelper) {
        this.field = field;
        this.fields = fields;
        this.modelNameHelper = modelNameHelper;
    }

    public FieldMetaModel getField() {
        return field;
    }

    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    public void show() {
        final FormStylePopup pop = new FormStylePopup();
        final TextBox fieldName = new TextBox();
        final TextBox fieldType = new TextBox();
        fieldName.addKeyPressHandler( new NoSpaceKeyPressHandler() );
        fieldType.addKeyPressHandler( new NoSpaceKeyPressHandler() );
        if ( field != null ) {
            fieldName.setText( field.name );
            fieldType.setText( field.type );
        }
        HorizontalPanel typeP = new HorizontalPanel();
        typeP.add( fieldType );
        final ListBox typeChoice = new ListBox();
        typeChoice.addItem( Constants.INSTANCE.chooseType() );

        for ( Map.Entry<String, String> entry : modelNameHelper.getTypeDescriptions().entrySet() ) {
            typeChoice.addItem( entry.getValue(),
                                entry.getKey() );
        }

        typeChoice.setSelectedIndex( 0 );
        typeChoice.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                fieldType.setText( typeChoice.getValue( typeChoice.getSelectedIndex() ) );
            }
        } );

        typeP.add( typeChoice );

        pop.addAttribute( Constants.INSTANCE.FieldNameAttribute(),
                          fieldName );
        pop.addAttribute( Constants.INSTANCE.Type(),
                          typeP );

        Button ok = new Button( Constants.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {

                String dataType = fieldType.getText();
                if ( !isDataTypeValid( dataType ) ) {
                    Window.alert( Constants.INSTANCE.InvalidDataTypeName( dataType ) );
                    return;
                }

                String name = fieldName.getText();
                if ( !isNameValid( name ) ) {
                    Window.alert( Constants.INSTANCE.InvalidModelName( name ) );
                    return;
                }
                if ( doesTheNameExist( name ) ) {
                    Window.alert( Constants.INSTANCE.NameTakenForModel( name ) );
                    return;
                }
                if ( factModelAlreadyHasAName( name ) ) {
                    if ( isTheUserSureHeWantsToChangeTheName() ) {
                        setNameAndClose();
                    }
                } else {
                    setNameAndClose();
                }
            }

            private boolean isDataTypeValid(String dataType) {
                if ( dataType == null || "".equals( dataType ) ) {
                    return false;
                }
                return VALID_DATATYPE.test( dataType );
            }

            private boolean isNameValid(String name) {
                if ( name == null || "".equals( name ) ) {
                    return false;
                }
                return VALID_NAME.test( name );
            }

            private boolean factModelAlreadyHasAName(String name) {
                return field.name != null && !field.name.equals( name );
            }

            private void setNameAndClose() {
                field.name = fieldName.getText();
                field.type = fieldType.getText();

                okCommand.execute();

                pop.hide();
            }

            private boolean isTheUserSureHeWantsToChangeTheName() {
                return Window.confirm( Constants.INSTANCE.ModelNameChangeWarning() );
            }

            private boolean doesTheNameExist(String name) {
                //The name may not have changed
                if ( field.name != null && field.name.equals( name ) ) {
                    return false;
                }
                //Check for field name is unique amongst other fields on the fact
                for ( FieldMetaModel f : fields ) {
                    if ( f.name.equals( name ) ) {
                        return true;
                    }
                }
                return false;
            }

        } );
        pop.addAttribute( "",
                          ok );

        pop.show();
    }
}
