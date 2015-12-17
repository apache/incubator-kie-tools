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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

@ApplicationScoped
public class DataModelChangeNotifier {

    private Event<DataModelerEvent> dataModelerEvent;

    @Inject
    public DataModelChangeNotifier( Event<DataModelerEvent> dataModelerEvent ) {
        this.dataModelerEvent = dataModelerEvent;
    }

    public void notifyObjectChange( ChangeType changeType,
            DataModelerContext context,
            String source,
            DataObject dataObject,
            String annotationClassName,
            String memberName,
            Object oldValue,
            Object newValue ) {

        DataObjectChangeEvent changeEvent = new DataObjectChangeEvent( changeType,
                context.getContextId(),
                source,
                dataObject,
                memberName, oldValue, newValue );
        changeEvent.withAnnotationClassName( annotationClassName );
        dataModelerEvent.fire( changeEvent );
    }

    public void notifyFieldChange( ChangeType changeType,
            DataModelerContext context,
            String source,
            DataObject dataObject,
            ObjectProperty field,
            String annotationClassName,
            String memberName,
            Object oldValue,
            Object newValue ) {

        DataObjectFieldChangeEvent changeEvent = new DataObjectFieldChangeEvent( changeType,
                context.getContextId(),
                source,
                dataObject,
                field,
                memberName, oldValue, newValue );
        changeEvent.withAnnotationClassName( annotationClassName );
        dataModelerEvent.fire( changeEvent );

        //TODO check if this invocation is needed
        context.getHelper().dataModelChanged( changeEvent );
    }

    protected void notifyChange( DataModelerEvent event ) {
        dataModelerEvent.fire( event );
    }
}
