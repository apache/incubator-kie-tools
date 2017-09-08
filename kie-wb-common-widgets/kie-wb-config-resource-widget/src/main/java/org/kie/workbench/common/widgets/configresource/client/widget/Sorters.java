/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.configresource.client.widget;

import java.util.Comparator;
import java.util.function.Function;

import org.appformer.project.datamodel.imports.Import;

public class Sorters {

    public static Comparator<Import> sortByFQCN() {
        return Comparator.comparing(Import::getType);
    }

    public static Comparator<Import> sortBySourceThenFQCN(final Function<Import, Boolean> isExternalImport) {
        return Comparator.comparing(isExternalImport::apply).thenComparing(sortByFQCN());
    }
}
