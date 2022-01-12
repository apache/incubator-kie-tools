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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.property.dmn.DefaultValueUtilities;

public class ContextEntryDefaultValueUtilities {

    public static final String PREFIX = ContextEntry.class.getSimpleName() + "-";

    public static String getNewContextEntryName(final Context context) {
        return PREFIX + DefaultValueUtilities.getMaxUnusedIndex(context.getContextEntry().stream()
                                                                        .map(ContextEntry::getVariable)
                                                                        .filter(informationItem -> informationItem != null)
                                                                        .map(InformationItem::getName)
                                                                        .filter(name -> name != null)
                                                                        .map(Name::getValue)
                                                                        .collect(Collectors.toList()),
                                                                PREFIX);
    }
}
