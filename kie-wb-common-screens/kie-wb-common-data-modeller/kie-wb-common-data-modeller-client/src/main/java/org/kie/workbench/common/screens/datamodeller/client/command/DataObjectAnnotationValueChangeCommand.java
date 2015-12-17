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
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class DataObjectAnnotationValueChangeCommand extends AbstractDataModelCommand {

    public DataObjectAnnotationValueChangeCommand( DataModelerContext context, String source, DataObject dataObject,
            String annotationClassName, String valuePair, Object newValue, boolean removeIfNull,
            DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, annotationClassName, valuePair, newValue, removeIfNull, notifier );
    }

    @Override
    public void execute() {
        Object oldValue = null;
        Annotation annotation = dataObject.getAnnotation( annotationClassName );

        if ( annotation != null ) {
            oldValue = annotation.getValue( valuePair );

            if ( newValue != null && !newValue.equals( oldValue ) ) {
                //notify annotation value change
                annotation.setValue( valuePair, newValue );
                notifyObjectChange( ChangeType.TYPE_ANNOTATION_VALUE_CHANGE, context, source, dataObject,
                        annotationClassName, valuePair, oldValue, newValue );
            } else if ( newValue == null && oldValue != null ) {
                if ( removeAnnotationIfValueIsNull ) {
                    dataObject.removeAnnotation( annotationClassName );
                    //notify annotation removed
                    notifyObjectChange( ChangeType.TYPE_ANNOTATION_REMOVE_CHANGE, context, source, dataObject,
                            annotationClassName, valuePair, oldValue, newValue );
                } else {
                    annotation.setValue( valuePair, newValue );
                    //notify annotation value change
                    notifyObjectChange( ChangeType.TYPE_ANNOTATION_VALUE_CHANGE, context, source, dataObject,
                            annotationClassName, valuePair, oldValue, newValue);
                }
            }
        } else if ( newValue != null) {
            annotation = new AnnotationImpl( context.getAnnotationDefinitions().get( annotationClassName ) );
            annotation.setValue( valuePair, newValue );
            dataObject.addAnnotation( annotation );
            //notify annotation added
            notifyObjectChange( ChangeType.TYPE_ANNOTATION_ADD_CHANGE, context, source, dataObject,
                    annotationClassName, valuePair, oldValue, newValue);
        }

    }
}
