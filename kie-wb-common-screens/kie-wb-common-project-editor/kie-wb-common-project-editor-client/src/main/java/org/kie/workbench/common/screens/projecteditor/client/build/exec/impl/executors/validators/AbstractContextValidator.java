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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.validators;

import org.guvnor.common.services.project.model.Module;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.ContextValidator;

public abstract class AbstractContextValidator implements ContextValidator {

    @Override
    public void validate(BuildExecutionContext context) {
        PortablePreconditions.checkNotNull("context", context);
        PortablePreconditions.checkNotNull("context.module", context.getModule());

        validateModule(context.getModule());
    }

    protected abstract void validateModule(Module module);
}
