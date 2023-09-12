/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.api.definition;

import org.kie.workbench.common.dmn.api.property.dmn.Name;

/**
 * Implementations have a {@link Name} property.
 */
public interface HasName extends HasValue<Name> {

    /**
     * Returns the {@link Name}
     * Convenience method replicating {@link HasValue#getValue()} to honour DMN model semantics.
     * @return
     */
    Name getName();

    /**
     * Sets the {@link Name}
     * Convenience method replicating {@link HasValue#setValue(Object)} to honour DMN model semantics.
     * @param name
     */
    void setName(final Name name);

    /**
     * Returns the {@link Name}
     * @return
     */
    @Override
    default Name getValue() {
        return getName();
    }

    /**
     * Sets the {@link Name}
     * @param name
     */
    @Override
    default void setValue(final Name name) {
        setName(name);
    }

    HasName NOP = new HasName() {

        private final Name NAME = new Name();

        @Override
        public Name getName() {
            return NAME;
        }

        @Override
        public void setName(final Name name) {
            //Do nothing
        }
    };
}
