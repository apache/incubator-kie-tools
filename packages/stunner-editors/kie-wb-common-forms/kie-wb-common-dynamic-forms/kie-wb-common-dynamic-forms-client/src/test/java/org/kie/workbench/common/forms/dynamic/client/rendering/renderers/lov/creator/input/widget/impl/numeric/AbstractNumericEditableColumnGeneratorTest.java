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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.numeric;

import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.AbstractEditableColumnGeneratorTest;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractNumericEditableColumnGeneratorTest<TYPE, GENERATOR extends AbstractNumericEditableColumnGenerator<TYPE>> extends AbstractEditableColumnGeneratorTest<TYPE, GENERATOR> {

    @Mock
    protected TableEntry<TYPE> tableEntry;

    @Test
    public void testDoValidateNoErrors() {
        String correctFlatValue = getCorrectFlatValue();

        assertTrue(generator.doValidate(correctFlatValue, tableEntry, cellEditionHandler));

        verify(cellEditionHandler, never()).showValidationError(Mockito.<String>any());
    }

    @Test
    public void testDoValidateWithErrors() {
        String wrongFlatValue = getWrongFlatValue();

        assertFalse(generator.doValidate(wrongFlatValue, tableEntry, cellEditionHandler));

        verify(cellEditionHandler, times(1)).showValidationError(Mockito.<String>any());
    }

    @Test
    public void testDoConvert() {
        String correctFlatValue = getCorrectFlatValue();

        TYPE correctValue = getCorrectValue();

        assertEquals(correctValue, generator.doConvert(correctFlatValue));
    }

    protected abstract String getCorrectFlatValue();

    protected abstract String getWrongFlatValue();

    protected abstract TYPE getCorrectValue();

}
