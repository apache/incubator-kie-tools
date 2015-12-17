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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.base.DefaultErrorCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
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

@Dependent
public class AdvancedAnnotationListEditor
    implements IsWidget,
                AdvancedAnnotationListEditorView.Presenter {

    private AdvancedAnnotationListEditorView view;

    private AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler;

    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private AdvancedAnnotationListEditorView.ValuePairChangeHandler valuePairChangeHandler;

    private AdvancedAnnotationListEditorView.AddAnnotationHandler addAnnotationHandler;

    private SyncBeanManager iocManager;

    private Caller<DataModelerService> modelerService;

    private Map<String, AnnotationSource> annotationSources;

    private List<Annotation> annotations;

    private KieProject project;

    private ElementType elementType;

    private boolean readonly = false;

    @Inject
    public AdvancedAnnotationListEditor( AdvancedAnnotationListEditorView view,
                                        Caller<DataModelerService> modelerService,
                                        SyncBeanManager iocManager ) {
        this.view = view;
        view.init( this );
        this.modelerService = modelerService;
        this.iocManager = iocManager;
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

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly( boolean readonly ) {
        this.readonly = readonly;
        view.setReadonly( readonly );
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
        String message = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_confirm_annotation_deletion(
                annotation.getClassName(),
                ( elementType != null ? elementType.name() : " object/field" ) );
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
        ValuePairEditorPopup valuePairEditor = createValuePairEditor( annotation, valuePair );
        if ( valuePairEditor.isGenericEditor() ) {
            AnnotationSource annotationSource = annotationSources.get( annotation.getClassName() );
            String valuePairSource = annotationSource != null ? annotationSource.getValuePairSource( valuePair ) : null;
            valuePairEditor.setValue( valuePairSource );
        } else {
            valuePairEditor.setValue( annotation.getValue( valuePair ) );
        }
        valuePairEditor.show();
    }

    @Override
    public void onClearValuePair( Annotation annotation, String valuePair ) {

        AnnotationDefinition annotationDefinition = annotation.getAnnotationDefinition();
        AnnotationValuePairDefinition valuePairDefinition = annotationDefinition.getValuePair( valuePair );
        if ( valuePairDefinition.getDefaultValue() == null ) {
            //if the value pair has no default value, it should be applied wherever the annotation is applied, if not
            //the resulting code won't compile.
            String message = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_pair_has_no_default_value( valuePair,  annotation.getClassName() );
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
                view.clear();
                annotationSources = annotationSourceResponse.getAnnotationSources();
                view.loadAnnotations( annotations, annotationSourceResponse.getAnnotationSources() );
            }
        };
    }

    private void doValuePairChange( final ValuePairEditorPopup valuePairEditor, final Object value ) {

        if ( valuePairEditor.isGenericEditor() ) {
            String strValue = value != null ? value.toString() : null;
            modelerService.call( getValuePairChangeSuccessCallback( valuePairEditor ), new DefaultErrorCallback() )
                    .resolveParseRequest( new AnnotationParseRequest( valuePairEditor.getAnnotationClassName(),
                                                elementType,
                                                valuePairEditor.getValuePairDefinition().getName(),
                                                strValue ), project );
        } else {
            applyValuePairChange( valuePairEditor, value );
        }
    }

    private RemoteCallback<AnnotationParseResponse> getValuePairChangeSuccessCallback(
            final ValuePairEditorPopup valuePairEditor ) {
        return new RemoteCallback<AnnotationParseResponse>() {

            @Override public void callback( AnnotationParseResponse annotationParseResponse ) {
                if ( !annotationParseResponse.hasErrors() && annotationParseResponse.getAnnotation() != null ) {
                    Object newValue = annotationParseResponse.getAnnotation().getValue( valuePairEditor.getValuePairDefinition().getName() );
                    applyValuePairChange( valuePairEditor, newValue );
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

    private void applyValuePairChange( ValuePairEditorPopup valuePairEditor, Object newValue ) {

        if ( !valuePairEditor.isValid() ) {
            valuePairEditor.setErrorMessage(
                    Constants.INSTANCE.advanced_domain_annotation_list_editor_message_invalid_value_for_value_pair( valuePairEditor.getValuePairDefinition().getName() )
            );
        } else {
            if ( !valuePairEditor.getValuePairDefinition().hasDefaultValue() && newValue == null ) {
                valuePairEditor.setErrorMessage(
                        Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_pair_cant_be_null( valuePairEditor.getValuePairDefinition().getName() )
                );
            } else {
                valuePairChangeHandler.onValuePairChange( valuePairEditor.getAnnotationClassName(),
                        valuePairEditor.getValuePairDefinition().getName(),
                        newValue );
                valuePairEditor.hide();
                dispose( valuePairEditor );
            }
        }

    }

    private ValuePairEditorPopup createValuePairEditor( Annotation annotation, String valuePair ) {
        final ValuePairEditorPopup valuePairEditor = iocManager.lookupBean( ValuePairEditorPopup.class ).getInstance();
        valuePairEditor.init( annotation.getClassName(), annotation.getAnnotationDefinition().getValuePair( valuePair ) );
        valuePairEditor.addPopupHandler( new ValuePairEditorPopupView.ValuePairEditorPopupHandler() {

            @Override
            public void onOk() {
                doValuePairChange( valuePairEditor, valuePairEditor.getValue() );
            }

            @Override
            public void onCancel() {
                valuePairEditor.hide();
                dispose( valuePairEditor );
            }

            @Override
            public void onClose() {
                valuePairEditor.hide();
                dispose( valuePairEditor );
            }

        } );

        return valuePairEditor;
    }

    private void dispose( ValuePairEditorPopup valuePairEditor ) {
        iocManager.destroyBean( valuePairEditor );
    }

}
