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

import java.util.LinkedList;
import java.util.Queue;

import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.shared.core.types.ImageSelectionMode;
import org.gwtbootstrap3.extras.notify.client.NotifyClientBundle;
import org.kie.j2cl.tools.di.ui.translation.client.annotation.Bundle;
import org.kie.j2cl.tools.processors.common.injectors.ScriptInjector;
import org.kie.j2cl.tools.processors.common.injectors.StyleInjector;
import org.kie.j2cl.tools.processors.common.resources.TextResource;
import org.kie.workbench.common.stunner.client.lienzo.resources.StunnerLienzoCoreResources;

@Bundle("resources/i18n/StunnerLienzoConstants.properties")
public class StunnerLienzoCore {

     private final Queue<Runnable> resources = new LinkedList<>();

    /**
     * It's really important to set the <code>ImageSelectionMode</code> to the
     * value <code>SELECT_BOUNDS</code> due to performance reasons (image rendering on different browsers).
     * Stunner does not use image filters neither requires the image to be drawn in the
     * selection context2D, so it uses the value <code>SELECT_BOUNDS</code> as default.
     * Also it's being used due to huge differences on the resulting performance when
     * rendering the images into the contexts.
     */
    public void init() {

        // sequence of resources is important
        resources.add(() -> injectScript(StunnerLienzoCoreResources.INSTANCE.jquery()));
        resources.add(() -> injectScript(StunnerLienzoCoreResources.INSTANCE.bootstrapJs()));
        resources.add(() -> injectStyle(StunnerLienzoCoreResources.INSTANCE.animate()));
        resources.add(() -> injectScript(StunnerLienzoCoreResources.INSTANCE.gwtbootstrap3()));
        resources.add(() -> injectStyle(StunnerLienzoCoreResources.INSTANCE.patternflyStyleAdditionsMin()));
        resources.add(() -> injectStyle(StunnerLienzoCoreResources.INSTANCE.patternflyStyleMin()));
        resources.add(() -> injectScript(StunnerLienzoCoreResources.INSTANCE.patternfly()));
        resources.add(() -> injectStyle(StunnerLienzoCoreResources.INSTANCE.uberfirePatternfly()));
        resources.add(() -> injectStyle(StunnerLienzoCoreResources.INSTANCE.fontAwesome()));
        resources.add(() -> injectStyle(StunnerLienzoCoreResources.INSTANCE.fonts()));
        resources.add(() -> injectScript(NotifyClientBundle.INSTANCE.notifyJS()));
        resources.add(() -> injectScript(StunnerLienzoCoreResources.INSTANCE.bootstrapSelectJs()));

        pollResource();

        LienzoCore.get().setDefaultImageSelectionMode(ImageSelectionMode.SELECT_BOUNDS);
    }


    private void injectStyle(TextResource resource) {
        StyleInjector.fromString(resource.getText()).inject();
        pollResource();
    }

    private void injectScript(TextResource resource) {
        ScriptInjector.fromString(resource.getText()).inject();
        pollResource();
    }

    private void pollResource() {
        if (!resources.isEmpty()) {
            resources.poll().run();
        }
    }


}
