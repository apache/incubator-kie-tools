/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

public class HasNameUtils {

    private HasNameUtils() {
        // Private constructor for utility class with only static members
    }

    public static void setName(final HasName hasName,
                               final String expressionName) {
        final Name name;
        if (Objects.isNull(hasName.getName())) {
            name = new Name();
            hasName.setName(name);
        } else {
            name = hasName.getName();
        }
        name.setValue(expressionName);
    }
}
