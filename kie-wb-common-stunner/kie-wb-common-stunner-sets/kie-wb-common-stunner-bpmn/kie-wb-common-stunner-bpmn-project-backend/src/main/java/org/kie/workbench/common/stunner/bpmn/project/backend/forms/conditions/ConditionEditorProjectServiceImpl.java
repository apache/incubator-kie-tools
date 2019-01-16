/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.forms.conditions;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.ConditionEditorServiceImpl;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.uberfire.backend.vfs.Path;

@Service
@Specializes
@ApplicationScoped
public class ConditionEditorProjectServiceImpl
        extends ConditionEditorServiceImpl
        implements ConditionEditorService {

    private final KieModuleService moduleService;

    private final ModuleClassLoaderHelper moduleClassLoaderHelper;

    private ConditionEditorProjectServiceImpl() {
        //Empty constructor for proxying
        this(null, null);
    }

    @Inject
    public ConditionEditorProjectServiceImpl(final KieModuleService moduleService,
                                             final ModuleClassLoaderHelper moduleClassLoaderHelper) {
        this.moduleService = moduleService;
        this.moduleClassLoaderHelper = moduleClassLoaderHelper;
    }

    protected ClassLoader resolveClassLoader(Path path) {
        KieModule module = moduleService.resolveModule(path);
        return moduleClassLoaderHelper.getModuleClassLoader(module);
    }
}
