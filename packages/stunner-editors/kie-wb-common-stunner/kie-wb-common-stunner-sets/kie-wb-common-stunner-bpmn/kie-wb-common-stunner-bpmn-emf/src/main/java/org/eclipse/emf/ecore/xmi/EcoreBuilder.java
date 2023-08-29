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


package org.eclipse.emf.ecore.xmi;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

/**
 * The interface describes an XML Schema to Ecore builder.
 */
public interface EcoreBuilder {

    /**
     * Given an XML schema location URI this method creates corresponding Ecore model(s)
     * @param uri - location of the XML Schema files.
     * @return Collection of resources containing the generated models.
     * @throws Exception
     * @see org.eclipse.emf.common.util.URI
     */
    public Collection<? extends Resource> generate(URI uri) throws Exception;

    /**
     * Given XML Schema location URIs this method creates corresponding Ecore model(s)
     * @param uris - locations of the XML Schema files.
     * @return Collection of resources containing the generated models.
     * @throws Exception
     * @see org.eclipse.emf.common.util.URI
     */
    public Collection<? extends Resource> generate(Collection<URI> uris) throws Exception;

    /**
     * Given a map of XML Schema targetNamespaces (String) to XML Schema location URIs, this method
     * generates corresponding Ecore model(s).
     * @param targetNamespaceToURI - a map of XML Schema targetNamespaces to XML Schema location URIs
     * @return Collection of resources containing the generated models.
     * @throws Exception
     * @see org.eclipse.emf.common.util.URI
     */
    public Collection<? extends Resource> generate(Map<String, URI> targetNamespaceToURI) throws Exception;

    /**
     * Sets extended meta data to register generated Ecore models.
     * Note the same extended meta data should be used for loading/saving an instance document.
     * @param extendedMetaData
     */
    public void setExtendedMetaData(ExtendedMetaData extendedMetaData);
}