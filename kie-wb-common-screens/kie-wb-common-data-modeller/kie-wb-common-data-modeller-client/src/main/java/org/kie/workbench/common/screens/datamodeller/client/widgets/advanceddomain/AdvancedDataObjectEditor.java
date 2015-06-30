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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain;

import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ElementType;

public class AdvancedDataObjectEditor
        extends ObjectEditor
        implements AdvancedDataObjectEditorView.Presenter {

    private AdvancedDataObjectEditorView view;

    @Inject
    public AdvancedDataObjectEditor( AdvancedDataObjectEditorView view ) {
        this.view = view;
        view.setPresenter( this );
        initWidget( view.asWidget() );
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
        clean();
        setReadonly( true );
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
    public void onValuePairChanged( String annotationClassName, String valuePair, Object newValue ) {
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

    public void clean() {
        view.clear();
    }

    @Override
    public void setContext( DataModelerContext context ) {
        super.setContext( context );
        view.init( context.getCurrentProject(), ElementType.TYPE );
    }

    private void refresh() {
        loadDataObject( dataObject );
    }
}