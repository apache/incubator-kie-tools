/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.definition.clone;

import java.util.AbstractMap;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

@ApplicationScoped
public class DeepCloneProcess extends AbstractCloneProcess {

    private final ClassUtils classUtils;

    protected DeepCloneProcess() {
        this(null, null, null);
    }

    @Inject
    public DeepCloneProcess(final FactoryManager factoryManager, final AdapterManager adapterManager, final ClassUtils classUtils) {
        super(factoryManager, adapterManager);
        this.classUtils = classUtils;
    }

    @Override
    public <S, T> T clone(S source, T target) {
        //the adapterManager.forDefinition().getProperties return a flattened set of properties (properties of propertySet as well)
        //in this way it could be a conflict with same property IDs, this could be improved by not retuning the flattened properties
        adapterManager.forDefinition().getProperties(source)
                .stream()
                .filter(p -> !adapterManager.forProperty().isReadOnly(p))
                .filter(p -> !Objects.equals(adapterManager.forProperty().getValue(p), adapterManager.forProperty().getDefaultValue(p)))
                .map(p -> {
                    String id = adapterManager.forProperty().getId(p);
                    Optional<?> propertyTarget = adapterManager.forDefinition().getProperties(target)
                            .stream()
                            .filter(prop -> Objects.equals(adapterManager.forProperty().getId(prop), id))
                            .findFirst();
                    return propertyTarget.isPresent() ? new AbstractMap.SimpleEntry(p, propertyTarget.get()) : null;
                })
                .filter(Objects::nonNull)
                .filter(entry -> isAllowedToClone(adapterManager.forProperty().getValue(entry.getKey())))
                .forEach(entry -> {
                    Object value = adapterManager.forProperty().getValue(entry.getKey());
                    adapterManager.forProperty().setValue(entry.getValue(), value);
                });

        return target;
    }

    private boolean isAllowedToClone(Object value) {
        return Objects.nonNull(value) &&
                ((value instanceof String) || (classUtils.isPrimitiveClass(value.getClass())));
    }
}
