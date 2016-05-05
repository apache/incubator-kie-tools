/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.table.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.uberfire.ext.widgets.table.client.UberfireSimplePager;

/**
 * A ClientBundle that provides images for this widget.
 */
public interface UberfireSimplePagerResources
        extends
        ClientBundle {

    UberfireSimplePagerResources INSTANCE = GWT.create( UberfireSimplePagerResources.class );

    /**
     * The image used to skip ahead multiple pages.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerFastForward.png")
    ImageResource simplePagerFastForward();

    /**
     * The disabled "fast forward" image.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerFastForwardDisabled.png")
    ImageResource simplePagerFastForwardDisabled();

    /**
     * The image used to go to the first page.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerFirstPage.png")
    ImageResource simplePagerFirstPage();

    /**
     * The disabled first page image.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerFirstPageDisabled.png")
    ImageResource simplePagerFirstPageDisabled();

    /**
     * The image used to go to the last page.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerLastPage.png")
    ImageResource simplePagerLastPage();

    /**
     * The disabled last page image.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerLastPageDisabled.png")
    ImageResource simplePagerLastPageDisabled();

    /**
     * The image used to go to the next page.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerNextPage.png")
    ImageResource simplePagerNextPage();

    /**
     * The disabled next page image.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerNextPageDisabled.png")
    ImageResource simplePagerNextPageDisabled();

    /**
     * The image used to go to the previous page.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerPreviousPage.png")
    ImageResource simplePagerPreviousPage();

    /**
     * The disabled previous page image.
     */
    @ImageResource.ImageOptions(flipRtl = true)
    @Source("images/simplepager/simplePagerPreviousPageDisabled.png")
    ImageResource simplePagerPreviousPageDisabled();

    /**
     * The styles used in this widget.
     */
    @Source("css/UberfireSimplePager.css")
    UberfireSimplePager.Style simplePagerStyle();

}
