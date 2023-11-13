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


package org.kie.workbench.common.forms.fields.shared.fieldTypes;

import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasRows;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractFieldDefinitionTest<FIELD extends FieldDefinition> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String LABEL = "label";
    public static final String BINDING = "binding";
    public static final String STANDALONE_CLASS_NAME = "class";

    public static final String PLACE_HOLDER = "placeHolder";
    public static final Integer MAX_LENGTH = 255;
    public static final Integer MAX_ROWS = 255;

    public static final Boolean READONLY = Boolean.TRUE;
    public static final Boolean REQUIRED = Boolean.TRUE;
    public static final Boolean VALIDATE_ON_CHANGE = Boolean.FALSE;

    @Test
    public void testCopyFrom() {
        FIELD originalFieldDefinition = getEmptyFieldDefinition();

        FIELD newFieldDefinition = getNewFieldDefinition();

        assertFalse(originalFieldDefinition.equals(newFieldDefinition));

        originalFieldDefinition.copyFrom(newFieldDefinition);

        // COPYING fields not affected by copyFrom to make equals work
        originalFieldDefinition.setId(newFieldDefinition.getId());
        originalFieldDefinition.setName(newFieldDefinition.getName());

        assertTrue(originalFieldDefinition.equals(newFieldDefinition));
    }

    @Test
    public void testRequired() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setRequired(true);
        assertTrue(newFieldDefinition.getRequired());
        newFieldDefinition.setRequired(false);
        assertFalse(newFieldDefinition.getRequired());
    }

    @Test
    public void testReadOnly() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setReadOnly(true);
        assertTrue(newFieldDefinition.getReadOnly());
        newFieldDefinition.setReadOnly(false);
        assertFalse(newFieldDefinition.getReadOnly());
    }

    @Test
    public void testValidateOnChange() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setValidateOnChange(true);
        assertTrue(newFieldDefinition.getValidateOnChange());
        newFieldDefinition.setValidateOnChange(false);
        assertFalse(newFieldDefinition.getValidateOnChange());
    }

    @Test
    public void testBinding() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setBinding(BINDING);
        assertEquals(BINDING, newFieldDefinition.getBinding());
    }

    @Test
    public void testName() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setName(NAME);
        assertEquals(NAME, newFieldDefinition.getName());
    }

    @Test
    public void testLabel() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setLabel(LABEL);
        assertEquals(LABEL, newFieldDefinition.getLabel());
    }

    @Test
    public void testStandaloneClassName() {
        FIELD newFieldDefinition = getEmptyFieldDefinition();
        newFieldDefinition.setStandaloneClassName(STANDALONE_CLASS_NAME);
        assertEquals(STANDALONE_CLASS_NAME, newFieldDefinition.getStandaloneClassName());
    }

    protected FIELD getNewFieldDefinition() {
        FIELD field = getFullFieldDefinition();

        field.setId(ID);
        field.setName(NAME);
        field.setLabel(LABEL);
        field.setBinding(BINDING);
        field.setReadOnly(READONLY);
        field.setRequired(REQUIRED);
        field.setStandaloneClassName(STANDALONE_CLASS_NAME);
        field.setValidateOnChange(VALIDATE_ON_CHANGE);

        if (field instanceof HasMaxLength) {
            ((HasMaxLength) field).setMaxLength(MAX_LENGTH);
        }

        if (field instanceof HasPlaceHolder) {
            ((HasPlaceHolder) field).setPlaceHolder(PLACE_HOLDER);
        }

        if (field instanceof HasRows) {
            ((HasRows) field).setRows(MAX_ROWS);
        }

        return field;
    }

    protected abstract FIELD getEmptyFieldDefinition();

    protected abstract FIELD getFullFieldDefinition();
}
