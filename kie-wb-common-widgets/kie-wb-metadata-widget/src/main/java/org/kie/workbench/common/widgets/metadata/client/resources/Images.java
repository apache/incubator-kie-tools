/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.metadata.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.kie.workbench.common.widgets.client.resources.ItemImages;

public class Images {

    public static final Images INSTANCE = new Images();

    private Images() {
    }

    public Image NewItem() {
        final Image image = new Image( ItemImages.INSTANCE.newItem() );
        image.setAltText( MetadataConstants.INSTANCE.NewItem() );
        return image;
    }

    public Image Trash() {
        final Image image = new Image( ImageResources.INSTANCE.trash() );
        image.setAltText( MetadataConstants.INSTANCE.Trash() );
        return image;
    }

    public Image Refresh() {
        final Image image = new Image( ImageResources.INSTANCE.refresh() );
        image.setAltText( MetadataConstants.INSTANCE.Refresh() );
        return image;
    }

}
