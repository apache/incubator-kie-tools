/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static org.junit.Assert.*;

public class DMNColumnHeaderMetaDataTest {

    private Optional<HasName> hasName;

    private DMNColumnHeaderMetaData header;

    @Before
    public void setup() {
        this.header = new DMNColumnHeaderMetaData(() -> hasName);
    }

    @Test
    public void checkGetTitleWithHasName() {
        hasName = Optional.of(new MockHasName("name"));
        assertEquals("name",
                     header.getTitle());
    }

    @Test
    public void checkGetTitleWithoutHasName() {
        hasName = Optional.empty();
        assertEquals("",
                     header.getTitle());
    }

    @Test
    public void checkSetTitleWithHasName() {
        final HasName mockHasName = new MockHasName("name");
        hasName = Optional.of(mockHasName);
        header.setTitle("new-name");
        assertEquals("new-name",
                     mockHasName.getName().getValue());
    }

    @Test
    public void checkSetTitleWithoutHasName() {
        final HasName mockHasName = new MockHasName("name");
        hasName = Optional.empty();
        header.setTitle("new-name");
        assertEquals("name",
                     mockHasName.getName().getValue());
    }

    private static class MockHasName implements HasName {

        private Name name;

        MockHasName(final String name) {
            this.name = new Name(name);
        }

        @Override
        public Name getName() {
            return name;
        }

        @Override
        public void setName(final Name name) {
            this.name = name;
        }
    }
}
