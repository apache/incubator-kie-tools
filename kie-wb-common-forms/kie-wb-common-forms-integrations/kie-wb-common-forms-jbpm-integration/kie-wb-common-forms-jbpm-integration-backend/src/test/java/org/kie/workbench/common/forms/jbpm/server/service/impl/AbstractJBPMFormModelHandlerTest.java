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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public abstract class AbstractJBPMFormModelHandlerTest {

    public static final String PROCESS_ID = "processId";

    @Mock
    protected BPMFinderService finderService;

    @Mock
    protected KieModuleService moduleService;

    @Mock
    protected KieModule module;

    @Mock
    protected ModuleClassLoaderHelper moduleClassLoaderHelper;

    @Mock
    protected ClassLoader classLoader;

    @Mock
    protected Path path;

    protected List<ModelProperty> propertyList = new ArrayList();

    public void init() throws ClassNotFoundException {
        when(moduleService.resolveModule(any())).thenReturn(module);
        when(moduleClassLoaderHelper.getModuleClassLoader(module)).thenReturn(classLoader);
        when(classLoader.loadClass(any())).thenAnswer((Answer<Class>) invocation -> String.class);

        propertyList.add(new ModelPropertyImpl("name", new TypeInfoImpl(String.class.getName())));
        propertyList.add(new ModelPropertyImpl("age", new TypeInfoImpl(Integer.class.getName())));
    }
}
