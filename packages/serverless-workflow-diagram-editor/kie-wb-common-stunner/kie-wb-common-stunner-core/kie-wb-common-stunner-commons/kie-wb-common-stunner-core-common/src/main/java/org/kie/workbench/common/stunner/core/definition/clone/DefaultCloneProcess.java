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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;

@ApplicationScoped
public class DefaultCloneProcess extends AbstractCloneProcess {

    public DefaultCloneProcess() {
        this(null, null);
    }

    @Inject
    public DefaultCloneProcess(FactoryManager factoryManager, AdapterManager adapterManager) {
        super(factoryManager, adapterManager);
    }

    @Override
    public <S, T> T clone(S source, T target) {
        final DefinitionAdapter<Object> definitionAdapter = adapterManager.forDefinition();
        final String namePropertyField = definitionAdapter.getMetaPropertyField(source, PropertyMetaTypes.NAME);
        final String targetNamePropertyField = definitionAdapter.getMetaPropertyField(target, PropertyMetaTypes.NAME);
        final Object nameProperty = definitionAdapter.getProperty(source, namePropertyField).get();
        final Object targetNameProperty = definitionAdapter.getProperty(target, targetNamePropertyField).get();
        final Object namePropertyValue = adapterManager.forProperty().getValue(nameProperty);

        adapterManager.forProperty().setValue(targetNameProperty, namePropertyValue);
        return target;
    }
}
