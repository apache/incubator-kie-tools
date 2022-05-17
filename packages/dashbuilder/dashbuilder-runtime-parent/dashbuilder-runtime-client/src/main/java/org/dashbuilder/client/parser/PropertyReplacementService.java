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

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PropertyReplacementService {

    private static String PROPERTY_KEY = "VALUE";
    private static String PROPERTY_REPLACEMENT_PATTERN = "\\$\\{" + PROPERTY_KEY + "\\}";

    public String replace(String content, Map<String, String> properties) {
        var contentSb = new StringBuffer(content);
        properties.forEach((k, v) -> {

            // V must be escaped to be a JSON String
            var replaceToken = PROPERTY_REPLACEMENT_PATTERN.replace(PROPERTY_KEY, k);
            var replacedContent = contentSb.toString().replaceAll(replaceToken, v);
            contentSb.replace(0, contentSb.length(), replacedContent);
        });

        return contentSb.toString();
    }

}
