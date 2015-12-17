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

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class FieldAnnotationValueChangeCommand extends AbstractDataModelCommand {

    protected ObjectProperty field;

    public FieldAnnotationValueChangeCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final ObjectProperty field, final String annotationClassName,
            final String valuePair, final Object newValue, final boolean removeIfNull, final DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, annotationClassName, valuePair, newValue, removeIfNull, notifier );
        this.field = field;
    }

    public ObjectProperty getField() {
        return field;
    }

    public void setField( ObjectProperty field ) {
        this.field = field;
    }

    @Override
    public void execute() {
        Object oldValue = null;
        Annotation annotation = field.getAnnotation( annotationClassName );

        if ( annotation != null ) {
            oldValue = annotation.getValue( valuePair );

            if ( newValue != null && !newValue.equals( oldValue ) ) {
                //notify annotation value change
                annotation.setValue( valuePair, newValue );
                notifyFieldChange( ChangeType.FIELD_ANNOTATION_VALUE_CHANGE, context, source, dataObject,
                        field, annotationClassName, valuePair, oldValue, newValue );
            } else if ( newValue == null ) {
                if ( removeAnnotationIfValueIsNull ) {
                    field.removeAnnotation( annotationClassName );
                    //notify annotation removed
                    notifyFieldChange( ChangeType.FIELD_ANNOTATION_REMOVE_CHANGE, context, source, dataObject,
                            field, annotationClassName, valuePair, oldValue, newValue );
                } else {
                    //annotations do not support nulls for the value paris, so just remove the value pair.
                    annotation.removeValue( valuePair );
                    //notify annotation value change
                    notifyFieldChange( ChangeType.FIELD_ANNOTATION_VALUE_CHANGE, context, source, dataObject,
                            field, annotationClassName, valuePair, oldValue, newValue );
                }

            }
        } else if ( newValue != null ) {
            annotation = new AnnotationImpl( context.getAnnotationDefinitions().get( annotationClassName ) );
            annotation.setValue( valuePair, newValue );
            field.addAnnotation( annotation );
            //notify annotation added
            notifyFieldChange( ChangeType.FIELD_ANNOTATION_ADD_CHANGE, context, source, dataObject,
                    field, annotationClassName, valuePair, oldValue, newValue);
        }
    }

}

