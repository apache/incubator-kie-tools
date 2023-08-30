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


package org.kie.workbench.common.forms.commons.rendering.shared.util.masks;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public abstract class MaskInterpreterTest<T extends MaskInterpreter<Product>> {

    public static final String SHORT_MASK = "{name}: {price}";
    public static final String LONG_MASK = "ID:{id}\nNAME:{name}\nDESCRIPTION:{description}\nPRICE:{price}";
    public static final String WRONG_MASK = "{{id}{otherwrongstuffhere}{{{}}";

    protected Integer id = 0;
    protected String name = "Electric Guitar";
    protected String description = "T-Shape Alder body mapple neck, humbucker & single pickups";
    protected double price = 2999.99;
    protected Product product = new Product(id,
                                            name,
                                            description,
                                            price);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected MaskInterpreter<Product> interpreter;

    protected abstract T getMaskInterpreter(String mask);

    @Test
    public void testValidateWrongMask() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid mask!");

        interpreter = getMaskInterpreter(WRONG_MASK);

        fail("We shouldn't be there! IllegalArgumentException must be thrown during the test!");
    }

    @Test
    public void testValidateNullMask() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Mask cannot be null!");

        interpreter = getMaskInterpreter(null);

        fail("We shouldn't be there! NullPointerException must be thrown during the test!");
    }

    @Test
    public void testShortMask() {
        String expecteResult = name + ": " + price;

        testMask(SHORT_MASK,
                 3,
                 expecteResult);
    }

    @Test
    public void testLongMask() {
        String expecteResult = "ID:" + id + "\nNAME:" + name + "\nDESCRIPTION:" + description + "\nPRICE:" + price;

        testMask(LONG_MASK,
                 8,
                 expecteResult);
    }

    protected void testMask(String mask,
                            int sections,
                            String expectedResult) {
        interpreter = getMaskInterpreter(mask);

        assertNotNull("Interpreter must have a mask: ",
                      interpreter.getMask());
        assertEquals("Interpreter must have " + sections + " sections",
                     sections,
                     interpreter.getSections().size());
        assertEquals("Parsed result should be '" + expectedResult + "'",
                     expectedResult,
                     interpreter.render(product));
    }
}
