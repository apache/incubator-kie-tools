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


package org.kie.workbench.common.stunner.core;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TestingSimpleDomainObject implements DomainObject {

    public static final String NAME = "name";

    public static class SomePropertyBean {

    }

    private final SomePropertyBean nameProperty;

    @SuppressWarnings("unchecked")
    public TestingSimpleDomainObject(final TestingGraphMockHandler graphMockHandler) {
        nameProperty = new SomePropertyBean();
        when(graphMockHandler.getDefinitionAdapter().accepts(eq(TestingSimpleDomainObject.class))).thenReturn(true);
        when(graphMockHandler.getDefinitionAdapter().accepts(eq(SomePropertyBean.class))).thenReturn(false);
        when(graphMockHandler.getPropertyAdapter().accepts(eq(SomePropertyBean.class))).thenReturn(true);
        when(graphMockHandler.getPropertyAdapter().accepts(eq(TestingSimpleDomainObject.class))).thenReturn(false);
        when(graphMockHandler.getPropertyAdapter().getId(eq(nameProperty))).thenReturn(NAME);
        when(graphMockHandler.getDefinitionAdapter().getMetaPropertyField(eq(this), eq(PropertyMetaTypes.NAME))).thenReturn("name");
        when(graphMockHandler.getDefinitionAdapter().getProperty(eq(this), eq("name"))).thenReturn((Optional) Optional.of(nameProperty));
    }

    public SomePropertyBean getNameProperty() {
        return nameProperty;
    }

    @Override
    public String getDomainObjectUUID() {
        return getClass().getName();
    }

    @Override
    public String getDomainObjectNameTranslationKey() {
        return "";
    }
}
