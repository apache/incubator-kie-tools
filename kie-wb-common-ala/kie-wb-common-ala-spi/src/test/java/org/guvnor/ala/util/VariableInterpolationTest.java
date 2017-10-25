/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.guvnor.ala.util;

import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.*;

public class VariableInterpolationTest {

    @Test
    public void basicTest() {
        final Test1 object = new Test1() {
        };
        final Test1 newObject = VariableInterpolation.interpolate(new HashMap<String, Object>() {{
                                                                      put("input",
                                                                          new HashMap<String, String>() {{
                                                                              put("value1",
                                                                                  "myvalue1");
                                                                              put("value2",
                                                                                  "myvalue2");
                                                                          }});
                                                                  }},
                                                                  object);

        assertEquals("myvalue1",
                     newObject.getValue1());
        assertEquals("myvalue2",
                     newObject.getValue2());
        assertEquals("myvalue1",
                     newObject.getValue3());
    }

    @Test
    public void missingContentTest() {
        final Test1 object = new Test1() {
        };
        final Test1 newObject = VariableInterpolation.interpolate(new HashMap<String, Object>() {{
                                                                      put("input",
                                                                          new HashMap<String, String>() {{
                                                                              put("value2",
                                                                                  "myvalue2");
                                                                          }});
                                                                  }},
                                                                  object);

        assertEquals("",
                     newObject.getValue1());
        assertEquals("myvalue2",
                     newObject.getValue2());
        assertEquals("",
                     newObject.getValue3());
    }

    @Test
    public void testPreserveImplementedInterfaces() {
        Object object = new Test1Class();
        Object newObject = VariableInterpolation.interpolate(new HashMap<>(),
                                                             object);
        assertTrue(newObject instanceof Test1);

        object = new Test2Class();
        newObject = VariableInterpolation.interpolate(new HashMap<>(),
                                                      object);
        assertTrue(newObject instanceof Test1);

        object = new Test3Class();
        newObject = VariableInterpolation.interpolate(new HashMap<>(),
                                                      object);
        assertTrue(newObject instanceof Test1);
    }

    public interface Test1 {

        default String getValue1() {
            return "${input.value1}";
        }

        default String getValue2() {
            return "${input.value2}";
        }

        default String getValue3() {
            return "${input.value1}";
        }
    }

    public class Test1Class implements Test1 {

    }

    public class Test2Class extends Test1Class {

    }

    public class Test3Class extends Test2Class {

    }
}