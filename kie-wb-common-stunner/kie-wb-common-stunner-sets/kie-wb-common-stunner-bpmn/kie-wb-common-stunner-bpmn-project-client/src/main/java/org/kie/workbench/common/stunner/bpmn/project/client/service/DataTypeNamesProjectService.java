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

package org.kie.workbench.common.stunner.bpmn.project.client.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.project.service.DataTypesService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class DataTypeNamesProjectService implements DataTypeNamesService {

    private final Promises promises;
    private final Caller<DataTypesService> dataTypesServiceCaller;

    @Inject
    public DataTypeNamesProjectService(final Promises promises,
                                       final Caller<DataTypesService> dataTypesServiceCaller) {
        this.promises = promises;
        this.dataTypesServiceCaller = dataTypesServiceCaller;
    }

    @Override
    public Promise<List<String>> call(final Path path) {
        return promises.promisify(dataTypesServiceCaller,
                                  s -> {
                                      s.getDataTypeNames(path);
                                  });
    }
}
