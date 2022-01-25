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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGeneratorManager;

@Singleton
public class EditableColumnGeneratorManagerImpl implements EditableColumnGeneratorManager {

    private Map<String, EditableColumnGenerator> generators = new HashMap<>();

    @PostConstruct
    public void init() {
        Collection<SyncBeanDef<EditableColumnGenerator>> beanDefs = IOC.getBeanManager().lookupBeans(EditableColumnGenerator.class);

        beanDefs.stream().map(beanDef -> beanDef.newInstance()).forEach(this::registerGenerator);
    }

    public void registerGenerator(EditableColumnGenerator generator) {
        for (String type : generator.getTypes()) {
            generators.put(type,
                           generator);
        }
    }

    @Override
    public EditableColumnGenerator getGenerator(String type) {
        return generators.get(type);
    }
}
