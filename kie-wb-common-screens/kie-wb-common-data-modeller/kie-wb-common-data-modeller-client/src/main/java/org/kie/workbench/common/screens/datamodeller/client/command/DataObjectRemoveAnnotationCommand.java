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

public class DataObjectRemoveAnnotationCommand extends AbstractDataModelCommand {

    public DataObjectRemoveAnnotationCommand( DataModelerContext context, String source, DataObject dataObject,
            String annotationClassName, DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, annotationClassName, notifier );
    }

    @Override
    public void execute() {
        if ( dataObject.getAnnotation( annotationClassName ) != null ) {
            dataObject.removeAnnotation( annotationClassName );
            notifyObjectChange( ChangeType.TYPE_ANNOTATION_REMOVE_CHANGE, context, source, dataObject,
                    annotationClassName, null, null, null );
        }

    }
}
