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
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLScriptElement;
import io.crysknife.ui.translation.client.annotation.Bundle;
import org.gwtbootstrap3.extras.notify.client.NotifyClientBundle;
import org.treblereel.j2cl.processors.common.injectors.ScriptInjector;
import org.treblereel.j2cl.processors.common.injectors.StyleInjector;

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
        resources.add(() -> injectScript("js/jquery-1.12.4.min.cache.js"));
        resources.add(() -> injectScript("js/bootstrap-3.4.1.min.cache.js"));
        resources.add(() -> injectStyle("css/animate-3.5.2.min.cache.css"));
        resources.add(() -> injectScript("js/gwtbootstrap3.js"));
        resources.add(() -> injectStyle("css/patternfly-additions.min.css"));
        resources.add(() -> injectStyle("css/patternfly.min.css"));
        resources.add(() -> injectScript("js/patternfly.min.js"));
        resources.add(() -> injectStyle("css/uberfire-patternfly.css"));
        resources.add(() -> injectStyle("css/font-awesome-4.7.0.min.cache.css"));
        resources.add(() -> injectStyle("css/fonts.css"));
        resources.add(() -> ScriptInjector.fromString(NotifyClientBundle.INSTANCE.notifyJS().getText(), htmlStyleElement -> pollResource()).inject());
        resources.add(() -> injectScript("js/bootstrap-select-1.12.4.min.cache.js"));
        //resources.add(() -> injectStyle("css/bootstrap-3.4.1.min.cache.css"));


        NotifyClientBundle.INSTANCE.notifyJS().getText();
        pollResource();

        LienzoCore.get().setDefaultImageSelectionMode(ImageSelectionMode.SELECT_BOUNDS);
    }


    private void injectStyle(String url) {
        StyleInjector.fromUrl(url, new StyleInjector.Callback(){
            @Override
            public void accept(HTMLElement htmlStyleElement) {
                DomGlobal.console.log("StyleInjector.Callback.accept " + url);
                pollResource();
            }
        }).inject();
    }

    private void injectScript(String url) {
        ScriptInjector.fromUrl(url, new ScriptInjector.Callback(){

            @Override
            public void accept(HTMLScriptElement htmlScriptElement) {
                DomGlobal.console.log("ScriptInjector.Callback.accept " + url);
                pollResource();
            }
        }).inject();
    }

    private void pollResource() {
        if (!resources.isEmpty()) {
            resources.poll().run();
        }
    }


}
