/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.parser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import org.dashbuilder.client.parser.yaml.JsYaml;
import org.dashbuilder.client.parser.yaml.JsYamlInjector;
import org.dashbuilder.shared.model.RuntimeModel;

@ApplicationScoped
public class YAMLRuntimeModelClientParser implements RuntimeModelClientParser {

    @Inject
    JSONRuntimeModelClientParser jsonParser;

    @Override
    public RuntimeModel parse(String content) {
        var object = convertToJson(content);
        var jsonContent = Global.JSON.stringify(object);
        return jsonParser.parse(jsonContent);
    }

    @Override
    public boolean test(String content) {
        try {
            convertToJson(content);
            return true;
        } catch (Exception e) {
            DomGlobal.console.debug("Not YAML Content");
            DomGlobal.console.debug(e);
        }
        return false;
    }

    private Object convertToJson(String content) {
        JsYamlInjector.ensureJsYamlInjected();
        var object = JsYaml.Builder.get().load(content);
        return object;
    }
}
