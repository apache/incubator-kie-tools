/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.contributors;

import java.util.Comparator;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Contributor {

    private String username;

    private ContributorType type;

    public Contributor() {
    }

    public Contributor(@MapsTo("username") final String username,
                       @MapsTo("type") final ContributorType type) {
        this.username = username;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public ContributorType getType() {
        return type;
    }

    public void setType(ContributorType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contributor)) {
            return false;
        }
        Contributor that = (Contributor) o;
        return Objects.equals(username,
                              that.username) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = ~~result;
        return result;
    }

    public static final Comparator<Contributor> COMPARATOR = (o1, o2) -> {
        if (o1.getType().equals(o2.getType())) {
            return o1.getUsername().toUpperCase().compareTo(o2.getUsername().toUpperCase());
        } else {
            if (ContributorType.PRIORITY_ORDER.indexOf(o1.getType()) < ContributorType.PRIORITY_ORDER.indexOf(o2.getType())) {
                return -1;
            } else {
                return 1;
            }
        }
    };
}
