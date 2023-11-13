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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.FormGenerationContext;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.Container;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EmbedsForm;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractEmbeddedFormsInitializerTest<FIELD extends FieldDefinition & EmbedsForm, INITIALIZER extends AbstractEmbeddedFormsInitializer<FIELD>> {

    @Mock
    FieldElement fieldElement;

    @Mock
    FormGenerationContext context;

    @Mock
    I18nHelper i18nHelper;

    FIELD field;

    Map<String, String> fieldElementParams = new HashMap<>();

    INITIALIZER initializer;

    @Before
    public void setUp() throws Exception {
        initializer = getInitializer();
        field = spy(getField());
        when(fieldElement.getParams()).thenReturn(fieldElementParams);
        when(context.getI18nHelper()).thenReturn(i18nHelper);
    }

    abstract INITIALIZER getInitializer();

    abstract FIELD getField();

    @Test
    public void testWithoutContainer() {
        initializer.initialize(field, fieldElement, context);

        verify(field, atLeastOnce()).getStandaloneClassName();

        verify(field, never()).setContainer(any());

        assertEquals(Container.FIELD_SET, field.getContainer());
    }

    @Test
    public void testWithWrongContainer() {
        fieldElementParams.put(AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM, "wrong");

        initializer.initialize(field, fieldElement, context);

        verify(field, atLeastOnce()).getStandaloneClassName();

        verify(field, never()).setContainer(any());

        assertEquals(Container.FIELD_SET, field.getContainer());
    }

    @Test
    public void testWithFieldSetContainer() {
        fieldElementParams.put(AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM, Container.FIELD_SET.name());

        initializer.initialize(field, fieldElement, context);

        verify(field, atLeastOnce()).getStandaloneClassName();

        verify(field).setContainer(Container.FIELD_SET);

        assertEquals(Container.FIELD_SET, field.getContainer());
    }

    @Test
    public void testWithCollapsibleContainer() {
        fieldElementParams.put(AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM, Container.COLLAPSIBLE.name());

        initializer.initialize(field, fieldElement, context);

        verify(field, atLeastOnce()).getStandaloneClassName();

        verify(field).setContainer(Container.COLLAPSIBLE);

        assertEquals(Container.COLLAPSIBLE, field.getContainer());
    }
}
