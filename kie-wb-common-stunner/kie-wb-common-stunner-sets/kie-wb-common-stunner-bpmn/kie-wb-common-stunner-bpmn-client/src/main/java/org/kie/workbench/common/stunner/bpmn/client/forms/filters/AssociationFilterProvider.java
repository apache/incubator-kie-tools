/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Collection;
import java.util.Collections;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

public class AssociationFilterProvider implements StunnerFormElementFilterProvider {

    public AssociationFilterProvider() {
    }

    @Override
    public Class<?> getDefinitionType() {
        return Association.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<FormElementFilter> provideFilters(String elementUUID,
                                                        Object definition) {
        FormElementFilter nameFilter = new FormElementFilter("general.name",
                                                             o -> false);
        return Collections.singletonList(nameFilter);
    }
}