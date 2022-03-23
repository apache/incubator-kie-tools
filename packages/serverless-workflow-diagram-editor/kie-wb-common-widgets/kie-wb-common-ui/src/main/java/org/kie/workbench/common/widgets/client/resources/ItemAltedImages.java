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

package org.kie.workbench.common.widgets.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class ItemAltedImages {

    public static ItemAltedImages INSTANCE = new ItemAltedImages();

    public Image NewItem() {
        Image image = new Image(ItemImages.INSTANCE.newItem());
        image.setAltText( CommonConstants.INSTANCE.NewItem());
        return image;
    }
}
