/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.BaseDomainEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;

@Dependent
public class MainDomainEditor extends BaseDomainEditor {

    public static final String MAIN_DOMAIN = "MAIN";

    public MainDomainEditor() {
    }

    @Inject
    public MainDomainEditor( MainDataObjectEditor objectEditor, MainDataObjectFieldEditor fieldEditor ) {
        super( objectEditor, fieldEditor );
    }

    public void refreshTypeList( boolean keepSelection ) {
        ( ( MainDataObjectEditor ) objectEditor ).refreshTypeList( keepSelection );
        ( ( MainDataObjectFieldEditor ) fieldEditor ).refreshTypeList( keepSelection );
    }

    public void setContext( DataModelerContext context ) {
        super.onContextChange( context );
    }

    protected void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showObjectEditor();
        }
    }

    protected void onDataObjectFieldSelected( @Observes DataObjectFieldSelectedEvent event ) {
        if ( event.isFromContext( getContextId() ) ) {
            showFieldEditor( );
            fieldEditor.onContextChange( context );
        }
    }
}