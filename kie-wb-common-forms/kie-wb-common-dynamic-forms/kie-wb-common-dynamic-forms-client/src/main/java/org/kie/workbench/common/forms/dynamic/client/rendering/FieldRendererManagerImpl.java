/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Dependent
public class FieldRendererManagerImpl implements FieldRendererManager {

    private ManagedInstance<FieldRenderer> renderers;

    protected static boolean initialized = false;

    protected static Map<String, Class<? extends FieldRenderer>> availableRenderers = new HashMap<>();

    protected static Map<Class<? extends FieldDefinition>, Class<? extends FieldRenderer>> fieldDefinitionRemderers = new HashMap<>();

    @Inject
    public FieldRendererManagerImpl(ManagedInstance<FieldRenderer> renderers) {
        this.renderers = renderers;
    }

    @PostConstruct
    public void init() {
        if (!initialized) {
            registerRenderers(IOC.getBeanManager().lookupBeans(FieldRenderer.class));
            initialized = true;
        }
    }

    protected void registerRenderers(Collection<SyncBeanDef<FieldRenderer>> renderers) {
        PortablePreconditions.checkNotNull("renderers",
                                           renderers);
        renderers.forEach(rendererDef -> {
            FieldRenderer renderer = rendererDef.getInstance();
            if (renderer != null) {
                if (renderer instanceof FieldDefinitionFieldRenderer) {
                    fieldDefinitionRemderers.put(((FieldDefinitionFieldRenderer) renderer).getSupportedFieldDefinition(),
                                                 (Class<? extends FieldRenderer>) rendererDef.getBeanClass());
                } else {
                    availableRenderers.put(renderer.getSupportedCode(),
                                           (Class<? extends FieldRenderer>) rendererDef.getBeanClass());
                }
            }
        });
    }

    @Override
    public FieldRenderer getRendererForField(FieldDefinition fieldDefinition) {
        Class<? extends FieldRenderer> rendererClass = fieldDefinitionRemderers.get(fieldDefinition.getClass());

        if (rendererClass == null) {
            rendererClass = availableRenderers.get(fieldDefinition.getFieldType().getTypeName());
        }

        if (rendererClass != null) {
            return renderers.select(rendererClass).get();
        }
        return null;
    }

    @PreDestroy
    public void destroy() {
        renderers.destroyAll();
    }
}
