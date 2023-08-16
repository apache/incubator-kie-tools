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

package org.eclipse.emf.ecore.xmi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.EcoreBuilder;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.DefaultEcoreBuilder;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLDOMHandler;
import org.xml.sax.SAXException;

/**
 * XMLProcessor provides helper methods to serialize and deserialize XML using EMF framework.
 */
public class XMLProcessor {

    protected EcoreBuilder ecoreBuilder;

    protected final static String XML_EXTENSION = "xml";

    protected final static String STAR_EXTENSION = "*";

    protected final static URI XML_URI = URI.createFileURI(XML_EXTENSION);

    protected Map<String, Resource.Factory> registrations;

    protected Map<Object, Object> loadOptions = new HashMap<Object, Object>();

    protected Map<Object, Object> saveOptions = new HashMap<Object, Object>();

    protected ExtendedMetaData extendedMetaData;

    protected EPackage.Registry registry;

    protected XMLProcessor(EPackage.Registry registry) {
        this.registry = registry;
        this.extendedMetaData = createExtendedMetaData();
        ecoreBuilder = createEcoreBuilder();
        loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        loadOptions.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<String, EStructuralFeature>());
        loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.FALSE);
        loadOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        saveOptions.put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList<>());
        saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
    }

    public XMLProcessor() {
        this.extendedMetaData = createExtendedMetaData();
        ecoreBuilder = createEcoreBuilder();
        loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.FALSE);
        loadOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
        saveOptions.put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
    }

    public XMLProcessor(URI schemaURI) throws SAXException {
        this(Collections.singleton(schemaURI));
    }

    public XMLProcessor(Collection<URI> schemaURIs) throws SAXException {
        this(new EPackageRegistryImpl());
        loadOptions.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        try {
            for (Resource resource : ecoreBuilder.generate(schemaURIs)) {
                for (EPackage ePackage : EcoreUtil.<EPackage>getObjectsByType(resource.getContents(), EcorePackage.Literals.EPACKAGE)) {
                    EcoreUtil.freeze(ePackage);
                }
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    protected Map<String, Resource.Factory> getRegistrations() {
        if (registrations == null) {
            Map<String, Resource.Factory> result = new HashMap<String, Resource.Factory>();
            result.put(STAR_EXTENSION, new XMLResourceFactoryImpl());
            registrations = result;
        }

        return registrations;
    }

    public EPackage.Registry getEPackageRegistry() {
        return registry;
    }

    public ExtendedMetaData getExtendedMetaData() {
        return extendedMetaData;
    }

    public Resource load(Node node, Map<?, ?> options) throws IOException {
        ResourceSet resourceSet = createResourceSet();
        XMLResource resource = (XMLResource) resourceSet.createResource(XML_URI);
        if (options != null) {
            Map<Object, Object> mergedOptions = new HashMap<Object, Object>(loadOptions);
            mergedOptions.putAll(options);
            resource.load(node, mergedOptions);
        } else {
            resource.load(node, loadOptions);
        }
        resourceSet.getPackageRegistry().putAll(registry);
        return resource;
    }

    public void save(OutputStream outputStream, Resource resource, Map<?, ?> options) throws IOException {
        if (options != null) {
            Map<Object, Object> mergedOptions = new HashMap<Object, Object>(saveOptions);
            mergedOptions.putAll(options);
            resource.save(outputStream, mergedOptions);
        } else {
            resource.save(outputStream, saveOptions);
        }
    }

    public void save(Document document, Resource resource, XMLDOMHandler handler, Map<?, ?> options) throws IOException {
        if (options != null) {
            Map<Object, Object> mergedOptions = new HashMap<Object, Object>(saveOptions);
            mergedOptions.putAll(options);
            ((XMLResource) resource).save(document, mergedOptions, handler);
        } else {
            ((XMLResource) resource).save(document, saveOptions, handler);
        }
    }

    public String saveToString(Resource resource, Map<?, ?> options) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (options != null) {
            Map<Object, Object> mergedOptions = new HashMap<Object, Object>(saveOptions);
            mergedOptions.putAll(options);

            ((XMLResource) resource).save(os, mergedOptions);
        } else {
            ((XMLResource) resource).save(os, saveOptions);
        }
        return os.toString();
    }

    protected ResourceSet createResourceSet() {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().putAll(getRegistrations());
        return resourceSet;
    }

    protected EcoreBuilder createEcoreBuilder() {
        return new DefaultEcoreBuilder(extendedMetaData);
    }

    /**
     * This methods can be used to provide a different ExtendedMetaData.
     * Note that if this method creates a new EPackage.Registry it must also assign the global registry
     * variable.
     * @return ExtendedMetaData
     */
    protected ExtendedMetaData createExtendedMetaData() {
        if (registry == null) {
            registry = new EPackageRegistryImpl();
        }
        return new BasicExtendedMetaData(registry);
    }
}
