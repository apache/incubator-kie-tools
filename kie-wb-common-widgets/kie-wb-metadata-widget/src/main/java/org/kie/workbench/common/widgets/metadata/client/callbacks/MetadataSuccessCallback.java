package org.kie.workbench.common.widgets.metadata.client.callbacks;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;

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
