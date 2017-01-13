/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.basicset.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.backend.service.AbstractDefinitionSetService;
import org.kie.workbench.common.stunner.backend.service.ErraiDiagramMarshaller;
import org.kie.workbench.common.stunner.basicset.BasicSetResourceType;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;

@ApplicationScoped
public class BasicSetBackendService extends AbstractDefinitionSetService {

    private ErraiDiagramMarshaller erraiDiagramMarshaller;
    private BasicSetResourceType basicSetResourceType;

    protected BasicSetBackendService() {
        this(null,
             null);
    }

    @Inject
    public BasicSetBackendService(final ErraiDiagramMarshaller erraiDiagramMarshaller,
                                  final BasicSetResourceType basicSetResourceType) {
        super(erraiDiagramMarshaller);
        this.basicSetResourceType = basicSetResourceType;
    }

    @Override
    public DefinitionSetResourceType getResourceType() {
        return basicSetResourceType;
    }
}
