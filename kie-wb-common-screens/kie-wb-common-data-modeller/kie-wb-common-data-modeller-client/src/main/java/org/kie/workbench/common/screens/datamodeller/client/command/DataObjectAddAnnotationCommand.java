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

package org.kie.workbench.common.screens.datamodeller.client.command;

import java.util.List;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class DataObjectAddAnnotationCommand extends AbstractDataModelCommand {

    private Annotation annotation;

    public DataObjectAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final String annotationClassName,
            final List<ValuePair> valuePairs, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.annotationClassName = annotationClassName;
        this.valuePairs = valuePairs;
    }

    public DataObjectAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final String annotationClassName, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.annotationClassName = annotationClassName;
    }

    public DataObjectAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, Annotation annotation, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.annotation = annotation;
    }

    @Override
    public void execute() {
        if ( annotation == null ) {
            annotation = new AnnotationImpl( context.getAnnotationDefinition( annotationClassName ) );
            if ( valuePairs != null ) {
                for ( ValuePair valuePair : valuePairs ) {
                    annotation.setValue( valuePair.getName(), valuePair.getValue() );
                }
            }
        }

        dataObject.addAnnotation( annotation );
        notifyObjectChange( ChangeType.TYPE_ANNOTATION_ADD_CHANGE, context, source, dataObject,
                annotation.getClassName(), null, null, null );
    }
}
