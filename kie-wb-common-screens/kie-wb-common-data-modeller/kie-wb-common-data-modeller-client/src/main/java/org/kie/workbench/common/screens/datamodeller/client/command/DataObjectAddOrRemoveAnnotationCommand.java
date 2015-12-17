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
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;

public class DataObjectAddOrRemoveAnnotationCommand extends AbstractDataModelCommand {

    boolean doAdd = true;

    public DataObjectAddOrRemoveAnnotationCommand( DataModelerContext context, String source, DataObject dataObject,
            String annotationClassName, boolean doAdd, DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        setAnnotationClassName( annotationClassName );
        this.doAdd = doAdd;
    }

    @Override
    public void execute() {
        if ( doAdd && dataObject.getAnnotation( annotationClassName ) == null ) {
            dataObject.addAnnotation( new AnnotationImpl( context.getAnnotationDefinition( annotationClassName ) ) );
            notifyObjectChange( ChangeType.TYPE_ANNOTATION_ADD_CHANGE, context, source, dataObject,
                    annotationClassName, null, null, null );
        } else if ( !doAdd && dataObject.getAnnotation( annotationClassName ) != null ) {
            dataObject.removeAnnotation( annotationClassName );
            notifyObjectChange( ChangeType.TYPE_ANNOTATION_REMOVE_CHANGE, context, source, dataObject,
                    annotationClassName, null, null, null );
        }

    }
}
