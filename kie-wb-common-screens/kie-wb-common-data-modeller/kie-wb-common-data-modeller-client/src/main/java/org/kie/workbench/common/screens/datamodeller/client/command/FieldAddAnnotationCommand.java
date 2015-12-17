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
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class FieldAddAnnotationCommand extends AbstractDataModelCommand {

    private ObjectProperty field;

    private Annotation annotation;

    public FieldAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final ObjectProperty field, final String annotationClassName,
            final List<ValuePair> valuePairs, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.annotationClassName = annotationClassName;
        this.field = field;
        this.valuePairs = valuePairs;
    }

    public FieldAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final ObjectProperty field, final String annotationClassName, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.annotationClassName = annotationClassName;
        this.field = field;
    }

    public FieldAddAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final ObjectProperty field, Annotation annotation, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.annotation = annotation;
        this.field = field;
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

        field.addAnnotation( annotation );
        notifyFieldChange( ChangeType.FIELD_ANNOTATION_ADD_CHANGE, context, source, dataObject,
                field, annotation.getClassName(), null, null, null );
    }
}
