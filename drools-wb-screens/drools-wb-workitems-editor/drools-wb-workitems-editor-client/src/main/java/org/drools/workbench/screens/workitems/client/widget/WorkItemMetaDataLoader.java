/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.workitems.client.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;

/**
 * Loader for Work Items Editors meta-data.
 */
@ApplicationScoped
public class WorkItemMetaDataLoader {

    @Inject
    private Caller<WorkItemsEditorService> workItemsService;

    private WorkItemDefinitionElements metaContent = null;

    public void loadMetaContent( final HasWorkItemDefinitionElements handler ) {
        if ( metaContent == null ) {
            workItemsService.call( getMetaContentSuccessCallback( handler ),
                                   new DefaultErrorCallback() ).loadDefinitionElements();
        } else {
            handler.setDefinitionElements( metaContent );
        }
    }

    private RemoteCallback<WorkItemDefinitionElements> getMetaContentSuccessCallback( final HasWorkItemDefinitionElements handler ) {
        return new RemoteCallback<WorkItemDefinitionElements>() {

            @Override
            public void callback( final WorkItemDefinitionElements content ) {
                metaContent = content;
                handler.setDefinitionElements( content );
            }
        };
    }

}
