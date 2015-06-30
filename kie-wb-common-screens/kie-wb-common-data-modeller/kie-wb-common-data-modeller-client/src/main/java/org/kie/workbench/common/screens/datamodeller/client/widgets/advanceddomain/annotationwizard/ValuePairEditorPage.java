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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorView;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.shared.project.KieProject;

@Dependent
public class ValuePairEditorPage
        extends CreateAnnotationWizardPage
        implements ValuePairEditorPageView.Presenter {


    @Inject
    private ValuePairEditorPageView view;

    private ValuePairEditorView.ValuePairEditorHandler editorHandler;

    private AnnotationValuePairDefinition valuePairDefinition;

    private Object currentValue = null;

    public ValuePairEditorPage() {
        setTitle( "Configure value pair" );
    }

    @PostConstruct
    private void init( ) {
        view.setPresenter( this );
        content.add( view );
    }

    public void init( AnnotationDefinition annotationDefinition,
            AnnotationValuePairDefinition valuePairDefinition, ElementType target, KieProject project ) {

        this.annotationDefinition = annotationDefinition;
        setValuePairDefinition( valuePairDefinition );
        this.target = target;
        this.project = project;

        setStatus( isRequired() ? PageStatus.NOT_VALIDATED : PageStatus.VALIDATED );
    }

    public String getValue() {
        return view.getValue();
    }

    public AnnotationValuePairDefinition getValuePairDefinition() {
        return valuePairDefinition;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void addEditorHandler( ValuePairEditorView.ValuePairEditorHandler editorHandler ) {
        this.editorHandler = editorHandler;
    }

    @Override
    public void onValidate() {
        if ( editorHandler != null ) {
            editorHandler.onValidate();
        } else {
            doOnValidate();
        }
    }

    @Override
    public void onValueChanged() {
        setStatus( PageStatus.NOT_VALIDATED );
        if ( editorHandler != null ) {
            editorHandler.onValueChanged( view.getValue() );
        } else {
            doOnValueChanged();
        }
    }

    private void setValuePairDefinition( AnnotationValuePairDefinition valuePairDefinition ) {
        this.valuePairDefinition = valuePairDefinition;

        String required =  isRequired() ? "* " : "";
        setTitle( "  -> " + required + valuePairDefinition.getName() );
        setHelpMessage( "Enter the value for the annotation value pair and press the validate button" );
        view.setNameLabel( required + valuePairDefinition.getName() + ":" );
    }

    private void doOnValidate() {

        modelerService.call( getOnValidateValidateSuccessCallback(), new CreateAnnotationWizard.CreateAnnotationWizardErrorCallback() )
                .resolveParseRequest( new AnnotationParseRequest( annotationDefinition.getClassName(), target,
                        valuePairDefinition.getName(), getValue() ), project );

    }

    private RemoteCallback<AnnotationParseResponse> getOnValidateValidateSuccessCallback( ) {
        return new RemoteCallback<AnnotationParseResponse>() {

            @Override
            public void callback( AnnotationParseResponse annotationParseResponse ) {
                PageStatus newStatus;

                if ( !annotationParseResponse.hasErrors() && annotationParseResponse.getAnnotation() != null ) {
                    currentValue = annotationParseResponse.getAnnotation().getValue( valuePairDefinition.getName() );
                    newStatus = PageStatus.VALIDATED;
                    setHelpMessage( "Value pair was validated!" );

                } else {
                    currentValue = null;
                    newStatus = PageStatus.NOT_VALIDATED;
                    //TODO improve this error handling
                    String errorMessage = "Value pair is not validated\n" +
                            buildErrorList( annotationParseResponse.getErrors() );

                    setHelpMessage( errorMessage );
                }

                setStatus( newStatus );
            }
        };
    }

    private void doOnValueChanged() {
        setHelpMessage( "Value is not validated" );
        currentValue = null;
    }

    private void clearHelpMessage() {
        view.clearHelpMessage();
    }

    private void setHelpMessage( String helpMessage ) {
        view.setHelpMessage( helpMessage );
    }

    private boolean isRequired() {
        return valuePairDefinition != null && valuePairDefinition.getDefaultValue() == null;
    }
}
