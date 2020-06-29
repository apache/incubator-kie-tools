/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor.ActivityDataIOEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListBoxValuesTest {

    /**
     * General test for adding custom values to ProcessVar ListBoxValues
     */
    @Test
    public void testProcessVarListBoxValues() {
        List<String> processVarStartValues = Arrays.asList(
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        );
        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null);
        processVarValues.addValues(processVarStartValues);
        processVarValues.addCustomValue("\"abc\"",
                                        "");
        processVarValues.update("\"abc\"");
        processVarValues.update("reason");
        processVarValues.addCustomValue("\"ghi\"",
                                        "");
        processVarValues.update("\"ghi\"");
        processVarValues.addCustomValue("\"def\"",
                                        "\"ghi\"");
        processVarValues.update("\"def\"");
        processVarValues.update("reason");
        // Add Constant with same value as a ProcessVar
        processVarValues.addCustomValue("\"employee\"",
                                        "");
        processVarValues.update("\"employee\"");
        processVarValues.update("performance");
        processVarValues.addCustomValue("123",
                                        "");
        processVarValues.update("123");
        processVarValues.update("\"reason\"");
        processVarValues.addCustomValue("\"jkl\"",
                                        "\"reason\"");
        processVarValues.update("\"jkl\"");
        String[] acceptableValuesWithoutCustomValues = {
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        };
        String[] expectedAcceptableValuesWithCustomValues = {
                "",
                "\"jkl\"",
                "Edit \"jkl\" ...",
                "123",
                "\"employee\"",
                "\"def\"",
                "\"abc\"",
                "Constant ...",
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        };
        Assert.assertArrayEquals(acceptableValuesWithoutCustomValues,
                                 processVarValues.getAcceptableValuesWithoutCustomValues().toArray());
        Assert.assertArrayEquals(expectedAcceptableValuesWithCustomValues,
                                 processVarValues.getAcceptableValuesWithCustomValues().toArray());
    }

    /**
     * General test for adding custom values to DataTypes ListBoxValues
     */
    String sDataTypes1 = "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object, ******:******,UserCommand [org.jbpm.examples.cmd]:org.jbpm.examples.cmd.UserCommand,User [org.jbpm.examples.data]:org.jbpm.examples.data.User,Invoice [org.kie.test]:org.kie.test.Invoice,InvoiceLine [org.kie.test]:org.kie.test.InvoiceLine,PositionTest1 [org.kie.test]:org.kie.test.PositionTest1,PositionTest2 [org.kie.test]:org.kie.test.PositionTest2,PositionTest3 [org.kie.test]:org.kie.test.PositionTest3,PositionTest5 [org.kie.test]:org.kie.test.PositionTest5,SubComponent [org.kie.test]:org.kie.test.SubComponent,TestFormulas [org.kie.test]:org.kie.test.TestFormulas,TestPatterns [org.kie.test]:org.kie.test.TestPatterns,TestTypes [org.kie.test]:org.kie.test.TestTypes,TestTypesLine [org.kie.test]:org.kie.test.TestTypesLine";
    AssignmentData assignmentData1 = new AssignmentData(null,
                                                        null,
                                                        null,
                                                        null,
                                                        sDataTypes1,
                                                        null);

    @Test
    public void testDataTypeListBoxValues() {
        ListBoxValues dataTypeValues = new ListBoxValues("Custom ...",
                                                         "Edit ",
                                                         new ListBoxValues.ValueTester() {
                                                             public String getNonCustomValueForUserString(String userValue) {
                                                                 if (assignmentData1 != null) {
                                                                     return assignmentData1.getDataTypeDisplayNameForUserString(userValue);
                                                                 } else {
                                                                     return null;
                                                                 }
                                                             }
                                                         });
        dataTypeValues.addValues(assignmentData1.getDataTypeDisplayNames());
        dataTypeValues.addCustomValue("com.test.MyType",
                                      "");
        dataTypeValues.update("com.test.MyType");
        dataTypeValues.update("String");
        dataTypeValues.addCustomValue("com.test.YourType",
                                      "String");
        dataTypeValues.update("com.test.YourType");
        // Get known type for SimpleType entered by user
        String nonCustomValue = dataTypeValues.getNonCustomValueForUserString("InvoiceLine");
        dataTypeValues.update(nonCustomValue);
        dataTypeValues.addCustomValue("com.test.HisType",
                                      "");
        dataTypeValues.update("com.test.HisType");
        String[] acceptableValuesWithoutCustomValues = {
                "String",
                "Integer",
                "Boolean",
                "Float",
                "Object",
                "UserCommand [org.jbpm.examples.cmd]",
                "User [org.jbpm.examples.data]",
                "Invoice [org.kie.test]",
                "InvoiceLine [org.kie.test]",
                "PositionTest1 [org.kie.test]",
                "PositionTest2 [org.kie.test]",
                "PositionTest3 [org.kie.test]",
                "PositionTest5 [org.kie.test]",
                "SubComponent [org.kie.test]",
                "TestFormulas [org.kie.test]",
                "TestPatterns [org.kie.test]",
                "TestTypes [org.kie.test]",
                "TestTypesLine [org.kie.test]"
        };
        String[] expectedAcceptableValuesWithCustomValues = {
                "",
                "com.test.HisType",
                "Edit com.test.HisType ...",
                "com.test.YourType",
                "com.test.MyType",
                "Custom ...",
                "Integer",
                "Boolean",
                "Float",
                "Object",
                "UserCommand [org.jbpm.examples.cmd]",
                "User [org.jbpm.examples.data]",
                "Invoice [org.kie.test]",
                "InvoiceLine [org.kie.test]",
                "PositionTest1 [org.kie.test]",
                "PositionTest2 [org.kie.test]",
                "PositionTest3 [org.kie.test]",
                "PositionTest5 [org.kie.test]",
                "SubComponent [org.kie.test]",
                "TestFormulas [org.kie.test]",
                "TestPatterns [org.kie.test]",
                "TestTypes [org.kie.test]",
                "TestTypesLine [org.kie.test]"
        };
        Assert.assertArrayEquals(acceptableValuesWithoutCustomValues,
                                 dataTypeValues.getAcceptableValuesWithoutCustomValues().toArray());
        Assert.assertArrayEquals(expectedAcceptableValuesWithCustomValues,
                                 dataTypeValues.getAcceptableValuesWithCustomValues().toArray());
    }

    @Test
    public void testAddDisplayValue() {
        List<String> processVarStartValues = Arrays.asList(
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        );
        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null,
                                                           ActivityDataIOEditorViewImpl.EXPRESSION_MAX_DISPLAY_LENGTH);
        // not double-quoted string - displayValue is the same
        String value = "sVar1";
        String displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // double-quoted string shorter than max - displayValue is the same
        value = "\"hello\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // value less than MAX and not a quoted string - displayValue is the same
        value = "sVar";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // value less than MAX and a quoted string - displayValue is the same
        value = "\"abcdeabcde\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // value longer than MAX and not a quoted string - displayValue is the same
        value = "sLongVar123";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // value much longer than MAX and not a quoted string - displayValue is the same
        value = "sVeryLongVariableName123";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is truncated with "(01)"
        value = "\"abcdeabcde1\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"abcdeabcde...(01)\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        value = "\"0123456789x\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"0123456789...\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        value = "\"0123456789y\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"0123456789...(01)\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 3rd truncated
        value = "\"0123456789z\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"0123456789...(02)\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        value = "\"hello then goodbye\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"hello then...\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        value = "\"hello then hello\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"hello then...(01)\"",
                            displayValue);
        // value longer than MAX but not a quoted string - displayValue is the same
        value = "hello then hello";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals(value,
                            displayValue);
        // Test getValueForDisplayValue for the entries above
        // not double-quoted string - displayValue is the same
        displayValue = "sVar1";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
        // double-quoted string shorter than max - displayValue is the same
        displayValue = "\"hello\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
        // value less than MAX and not a quoted string - displayValue is the same
        displayValue = "sVar";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
        // value less than MAX and a quoted string - displayValue is the same
        displayValue = "\"abcdeabcde\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
        // value longer than MAX and not a quoted string - displayValue is the same
        displayValue = "sLongVar123";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
        // value much longer than MAX and not a quoted string - displayValue is the same
        displayValue = "sVeryLongVariableName123";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
        // value longer than MAX and a quoted string - displayValue is truncated with "(01)"
        displayValue = "\"abcdeabcde...(01)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"abcdeabcde1\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        displayValue = "\"0123456789...\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"0123456789x\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        displayValue = "\"0123456789...(01)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"0123456789y\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 3rd truncated
        displayValue = "\"0123456789...(02)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"0123456789z\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        displayValue = "\"hello then...\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"hello then goodbye\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        displayValue = "\"hello then...(01)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"hello then hello\"",
                            value);
        // value longer than MAX but not a quoted string - displayValue is the same
        displayValue = "hello then hello";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);
    }

    @Test
    public void testCopyConstructor() {
        List<String> processVarStartValues = Arrays.asList(
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        );
        ListBoxValues listBoxValues = new ListBoxValues("Constant ...",
                                                        "Edit ",
                                                        null);
        listBoxValues.addValues(processVarStartValues);
        listBoxValues.addCustomValue("\"abc\"",
                                     "");
        listBoxValues.addCustomValue("\"def\"",
                                     "");

        // Copy custom values as well as non-custom
        ListBoxValues copy1 = new ListBoxValues(listBoxValues,
                                                true);
        assertTrue(copy1.getAcceptableValuesWithCustomValues().size() == 8);
        assertTrue(copy1.getAcceptableValuesWithoutCustomValues().size() == 4);
        assertTrue(copy1.getAcceptableValuesWithCustomValues().contains("\"abc\""));
        assertTrue(copy1.getAcceptableValuesWithCustomValues().contains("\"def\""));

        // Don't copy custom values as well as non-custom
        ListBoxValues copy2 = new ListBoxValues(listBoxValues,
                                                false);
        assertTrue(copy2.getAcceptableValuesWithCustomValues().size() == 6);
        assertTrue(copy2.getAcceptableValuesWithoutCustomValues().size() == 4);
        Assert.assertFalse(copy2.getAcceptableValuesWithCustomValues().contains("\"abc\""));
        Assert.assertFalse(copy2.getAcceptableValuesWithCustomValues().contains("\"def\""));
    }

    @Test
    public void testAddNullValuesAsMap() {
        ListBoxValues values = new ListBoxValues("Constant ...",
                                                 "Edit ",
                                                 null);
        Map<String, String> testNull = null;
        values.addValues(testNull);
        assertTrue(values.getAcceptableValuesWithCustomValues().isEmpty());
    }

    @Test
    public void testAddEmptyValuesAsMap() {
        ListBoxValues values = new ListBoxValues("Constant ...",
                                                 "Edit ",
                                                 null);
        Map<String, String> testEmpty = new HashMap<>();
        values.addValues(testEmpty);
        assertEquals(2, values.getAcceptableValuesWithCustomValues().size());
        assertTrue(values.getAcceptableValuesWithoutCustomValues().isEmpty());
        assertTrue(values.mapDisplayValuesToValues.isEmpty());
    }

    @Test
    public void testAddValueWithDifferentKeysValues() {
        ListBoxValues values = new ListBoxValues("Constant ...",
                                                 "Edit ",
                                                 null);
        Map<String, String> testMap = new HashMap<>();
        String displayName1 = "firstDisplayName";
        String displayName2 = "secondDisplayName";
        String valueName1 = "02firstKey";
        String valueName2 = "01secondKey";
        testMap.put(valueName1, displayName1);
        testMap.put(valueName2, displayName2);
        values.addValues(testMap);

        assertEquals(4, values.getAcceptableValuesWithCustomValues().size());
        assertEquals(2, values.getAcceptableValuesWithoutCustomValues().size());
        assertEquals(2, values.mapDisplayValuesToValues.size());
        assertEquals(displayName1, values.getDisplayNameForValue(valueName1));
        assertEquals(displayName2, values.getDisplayNameForValue(valueName2));
        assertEquals(valueName1, values.getValueForDisplayValue(displayName1));
        assertEquals(valueName2, values.getValueForDisplayValue(displayName2));

        String nonMappedValue = "nonMappedValue";
        assertEquals(nonMappedValue, values.getDisplayNameForValue(nonMappedValue));
    }

    @Test
    public void testEmptyValueDisplayNameMap() {
        ListBoxValues values = new ListBoxValues("Constant ...",
                                                 "Edit ",
                                                 null);

        String nonMappedValue = "nonMappedValue";
        assertEquals(nonMappedValue, values.getDisplayNameForValue(nonMappedValue));
    }
}
