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


package org.kie.workbench.common.stunner.client.lienzo;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import io.crysknife.ui.translation.client.annotation.Bundle;
import org.kie.workbench.common.stunner.client.lienzo.canvas.patternfly.PatternFlyBundle;

@Bundle("resources/i18n/StunnerLienzoConstants.properties")
public class StunnerLienzoCore {

    /**
     * It's really important to set the <code>ImageSelectionMode</code> to the
     * value <code>SELECT_BOUNDS</code> due to performance reasons (image rendering on different browsers).
     * Stunner does not use image filters neither requires the image to be drawn in the
     * selection context2D, so it uses the value <code>SELECT_BOUNDS</code> as default.
     * Also it's being used due to huge differences on the resulting performance when
     * rendering the images into the contexts.
     */
    public void init() {

        PatternFlyBundle.INSTANCE.bootstrapcss().insureInjectedAsStyle();;
        PatternFlyBundle.INSTANCE.jquery().insureInjectedAsScript();
        PatternFlyBundle.INSTANCE.bootstrapjs().insureInjectedAsScript();
        PatternFlyBundle.INSTANCE.animate().insureInjectedAsStyle();
        PatternFlyBundle.INSTANCE.gwtbootstrap3().insureInjectedAsScript();
        PatternFlyBundle.INSTANCE.patternflyadditions().insureInjectedAsStyle();
        PatternFlyBundle.INSTANCE.patternflycss().insureInjectedAsStyle();
        PatternFlyBundle.INSTANCE.patternflyjs().insureInjectedAsScript();
        PatternFlyBundle.INSTANCE.uberfirepatternfly().insureInjectedAsStyle();
        PatternFlyBundle.INSTANCE.fontawesome().insureInjectedAsStyle();
        PatternFlyBundle.INSTANCE.fonts().insureInjectedAsStyle();
        LienzoCore.get().setDefaultImageSelectionMode(ImageSelectionMode.SELECT_BOUNDS);
    }
}
