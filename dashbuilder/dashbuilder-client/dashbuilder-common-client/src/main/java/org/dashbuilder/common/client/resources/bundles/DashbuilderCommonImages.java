/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.common.client.resources.bundles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT managed images for common client components.
 */
public interface DashbuilderCommonImages extends ClientBundle {

    /*
            DEFAULT SLIDER
     */
    @Source("images/slider/default/lessh.png")
    ImageResource lessh();

    @Source("images/slider/default/lessv.png")
    ImageResource lessv();
    
    @Source("images/slider/default/moreh.png")
    ImageResource moreh();

    @Source("images/slider/default/morev.png")
    ImageResource morev();

    @Source("images/slider/default/scaleh.png")
    DataResource scaleh();

    @Source("images/slider/default/scalev.png")
    DataResource scalev();

    @Source("images/slider/default/dragh.png")
    ImageResource dragh();

    @Source("images/slider/default/dragv.png")
    ImageResource dragv();

    /*
            TRIANGLE SLIDER
     */

    @Source("images/slider/triangle/drag.png")
    ImageResource dragt();

    @Source("images/slider/triangle/line.png")
    DataResource linet();

    @Source("images/slider/triangle/more_less.png")
    ImageResource moreLesst();
}
