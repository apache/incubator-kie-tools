/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.RootElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class EventPropertyWriterTest {

    protected Event event;
    protected final static String ERROR_CODE = "ERROR_CODE";
    protected final static String elementId = "MY_ID";
    protected EventPropertyWriter propertyWriter;

    @Test
    public void testAddEmptyError() {
        final ArgumentCaptor<RootElement> captor = ArgumentCaptor.forClass(RootElement.class);

        ErrorRef errorRef = new ErrorRef();
        propertyWriter.addError(errorRef);
        ErrorEventDefinition definition = getErrorDefinition();
        assertNull(definition.getErrorRef().getErrorCode());
        assertFalse(definition.getErrorRef().getId().isEmpty());

        verify(propertyWriter).addRootElement(captor.capture());
        Error error = (Error) captor.getValue();

        assertNull(error.getErrorCode());
        assertFalse(error.getId().isEmpty());
    }

    public abstract ErrorEventDefinition getErrorDefinition();

    @Test
    public void testAddError() {
        final ArgumentCaptor<RootElement> captor = ArgumentCaptor.forClass(RootElement.class);

        ErrorRef errorRef = new ErrorRef();
        errorRef.setValue(ERROR_CODE);
        propertyWriter.addError(errorRef);
        ErrorEventDefinition definition = getErrorDefinition();
        assertEquals(ERROR_CODE, definition.getErrorRef().getErrorCode());
        assertFalse(definition.getErrorRef().getId().isEmpty());

        verify(propertyWriter).addRootElement(captor.capture());
        Error error = (Error) captor.getValue();

        assertEquals(ERROR_CODE, error.getErrorCode());
        assertFalse(error.getId().isEmpty());
    }
}