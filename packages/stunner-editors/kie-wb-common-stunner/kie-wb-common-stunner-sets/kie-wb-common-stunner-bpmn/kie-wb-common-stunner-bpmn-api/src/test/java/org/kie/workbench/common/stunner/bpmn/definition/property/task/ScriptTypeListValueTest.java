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


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.ArrayList;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils;

public class ScriptTypeListValueTest {

    private static final String LANGUAGE = "LANGUAGE";
    private static final String SCRIPT = "SCRIPT";

    @Test
    public void testScriptTypeListValueEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new ScriptTypeListValue(),
                             new ScriptTypeListValue())

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue()),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue()))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    null)),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    null)))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    "b")),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    "b")))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    null)),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                    null)))

                .addTrueCase(new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    "b")),
                             new ScriptTypeListValue().addValue(new ScriptTypeValue(null,
                                                                                    "b")))
                .addTrueCase(mockList(10),
                             mockList(10))
                .addTrueCase(mockList(0),
                             mockList(0))
                .addFalseCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "b")),
                              new ScriptTypeListValue().addValue(new ScriptTypeValue("X",
                                                                                     "b")))

                .addFalseCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "b")),
                              new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "Y")))

                .addFalseCase(new ScriptTypeListValue().addValue(new ScriptTypeValue("a",
                                                                                     "b")),
                              new ScriptTypeListValue().addValue(new ScriptTypeValue("X",
                                                                                     "Y")))
                .addFalseCase(mockList(0),
                              mockList(5))
                .addFalseCase(mockList(1),
                              mockList(4))
                .test();
    }

    private ScriptTypeListValue mockList(int count) {
        ArrayList<ScriptTypeValue> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            values.add(new ScriptTypeValue(LANGUAGE + i,
                                           SCRIPT + i));
        }
        return new ScriptTypeListValue(values);
    }
}
