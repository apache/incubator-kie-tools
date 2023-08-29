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


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import com.ait.lienzo.client.core.shape.Group;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.TransformDefinitionImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GroupDefinitionGeneratorTest {

    private static final String ID = "group1";
    private static final double X = 1.5d;
    private static final double Y = 1232.9d;
    private static final double ALPHA = 0.7d;
    private static final boolean LISTENING = false;
    private static final double VBOX_MIN_X = 12.6;
    private static final double VBOX_MIN_Y = 23.4;
    private static final double VBOX_WIDTH = 300;
    private static final double VBOX_HEIGHT = 321.86;

    private GroupDefinitionGenerator tested;

    @Before
    public void setup() throws Exception {
        tested = new GroupDefinitionGenerator();
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(GroupDefinition.class,
                     tested.getDefinitionType());
        assertEquals("Group",
                     tested.getTemplatePath());
    }

    @Test
    public void testGenerate() throws Exception {
        final GroupDefinition definition = new GroupDefinition(ID);
        definition.setX(X);
        definition.setY(Y);
        definition.setAlpha(ALPHA);
        definition.setTransformDefinition(new TransformDefinitionImpl(VBOX_WIDTH,
                                                                      VBOX_HEIGHT,
                                                                      VBOX_MIN_X,
                                                                      VBOX_MIN_Y));
        final String generated = tested.generate(definition).toString();
        assertTrue(generated.contains("new " + Group.class.getName() + "()"));
        GeneratorAssertions.assertDraggable(generated);
        GeneratorAssertions.assertID(generated,
                                     ID);
        GeneratorAssertions.assertX(generated,
                                    X);
        GeneratorAssertions.assertY(generated,
                                    Y);
        GeneratorAssertions.assertAlpha(generated,
                                        ALPHA);
        GeneratorAssertions.assertScale(generated,
                                        VBOX_WIDTH,
                                        VBOX_HEIGHT);
        GeneratorAssertions.assertListening(generated,
                                            LISTENING);
    }
}
