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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ElementType;

@Dependent
public class AdvancedDataObjectEditor
        extends ObjectEditor
        implements AdvancedDataObjectEditorView.Presenter {

    private AdvancedDataObjectEditorView view;

    @Inject
    public AdvancedDataObjectEditor( DomainHandlerRegistry handlerRegistry,
            Event<DataModelerEvent> dataModelerEvent,
            DataModelCommandBuilder commandBuilder,
            AdvancedDataObjectEditorView view ) {
        super( handlerRegistry, dataModelerEvent, commandBuilder );
        this.view = view;
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getName() {
        return "ADVANCED_OBJECT_EDITOR";
    }

    @Override
    public String getDomainName() {
        return AdvancedDomainEditor.ADVANCED_DOMAIN;
    }

    protected void loadDataObject( DataObject dataObject ) {
        clear();
        setReadonly( context != null && context.isReadonly() );
        view.setReadonly( isReadonly() );
        this.dataObject = dataObject;
        if ( dataObject != null ) {
            view.loadAnnotations( dataObject.getAnnotations() );
        }
    }

    @Override
    public void onDeleteAnnotation( Annotation annotation ) {
        commandBuilder.buildDataObjectRemoveAnnotationCommand( getContext(),
                getName(),
                getDataObject(),
                annotation.getClassName() ).execute();
        view.removeAnnotation( annotation );
    }

    @Override
    public void onValuePairChange( String annotationClassName, String valuePair, Object newValue ) {
        commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                getName(),
                getDataObject(),
                annotationClassName,
                valuePair,
                newValue,
                false ).execute();
        //TODO provide a way for refreshing only the changed annotation
        refresh();
    }

    @Override
    public void onClearValuePair( Annotation annotation, String valuePair ) {
        commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                getName(),
                getDataObject(),
                annotation.getClassName(),
                valuePair,
                null,
                false ).execute();
        //TODO provide a way for refreshing only the changed annotation
        refresh();
    }

    @Override
    public void onAddAnnotation( Annotation annotation ) {
        commandBuilder.buildDataObjectAddAnnotationCommand( getContext(),
                getName(),
                getDataObject(),
                annotation ).execute();
        //TODO provide a way for refreshing only the changed annotation
        refresh();
    }

    public void clear() {
        view.clear();
    }

    @Override
    public void onContextChange( DataModelerContext context ) {
        view.init( context != null ? context.getCurrentProject() : null, ElementType.TYPE );
        view.setReadonly( context != null && context.isReadonly() );
        super.onContextChange( context );
    }

    private void refresh() {
        loadDataObject( dataObject );
    }
}