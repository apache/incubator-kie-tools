package org.kie.guvnor.workitems.client.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.DefaultErrorCallback;
import org.kie.guvnor.workitems.model.WorkItemDefinitionElements;
import org.kie.guvnor.workitems.service.WorkItemsEditorService;

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
