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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.uberfire.client.mvp.UberView;

public interface SearchAnnotationPageView
    extends UberView<SearchAnnotationPageView.Presenter> {

    interface Presenter {

        void onSearchClass();

        void onSearchClassChanged();

        void addSearchAnnotationHandler( SearchAnnotationHandler searchAnnotationHandler );
    }

    interface SearchAnnotationHandler {

        void onSearchClassChanged();

        void onAnnotationDefinitionChange( AnnotationDefinition annotationDefinition );
    }

    String getClassName();

    void setClassName( String className );

    void setClassNameFocus( boolean focus );

    void clearHelpMessage();

    void setHelpMessage( String helpMessage );

}
