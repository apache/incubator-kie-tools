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
package org.drools.guvnor.client.editors.factmodel;

import java.util.List;

import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FieldMetaModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FactFieldEditor extends Composite {

    interface FactFieldsEditorBinder
        extends
        UiBinder<Widget, FactFieldEditor> {
    }

    private static FactFieldsEditorBinder uiBinder = GWT.create( FactFieldsEditorBinder.class );

    @UiField
    Label                                 fieldType;
    @UiField
    Label                                 fieldName;
    @UiField
    Image                                 editFieldIcon;
    @UiField
    Image                                 deleteFieldIcon;

    private FieldMetaModel                field;
    private List<FieldMetaModel>          fields;
    private final ModelNameHelper         modelNameHelper;

    private Command                       deleteCommand;

    public FactFieldEditor(final FieldMetaModel field,
                           final List<FieldMetaModel> fields,
                           final ModelNameHelper modelNameHelper) {

        this.field = field;
        this.fields = fields;
        this.modelNameHelper = modelNameHelper;

        initWidget( uiBinder.createAndBindUi( this ) );

        fieldName.setStyleName( "guvnor-bold-label" );

        setTypeText( field.type );
        fieldName.setText( field.name );

        editFieldIcon.setTitle( Constants.INSTANCE.Rename() );
        deleteFieldIcon.setTitle( Constants.INSTANCE.Delete() );
    }

    @UiHandler("editFieldIcon")
    void editFieldIconClick(ClickEvent event) {
        final FieldEditorPopup popup = new FieldEditorPopup( field,
                                                             fields,
                                                             modelNameHelper );
        popup.setOkCommand( new Command() {
            public void execute() {
                setTypeText( field.type );
                fieldName.setText( field.name );
            }
        } );

        popup.show();
    }

    @UiHandler("deleteFieldIcon")
    void deleteFieldIconClick(ClickEvent event) {
        deleteCommand.execute();
    }

    private void setTypeText(String typeName) {
        fieldType.setText( modelNameHelper.getUserFriendlyTypeName( typeName ) );
    }

    public void setDeleteCommand(Command deleteCommand) {
        this.deleteCommand = deleteCommand;
    }
}
