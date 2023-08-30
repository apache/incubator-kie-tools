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

import org.eclipse.emf.common.util.URI;

public class URIHandler {


    public static class PlatformSchemeAware extends URIHandler {

        @Override
        public URI deresolve(URI uri) {
            return !uri.isPlatform() || (uri.segmentCount() > 0 && baseURI.segmentCount() > 0 && uri.segment(0).equals(baseURI.segment(0))) ?
                    super.deresolve(uri) : uri;
        }
    }

    protected URI baseURI;
    protected boolean resolve;

    public void setBaseURI(URI uri) {
        baseURI = uri;
        resolve = uri != null && uri.isHierarchical() && !uri.isRelative();
    }

    public URI resolve(URI uri) {
        return resolve && uri.isRelative() && uri.hasRelativePath() ? uri.resolve(baseURI) : uri;
    }

    public URI deresolve(URI uri) {
        if (resolve && !uri.isRelative()) {
            URI deresolvedURI = uri.deresolve(baseURI, true, true, false);
            if (deresolvedURI.hasRelativePath()) {
                uri = deresolvedURI;
            }
        }
        return uri;
    }

    public URI getBaseURI() {
        return baseURI;
    }
}
