/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.resources;

import com.google.gwt.user.client.ui.Image;
import org.uberfire.client.resources.i18n.CommonConstants;

/**
 *
 */
public class CommonAltedImages {

    public static final CommonAltedImages INSTANCE = new CommonAltedImages();

    public Image close() {
        final Image image = new Image( CommonImages.INSTANCE.close() );
        image.setAltText( CommonConstants.INSTANCE.Close() );
        return image;
    }

}
