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

package org.kie.workbench.common.dmn.backend.editors.common;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DMNIncludedNodeFactory {

    DMNIncludedNode makeDMNIncludeModel(final Path path,
                                        final DMNIncludedModel includeModel,
                                        final DRGElement drgElement) {

        final String fileName = path.getFileName();
        final String modelName = includeModel.getModelName();
        final String drgElementId = drgElement.getId().getValue();
        final String drgElementName = drgElement.getName().getValue();
        final Class<? extends DRGElement> drgElementClass = drgElement.getClass();

        return new DMNIncludedNode(fileName, modelName, drgElementId, drgElementName, drgElementClass);
    }
}
