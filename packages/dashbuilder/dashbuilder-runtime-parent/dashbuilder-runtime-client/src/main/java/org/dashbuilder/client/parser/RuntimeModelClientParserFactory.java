/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class RuntimeModelClientParserFactory {

    @Inject
    Instance<RuntimeModelClientParser> parsers;

    @Inject
    YAMLRuntimeModelClientParser yamlParser;

    @Inject
    JSONRuntimeModelClientParser jsonParser;

    public Optional<RuntimeModelClientParser> get(String content) {
        for (var p : parsers) {
            if (p.test(content)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /**
     * 
     * Editors only supports a specific range of parsers.
     * @param content
     * @return
     */
    public RuntimeModelClientParser getEditorParser(String content) {
        if (jsonParser.test(content)) {
            return jsonParser;
        }
        return yamlParser;
    }

}
