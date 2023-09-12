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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.styling.FontSize;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.FALSE;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.NOT_SET;
import static org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly.ReadOnly.TRUE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TextAnnotationTest {

    private TextAnnotation textAnnotation;
    private static final String[] READONLY_FIELDS = {
            "Description",
            "Text",
            "TextFormat"};

    @Before
    public void setup() {
        textAnnotation = spy(new TextAnnotation());
    }

    @Test
    public void testGetReadOnlyNotSet() {

        textAnnotation.setAllowOnlyVisualChange(false);
        checkIfItIsNotSet("something");
    }

    @Test
    public void testGetReadOnlyWithReadOnlyValuesAndAllowOnlyVisualChangesNotSet() {

        textAnnotation.setAllowOnlyVisualChange(false);

        for (final String readonlyField : READONLY_FIELDS) {
            checkIfItIsNotSet(readonlyField);
        }
    }

    private void checkIfItIsNotSet(final String property) {

        final DynamicReadOnly.ReadOnly actual = textAnnotation.getReadOnly(property);

        assertEquals(NOT_SET, actual);
    }

    @Test
    public void testGetReadOnlyWithReadOnlyValues() {

        textAnnotation.setAllowOnlyVisualChange(true);
        for (final String readonlyField : READONLY_FIELDS) {
            checkIfIsReadOnly(readonlyField);
        }
    }

    private void checkIfIsReadOnly(final String property) {

        final DynamicReadOnly.ReadOnly actual = textAnnotation.getReadOnly(property);

        assertEquals(TRUE, actual);
    }

    @Test
    public void testGetReadOnlyWithNotReadOnlyValues() {

        textAnnotation.setAllowOnlyVisualChange(true);

        checkIfItIsNotReadOnly("Font");
        checkIfItIsNotReadOnly("Something");
    }

    @Test
    public void testGetContentDefinitionId() {

        final String contentDefinitionId = "the id";
        final Id id = mock(Id.class);

        doReturn(id).when(textAnnotation).getId();
        when(id.getValue()).thenReturn(contentDefinitionId);

        final String currentId = textAnnotation.getContentDefinitionId();

        assertEquals(contentDefinitionId, currentId);
    }

    @Test
    public void testDifferentStylingSet() {

        final TextAnnotation modelOne = new TextAnnotation(new Id("123"),
                                                           new Description(),
                                                           new Text(),
                                                           new TextFormat(),
                                                           new StylingSet(),
                                                           new GeneralRectangleDimensionsSet());

        final TextAnnotation modelTwo = new TextAnnotation(new Id("123"),
                                                           new Description(),
                                                           new Text(),
                                                           new TextFormat(),
                                                           new StylingSet(),
                                                           new GeneralRectangleDimensionsSet());

        assertEquals(modelOne, modelTwo);

        modelOne.getStylingSet().setFontSize(new FontSize(10.0));
        modelTwo.getStylingSet().setFontSize(new FontSize(11.0));

        assertNotEquals(modelOne, modelTwo);
    }

    private void checkIfItIsNotReadOnly(final String property) {

        final DynamicReadOnly.ReadOnly actual = textAnnotation.getReadOnly(property);

        assertEquals(FALSE, actual);
    }
}
