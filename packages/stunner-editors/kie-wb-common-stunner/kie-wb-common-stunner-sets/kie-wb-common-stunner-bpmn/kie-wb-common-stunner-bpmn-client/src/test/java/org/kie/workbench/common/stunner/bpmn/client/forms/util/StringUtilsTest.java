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
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataAttribute;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringUtilsTest {

    @Mock
    private URL url;

    private final String EMPTY_STRING = "";
    private final String TEST_STRING = "some string";
    private final String MODIFIED_STRING = "some string";

    @Before
    public void setUp() {
        StringUtils.setURL(url);
        when(url.encode(anyString())).thenReturn(MODIFIED_STRING);
        when(url.decode(anyString())).thenReturn(MODIFIED_STRING);

        when(url.encodeQueryString(anyString())).thenReturn(MODIFIED_STRING);
        when(url.decodeQueryString(anyString())).thenReturn(MODIFIED_STRING);
    }

    @Test
    public void testCreateDataTypeDisplayName() {
        assertEquals("Chairs [com.test]",
                     StringUtils.createDataTypeDisplayName("com.test.Chairs"));
    }

    @Test
    public void testCreateDataTypeDisplayNameWithGenerics() {
        //if < > in string, it should not apply FQDN
        assertEquals("java.util.Map<java.util.List<String>, String>",
                     StringUtils.createDataTypeDisplayName("java.util.Map<java.util.List<String>, String>"));
    }

    @Test
    public void testIsGenericsFormatOk() {
        //ok
        assertTrue("Should be ok generics format", StringUtils.isOkWithGenericsFormat("String"));
        assertTrue("Should be ok generics format", StringUtils.isOkWithGenericsFormat("List"));
    }

    @Test
    public void testIsGenericsFormatWrong() {
        //wrong
        assertFalse("Should be ok generics format", StringUtils.isOkWithGenericsFormat("<<X>>"));
        assertFalse("Should be ok generics format", StringUtils.isOkWithGenericsFormat("<<X,<SomeClass>"));
        assertFalse("Should be ok generics format", StringUtils.isOkWithGenericsFormat("<SomeClass>"));
        assertFalse("Should be ok generics format", StringUtils.isOkWithGenericsFormat("List<.>"));
    }

    @Test
    public void testIsGenericsFormatCorrectMap() {
        assertTrue("Should be ok generics format", StringUtils.isOkWithGenericsFormat("Map<String,String>"));
    }

    @Test
    public void testIsGenericsFormatInCorrectMap() {
        assertFalse("Should NOT be ok generics format", StringUtils.isOkWithGenericsFormat("Map<String><String>"));
    }

    @Test
    public void testIsGenericsFormatUnbalanced() {
        assertFalse("Should NOT be ok generics format", StringUtils.isOkWithGenericsFormat("Map<String, String"));
    }

    @Test
    public void testIsGenericsFormatCorrectList() {
        assertTrue("Should be ok generics format", StringUtils.isOkWithGenericsFormat("List<String>"));
    }

    @Test
    public void testIsGenericsFormatInCorrectList() {
        assertFalse("Should NOT be ok generics format", StringUtils.isOkWithGenericsFormat("List<List<String>"));
    }

    @Test
    public void testIsGenericsFormatCorrectSet() {
        assertTrue("Should be ok generics format", StringUtils.isOkWithGenericsFormat("Set<String>"));
    }

    @Test
    public void testIsGenericsFormatInCorrectSet() {
        assertFalse("Should NOT be ok generics format", StringUtils.isOkWithGenericsFormat("Set<Set<String>"));
    }

    @Test
    public void testIsGenericsFormatCorrectStack() {
        assertTrue("Should be ok generics format", StringUtils.isOkWithGenericsFormat("Stack<String>"));
    }

    @Test
    public void testIsGenericsFormatInCorrectStack() {
        assertFalse("Should NOT be ok generics format", StringUtils.isOkWithGenericsFormat("Stack<Stack<String>"));
    }

    @Test
    public void testPreFilterVariablesForGenerics() {
        assertEquals("Bad Formad", "map:Map<String*String>:", StringUtils.preFilterVariablesForGenerics("map:Map<String,String>:"));
        assertEquals("Bad Formad", "list:List<String>:", StringUtils.preFilterVariablesForGenerics("list:List<String>:"));
        assertEquals("Should be null", null, StringUtils.preFilterVariablesForGenerics(null));
        assertEquals("Should be empy", "", StringUtils.preFilterVariablesForGenerics(""));
    }

    @Test
    public void testPreFilterVariablesTwoSemicolonForGenerics() {
        assertEquals("Bad Formad", "map:Map<String*String>", StringUtils.preFilterVariablesTwoSemicolonForGenerics("map:Map<String,String>"));
        assertEquals("Bad Formad", "list:List<String>", StringUtils.preFilterVariablesTwoSemicolonForGenerics("list:List<String>"));
        assertEquals("Should be null", null, StringUtils.preFilterVariablesTwoSemicolonForGenerics(null));
        assertEquals("Should be empy", "", StringUtils.preFilterVariablesTwoSemicolonForGenerics(""));
    }

    @Test
    public void testPostFilterForGenerics() {
        assertEquals("Bad Formad", "map:Map<String,String>", StringUtils.postFilterForGenerics("map:Map<String*String>"));
        assertEquals("Bad Formad", "list:List<Map<String,String>>", StringUtils.postFilterForGenerics("list:List<Map<String*String>>"));
        assertEquals("Should be null", null, StringUtils.postFilterForGenerics(null));
        assertEquals("Should be empy", "", StringUtils.postFilterForGenerics(""));
    }

    @Test
    public void testRegexSequence() {

        String test1 = "123Test";
        assertTrue(test1.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test2 = "123Test ";
        assertFalse(test2.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test3 = "123Test #";
        assertFalse(test3.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test4 = "123Test";
        assertTrue(test4.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));

        String test5 = "123Test ";
        assertTrue(test5.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));

        String test6 = "123Test #";
        assertFalse(test6.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));
    }

    @Test
    public void testgetStringForList() {
        List<Variable> variables = new ArrayList<>();
        Variable inputVariable1 = new Variable("input1",
                                               Variable.VariableType.INPUT,
                                               "Boolean",
                                               null);
        Variable inputVariable2 = new Variable("input2",
                                               Variable.VariableType.INPUT,
                                               "Object",
                                               null);
        variables.add(inputVariable1);
        variables.add(inputVariable2);

        List<MetaDataAttribute> attributes = new ArrayList<>();
        MetaDataAttribute metaDataAttribute1 = new MetaDataAttribute("input1", "value");
        MetaDataAttribute metaDataAttribute2 = new MetaDataAttribute("input2", "value");
        attributes.add(metaDataAttribute1);
        attributes.add(metaDataAttribute2);

        assertEquals("input1:Boolean:,input2:Object:", StringUtils.getStringForList(variables));
        assertEquals("input1ßvalue,input2ßvalue", StringUtils.getStringForList(attributes, null));
        assertEquals("input1ßvalue,input2ßvalue", StringUtils.getStringForList(attributes, ""));
        assertEquals("input1ßvalueØinput2ßvalue", StringUtils.getStringForList(attributes, "Ø"));
    }

    @Test
    public void testEmptyEncode() {
        assertNull(StringUtils.encode(null));

        assertSame(EMPTY_STRING, StringUtils.encode(EMPTY_STRING));
        verify(url, never()).encode(anyString());
    }

    @Test
    public void testEncode() {
        assertEquals(TEST_STRING, StringUtils.encode(TEST_STRING));
        verify(url).encode(TEST_STRING);
    }

    @Test
    public void testEmptyDecode() {
        assertNull(StringUtils.decode(null));

        assertSame(EMPTY_STRING, StringUtils.decode(EMPTY_STRING));
        verify(url, never()).decode(anyString());
    }

    @Test
    public void testDecode() {
        assertEquals(TEST_STRING, StringUtils.decode(TEST_STRING));
        verify(url).decode(TEST_STRING);
    }

    @Test
    public void testUrlDecode() {
        assertEquals(TEST_STRING, StringUtils.urlDecode(TEST_STRING));
        verify(url).decodeQueryString(TEST_STRING);
    }

    @Test
    public void testEmptyUrlDecode() {
        assertNull(StringUtils.urlDecode(null));

        assertSame(EMPTY_STRING, StringUtils.urlDecode(EMPTY_STRING));
        verify(url, never()).decodeQueryString(anyString());
    }

    @Test
    public void testUrlEncode() {
        assertEquals(TEST_STRING, StringUtils.urlEncode(TEST_STRING));
        verify(url).encodeQueryString(TEST_STRING);
    }

    @Test
    public void testEmptyUrlEncode() {
        assertNull(StringUtils.urlEncode(null));

        assertSame(EMPTY_STRING, StringUtils.urlEncode(EMPTY_STRING));
        verify(url, never()).encodeQueryString(anyString());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testIsEmpty() {
        String string = null;
        assertTrue(StringUtils.isEmpty(string));
        string = "";
        assertTrue(StringUtils.isEmpty(string));
        string = "Hello";
        assertFalse(StringUtils.isEmpty(string));

        List<String> list = null;
        assertTrue(StringUtils.isEmpty(list));
        list = new ArrayList<>();
        assertTrue(StringUtils.isEmpty(list));
        list.add("hello");
        assertFalse(StringUtils.isEmpty(list));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testNonEmpty() {
        String string = null;
        assertFalse(StringUtils.nonEmpty(string));
        string = "";
        assertFalse(StringUtils.nonEmpty(string));
        string = "Hello";
        assertTrue(StringUtils.nonEmpty(string));

        List<String> list = null;
        assertFalse(StringUtils.nonEmpty(list));
        list = new ArrayList<>();
        assertFalse(StringUtils.nonEmpty(list));
        list.add("hello");
        assertTrue(StringUtils.nonEmpty(list));
    }

    @Test
    public void testDataTypeDisplayName() {
        assertEquals("MyObject", StringUtils.createDataTypeDisplayName("MyObject"));
        assertEquals("MyClass [com.test]", StringUtils.createDataTypeDisplayName("com.test.MyClass"));
        assertEquals("Applicant [mortages.mortages]", StringUtils.createDataTypeDisplayName("mortages.mortages.Applicant"));
    }

    @Test
    public void testGetDataTypes() {
        Set<String> setDataTypes = StringUtils.getSetDataTypes("var:com.var:");
        assertTrue("Data Type not the same", setDataTypes.contains("com.var"));

        setDataTypes = StringUtils.getSetDataTypes(null);
        assertTrue("Data Types must be empty", setDataTypes.size() == 0);

        setDataTypes = StringUtils.getSetDataTypes("");
        assertTrue("Data Types must be empty", setDataTypes.size() == 0);

        setDataTypes = StringUtils.getSetDataTypes("var:com.var");
        assertTrue("Invalid Data Types must be empty", setDataTypes.size() == 0);
    }
}
