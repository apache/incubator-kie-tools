/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.parser.yaml;

import com.google.gwt.core.client.ScriptInjector;
import org.dashbuilder.client.resources.NativeLibraryResources;

public class JsYamlInjector {

    static boolean jsYamlJnject;

    public static void ensureJsYamlInjected() {
        if (!jsYamlJnject) {
            injectJsYamlResources();
            jsYamlJnject = true;
        }
    }

    private static void injectJsYamlResources() {
        ScriptInjector.fromString(NativeLibraryResources.INSTANCE.jsYaml().getText())
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
    }
}
