/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.backend.validation.FileNameValidator;
import org.uberfire.ext.editor.commons.backend.validation.ValidationUtils;

/**
 * Package Name validation
 */
@ApplicationScoped
public class ModuleNameValidator
        implements FileNameValidator {

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    private KieModuleService moduleService;

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean accept(final String fileName) {
        return false;
    }

    @Override
    public boolean accept(final Path path) {
        final Module module = moduleService.resolveModule(path);
        return module.getRootPath().equals(path);
    }

    @Override
    public boolean isValid(final String value) {
        return ValidationUtils.isFileName(value);
    }
}
