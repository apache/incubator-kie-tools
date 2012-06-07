/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface RoundedCornersResource
    extends
    ClientBundle {

    RoundedCornersResource INSTANCE = GWT.create( RoundedCornersResource.class );

    @Source("images/corners/whiteTopLeft.gif")
    ImageResource whiteTopLeftCorner();

    @Source("images/corners/whiteTopRight.gif")
    ImageResource whiteTopRightCorner();

    @Source("images/corners/whiteBottomLeft.gif")
    ImageResource whiteBottomLeftCorner();

    @Source("images/corners/whiteBottomRight.gif")
    ImageResource whiteBottomRightCorner();

    @Source("images/corners/greyTopLeftCorner.gif")
    ImageResource greyTopLeftCorner();

    @Source("images/corners/greyTopRightCorner.gif")
    ImageResource greyTopRightCorner();

    @Source("images/corners/greyBottomLeftCorner.gif")
    ImageResource greyBottomLeftCorner();

    @Source("images/corners/greyBottomRightCorner.gif")
    ImageResource greyBottomRightCorner();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("images/corners/greyBottom.gif")
    ImageResource greyBottom();

    @ImageOptions(flipRtl = true, repeatStyle = RepeatStyle.Horizontal)
    @Source("images/corners/greyTop.gif")
    ImageResource greyTop();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Source("images/corners/greyLeftSide.gif")
    ImageResource greySideLeft();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Source("images/corners/greyRightSide.gif")
    ImageResource greySideRight();

    @Source("css/RoundCorners.css")
    RoundCornersCss roundCornersCss();

}
