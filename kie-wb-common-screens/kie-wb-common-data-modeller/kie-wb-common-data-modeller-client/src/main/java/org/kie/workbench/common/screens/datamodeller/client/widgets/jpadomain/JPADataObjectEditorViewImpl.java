/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@Dependent
public class JPADataObjectEditorViewImpl
        extends Composite
        implements JPADataObjectEditorView {

    interface JPADataObjectEditorUIBinder
            extends UiBinder<Widget, JPADataObjectEditorViewImpl> {

    }

    private static JPADataObjectEditorUIBinder uiBinder = GWT.create( JPADataObjectEditorUIBinder.class );

    private static final String JPA_DATA_OBJECT_EDITOR_EVENT = "JPA_DATA_OBJECT_EDITOR_EVENT";

    @UiField
    PropertyEditorWidget propertyEditor;

    private Presenter presenter;

    public JPADataObjectEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    protected void init() {
        propertyEditor.setFilterGroupVisible( false );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setLastOpenAccordionGroupTitle( String accordionGroupTitle ) {
        propertyEditor.setLastOpenAccordionGroupTitle( accordionGroupTitle );
    }

    public void loadPropertyEditorCategories( List<PropertyEditorCategory> categories ) {
        propertyEditor.handle( new PropertyEditorEvent( getCurrentEditorEventId(), categories ) );
    }

    private void onPropertyEditorChange( @Observes PropertyEditorChangeEvent event ) {
        PropertyEditorFieldInfo property = event.getProperty();

        if ( isFromCurrentEditor( property.getEventId() ) ) {

            DataModelerPropertyEditorFieldInfo fieldInfo = ( DataModelerPropertyEditorFieldInfo ) event.getProperty();

            if ( ENTITY_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onEntityFieldChange( event.getNewValue() );
            } else if ( TABLE_NAME_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onTableNameChange( event.getNewValue() );
            }
        }
    }

    private String getCurrentEditorEventId() {
        return JPA_DATA_OBJECT_EDITOR_EVENT + "-" + this.hashCode();
    }

    private boolean isFromCurrentEditor( String propertyEditorEventId ) {
        return getCurrentEditorEventId().equals( propertyEditorEventId );
    }


    @Override
    public void showYesNoCancelPopup( String title,
            String message,
            Command yesCommand,
            String yesButtonText,
            ButtonType yesButtonType,
            Command noCommand,
            String noButtonText,
            ButtonType noButtonType,
            Command cancelCommand,
            String cancelButtonText,
            ButtonType cancelButtonType ) {

        YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup( title,
                message,
                yesCommand,
                yesButtonText,
                yesButtonType,
                noCommand,
                noButtonText,
                noButtonType,
                cancelCommand,
                cancelButtonText,
                cancelButtonType );
        yesNoCancelPopup.setClosable( false );
        yesNoCancelPopup.show();
    }

}