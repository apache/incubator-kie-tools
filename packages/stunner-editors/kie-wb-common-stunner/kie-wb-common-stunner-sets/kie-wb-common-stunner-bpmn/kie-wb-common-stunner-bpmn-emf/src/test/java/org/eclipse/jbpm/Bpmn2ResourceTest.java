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


package org.eclipse.jbpm;

import java.util.ArrayList;
import java.util.Map;

import bpsim.BpsimPackage;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.emf.common.util.Reflect;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.util.ElementHandler;
import org.jboss.drools.DroolsPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class Bpmn2ResourceTest {

    private Bpmn2Resource tested;

    @Before
    public void setUp() {
        tested = spy(Bpmn2ResourceFactory.getInstance().create());
    }

    @Test
    public void testLoad() throws Exception {
        XMLParser p = mock(XMLParser.class);
        when(tested.createParser()).thenReturn(p);
        Document doc = mock(Document.class);
        when(p.parse(eq("someContent"))).thenReturn(doc);
        tested.load("someContent");
        verify(tested, times(1)).load(eq(doc));
    }

    @Test
    public void testInit() {
        testInitGwtRefectTypes();
    }

    private void testInitGwtRefectTypes() {
        assertTrue(Reflect.isInstance(Object.class, "Object"));
    }

    @Test
    public void testInitPackageRegistry() {
        EPackage.Registry packageRegistry = mock(EPackage.Registry.class);
        DroolsPackage drools = mock(DroolsPackage.class);
        BpsimPackage bpsim = mock(BpsimPackage.class);
        BpmnDiPackage bpmnDiPackage = mock(BpmnDiPackage.class);
        DiPackage diPackage = mock(DiPackage.class);
        DcPackage dcPackage = mock(DcPackage.class);
        Bpmn2Package bpmn2Package = mock(Bpmn2Package.class);
        Bpmn2Resource.initPackageRegistry(packageRegistry,
                                          drools,
                                          bpsim,
                                          bpmnDiPackage,
                                          diPackage,
                                          dcPackage,
                                          bpmn2Package);
        verify(packageRegistry, times(1)).put(Bpmn2Resource.URI_DROOLS, drools);
        verify(packageRegistry, times(1)).put(Bpmn2Resource.URI_BPSIM, bpsim);
        verify(packageRegistry, times(1)).put(Bpmn2Resource.URI_BPMN_DI, bpmnDiPackage);
        verify(packageRegistry, times(1)).put(Bpmn2Resource.URI_DI, diPackage);
        verify(packageRegistry, times(1)).put(Bpmn2Resource.URI_DC, dcPackage);
        verify(packageRegistry, times(1)).put(Bpmn2Resource.URI_BPMN2, bpmn2Package);
    }

    @Test
    public void testCreateLoadOptions() {
        Map<Object, Object> options = tested.createLoadOptions();
        assertCreateLoadOptions(options);
    }

    @Test
    public void testCreateSaveOptions() {
        Map<Object, Object> options = tested.createSaveOptions();
        assertCreateSaveOptions(options);
    }

    private void assertCreateLoadOptions(Map<Object, Object> options) {
        assertEquals(9, options.size());
        assertTrue((Boolean) options.get(XMLResource.OPTION_DOM_USE_NAMESPACES_IN_SCOPE));
        assertTrue(options.get(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP) instanceof Map);
        assertTrue((Boolean) options.get(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE));
        assertTrue(options.get(XMLResource.OPTION_EXTENDED_META_DATA) instanceof XmlExtendedMetadata);
        assertTrue((Boolean) options.get(XMLResource.OPTION_DEFER_IDREF_RESOLUTION));
        assertTrue((Boolean) options.get(XMLResource.OPTION_DISABLE_NOTIFY));
        assertEquals(XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD, options.get(XMLResource.OPTION_PROCESS_DANGLING_HREF));
        assertEquals("UTF-8", options.get(XMLResource.OPTION_ENCODING));
        assertTrue(options.get(XMLResource.OPTION_URI_HANDLER) instanceof QNameURIHandler);
    }

    private void assertCreateSaveOptions(Map<Object, Object> options) {
        assertEquals(10, options.size());
        assertTrue((Boolean) options.get(XMLResource.OPTION_DECLARE_XML));
        assertTrue((Boolean) options.get(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE));
        assertTrue(options.get(XMLResource.OPTION_EXTENDED_META_DATA) instanceof XmlExtendedMetadata);
        assertTrue((Boolean) options.get(XMLResource.OPTION_DEFER_IDREF_RESOLUTION));
        assertTrue((Boolean) options.get(XMLResource.OPTION_DISABLE_NOTIFY));
        assertEquals(XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD, options.get(XMLResource.OPTION_PROCESS_DANGLING_HREF));
        assertEquals("UTF-8", options.get(XMLResource.OPTION_ENCODING));
        assertTrue(options.get(XMLResource.OPTION_ELEMENT_HANDLER) instanceof ElementHandler);
        assertTrue(options.get(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE) instanceof ArrayList);
        assertTrue(options.get(XMLResource.OPTION_URI_HANDLER) instanceof QNameURIHandler);
    }
}
