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

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes.DataTypeCache;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DataTypeNamesStandaloneService implements DataTypeNamesService {

    Set<String> dataTypesSet = new HashSet<>();

    boolean cacheRead = false;
    @Inject
    DataTypeCache cache;

    private static Set<String> simpleDataTypes = new HashSet<>(Arrays.asList("Boolean",
                                                                      "Float",
                                                                      "Integer",
                                                                      "Object",
                                                                      "String"));

    @Override
    public Promise<List<String>> call(final Path path) {
        if (!cacheRead && cache != null) {
            cache.getCachedDataTypes().removeAll(simpleDataTypes);
            dataTypesSet.addAll(cache.getCachedDataTypes());
            cacheRead = true;
        }

        return Promise.resolve(new ArrayList<>(dataTypesSet));
    }

    @Override
    public void add(String value, String oldValue) {

        if (simpleDataTypes.contains(value)) {
            return;
        }

        if (dataTypesSet.contains(oldValue)) {
            dataTypesSet.remove(oldValue);
        }

        dataTypesSet.add(value);
    }
}
