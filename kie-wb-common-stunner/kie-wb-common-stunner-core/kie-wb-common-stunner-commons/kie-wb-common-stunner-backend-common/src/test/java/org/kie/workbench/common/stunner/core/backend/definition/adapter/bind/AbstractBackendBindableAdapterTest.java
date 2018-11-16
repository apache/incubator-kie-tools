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

package org.kie.workbench.common.stunner.core.backend.definition.adapter.bind;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.AbstractBackendAdapterTest;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;

public abstract class AbstractBackendBindableAdapterTest extends AbstractBackendAdapterTest {

    protected Map<PropertyMetaTypes, Class> metaPropertyTypeClasses = new HashMap<>();
    protected Map<Class, Class> baseTypes = new HashMap<>();
    protected Map<Class, Set<String>> propertySetsFieldNames = new HashMap<>();
    protected Map<Class, Set<String>> propertiesFieldNames = new HashMap<>();
    protected Map<Class, Class> propertyGraphFactoryFieldNames = new HashMap<>();
    protected Map<Class, String> propertyIdFieldNames = new HashMap<>();
    protected Map<Class, String> propertyLabelsFieldNames = new HashMap<>();
    protected Map<Class, String> propertyTitleFieldNames = new HashMap<>();
    protected Map<Class, String> propertyCategoryFieldNames = new HashMap<>();
    protected Map<Class, String> propertyDescriptionFieldNames = new HashMap<>();
    protected Map<Class, String> propertyNameFields = new HashMap<>();

    public void setUp() {
        super.setup();
    }
}
