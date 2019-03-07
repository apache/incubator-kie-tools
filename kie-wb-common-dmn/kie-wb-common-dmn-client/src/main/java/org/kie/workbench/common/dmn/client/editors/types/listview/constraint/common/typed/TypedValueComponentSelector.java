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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic.GenericSelector;

@Dependent
public class TypedValueComponentSelector {

    private final GenericSelector genericSelector;
    private final DateSelector dateSelector;

    @Inject
    public TypedValueComponentSelector(final GenericSelector genericSelector,
                                       final DateSelector dateSelector) {
        this.genericSelector = genericSelector;
        this.dateSelector = dateSelector;
    }

    public TypedValueSelector makeSelectorForType(final String type) {

        if (Objects.equals(BuiltInType.DATE.getName(), type)) {
            return dateSelector;
        }

        return genericSelector;
    }
}
