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
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationDefinitionResponse;
import org.kie.workbench.common.services.shared.project.KieProject;

@Dependent
public class SearchAnnotationPage
        extends CreateAnnotationWizardPage
        implements SearchAnnotationPageView.Presenter {


    private SearchAnnotationPageView.SearchAnnotationHandler searchAnnotationHandler;

    @Inject
    private SearchAnnotationPageView view;

    public SearchAnnotationPage( ) {
        setTitle( "Search annotation" );
    }

    @PostConstruct
    private void init( ) {
        view.setPresenter( this );
        content.add( view );
    }

    public void init( KieProject project, ElementType target ) {
        this.project = project;
        this.target = target;
    }

    @Override
    public void onSearchClass() {
        AnnotationDefinitionRequest definitionRequest = new AnnotationDefinitionRequest( view.getClassName() );
        modelerService.call( getOnSearchClassSuccessCallback( definitionRequest) ).resolveDefinitionRequest( definitionRequest, project );
    }

    private RemoteCallback<AnnotationDefinitionResponse> getOnSearchClassSuccessCallback( final AnnotationDefinitionRequest definitionRequest ) {
        return new RemoteCallback<AnnotationDefinitionResponse>() {
            @Override
            public void callback( AnnotationDefinitionResponse definitionResponse ) {
                processAnnotationDefinitionRequest( definitionRequest, definitionResponse );
            }
        };
    }

    private void processAnnotationDefinitionRequest( AnnotationDefinitionRequest definitionRequest,
            AnnotationDefinitionResponse definitionResponse ) {

        annotationDefinition = definitionResponse.getAnnotationDefinition();
        if ( definitionResponse.hasErrors() || definitionResponse.getAnnotationDefinition() == null ) {
            //TODO improve this, use a details section to provide more info.
            String message = "Class name " + definitionRequest.getClassName() + " was not found. \n It was not possible to load annotation definition ";
            message += "\n" + buildErrorList( definitionResponse.getErrors() );
            setHelpMessage( message );
        }

        setStatus( annotationDefinition != null ? PageStatus.VALIDATED : PageStatus.NOT_VALIDATED );
        if ( searchAnnotationHandler != null ) {
            searchAnnotationHandler.onAnnotationDefinitionChange( annotationDefinition );
        }
    }
    @Override
    public void onSearchClassChanged() {
        setHelpMessage( "Annotation definition is not loaded." );
        annotationDefinition = null;
        if ( searchAnnotationHandler != null ) {
            searchAnnotationHandler.onSearchClassChanged();
        }
        setStatus( PageStatus.NOT_VALIDATED );
    }

    @Override
    public void addSearchAnnotationHandler( SearchAnnotationPageView.SearchAnnotationHandler searchAnnotationHandler ) {
        this.searchAnnotationHandler = searchAnnotationHandler;
    }

    void clearHelpMessage() {
        view.clearHelpMessage();
    }

    void setHelpMessage( String helpMessage ) {
        view.setHelpMessage( helpMessage );
    }

}
