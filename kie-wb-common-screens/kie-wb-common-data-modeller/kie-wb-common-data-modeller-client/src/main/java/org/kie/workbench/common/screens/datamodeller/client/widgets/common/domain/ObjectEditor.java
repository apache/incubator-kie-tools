/*
 * Copyright 2015 JBoss Inc
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
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

public abstract class ObjectEditor extends Composite {

    protected DataModelerContext context;

    protected DataObject dataObject;

    protected boolean readonly = false;

    @Inject
    protected Event<DataModelerEvent> dataModelerEvent;

    protected ObjectEditor() {
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    protected abstract void clean();

    protected void setReadonly( boolean readonly ) {
        this.readonly = readonly;
    }

    protected boolean isReadonly() {
        return readonly;
    }

    protected abstract void loadDataObject( DataObject dataObject );

    protected void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            loadDataObject( event.getCurrentDataObject() );
        }
    }

}
