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
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;

public class FieldTypeChangeCommand extends AbstractDataModelCommand {

    protected ObjectProperty field;

    protected boolean multiple;

    public FieldTypeChangeCommand( DataModelerContext context, String source, DataObject dataObject,
            ObjectProperty field, String newType, boolean multiple, DataModelChangeNotifier notifier ) {

        super( context, source, dataObject, null, null, newType, false, notifier );
        this.field = field;
        this.multiple = multiple;

    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple( boolean multiple ) {
        this.multiple = multiple;
    }

    public ObjectProperty getField() {
        return field;
    }

    public void setField( ObjectProperty field ) {
        this.field = field;
    }

    @Override
    public void execute() {

        String oldType = field.getClassName();

        String newType = (String) newValue;

        field.setClassName( newType );
        field.setMultiple( multiple );
        if ( multiple && field.getBag() == null ) {
            field.setBag( ObjectPropertyImpl.DEFAULT_PROPERTY_BAG );
        }

        if ( !getContext().getHelper().isBaseType( newType ) ) {
            getContext().getHelper().dataObjectUnReferenced( oldType, getDataObject().getClassName() );
            getContext().getHelper().dataObjectReferenced( newType, getDataObject().getClassName() );
        }

        notifyFieldChange( ChangeType.FIELD_TYPE_CHANGE, context, source, dataObject, field, null, null, oldType,
                newType );

    }
}