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

package org.kie.workbench.common.screens.archetype.mgmt.shared.model;

import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class PaginatedArchetypeList {

    private final List<Archetype> archetypes;

    private final Integer pageNumber;

    private final Integer pageSize;

    private final Integer total;

    public PaginatedArchetypeList(@MapsTo("archetypes") final List<Archetype> archetypes,
                                  @MapsTo("pageNumber") final Integer pageNumber,
                                  @MapsTo("pageSize") final Integer pageSize,
                                  @MapsTo("total") final Integer total) {
        this.archetypes = checkNotNull("archetypes",
                                       archetypes);
        this.pageNumber = checkNotNull("pageNumber",
                                       pageNumber);
        this.pageSize = checkNotNull("pageSize",
                                     pageSize);
        this.total = checkNotNull("total",
                                  total);
    }

    public List<Archetype> getArchetypes() {
        return archetypes;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public boolean isEmpty() {
        return total == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaginatedArchetypeList that = (PaginatedArchetypeList) o;
        return archetypes.equals(that.archetypes) &&
                pageNumber.equals(that.pageNumber) &&
                pageSize.equals(that.pageSize) &&
                total.equals(that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(archetypes, pageNumber, pageSize, total);
    }
}
