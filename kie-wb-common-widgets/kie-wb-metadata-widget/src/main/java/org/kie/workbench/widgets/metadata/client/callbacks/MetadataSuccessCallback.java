package org.kie.workbench.widgets.metadata.client.callbacks;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.kie.workbench.widgets.metadata.client.widget.MetadataWidget;
import org.kie.guvnor.services.metadata.model.Metadata;

/**
 * Callback to set the MetaData Widgets content
 */
public class MetadataSuccessCallback implements RemoteCallback<Metadata> {

    final MetadataWidget metadataWidget;
    final boolean isReadOnly;

    public MetadataSuccessCallback( final MetadataWidget metadataWidget,
                                    final boolean isReadOnly ) {
        this.metadataWidget = metadataWidget;
        this.isReadOnly = isReadOnly;
    }

    @Override
    public void callback( final Metadata metadata ) {
        metadataWidget.setContent( metadata,
                                   isReadOnly );
        metadataWidget.hideBusyIndicator();
    }
}
