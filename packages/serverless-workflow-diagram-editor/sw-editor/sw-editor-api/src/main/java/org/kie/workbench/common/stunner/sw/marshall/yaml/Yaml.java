/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.marshall.yaml;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsMethod;
import jsinterop.base.JsPropertyMap;

public class Yaml {

    private static boolean initialized = false;

    public interface YamlBeautifier extends ClientBundle {

        YamlBeautifier INSTANCE = GWT.create(YamlBeautifier.class);

        // The File Saver js.
        @Source("bundle.js")
        TextResource bundle();
    }


    public static String beautify(String yaml) {
        if(!initialized) {
            init();
            initialized = true;
        }


        JsPropertyMap options = JsPropertyMap.of();
        options.set("lineWidth", 600);
        options.set("merge", true);
        return beautify(yaml, options);

    }

    private static void init() {
        DomGlobal.console.log("Initializing YAML");
        ScriptInjector
                .fromString(
                        YamlBeautifier.INSTANCE.bundle().getText())
                .setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
    }


    @JsMethod(namespace = "org.kie.workbench.common.stunner.sw.marshall.Yaml", name = "beautify")
    private native static String beautify(String yaml, JsPropertyMap options);

}
