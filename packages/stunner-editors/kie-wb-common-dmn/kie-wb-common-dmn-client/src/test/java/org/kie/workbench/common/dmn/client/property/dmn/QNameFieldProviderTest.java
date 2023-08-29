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

package org.kie.workbench.common.dmn.client.property.dmn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class QNameFieldProviderTest {

    @Mock
    private TypeInfo typeInfo;

    private QNameFieldProvider provider;

    @Before
    public void setup() {
        this.provider = new QNameFieldProvider();
    }

    @Test
    public void testGetPriority() {
        assertEquals(QNameFieldProvider.PRIORITY,
                     provider.getPriority());
    }

    @Test
    public void testDoRegisterFields() {
        provider.doRegisterFields();

        assertTrue(provider.supports(QName.class));
        assertFalse(provider.supports(String.class));
    }

    @Test
    public void testCreateFieldByType() {
        final QNameFieldDefinition definition1 = provider.createFieldByType(typeInfo);
        final QNameFieldDefinition definition2 = provider.createFieldByType(typeInfo);
        assertNotEquals(definition1, definition2);
    }

    @Test
    public void testGetFieldType() {
        assertEquals(QNameFieldType.class, provider.getFieldType());
    }

    @Test
    public void testGetFieldTypeName() {
        assertEquals(QNameFieldDefinition.FIELD_TYPE.getTypeName(), provider.getFieldTypeName());
    }

    @Test
    public void testGetDefaultField() {
        final QNameFieldDefinition definition1 = provider.getDefaultField();
        final QNameFieldDefinition definition2 = provider.getDefaultField();
        assertNotEquals(definition1, definition2);
    }
}
