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

public class FieldRemoveAnnotationCommand extends AbstractDataModelCommand {

    private ObjectProperty field;

    public FieldRemoveAnnotationCommand( final DataModelerContext context, final String source,
            final DataObject dataObject, final ObjectProperty field, final String annotationClassName,
            DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        setAnnotationClassName( annotationClassName );
        this.field = field;
    }

    @Override
    public void execute() {
        if ( field.removeAnnotation( annotationClassName ) != null ) {
            notifyFieldChange( ChangeType.FIELD_ANNOTATION_REMOVE_CHANGE, context, source, dataObject,
                    field, annotationClassName, null, annotationClassName, null );
        }
    }
}