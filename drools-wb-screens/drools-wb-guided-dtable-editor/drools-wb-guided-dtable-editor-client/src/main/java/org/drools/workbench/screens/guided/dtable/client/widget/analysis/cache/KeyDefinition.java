/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.uberfire.commons.validation.PortablePreconditions;

public class KeyDefinition
        implements Comparable<KeyDefinition> {

    private final String  id;
    private       boolean canBeEmpty;
    private       boolean isValueList;

    private KeyDefinition( final String id ) {
        this.id = PortablePreconditions.checkNotNull( "id", id );
    }

    public String getId() {
        return id;
    }

    @Override
    public int compareTo( final KeyDefinition other ) {
        return id.compareTo( other.id );
    }

    public static Builder newKeyDefinition() {
        return new Builder();
    }

    public boolean canBeEmpty() {
        return canBeEmpty;
    }

    public boolean isValueList() {
        return isValueList;
    }

    public static class Builder {
        private String id;

        private boolean canBeEmpty = false;
        private boolean valueList  = false;

        public Builder withId( final String id ) {
            this.id = id;
            return this;
        }

        public KeyDefinition build() {
            final KeyDefinition keyDefinition = new KeyDefinition( id );
            keyDefinition.canBeEmpty = canBeEmpty;
            keyDefinition.isValueList = valueList;
            return keyDefinition;
        }

        public Builder canBeEmpty() {
            canBeEmpty = true;
            return this;
        }

        public Builder valueList() {
            valueList = true;
            return this;
        }


    }
}
