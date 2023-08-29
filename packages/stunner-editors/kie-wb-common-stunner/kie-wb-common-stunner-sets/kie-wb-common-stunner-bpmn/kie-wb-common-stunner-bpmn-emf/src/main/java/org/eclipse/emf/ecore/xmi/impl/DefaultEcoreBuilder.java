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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.EcoreBuilder;

public class DefaultEcoreBuilder implements EcoreBuilder {

    private ExtendedMetaData extendedMetaData;

    public DefaultEcoreBuilder(final ExtendedMetaData extendedMetaData) {
        this.extendedMetaData = extendedMetaData;
    }

    @Override
    public Collection<? extends Resource> generate(URI uri) throws Exception {
        return generate(Collections.singleton(uri));
    }

    @Override
    public Collection<? extends Resource> generate(Collection<URI> uris) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Resource> generate(Map<String, URI> targetNamespaceToURI) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public void setExtendedMetaData(ExtendedMetaData extendedMetaData) {
        this.extendedMetaData = extendedMetaData;
    }
}
