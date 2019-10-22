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

package org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds;

import javax.enterprise.inject.Specializes;

import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.backend.editors.common.IncludedModelFactory;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.uberfire.backend.vfs.Path;

@Specializes
public class MockDMNIncludedModelFactory extends IncludedModelFactory {

    public MockDMNIncludedModelFactory() {
        super(null, null, null, null, null);
    }

    @Override
    public DMNIncludedModel create(final Path dmnModelPath,
                                   final Path includedModelPath) throws DMNIncludeModelCouldNotBeCreatedException {
        throw new DMNIncludeModelCouldNotBeCreatedException("Not available in kogito");
    }
}

