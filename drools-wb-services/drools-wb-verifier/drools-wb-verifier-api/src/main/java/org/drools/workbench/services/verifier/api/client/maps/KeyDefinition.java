/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.api.client.maps;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class KeyDefinition
        implements Comparable<KeyDefinition> {

    private final String id;
    private boolean updatable;

    private KeyDefinition(final String id) {
        this.id = checkNotNull("id",
                               id);
    }

    public String getId() {
        return id;
    }

    @Override
    public int compareTo(final KeyDefinition other) {
        return id.compareTo(other.id);
    }

    public static Builder newKeyDefinition() {
        return new Builder();
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public static class Builder {

        private String id;

        private boolean updatable = false;

        public Builder withId(final String id) {
            this.id = checkNotNull("id",
                                   id);
            return this;
        }

        public KeyDefinition build() {
            checkNotNull("id",
                         id);
            final KeyDefinition keyDefinition = new KeyDefinition(id);
            keyDefinition.updatable = updatable;
            return keyDefinition;
        }

        public Builder updatable() {
            updatable = true;
            return this;
        }
    }
}
