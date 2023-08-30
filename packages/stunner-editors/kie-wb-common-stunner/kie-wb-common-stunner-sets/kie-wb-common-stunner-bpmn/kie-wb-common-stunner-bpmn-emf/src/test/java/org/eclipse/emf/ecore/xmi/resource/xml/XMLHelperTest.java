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


package org.eclipse.emf.ecore.xmi.resource.xml;

import java.util.function.Consumer;

import org.eclipse.bpmn2.impl.BaseElementImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.emf.Bpmn2Marshalling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XMLHelperTest {

    private XMLHelper tested;

    @Before
    public void setUp() {
        tested = new XMLHelper();
    }

    @Test
    public void testGetEObjectHREF() {
        BaseElementImpl obj = mock(BaseElementImpl.class);
        when(obj.eProxyURI()).thenReturn(URI.createURI("somePath"));
        String href = tested.getHREF(obj);
        assertNull(href);
        when(obj.getId()).thenReturn("id1");
        href = tested.getHREF(obj);
        assertEquals("id1", href);
    }

    @Test
    public void testSafeSetValue() {
        BaseElementImpl obj = mock(BaseElementImpl.class);
        EStructuralFeature feature = mock(EStructuralFeature.class);
        when(feature.getEType()).thenThrow(new UnsupportedOperationException());
        Consumer<String> logger = mock(Consumer.class);
        Bpmn2Marshalling.setLogger(logger);
        tested.setValue(obj, feature, "Object", 1);
        verify(logger, times(1)).accept(anyString());
    }
}
