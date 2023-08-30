/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.widgets.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

/**
 *
 */
public final class CommonAltedImages {

    public static final CommonAltedImages INSTANCE = new CommonAltedImages();

    private CommonAltedImages() {

    }

    public Image Edit() {
        final Image image = new Image( CommonImages.INSTANCE.edit() );
        image.setAltText( CommonConstants.INSTANCE.Edit() );
        return image;
    }

    public Image DeleteItemSmall() {
        Image image = new Image( CommonImages.INSTANCE.DeleteItemSmall() );
        image.setAltText( CommonConstants.INSTANCE.DeleteItem() );
        return image;
    }

    public Image NewItemBelow() {
        Image image = new Image( CommonImages.INSTANCE.newItemBelow() );
        image.setAltText( CommonConstants.INSTANCE.NewItemBelow() );
        return image;
    }

    public Image MoveDown() {
        Image image = new Image( CommonImages.INSTANCE.shuffleDown() );
        image.setAltText( CommonConstants.INSTANCE.MoveDown() );

        return image;
    }

    public Image MoveUp() {
        Image image = new Image( CommonImages.INSTANCE.shuffleUp() );
        image.setAltText( CommonConstants.INSTANCE.MoveUp() );
        return image;
    }
}
