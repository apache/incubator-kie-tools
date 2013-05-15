package org.kie.workbench.common.widgets.viewsource.client.callbacks;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.viewsource.client.screen.ViewSourceView;

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
