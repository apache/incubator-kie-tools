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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class HasNameUtilsTest {

    @Mock
    private HasName hasName;

    private final static String EXPRESSION_NAME = "expression name";

    @Test
    public void testSetName_WhenHaveNameObject() {

        final Name name = mock(Name.class);

        when(hasName.getName()).thenReturn(name);

        HasNameUtils.setName(hasName, EXPRESSION_NAME);

        verify(name).setValue(EXPRESSION_NAME);
    }

    @Test
    public void testSetExpressionName_WhenNameIsNull() {

        final ArgumentCaptor<Name> nameCaptor = ArgumentCaptor.forClass(Name.class);
        HasNameUtils.setName(hasName, EXPRESSION_NAME);

        verify(hasName).setName(nameCaptor.capture());

        final Name currentName = nameCaptor.getValue();

        assertEquals(EXPRESSION_NAME, currentName.getValue());
    }
}
