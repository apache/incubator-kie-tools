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
import org.drools.guvnor.shared.common.vo.assets.factmodel.AnnotationMetaModel;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FieldMetaModel;
import org.uberfire.client.common.AddButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FactFieldsEditor extends Composite {

    interface FactFieldsEditorBinder
        extends
        UiBinder<Widget, FactFieldsEditor> {
    }

    private static FactFieldsEditorBinder   uiBinder = GWT.create( FactFieldsEditorBinder.class );

    @UiField
    VerticalPanel                           fieldsPanel;

    @UiField
    AddButton                               addFieldIcon;

    @UiField
    AddButton                               addAnnotationIcon;

    private final ModelNameHelper           modelNameHelper;

    private final List<FieldMetaModel>      fields;

    private final List<AnnotationMetaModel> annotations;

    public FactFieldsEditor(final List<FieldMetaModel> fields,
                            final List<AnnotationMetaModel> annotations,
                            final ModelNameHelper modelNameHelper) {

        this.fields = fields;
        this.annotations = annotations;
        this.modelNameHelper = modelNameHelper;

        initWidget( uiBinder.createAndBindUi( this ) );

        addAnnotationRows();
        addFieldRows();

        addFieldIcon.setTitle( Constants.INSTANCE.AddField() );
        addFieldIcon.setText( Constants.INSTANCE.AddField() );

        addAnnotationIcon.setTitle( Constants.INSTANCE.AddAnnotation() );
        addAnnotationIcon.setText( Constants.INSTANCE.AddAnnotation() );

    }

    @UiHandler("addFieldIcon")
    void addNewFieldClick(ClickEvent event) {
        final FieldEditorPopup popup = new FieldEditorPopup( fields,
                                                             modelNameHelper );

        popup.setOkCommand( new Command() {

            public void execute() {
                createNewField( popup );
            }

            private void createNewField(final FieldEditorPopup popup) {
                FieldMetaModel field = popup.getField();
                fields.add( field );
                addFieldRow( field );
            }
        } );

        popup.show();

    }

    @UiHandler("addAnnotationIcon")
    void addNewAnnotationClick(ClickEvent event) {
        final AnnotationEditorPopup popup = new AnnotationEditorPopup( annotations );

        popup.setOkCommand( new Command() {

            public void execute() {
                createNewAnnotation( popup );
            }

            private void createNewAnnotation(final AnnotationEditorPopup popup) {
                AnnotationMetaModel annotation = popup.getAnnotation();
                annotations.add( annotation );
                addAnnotationRow( annotation );
            }
        } );

        popup.show();

    }

    private void addFieldRows() {
        for ( FieldMetaModel fieldMetaModel : fields ) {
            addFieldRow( fieldMetaModel );
        }
    }

    private void addAnnotationRows() {
        for ( AnnotationMetaModel annotation : annotations ) {
            addAnnotationRow( annotation );
        }
    }

    private void addFieldRow(final FieldMetaModel field) {
        final FactFieldEditor editor = new FactFieldEditor( field,
                                                            fields,
                                                            modelNameHelper );

        editor.setDeleteCommand( new Command() {
            public void execute() {
                if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveTheField0( field.name ) ) ) {
                    fieldsPanel.remove( editor );
                    fields.remove( field );
                }
            }
        } );

        fieldsPanel.add( editor );
    }

    private void addAnnotationRow(final AnnotationMetaModel annotation) {
        final AnnotationEditor editor = new AnnotationEditor( annotation,
                                                              annotations );

        editor.setDeleteCommand( new Command() {
            public void execute() {
                if ( Window.confirm( Constants.INSTANCE.AreYouSureYouWantToRemoveTheAnnotation0( annotation.name ) ) ) {
                    fieldsPanel.remove( editor );
                    annotations.remove( annotation );
                }
            }
        } );

        fieldsPanel.insert( editor,
                            findPositionOfLastAnnotation() );
    }

    //Insert Annotations above field definitions
    private int findPositionOfLastAnnotation() {
        int position = 0;
        for ( int i = 0; i < fieldsPanel.getWidgetCount(); i++ ) {
            Widget w = fieldsPanel.getWidget( i );
            if ( w instanceof FactFieldEditor ) {
                break;
            }
            position = i + 1;
        }
        return position;
    }

}
