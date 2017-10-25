/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata.attribute;

import java.util.HashMap;
import java.util.Map;

public final class GeneratedAttributesUtil {

    private GeneratedAttributesUtil() {
    }

    /**
     * Remove attribute mappings specific to GeneratedAttributesView from the
     * map of all available file attributes.
     */
    public static Map<String, Object> cleanup(final Map<String, Object> _attrs) {
        final Map<String, Object> attrs = new HashMap<>(_attrs);

        attrs.replace(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME, null);

        return attrs;
    }
}
