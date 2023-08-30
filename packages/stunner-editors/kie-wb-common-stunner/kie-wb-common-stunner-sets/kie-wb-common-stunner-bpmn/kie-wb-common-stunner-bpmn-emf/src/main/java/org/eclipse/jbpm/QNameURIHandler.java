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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.resource.xml.URIHandler;

/**
 * This simple XMLResource URI Handler converts between the QName-based reference model in BPMN 2.0 and the URI based model in EMF.
 * <p>
 * The prefix can be resolved using the prefix declaration in the file, then going to the "imports" element, searching for a fitting import and taking this file as baseURI.
 */
public class QNameURIHandler extends URIHandler {

    private final BpmnXmlHelper xmlHelper;

    /**
     *
     */
    public QNameURIHandler(BpmnXmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    /**
     * The method converts a QName, e.g. "ns:element1" to a URI string, e.g. file1.bpmn#element1.
     * The method is called during load.
     * @param qName
     * @return
     */
    public String convertQNameToUri(String qName) {
        if (qName.contains("#") || qName.contains("/")) {
            // We already have an URI and not QName, e.g. URL
            return qName;
        }

        // Split into prefix and local part (fragment)
        String[] parts = qName.split(":");
        String prefix, fragment;
        if (parts.length == 1) {
            prefix = null;
            fragment = qName;
        } else if (parts.length == 2) {
            prefix = parts[0];
            fragment = parts[1];
        } else {
            throw new IllegalArgumentException("Illegal QName: " + qName);
        }

        if (fragment.contains(".")) {
            // HACK: officially IDs can contain ".", but unfortunately XmlHandler calls resolve also for xsi:schemaLocation stuff and similar, that are
            // NO URIs. We must not process them.
            return qName;
        }

        if (!xmlHelper.isTargetNamespace(prefix)) {
            return xmlHelper.getPathForPrefix(prefix).appendFragment(fragment).toString();
        } else {
            return baseURI.appendFragment(fragment).toString();
        }
    }

    /**
     * Called from the framework during load. We will resolve to an absolute URI after - necessarily creating
     * a relative URI from a QName.
     */
    @Override
    public URI resolve(URI uri) {
        return super.resolve(URI.createURI(convertQNameToUri(uri.toString())));
    }

    /**
     * Called from the framework during save. We deresolve absolute URIs to relative ones. Then we try to
     * convert to QName
     */
    @Override
    public URI deresolve(URI uri) {
        String fragment = uri.fragment();
        if (fragment != null && !fragment.startsWith("/")) // We better don't try to QName XPath references to e.g. XML or WSDL context for now.
        {
            String prefix = "";

            if (uri.hasPath()) {
                prefix = xmlHelper.getNsPrefix(uri.trimFragment());
            }
            if (prefix.length() > 0) {
                return URI.createURI(prefix + ":" + fragment);
            } else
            // no prefix, just fragment (i.e. without the '#')
            {
                return URI.createURI(fragment);
            }
        }
        return super.deresolve(uri);
    }
}