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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.base.DefaultErrorCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorPopup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorPopupView;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSourceResponse;
import org.kie.workbench.common.services.datamodeller.driver.model.DriverError;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.Command;

public class AdvancedAnnotationListEditor
    implements IsWidget,
                AdvancedAnnotationListEditorView.Presenter {

    private AdvancedAnnotationListEditorView view;

    private AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler;

    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private AdvancedAnnotationListEditorView.ValuePairChangeHandler valuePairChangeHandler;

    private AdvancedAnnotationListEditorView.AddAnnotationHandler addAnnotationHandler;

    private ValuePairEditorPopup valuePairEditor;

    @Inject
    private Caller<DataModelerService> modelerService;

    private Map<String, AnnotationSource> annotationSources;

    private List<Annotation> annotations;

    private KieProject project;

    private ElementType elementType;

    @Inject
    public AdvancedAnnotationListEditor( AdvancedAnnotationListEditorView view,
            ValuePairEditorPopup valuePairEditor ) {
        this.view = view;
        view.setPresenter( this );
        this.valuePairEditor = valuePairEditor;

        this.valuePairEditor.addPopupHandler( new ValuePairEditorPopupView.ValuePairEditorPopupHandler() {

            @Override
            public void onOk() {
                doValuePairChange( AdvancedAnnotationListEditor.this.valuePairEditor.getAnnotationClassName(),
                        AdvancedAnnotationListEditor.this.valuePairEditor.getName(),
                        AdvancedAnnotationListEditor.this.valuePairEditor.getValue() );
            }

            @Override
            public void onCancel() {
                AdvancedAnnotationListEditor.this.valuePairEditor.hide();
            }

            @Override
            public void onClose() {
                AdvancedAnnotationListEditor.this.valuePairEditor.hide();
            }

        } );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init( final KieProject project, final ElementType elementType ) {
        this.project = project;
        this.elementType = elementType;
    }

    public void loadAnnotations( List<Annotation> annotations ) {
        this.annotations = annotations;
        if ( annotations != null && annotations.size() > 0 ) {
            AnnotationSourceRequest sourceRequest = new AnnotationSourceRequest();
            sourceRequest.withAnnotations( annotations );
            modelerService.call( getLoadAnnotationSourcesSuccessCallback(), new DefaultErrorCallback() )
                    .resolveSourceRequest( sourceRequest );
        }
    }

    public void loadAnnotations( List<Annotation> annotations, Map<String, AnnotationSource> annotationSources ) {
        view.loadAnnotations( annotations, annotationSources );
    }

    @Override
    public void onAddAnnotation() {
        view.invokeCreateAnnotationWizard( new Callback<Annotation>() {
            @Override
            public void callback( Annotation annotation ) {
                if ( annotation != null && addAnnotationHandler != null ) {
                    addAnnotationHandler.onAddAnnotation( annotation );
                }
            }
        }, project, elementType );
    }

    @Override
    public void onDeleteAnnotation( final Annotation annotation ) {
        //TODO add object or field description to the message
        String message = "Are you sure that you want to remove annotation: @" +
                annotation.getClassName() + " from object/field";
        view.showYesNoDialog( message,
                new Command() {
                    @Override
                    public void execute() {
                        if ( deleteAnnotationHandler != null ) {
                            deleteAnnotationHandler.onDeleteAnnotation( annotation );
                        }
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        //do nothing
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        //do nothing.
                    }
                }
        );
    }

    @Override
    public void onEditValuePair( Annotation annotation, String valuePair ) {

        valuePairEditor.clear();
        AnnotationSource annotationSource = annotationSources.get( annotation.getClassName() );
        valuePairEditor.setValue( annotationSource != null ? annotationSource.getValuePairSource( valuePair ) : null );
        valuePairEditor.setName( valuePair );
        valuePairEditor.setAnnotationClassName( annotation.getClassName() );
        valuePairEditor.show();
    }

    @Override
    public void onClearValuePair( Annotation annotation, String valuePair ) {

        AnnotationDefinition annotationDefinition = annotation.getAnnotationDefinition();
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( valuePair );
        if ( valuePairDefinition.getDefaultValue() == null ) {
            //if the value pair has no default value, it should be applied wherever the annotation is applied, if not
            //the resulting code won't compile.
            String message = "Value pair: \"" + valuePair + "\" has no default value on @" + annotation.getClassName() + " annotation specification.\n" +
                    "So it should have a value whenever the annotation is applied, if not the resulting code will not be valid.";
            view.showYesNoDialog( message, null, null, new Command() {
                @Override
                public void execute() {
                    //do nothing
                }
            } );
        } else if ( clearValuePairHandler != null ) {
            clearValuePairHandler.onClearValuePair( annotation, valuePair );
        }
    }

    @Override
    public void addDeleteAnnotationHandler( AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler ) {
        this.deleteAnnotationHandler = deleteAnnotationHandler;
    }

    @Override
    public void addClearValuePairHandler( AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler ) {
        this.clearValuePairHandler = clearValuePairHandler;
    }

    @Override
    public void addValuePairChangeHandler( AdvancedAnnotationListEditorView.ValuePairChangeHandler valuePairChangeHandler ) {
        this.valuePairChangeHandler = valuePairChangeHandler;
    }

    @Override
    public void addAddAnnotationHandler( AdvancedAnnotationListEditorView.AddAnnotationHandler addAnnotationHandler ) {
        this.addAnnotationHandler = addAnnotationHandler;
    }

    public void clear() {
        view.clear();
    }

    public void removeAnnotation( Annotation annotation ) {
        view.removeAnnotation( annotation );
    }

    private RemoteCallback<AnnotationSourceResponse> getLoadAnnotationSourcesSuccessCallback() {

        return new RemoteCallback<AnnotationSourceResponse>() {
            @Override
            public void callback( AnnotationSourceResponse annotationSourceResponse ) {
                annotationSources = annotationSourceResponse.getAnnotationSources();
                view.loadAnnotations( annotations, annotationSourceResponse.getAnnotationSources() );
            }
        };
    }

    private void doValuePairChange( String annotationClassName, String valuePairName, String text ) {

        modelerService.call( getValuePairChangeSuccessCallback( annotationClassName, valuePairName, text ), new DefaultErrorCallback() )
                .resolveParseRequest( new AnnotationParseRequest( annotationClassName, ElementType.FIELD, valuePairName,
                        text ), project );
    }

    private RemoteCallback<AnnotationParseResponse> getValuePairChangeSuccessCallback( final String annotationClassName,
            final String valuePairName,
            final String value ) {
        return new RemoteCallback<AnnotationParseResponse>() {

            @Override public void callback( AnnotationParseResponse annotationParseResponse ) {
                if ( !annotationParseResponse.hasErrors() && annotationParseResponse.getAnnotation() != null ) {
                    Object newValue = annotationParseResponse.getAnnotation().getValue( valuePairName );

                    if ( valuePairChangeHandler != null ) {
                        valuePairChangeHandler.onValuePairChange( annotationClassName, valuePairName, newValue );
                    }
                    valuePairEditor.hide();
                    valuePairEditor.clear();

                } else {

                    //TODO improve this error handling
                    String errorMessage = "";
                    for ( DriverError error : annotationParseResponse.getErrors() ) {
                        errorMessage = errorMessage + "\n" + error.getMessage();
                    }
                    valuePairEditor.setErrorMessage( errorMessage );
                }
            }
        };
    }

}
