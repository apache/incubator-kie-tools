/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.definition.clone;

import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

@ApplicationScoped
public class DeepCloneProcess extends AbstractCloneProcess implements IDeepCloneProcess {

    private final ClassUtils classUtils;

    protected DeepCloneProcess() {
        this(null,
             null,
             null);
    }

    @Inject
    public DeepCloneProcess(final FactoryManager factoryManager,
                            final AdapterManager adapterManager,
                            final ClassUtils classUtils) {
        super(factoryManager,
              adapterManager);
        this.classUtils = classUtils;
    }

    @Override
    @SuppressWarnings("all")
    public <S, T> T clone(final S source,
                          final T target) {
        final AdapterRegistry adapters = adapterManager.registry();
        final DefinitionAdapter<Object> sourceDefinitionAdapter = adapters.getDefinitionAdapter(source.getClass());
        for (String field : sourceDefinitionAdapter.getPropertyFields(source)) {
            Optional<?> property = sourceDefinitionAdapter.getProperty(source, field);
            property.ifPresent(p -> {
                final Object value = adapters.getPropertyAdapter(p.getClass()).getValue(p);
                if (null != value && isAllowedToClone(value)) {
                    Optional<?> targetProperty = adapters.getDefinitionAdapter(target.getClass()).getProperty(target, field);
                    targetProperty.ifPresent(tp -> {
                        final PropertyAdapter tpa = adapters.getPropertyAdapter(tp.getClass());
                        tpa.setValue(tp, value);
                    });
                }
            });
        }
        return target;
    }

    private boolean isAllowedToClone(Object value) {
        return Objects.nonNull(value) && (isSimpleValue(value));
    }

    private boolean isSimpleValue(Object value) {
        return (value instanceof String) || classUtils.isPrimitiveClass(value.getClass());
    }
}
