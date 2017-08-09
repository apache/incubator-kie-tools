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
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

public class DMNColumnHeaderMetaData extends BaseHeaderMetaData {

    private Supplier<Optional<HasName>> supplier;

    private static final HasName NOP = new HasName() {

        @Override
        public Name getName() {
            return new Name("");
        }

        @Override
        public void setName(final Name name) {
            //Do nothing
        }
    };

    public DMNColumnHeaderMetaData(final Supplier<Optional<HasName>> supplier) {
        super("");
        this.supplier = supplier;
    }

    @Override
    public String getTitle() {
        return supplier.get().orElse(NOP).getName().getValue();
    }

    @Override
    public void setTitle(final String title) {
        supplier.get().orElse(NOP).getName().setValue(title);
    }
}