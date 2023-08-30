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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;

@ApplicationScoped
public class IncludedModelsIndex {

    private final Map<String, Import> index = new HashMap<>();

    public void index(final BaseIncludedModelActiveRecord includedModel,
                      final Import anImport) {
        index.put(key(includedModel), anImport);
    }

    public Import getImport(final BaseIncludedModelActiveRecord includedModel) {
        return index.get(key(includedModel));
    }

    public Collection<Import> getIndexedImports() {
        return index.values();
    }

    public void clear() {
        index.clear();
    }

    int size() {
        return index.size();
    }

    private String key(final BaseIncludedModelActiveRecord includedModel) {
        return includedModel.getUUID();
    }
}
