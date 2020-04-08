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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataAttribute;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void testCreateDataTypeDisplayName() {
        assertEquals("Chairs [com.test]",
                     StringUtils.createDataTypeDisplayName("com.test.Chairs"));
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

        assertEquals("input1:Boolean,input2:Object", StringUtils.getStringForList(variables));
        assertEquals("input1ßvalue,input2ßvalue", StringUtils.getStringForList(attributes, null));
        assertEquals("input1ßvalue,input2ßvalue", StringUtils.getStringForList(attributes, ""));
        assertEquals("input1ßvalueØinput2ßvalue", StringUtils.getStringForList(attributes, "Ø"));
    }
}
