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
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class FieldAddOrRemoveAnnotationCommand extends AbstractDataModelCommand {

    private boolean doAdd = true;

    private ObjectProperty field;

    public FieldAddOrRemoveAnnotationCommand( DataModelerContext context, String source, DataObject dataObject,
            ObjectProperty field,
            String annotationClassName, boolean doAdd, DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.field = field;
        setAnnotationClassName( annotationClassName );
        this.doAdd = doAdd;
    }

    @Override
    public void execute() {
        if ( doAdd && field.getAnnotation( annotationClassName ) == null ) {
            field.addAnnotation( new AnnotationImpl( context.getAnnotationDefinition( annotationClassName ) ) );
            notifyFieldChange( ChangeType.FIELD_ANNOTATION_ADD_CHANGE, context, source, dataObject, field,
                    annotationClassName, null, null, null );
        } else if ( !doAdd && field.getAnnotation( annotationClassName ) != null ) {
            field.removeAnnotation( annotationClassName );
            notifyFieldChange( ChangeType.FIELD_ANNOTATION_REMOVE_CHANGE, context, source, dataObject, field,
                    annotationClassName, null, null, null );
        }

    }
}
