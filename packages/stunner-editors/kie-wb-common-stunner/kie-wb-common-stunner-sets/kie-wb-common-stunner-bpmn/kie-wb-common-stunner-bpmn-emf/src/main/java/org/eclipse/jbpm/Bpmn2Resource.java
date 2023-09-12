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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bpsim.BpsimPackage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.emf.common.util.Reflect;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLSave;
import org.eclipse.emf.ecore.xmi.util.ElementHandler;
import org.jboss.drools.DroolsPackage;
import org.omg.spec.bpmn.non.normative.color.impl.ColorPackageImpl;

public class Bpmn2Resource extends XMLResourceImpl {

    static final String URI_BPMN2 = "http://www.omg.org/spec/BPMN/20100524/MODEL";
    static final String URI_DI = "http://www.omg.org/spec/DD/20100524/DI";
    static final String URI_DC = "http://www.omg.org/spec/DD/20100524/DC";
    static final String URI_BPMN_DI = "http://www.omg.org/spec/BPMN/20100524/DI";
    static final String URI_DROOLS = "http://www.jboss.org/drools";
    static final String URI_BPSIM = "http://www.bpsim.org/schemas/1.0";
    private final QNameURIHandler uriHandler;

    static {
        initPackageRegistry(EPackage.Registry.INSTANCE,
                            DroolsPackage.eINSTANCE,
                            BpsimPackage.eINSTANCE,
                            BpmnDiPackage.eINSTANCE,
                            DiPackage.eINSTANCE,
                            DcPackage.eINSTANCE,
                            Bpmn2Package.eINSTANCE);
        initGwtReflectTypes();
    }

    protected Bpmn2Resource(URI uri) {
        super(uri);
        this.uriHandler = new QNameURIHandler(new BpmnXmlHelper(this));
    }

    public void load(Node node) throws IOException {
        super.load(node, createLoadOptions());
    }

    public void load(String contents) throws IOException {
        XMLParser parser = createParser();
        Document doc = parser.parse(contents);
        load(doc);
    }

    public String toBPMN2() throws IOException {
        String raw = "";
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            this.save(outputStream, createSaveOptions());
            raw = outputStream.toString("UTF-8");
        }
        return raw;
    }

    @Override
    protected XMLSave createXMLSave() {
        return new JBPMXMLSave(createXMLHelper()) {
            @Override
            protected boolean shouldSaveFeature(EObject o, EStructuralFeature f) {
                if (Bpmn2Package.eINSTANCE.getDocumentation_Text().equals(f)) {
                    return false;
                }
                if (Bpmn2Package.eINSTANCE.getFormalExpression_Body().equals(f)) {
                    return false;
                }
                return super.shouldSaveFeature(o,
                                               f);
            }
        };
    }

    XMLParser createParser() {
        return GWT.create(XMLParser.class);
    }

    Map<Object, Object> createSaveOptions() {
        final Map<Object, Object> options = createDefaultOptions();
        options.put(XMLResource.OPTION_DECLARE_XML, true);
        options.put(XMLResource.OPTION_ELEMENT_HANDLER, new ElementHandler(true));
        options.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList<>());
        return options;
    }

    Map<Object, Object> createLoadOptions() {
        Map<Object, Object> options = createDefaultOptions();
        options.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<>());
        options.put(XMLResource.OPTION_DOM_USE_NAMESPACES_IN_SCOPE, true);
        return options;
    }

    static void initGwtReflectTypes() {
        Reflect.register(Object.class, new Reflect.Helper() {
            @Override
            public boolean isInstance(Object instance) {
                return true;
            }

            @Override
            public Object newArrayInstance(int size) {
                return new Object[size];
            }
        });
    }

    static void initPackageRegistry(EPackage.Registry packageRegistry,
                                    DroolsPackage drools,
                                    BpsimPackage bpsim,
                                    BpmnDiPackage bpmnDiPackage,
                                    DiPackage diPackage,
                                    DcPackage dcPackage,
                                    Bpmn2Package bpmn2Package) {
        packageRegistry.put(URI_BPMN2, bpmn2Package);
        packageRegistry.put(URI_DI, diPackage);
        packageRegistry.put(URI_DC, dcPackage);
        packageRegistry.put(URI_BPMN_DI, bpmnDiPackage);
        packageRegistry.put(URI_DROOLS, drools);
        packageRegistry.put(URI_BPSIM, bpsim);
        ColorPackageImpl.init();
    }

    private Map<Object, Object> createDefaultOptions() {
        final Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        options.put(XMLResource.OPTION_EXTENDED_META_DATA, new XmlExtendedMetadata());
        options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, true);
        options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, true);
        options.put(XMLResource.OPTION_DISABLE_NOTIFY, true);
        options.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
        //*important* to resolve property proxy (BasicEObjectImpl.eResolveProxy)
        options.put(XMLResource.OPTION_URI_HANDLER, uriHandler);
        return options;
    }
}
