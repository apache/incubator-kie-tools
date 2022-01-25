/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.editors.types;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

public class BuiltInTypeUtils {

    public static boolean isBuiltInType(final String type) {
        return builtInTypeNames()
                .anyMatch(builtInTypeName -> Objects.equals(builtInTypeName, type));
    }

    public static Optional<BuiltInType> findBuiltInTypeByName(final String name) {
        return getBuiltInTypeValuesStream()
                .filter(builtInType -> Arrays.asList(builtInType.getNames()).contains(name))
                .findAny();
    }

    private static Stream<String> builtInTypeNames() {
        return getBuiltInTypeValuesStream().map(BuiltInType::getNames).flatMap(Arrays::stream);
    }

    private static Stream<BuiltInType> getBuiltInTypeValuesStream() {
        return Stream.of(BuiltInType.values());
    }
}
