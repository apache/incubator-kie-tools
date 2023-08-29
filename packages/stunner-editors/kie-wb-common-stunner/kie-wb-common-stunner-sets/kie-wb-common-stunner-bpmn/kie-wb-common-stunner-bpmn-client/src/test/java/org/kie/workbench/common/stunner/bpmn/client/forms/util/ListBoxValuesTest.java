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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor.ActivityDataIOEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ListBoxValuesTest {

    final String EMPTY_STRING = "";
    final String EDIT = "Edit ";
    final String CUSTOM_VALUE = "Custom Value";
    final String EDIT_CUSTOM_VALUE = "Edit Custom Value ...";

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
                                                           EDIT,
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
                "Constant ...",
                "\"jkl\"",
                "Edit \"jkl\" ...",
                "123",
                "\"employee\"",
                "\"def\"",
                "\"abc\"",
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
                                                         EDIT,
                                                         userValue -> {
                                                             if (assignmentData1 != null) {
                                                                 return assignmentData1.getDataTypeDisplayNameForUserString(userValue);
                                                             } else {
                                                                 return null;
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
                "Custom ...",
                "com.test.HisType",
                "Edit com.test.HisType ...",
                "com.test.YourType",
                "com.test.MyType",
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
    public void testSetEditPromptForLongValues() {
        ListBoxValues processVarValues = new ListBoxValues("Expression ...",
                                                           EDIT,
                                                           null,
                                                           ActivityDataIOEditorViewImpl.EXPRESSION_MAX_DISPLAY_LENGTH);
        String value = "\"abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde1234567890\"";
        processVarValues.addCustomValue(value, "");
        String displayValue = processVarValues.getDisplayNameForValue(value);
        List<String> values = processVarValues.update(displayValue);
        assertTrue("ListBox values doesn't contain Edit message for long value", values.contains(EDIT + displayValue + " ..."));
    }

    @Test
    public void testAddDisplayValue() {
        ListBoxValues processVarValues = new ListBoxValues("Expression ...",
                                                           EDIT,
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
        value = "\"abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde12345\"";
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
        value = "\"abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde1234567890\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde12345...(01)\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        value = "\"01234567890123456789012345678901234567890123456789012345678901234x\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"01234567890123456789012345678901234567890123456789012345678901234...\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        value = "\"01234567890123456789012345678901234567890123456789012345678901234y\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"01234567890123456789012345678901234567890123456789012345678901234...(01)\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 3rd truncated
        value = "\"01234567890123456789012345678901234567890123456789012345678901234z\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"01234567890123456789012345678901234567890123456789012345678901234...(02)\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        value = "\"hello then hello then hello then hello then hello then hello then goodbye\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"hello then hello then hello then hello then hello then hello then...\"",
                            displayValue);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        value = "\"hello then hello then hello then hello then hello then hello then hello\"";
        displayValue = processVarValues.addDisplayValue(value);
        Assert.assertEquals("\"hello then hello then hello then hello then hello then hello then...(01)\"",
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
        displayValue = "\"abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde12345...(01)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde1234567890\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        displayValue = "\"01234567890123456789012345678901234567890123456789012345678901234...\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"01234567890123456789012345678901234567890123456789012345678901234x\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        displayValue = "\"01234567890123456789012345678901234567890123456789012345678901234...(01)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"01234567890123456789012345678901234567890123456789012345678901234y\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 3rd truncated
        displayValue = "\"01234567890123456789012345678901234567890123456789012345678901234...(02)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"01234567890123456789012345678901234567890123456789012345678901234z\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 1st truncated
        displayValue = "\"hello then hello then hello then hello then hello then hello then...\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"hello then hello then hello then hello then hello then hello then goodbye\"",
                            value);
        // value longer than MAX and a quoted string - displayValue is 2nd truncated
        displayValue = "\"hello then hello then hello then hello then hello then hello then...(01)\"";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals("\"hello then hello then hello then hello then hello then hello then hello\"",
                            value);
        // value longer than MAX but not a quoted string - displayValue is the same
        displayValue = "hello then hello";
        value = processVarValues.getValueForDisplayValue(displayValue);
        Assert.assertEquals(displayValue,
                            value);

        final String keyFirst = "1st";
        final String valueFirst = "First";
        final String keySecond = "Second";
        final String valueSecond = "Second";
        ListBoxValues test2 = new ListBoxValues("Constant ...",
                                                "Edit ",
                                                null);
        test2.mapDisplayValuesToValues.put(keyFirst, valueFirst);
        test2.mapDisplayValuesToValues.put(keySecond, valueSecond);
        assertEquals(keyFirst, test2.addDisplayValue(valueFirst));
        assertEquals(keySecond, test2.addDisplayValue(valueSecond));
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
        assertTrue(copy1.getAcceptableValuesWithCustomValues().size() == 7);
        assertTrue(copy1.getAcceptableValuesWithoutCustomValues().size() == 4);
        assertTrue(copy1.getAcceptableValuesWithCustomValues().contains("\"abc\""));
        assertTrue(copy1.getAcceptableValuesWithCustomValues().contains("\"def\""));

        // Don't copy custom values as well as non-custom
        ListBoxValues copy2 = new ListBoxValues(listBoxValues,
                                                false);
        assertTrue(copy2.getAcceptableValuesWithCustomValues().size() == 5);
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
        assertEquals(1, values.getAcceptableValuesWithCustomValues().size());
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

        assertEquals(3, values.getAcceptableValuesWithCustomValues().size());
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

    @Test
    public void testAddValues() {
        List<String> processVarStartValuesList = Arrays.asList(
                "** Variable Definitions **",
                "employee",
                "reason",
                "performance"
        );

        Map<String, String> processVarStartValuesMap = new TreeMap<>();
        processVarStartValuesMap.put("def", "** Variable Definitions **");
        processVarStartValuesMap.put("emp", "employee");
        processVarStartValuesMap.put("rea", "reason");
        processVarStartValuesMap.put("per", "performance");

        ListBoxValues test1 = new ListBoxValues("CustomPrompt",
                                                "Edit",
                                                null,
                                                ActivityDataIOEditorViewImpl.EXPRESSION_MAX_DISPLAY_LENGTH,
                                                false);
        test1.addValues(processVarStartValuesList);

        Assert.assertFalse(test1.acceptableValuesWithCustomValues.contains(""));

        ListBoxValues test2 = new ListBoxValues("CustomPrompt",
                                                "Edit",
                                                null,
                                                ActivityDataIOEditorViewImpl.EXPRESSION_MAX_DISPLAY_LENGTH,
                                                true);
        test2.addValues(processVarStartValuesList);

        Assert.assertTrue(test2.acceptableValuesWithCustomValues.contains(""));

        ListBoxValues test3 = new ListBoxValues("CustomPrompt",
                                                "Edit",
                                                null,
                                                true);
        test3.addValues(processVarStartValuesList);

        Assert.assertTrue(test3.acceptableValuesWithCustomValues.contains(""));

        ListBoxValues test4 = new ListBoxValues("CustomPrompt",
                                                "Edit",
                                                null,
                                                true);
        List<String> emptyList = null;
        test4.addValues(emptyList);

        ListBoxValues test5 = new ListBoxValues("CustomPrompt",
                                                "Edit",
                                                null,
                                                true);
        test5.addValues(processVarStartValuesMap);
        Assert.assertTrue(test5.acceptableValuesWithCustomValues.contains(""));

        ListBoxValues test6 = new ListBoxValues("CustomPrompt",
                                                "Edit",
                                                null,
                                                false);
        test6.addValues(processVarStartValuesMap);
        Assert.assertFalse(test6.acceptableValuesWithCustomValues.contains(""));
    }

    @Test
    public void testAddCustomValues() {
        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null);

//        assertNull(processVarValues.addCustomValue(null, null));
//        assertEquals(EMPTY_STRING, processVarValues.addCustomValue(EMPTY_STRING, null));

        processVarValues.customValues.add(CUSTOM_VALUE);
        assertEquals(CUSTOM_VALUE, processVarValues.addCustomValue(CUSTOM_VALUE, null));
    }

    @Test
    public void testUpdate() {
        final String first = "1st";
        final String second = "2nd";

        ListBoxValues test1 = new ListBoxValues("Constant ...",
                                                "Edit ",
                                                null);
        test1.customValues.add(CUSTOM_VALUE);

        List<String> result1 = test1.update(CUSTOM_VALUE);
        assertEquals(1, result1.size());
        assertEquals(EDIT_CUSTOM_VALUE, result1.get(0));

        ListBoxValues test2 = new ListBoxValues("Constant ...",
                                                "Edit ",
                                                null);
        test2.customValues.add(CUSTOM_VALUE);

        test2.customValues.add(CUSTOM_VALUE);
        test2.acceptableValuesWithCustomValues.add(first);
        test2.acceptableValuesWithCustomValues.add(second);

        List<String> result2 = test2.update(CUSTOM_VALUE);
        assertEquals(3, result2.size());
        assertEquals(first, result2.get(0));
        assertEquals(second, result2.get(1));
        assertEquals(EDIT_CUSTOM_VALUE, result2.get(2));
    }

    @Test
    public void testIsCustomValue() {
        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null);

        processVarValues.addCustomValue(CUSTOM_VALUE, null);

        assertFalse(processVarValues.isCustomValue(null));
        assertFalse(processVarValues.isCustomValue(EMPTY_STRING));
        assertTrue(processVarValues.isCustomValue(CUSTOM_VALUE));
    }

    @Test
    public void testGetEditValuePrompt() {
        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null);
        assertNull(processVarValues.getEditValuePrompt(CUSTOM_VALUE));

        processVarValues.addCustomValue(CUSTOM_VALUE, null);
        assertEquals(CUSTOM_VALUE, processVarValues.getEditValuePrompt(CUSTOM_VALUE));
    }

    @Test
    public void testCreateDisplayValues() {
        List<String> acceptableValues = new ArrayList<>();
        acceptableValues.add(CUSTOM_VALUE);
        acceptableValues.add(null);

        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null);
        List<String> result = processVarValues.createDisplayValues(acceptableValues);
        assertEquals(1, result.size());
        assertEquals(CUSTOM_VALUE, result.get(0));
    }

    @Test
    public void testToString() {
        ListBoxValues processVarValues = new ListBoxValues("Constant ...",
                                                           "Edit ",
                                                           null);
        processVarValues.acceptableValuesWithoutCustomValues.add(CUSTOM_VALUE);
        processVarValues.acceptableValuesWithCustomValues.add(CUSTOM_VALUE);
        String expected = "acceptableValuesWithoutCustomValues:\n" +
                "\tCustom Value,\n" +
                "\n" +
                "acceptableValuesWithCustomValues:\n" +
                "\tCustom Value,\n";

        String result = processVarValues.toString();
        assertEquals(expected, result);
    }
}