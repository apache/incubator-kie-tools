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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;

public class XmlExtendedMetadata extends BasicExtendedMetaData {

    private static Map<String, String> xmiToXmlNamespaceMap;

    private static void initXmiToXmlNamespaceMap() {
        xmiToXmlNamespaceMap = new HashMap(6);
        String[] namespaces = new String[]{
                "http://www.omg.org/spec/BPMN/20100524/MODEL-XMI",
                "http://www.omg.org/spec/BPMN/20100524/DI-XMI",
                "http://www.omg.org/spec/DD/20100524/DI-XMI",
                "http://www.omg.org/spec/DD/20100524/DC-XMI"
        };
        String[] var4 = namespaces;
        int var3 = namespaces.length;

        for (int var2 = 0; var2 < var3; ++var2) {
            String curNs = var4[var2];
            xmiToXmlNamespaceMap.put(curNs, xmiToXsdNamespaceUri(curNs));
        }
    }

    public XmlExtendedMetadata() {
    }

    public String getNamespace(EPackage ePackage) {
        if (xmiToXmlNamespaceMap == null) {
            initXmiToXmlNamespaceMap();
        }

        String ns = super.getNamespace(ePackage);
        String xmlNs;
        return (xmlNs = (String) xmiToXmlNamespaceMap.get(ns)) != null ? xmlNs : ns;
    }

    public EClassifier getType(EPackage ePackage, String name) {
        if (Bpmn2Package.eINSTANCE.equals(ePackage)) {
            if ("tBaseElementWithMixedContent".equals(name)) {
                return Bpmn2Package.Literals.BASE_ELEMENT;
            }

            if ("tImplementation".equals(name)) {
                return org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING;
            }

            if ("tScript".equals(name)) {
                return org.eclipse.emf.ecore.EcorePackage.Literals.EOBJECT;
            }

            if ("tText".equals(name)) {
                return org.eclipse.emf.ecore.EcorePackage.Literals.EOBJECT;
            }

            if ("tTransactionMethod".equals(name)) {
                return org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING;
            }
        }

        return super.getType(ePackage, name);
    }

    public static String xmiToXsdNamespaceUri(String xmiNsUri) {
        if (!xmiNsUri.endsWith("-XMI")) {
            throw new IllegalArgumentException("XMI namespace expected");
        } else {
            return xmiNsUri.substring(0, xmiNsUri.length() - 4);
        }
    }
}
