package org.drools.workbench.screens.workitems.client.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;

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
