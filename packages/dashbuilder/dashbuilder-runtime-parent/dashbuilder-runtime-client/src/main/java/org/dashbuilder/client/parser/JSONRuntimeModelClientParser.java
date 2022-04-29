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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.shared.marshalling.RuntimeModelJSONMarshaller;
import org.dashbuilder.shared.model.RuntimeModel;

@ApplicationScoped
public class JSONRuntimeModelClientParser implements RuntimeModelClientParser {

    @Inject
    PropertyReplacementService replaceService;

    @Override
    public RuntimeModel parse(String jsonContent) {
        var properties = RuntimeModelJSONMarshaller.get().retrieveProperties(jsonContent);
        var newContent = replaceService.replace(jsonContent, properties);
        return RuntimeModelJSONMarshaller.get().fromJson(newContent);
    }

    @Override
    public boolean test(String content) {
        return content.trim().startsWith("{");
    }

}