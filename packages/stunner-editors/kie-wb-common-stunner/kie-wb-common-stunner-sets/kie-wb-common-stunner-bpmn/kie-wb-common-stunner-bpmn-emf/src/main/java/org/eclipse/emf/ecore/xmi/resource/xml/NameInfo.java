/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.emf.ecore.xmi.resource.xml;

/**
 * Implementation of the {@link NameInfo} interface.
 */
public class NameInfo {

    protected String localPart;
    protected String qualifiedName;
    protected String namespaceURI;

    public String getLocalPart() {
        return localPart;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setLocalPart(String name) {
        this.localPart = name;
    }

    public void setNamespaceURI(String uri) {
        this.namespaceURI = uri;
    }

    public void setQualifiedName(String name) {
        this.qualifiedName = name;
    }
}
