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


package org.uberfire.workbench.category;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CategoryTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // Should be equal
                {null, null, true},
                {"", "", true},
                {"a", "a", true},
                {"A", "a", true},
                {"a", "A", true},

                // Should NOT be equal
                {null, "", false},
                {"", null, false},
                {null, "a", false},
                {"a", null, false},
                {"", "a", false},
                {"a", "", false},
                {"a", null, false},
                {"a", "b", false},
                {"a", "B", false},
        });
    }

    private String cat1Name, cat2Name;
    private boolean shouldBeEqual;

    public CategoryTest(String cat1Name,
                        String cat2name,
                        boolean shouldBeEqual) {
        this.cat1Name = cat1Name;
        this.cat2Name = cat2name;
        this.shouldBeEqual = shouldBeEqual;
    }

    @Test
    public void test() {
        Category c1 = new Category() {
            @Override
            public String getName() {
                return cat1Name;
            }
        };

        Category c2 = new Category() {
            @Override
            public String getName() {
                return cat2Name;
            }
        };
        assertEquals(shouldBeEqual,
                     c1.equals(c2));
    }
}