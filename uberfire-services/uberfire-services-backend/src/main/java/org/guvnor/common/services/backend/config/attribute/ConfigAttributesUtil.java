/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.config.attribute;

import java.util.HashMap;
import java.util.Map;

import static org.kie.soup.commons.validation.Preconditions.checkNotEmpty;

/**
 *
 */
public final class ConfigAttributesUtil {

    private ConfigAttributesUtil() {

    }

    public static Map<String, Object> toMap(final ConfigAttributes attrs,
                                            final String... attributes) {
        return new HashMap<String, Object>() {{
            for (final String attribute : attributes) {
                checkNotEmpty("attribute",
                              attribute);

                if (attribute.equals("*")) {
                    break;
                }
            }
        }};
    }

    private static String buildAttrName(final String title,
                                        final int i) {
        return title + "[" + i + "]";
    }
}
