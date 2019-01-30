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

package org.kie.workbench.common.dmn.api.definition.v1_1.common;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasTypeRefs;

import static java.util.Collections.emptyList;

public class HasTypeRefHelper {

    public static List<HasTypeRef> getNotNullHasTypeRefs(final HasTypeRef hasTypeRef) {

        final Optional<HasTypeRef> optional = Optional.ofNullable(hasTypeRef);

        return optional
                .map(HasTypeRef::getHasTypeRefs)
                .orElse(emptyList());
    }

    public static List<HasTypeRef> getFlatHasTypeRefs(final List<? extends HasTypeRefs> hasTypeRefList) {
        return hasTypeRefList
                .stream()
                .flatMap(typeRefs -> typeRefs.getHasTypeRefs().stream())
                .collect(Collectors.toList());
    }
}
