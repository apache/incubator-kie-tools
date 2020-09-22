/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.shared.project;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ModuleService;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * KIE specific implementation of ModuleService
 */
@Remote
public interface KieModuleService
        extends ModuleService<KieModule> {

    KieModulePackages resolveModulePackages(final Module activeModule);

    /**
     *
     * @param activeModule Module from where to look for the pkg.
     * @param packageName Package name for example "org.test".
     * @return The package or null if package could not be resolved.
     */
    Package resolvePackage(final Module activeModule,
                           final String packageName);
}
