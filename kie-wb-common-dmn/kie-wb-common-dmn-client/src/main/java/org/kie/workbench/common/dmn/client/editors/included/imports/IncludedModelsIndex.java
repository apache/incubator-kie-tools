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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;

@ApplicationScoped
public class IncludedModelsIndex {

    private final Map<String, Import> index = new HashMap<>();

    public void index(final IncludedModel includedModel,
                      final Import anImport) {
        index.put(key(includedModel), anImport);
    }

    public Import getImport(final IncludedModel includedModel) {
        return index.get(key(includedModel));
    }

    public void clear() {
        index.clear();
    }

    int size() {
        return index.size();
    }

    private String key(final IncludedModel includedModel) {
        return includedModel.getUUID();
    }
}
