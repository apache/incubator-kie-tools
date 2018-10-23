/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.datamodel.backend.server.builder.util;

import org.junit.Test;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;

import static org.jgroups.util.Util.assertFalse;
import static org.jgroups.util.Util.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataEnumLoader tests
 */
public class DataEnumLoaderTest {

    @Test
    public void testSimpleEnum() {
        final String e = "'Fact.field1':[\"A\", \"B\"]";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertFalse(loader.hasErrors());
    }

    @Test
    public void testValidEnumMultiLine() {
        final String e = "'Fact.field1':[\"A\", \"B\"]" + System.lineSeparator() + "'Fact.field2':[1, 2]";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertFalse(loader.hasErrors());
    }

    @Test
    public void testSimpleAndDependentEnum() {
        final String e = "'Fact.field1':[\"A\", \"B\"]" + System.lineSeparator() + "'Fact.field2[field1=A]':[1, 2]";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertFalse(loader.hasErrors());
    }

    @Test
    public void testValidDependentEnum() {
        final String e = "'Fact.field2[field1=A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertFalse(loader.hasErrors());
    }

    @Test
    public void testInvalidDependentEnum_Empty() {
        final String e = "'Fact.field2[]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: Empty [] detected."));
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoFieldNoValue1() {
        final String e = "'Fact.field2[=]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: No field or value detected."));
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoFieldNoValue2() {
        final String e = "'Fact.field2[  =  ]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: No field or value detected."));
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoField1() {
        final String e = "'Fact.field2[=A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: No field detected."));
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoField2() {
        final String e = "'Fact.field2[  =A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: No field detected."));
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoValue1() {
        final String e = "'Fact.field2[field1=]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: No value detected."));
    }

    @Test
    public void testInvalidDependentEnum_EqualsNoValue2() {
        final String e = "'Fact.field2[field1=  ]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: No value detected."));
    }

    @Test
    public void testInvalidDependentEnum_ContainsQuotes1() {
        final String e = "'Fact.field2[field1=\"A\"]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: Found quote literal."));
    }

    @Test
    public void testInvalidDependentEnum_ContainsQuotes2() {
        final String e = "'Fact.field2[field1=\"A]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: Found quote literal."));
    }

    @Test
    public void testInvalidDependentEnum_ContainsQuotes3() {
        final String e = "'Fact.field2[field1=A\"]':'[1, 2, 3]'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid dependent definition: Found quote literal."));
    }

    @Test
    public void testInvalidDependentEnum_AdvancedEnum1() {
        final String e = "'Fact.field[dependentField1, dependentField2]' : '(new org.company.DataHelper()).getList(\"@{dependentField1}\", \"@{dependentField2}\")'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertFalse(loader.hasErrors());
    }

    @Test
    public void testInvalidDependentEnum_AdvancedEnum2() {
        final String e = "'Fact.field[dependentField1]' : '(new org.company.DataHelper()).getList(\"@{dependentField1}\")'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertFalse(loader.hasErrors());
    }

    @Test
    public void testInvalidDependentEnum_AdvancedEnum3() {
        final String e = "'Fact.field[dependentField1,]' : '(new org.company.DataHelper()).getList(\"@{dependentField1}\")'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid definition: Field definitions are incomplete."));
    }

    @Test
    public void testInvalidDependentEnum_AdvancedEnum4() {
        final String e = "'Fact.field[,dependentField1]' : '(new org.company.DataHelper()).getList(\"@{dependentField1}\")'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid definition: Field definitions are incomplete."));
    }

    @Test
    public void testInvalidDependentEnum_AdvancedEnum5() {
        final String e = "'Fact.field[,]' : '(new org.company.DataHelper()).getList(\"@{dependentField1}\")'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid definition: Field definitions are incomplete."));
    }

    @Test
    public void testInvalidDependentEnum_AdvancedEnum6() {
        final String e = "'Fact.field[  ,  ]' : '(new org.company.DataHelper()).getList(\"@{dependentField1}\")'";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertTrue(loader.hasErrors());
        assertTrue(loader.getErrors().contains("Invalid definition: Field definitions are incomplete."));
    }

    @Test
    public void testInvalidEnum_Field() {
        final String e = "asd<>\";";
        final DataEnumLoader loader = new DataEnumLoader(e, new RawMVELEvaluator());
        assertThat(loader.hasErrors()).isTrue();
        assertThat(loader.getErrors()).contains("Unable to load enumeration data.",
                                                "[Error: unterminated string literal]\n" +
                                                        "[Near : {... [ asd<>\"; ] ....}]\n" +
                                                        "                        ^\n" +
                                                        "[Line: 1, Column: 12]",
                                                "Error type: org.mvel2.CompileException");
    }
}
