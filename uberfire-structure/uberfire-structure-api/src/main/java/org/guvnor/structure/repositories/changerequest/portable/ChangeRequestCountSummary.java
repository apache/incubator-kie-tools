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

package org.guvnor.structure.repositories.changerequest.portable;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ChangeRequestCountSummary {

    private Integer total;
    private Integer open;

    public ChangeRequestCountSummary(@MapsTo("total") final Integer total,
                                     @MapsTo("open") final Integer open) {
        this.total = total;
        this.open = open;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getOpen() {
        return open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeRequestCountSummary that = (ChangeRequestCountSummary) o;
        return total.equals(that.total) &&
                open.equals(that.open);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, open);
    }
}
