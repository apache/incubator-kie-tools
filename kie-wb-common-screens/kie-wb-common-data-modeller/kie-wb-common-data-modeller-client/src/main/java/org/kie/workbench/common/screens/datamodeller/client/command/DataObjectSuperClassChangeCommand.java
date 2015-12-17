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
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

public class DataObjectSuperClassChangeCommand extends AbstractDataModelCommand {

    private String newSuperClass;

    public DataObjectSuperClassChangeCommand( DataModelerContext context, String source, DataObject dataObject,
            String newSuperClass,
            DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.newSuperClass = newSuperClass;
    }

    @Override
    public void execute() {
        String oldSuperClass = dataObject.getSuperClassName();

        getDataObject().setSuperClassName( newSuperClass );

        // Remove former extension refs if superclass has changed
        if ( oldSuperClass != null && !"".equals( oldSuperClass ) ) {
            getContext().getHelper().dataObjectExtended( oldSuperClass, getDataObject().getClassName(), false );
        }
        getContext().getHelper().dataObjectExtended( newSuperClass, getDataObject().getClassName(), true );


        DataObjectChangeEvent event = new DataObjectChangeEvent( ChangeType.SUPER_CLASS_NAME_CHANGE,
                getContext().getContextId(), getSource(), getDataObject(), null, oldSuperClass, newSuperClass );

        notifyChange( event );

    }
}
