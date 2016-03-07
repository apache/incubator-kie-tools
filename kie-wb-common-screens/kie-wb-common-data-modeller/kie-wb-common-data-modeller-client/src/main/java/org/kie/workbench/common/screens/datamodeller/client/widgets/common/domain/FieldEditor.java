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

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

public abstract class FieldEditor extends BaseEditor {

    protected DataObject dataObject;

    protected ObjectProperty objectField;

    public DataObject getDataObject() {
        return dataObject;
    }

    public ObjectProperty getObjectField() {
        return objectField;
    }

    public FieldEditor( DomainHandlerRegistry handlerRegistry,
            Event<DataModelerEvent> dataModelerEvent,
            DataModelCommandBuilder commandBuilder ) {
        super( handlerRegistry, dataModelerEvent, commandBuilder );
    }

    protected abstract void loadDataObjectField( DataObject dataObject,
            ObjectProperty objectField );

    @Override
    public void onContextChange( DataModelerContext context ) {
        super.onContextChange( context );
        if ( context == null ) {
            dataObject = null;
            objectField = null;
        } else {
            dataObject = context.getDataObject();
            objectField = context.getObjectProperty();
        }
        loadDataObjectField( dataObject, objectField );
    }

    // Event observers

    protected void onDataObjectFieldChange( @Observes DataObjectFieldChangeEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) &&
                !getName().equals( event.getSource() ) ) {
            loadDataObjectField( event.getCurrentDataObject(), event.getCurrentField() );
        }
    }

    protected void onDataObjectFieldDeleted( @Observes DataObjectFieldDeletedEvent event ) {
        // When all attributes from the current object has been deleted clean
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            if ( getDataObject() != null && getDataObject().getProperties().size() == 0 ) {
                clear();
                setReadonly( true );
            }
        }
    }

    protected DataObjectFieldChangeEvent createFieldChangeEvent( ChangeType changeType ) {

        DataObjectFieldChangeEvent changeEvent = new DataObjectFieldChangeEvent( changeType,
                getContext().getContextId(),
                getName(),
                getDataObject(),
                getObjectField(),
                null, null, null );
        return changeEvent;
    }

}
