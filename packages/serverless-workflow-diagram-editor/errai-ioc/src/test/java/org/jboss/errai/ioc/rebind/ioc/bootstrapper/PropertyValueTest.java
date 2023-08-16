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


package org.jboss.errai.ioc.rebind.ioc.bootstrapper;

import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PropertyValueTest {

    private static final String SYSTEM_PROPERTY_NAME = "errai.test";

    @Mock
    private InjectionContext context;

    @InjectMocks
    private IOCProcessor processor;

    @Test
    public void testGetPropertyValue() {
        assertFalse(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                               "true",
                                               false,
                                               false));
        assertTrue(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                              "True",
                                              true,
                                              false));
        assertTrue(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                              "true",
                                              true,
                                              true));
        assertTrue(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                              "test",
                                              true,
                                              false));
        assertFalse(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                               "false",
                                               false,
                                               true));
        System.setProperty(SYSTEM_PROPERTY_NAME,
                           "true");
        assertTrue(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                              "True",
                                              false,
                                              false));
        assertTrue(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                              "true",
                                              true,
                                              true));
        System.setProperty(SYSTEM_PROPERTY_NAME,
                           "false");
        assertFalse(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                               "true",
                                               true,
                                               true));
        assertFalse(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                               "true",
                                               true,
                                               false));
        assertFalse(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                               "test",
                                               true,
                                               false));
        System.setProperty(SYSTEM_PROPERTY_NAME,
                           "test");
        assertTrue(processor.getPropertyValue(SYSTEM_PROPERTY_NAME,
                                              "test",
                                              false,
                                              true));
    }
}
