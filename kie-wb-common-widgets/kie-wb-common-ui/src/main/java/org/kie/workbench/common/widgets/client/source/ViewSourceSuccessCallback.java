package org.kie.workbench.common.widgets.client.source;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.source.ViewSourceView;

/**
 * Callback to set the ViewSource Widgets content
 */
public class ViewSourceSuccessCallback implements RemoteCallback<String> {

    final ViewSourceView viewSource;

    public ViewSourceSuccessCallback( final ViewSourceView viewSource ) {
        this.viewSource = viewSource;
    }

    @Override
    public void callback( final String drl ) {
        viewSource.setContent( drl );
        viewSource.hideBusyIndicator();
    }
}
