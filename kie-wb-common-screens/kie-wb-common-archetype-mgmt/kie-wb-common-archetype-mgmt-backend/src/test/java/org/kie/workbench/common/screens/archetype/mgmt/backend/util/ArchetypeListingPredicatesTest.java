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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypeListingPredicatesTest {

    @Test
    public void matchAllTest() {
        final List<Archetype> archetypes = Collections.nCopies(5, mock(Archetype.class));

        final List<Archetype> filteredList = archetypes.stream()
                .filter(ArchetypeListingPredicates.matchAll())
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(5);
    }

    @Test
    public void matchGavTest() {
        final Function<GAV, Archetype> createArchetypeMock = gav -> {
            final Archetype archetype = mock(Archetype.class);
            doReturn(gav).when(archetype).getGav();
            return archetype;
        };

        final GAV expectedGav = createGav("myArtifact1");

        final List<Archetype> archetypes = Arrays.asList(createArchetypeMock.apply(expectedGav),
                                                         createArchetypeMock.apply(createGav("myArtifact2")),
                                                         createArchetypeMock.apply(createGav("myArtifact3")),
                                                         createArchetypeMock.apply(createGav("myArtifact4")));

        final List<Archetype> filteredList = archetypes.stream()
                .filter(ArchetypeListingPredicates.matchGav(expectedGav))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(1);
        assertThat(filteredList.get(0).getGav()).isSameAs(expectedGav);
    }

    @Test
    public void matchSearchFilterTest() {
        final Function<String, Archetype> createArchetypeMock = toString -> {
            final Archetype archetype = mock(Archetype.class);
            doReturn(toString).when(archetype).toString();
            return archetype;
        };

        final List<Archetype> archetypes = Arrays.asList(createArchetypeMock.apply("Archetype 1"),
                                                         createArchetypeMock.apply("Archetype 2"),
                                                         createArchetypeMock.apply("3"));

        final List<Archetype> filteredList = archetypes.stream()
                .filter(ArchetypeListingPredicates.matchSearchFilter("Archetype",
                                                                     elem -> elem.toString().toLowerCase()))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    @Test
    public void matchStatusTest() {
        final Function<ArchetypeStatus, Archetype> createArchetypeMock = status -> {
            final Archetype archetype = mock(Archetype.class);
            doReturn(status).when(archetype).getStatus();
            return archetype;
        };

        final List<Archetype> archetypes = Arrays.asList(createArchetypeMock.apply(ArchetypeStatus.VALID),
                                                         createArchetypeMock.apply(ArchetypeStatus.INVALID),
                                                         createArchetypeMock.apply(ArchetypeStatus.VALID),
                                                         createArchetypeMock.apply(ArchetypeStatus.VALID),
                                                         createArchetypeMock.apply(ArchetypeStatus.INVALID));

        final List<Archetype> filteredList = archetypes.stream()
                .filter(ArchetypeListingPredicates.matchStatus(ArchetypeStatus.VALID))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(3);
    }

    @Test
    public void matchSearchFilterAndStatusListTest() {
        final BiFunction<String, ArchetypeStatus, Archetype> createArchetypeMock = (toString, status) -> {
            final Archetype archetype = mock(Archetype.class);
            doReturn(toString).when(archetype).toString();
            doReturn(status).when(archetype).getStatus();
            return archetype;
        };

        final List<Archetype> changeRequests =
                Arrays.asList(createArchetypeMock.apply("Archetype 1", ArchetypeStatus.VALID),
                              createArchetypeMock.apply("Archetype 2", ArchetypeStatus.VALID),
                              createArchetypeMock.apply("Archetype 3", ArchetypeStatus.INVALID));

        final List<Archetype> filteredList = changeRequests.stream()
                .filter(ArchetypeListingPredicates.matchSearchFilterAndStatus("Archetype",
                                                                              elem -> elem.toString().toLowerCase(),
                                                                              ArchetypeStatus.VALID))
                .collect(Collectors.toList());

        assertThat(filteredList).hasSize(2);
    }

    private GAV createGav(final String artifactId) {
        return new GAV("com.myspace",
                       artifactId,
                       "1.0.0");
    }
}