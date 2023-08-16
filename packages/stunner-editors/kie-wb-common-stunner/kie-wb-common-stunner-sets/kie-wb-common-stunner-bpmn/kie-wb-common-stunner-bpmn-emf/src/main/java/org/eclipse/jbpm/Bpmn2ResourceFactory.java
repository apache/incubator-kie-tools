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

import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class Bpmn2ResourceFactory {

    private static final Bpmn2ResourceFactory INSTANCE = new Bpmn2ResourceFactory();

    private ResourceSet resourceSet;

    private Bpmn2ResourceFactory() {
        init();
    }

    public static Bpmn2ResourceFactory getInstance() {
        return INSTANCE;
    }

    private void init() {
        resourceSet = new ResourceSetImpl();
        resourceSet.setURIConverter(new ExtensibleURIConverterImpl());
        final Resource.Factory.Registry resourceFactoryRegistry = resourceSet.getResourceFactoryRegistry();
        resourceFactoryRegistry.getExtensionToFactoryMap().put(
                Resource.Factory.Registry.DEFAULT_EXTENSION, new ResourceFactoryImpl() {
                    @Override
                    public Resource createResource(URI uri) {
                        return new Bpmn2Resource(uri);
                    }
                });
    }

    public Bpmn2Resource create() {
        return Optional.ofNullable(resourceSet.createResource(URI.createURI("file://dummyUri.xml")))
                .filter(resource -> resource instanceof Bpmn2Resource)
                .map(resource -> (Bpmn2Resource) resource)
                .orElseThrow(() -> new RuntimeException("Bpmn2Resource cannot be created"));
    }
}
