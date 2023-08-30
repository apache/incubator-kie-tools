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

package org.eclipse.emf.ecore.xmi.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.resource.xml.URIHandler;

public class EcoreResourceFactoryImpl extends ResourceFactoryImpl {

    /**
     * Constructor for EcoreResourceFactoryImpl.
     */
    public EcoreResourceFactoryImpl() {
        super();
    }

    @Override
    public Resource createResource(URI uri) {
        XMLResource result =
                new XMLResourceImpl(uri) {
                    @Override
                    protected boolean useIDs() {
                        return eObjectToIDMap != null || idToEObjectMap != null;
                    }
                };
        result.setEncoding("UTF-8");

        result.getDefaultSaveOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
        result.getDefaultSaveOptions().put(XMLResource.OPTION_LINE_WIDTH, 80);
        result.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER, new URIHandler.PlatformSchemeAware());
        return result;
    }
}
