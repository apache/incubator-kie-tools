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

package org.kie.workbench.common.widgets.metadata.client.callbacks;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;

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
