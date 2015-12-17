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

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public interface AdvancedAnnotationListEditorView
    extends UberView<AdvancedAnnotationListEditorView.Presenter> {

    interface Presenter {

        void onAddAnnotation();

        void onDeleteAnnotation( Annotation annotation );

        void onEditValuePair( Annotation annotation, String valuePair );

        void onClearValuePair( Annotation annotation, String valuePair );

        void addClearValuePairHandler( ClearValuePairHandler clearValuePairHandler );

        void addDeleteAnnotationHandler( DeleteAnnotationHandler deleteAnnotationHandler );

        void addValuePairChangeHandler( ValuePairChangeHandler valuePairChangeHandler );

        void addAddAnnotationHandler( AddAnnotationHandler addAnnotationHandler );

    }

    interface DeleteAnnotationHandler {

        void onDeleteAnnotation( final Annotation annotation );

    }

    interface ClearValuePairHandler {

        void onClearValuePair( Annotation annotation, String valuePair );

    }

    interface ValuePairChangeHandler {

        void onValuePairChange( String annotationClassName, String valuePairName, Object newValue );
    }

    interface AddAnnotationHandler {

        void onAddAnnotation( Annotation annotation );

    }

    void loadAnnotations( List<Annotation> annotations, Map<String, AnnotationSource> annotationSources );

    void removeAnnotation( Annotation annotation );

    void showYesNoDialog( String message, Command yesCommand, Command noCommand, Command cancelCommand );

    void invokeCreateAnnotationWizard( final Callback<Annotation> callback,
            KieProject kieProject,
            ElementType elementType );

    void setReadonly( boolean readonly );

    void clear();

}
