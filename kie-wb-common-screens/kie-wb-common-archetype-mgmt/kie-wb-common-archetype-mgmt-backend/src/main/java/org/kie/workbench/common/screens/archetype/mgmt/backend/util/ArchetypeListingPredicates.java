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

package org.kie.workbench.common.screens.archetype.mgmt.backend.util;

import java.util.function.Function;
import java.util.function.Predicate;

import org.guvnor.common.services.project.model.GAV;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;

public class ArchetypeListingPredicates {

    private ArchetypeListingPredicates() {

    }

    public static Predicate<Archetype> matchAll() {
        return elem -> true;
    }

    public static Predicate<Archetype> matchGav(final GAV gav) {
        return elem -> elem.getGav().equals(gav);
    }

    public static Predicate<Archetype> matchSearchFilter(final String searchFilter,
                                                         final Function<Archetype, String> searchableElementFunction) {
        return elem -> searchableElementFunction.apply(elem).contains(searchFilter.toLowerCase());
    }

    public static Predicate<Archetype> matchStatus(final ArchetypeStatus status) {
        return elem -> elem.getStatus() == status;
    }

    public static Predicate<Archetype> matchSearchFilterAndStatus(final String searchFilter,
                                                                  final Function<Archetype, String> searchableElementFunction,
                                                                  final ArchetypeStatus status) {
        return matchSearchFilter(searchFilter,
                                 searchableElementFunction)
                .and(matchStatus(status));
    }
}
