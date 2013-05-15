/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.widgets.configresource.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.kie.workbench.common.widgets.client.resources.ItemImages;

public class Images {

    public static final Images INSTANCE = new Images();

    private Images() {
    }

    public Image NewItem() {
        final Image image = new Image( ItemImages.INSTANCE.newItem() );
        image.setAltText( ImportConstants.INSTANCE.NewItem() );
        return image;
    }

    public Image Trash() {
        final Image image = new Image( ImageResources.INSTANCE.trash() );
        image.setAltText( ImportConstants.INSTANCE.Trash() );
        return image;
    }

    public Image NewItemDisabled() {
        final Image image = new Image( ImageResources.INSTANCE.newItemDisabled() );
        image.setAltText( ImportConstants.INSTANCE.NewItemDisabled() );
        return image;
    }

    public Image TrashDisabled() {
        final Image image = new Image( ImageResources.INSTANCE.trash() );
        image.setAltText( ImportConstants.INSTANCE.Trash() );
        return image;
    }

    public Image Home() {
        final Image image = new Image( ImageResources.INSTANCE.homeIcon() );
        image.setAltText( ImportConstants.INSTANCE.Home() );
        return image;
    }
}
