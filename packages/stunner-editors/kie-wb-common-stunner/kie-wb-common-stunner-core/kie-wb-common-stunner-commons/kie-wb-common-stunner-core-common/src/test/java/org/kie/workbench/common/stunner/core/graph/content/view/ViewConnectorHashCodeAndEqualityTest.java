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


package org.kie.workbench.common.stunner.core.graph.content.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class ViewConnectorHashCodeAndEqualityTest {

    @Test
    public void testViewConnectorEquals() {
        ViewConnectorImpl<String> a = new ViewConnectorImpl<>("a",
                                                              Bounds.create(0.0,
                                                                            0.0,
                                                                            1.0,
                                                                            1.0));
        ViewConnectorImpl<String> b = new ViewConnectorImpl<>("a",
                                                              Bounds.create(0.0,
                                                                            0.0,
                                                                            1.0,
                                                                            1.0));
        assertEquals(a,
                     b);

        b.setDefinition("b");
        assertNotEquals(a,
                        b);

        b.setDefinition("a");
        b.setBounds(Bounds.create(0.0,
                                  0.0,
                                  5.0,
                                  5.0));
        assertNotEquals(a,
                        b);
        b.setBounds(Bounds.create(0.0,
                                  0.0,
                                  1.0,
                                  1.0));

        b.setSourceConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertNotEquals(a,
                        b);

        a.setSourceConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertEquals(a,
                     b);
        a.setSourceConnection(MagnetConnection.Builder.at(1.0,
                                                          1.0));
        assertNotEquals(a,
                        b);
        b.setSourceConnection(MagnetConnection.Builder.at(1.0,
                                                          1.0));

        b.setTargetConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertNotEquals(a,
                        b);
        a.setTargetConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertEquals(a,
                     b);
        a.setTargetConnection(MagnetConnection.Builder.at(2.0,
                                                          2.0));
        assertNotEquals(a,
                        b);
    }

    @Test
    public void testGraphHashCode() {
        ViewConnectorImpl<String> a = new ViewConnectorImpl<>("a",
                                                              Bounds.create(0.0,
                                                                            0.0,
                                                                            1.0,
                                                                            1.0));
        ViewConnectorImpl<String> b = new ViewConnectorImpl<>("a",
                                                              Bounds.create(0.0,
                                                                            0.0,
                                                                            1.0,
                                                                            1.0));
        assertEquals(a.hashCode(),
                     b.hashCode());

        b.setDefinition("b");
        assertNotEquals(a.hashCode(),
                        b.hashCode());

        b.setDefinition("a");
        b.setBounds(Bounds.create(0.0,
                                  0.0,
                                  5.0,
                                  5.0));
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        b.setBounds(Bounds.create(0.0,
                                  0.0,
                                  1.0,
                                  1.0));

        b.setSourceConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertNotEquals(a.hashCode(),
                        b.hashCode());

        a.setSourceConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertEquals(a.hashCode(),
                     b.hashCode());
        a.setSourceConnection(MagnetConnection.Builder.at(1.0,
                                                          1.0));
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        b.setSourceConnection(MagnetConnection.Builder.at(1.0,
                                                          1.0));

        b.setTargetConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        a.setTargetConnection(MagnetConnection.Builder.at(0.0,
                                                          0.0));
        assertEquals(a.hashCode(),
                     b.hashCode());
        a.setTargetConnection(MagnetConnection.Builder.at(3.0,
                                                          2.0));
        assertNotEquals(a.hashCode(),
                        b.hashCode());
    }
}
