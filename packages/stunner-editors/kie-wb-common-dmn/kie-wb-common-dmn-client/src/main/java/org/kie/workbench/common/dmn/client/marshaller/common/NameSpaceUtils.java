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
package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;

public class NameSpaceUtils {

    public static Map<String, String> extractNamespacesKeyedByUri(final JSITDMNElement jsiDMNElement) {
        final Map<String, String> namespaces = new HashMap<>();
        final Map<QName, String> otherAttributes = JSITDMNElement.getOtherAttributesMap(jsiDMNElement);

        //Filter otherAttributes by NameSpace definitions
        for (Map.Entry<QName, String> e : otherAttributes.entrySet()) {
            final QName qName = e.getKey();
            final String nsLocalPart = qName.getLocalPart();
            final String nsNamespaceURI = qName.getNamespaceURI();
            if (Objects.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, nsNamespaceURI)) {
                if (!Objects.equals(XMLConstants.XMLNS_ATTRIBUTE, nsLocalPart)) {
                    namespaces.put(e.getValue(), nsLocalPart);
                }
            }
        }
        return namespaces;
    }

    public static Map<String, String> extractNamespacesKeyedByPrefix(final JSITDMNElement jsiDMNElement) {
        final Map<String, String> namespaces = new HashMap<>();
        final Map<QName, String> otherAttributes = JSITDMNElement.getOtherAttributesMap(jsiDMNElement);

        //Filter otherAttributes by NameSpace definitions
        for (Map.Entry<QName, String> e : otherAttributes.entrySet()) {
            final QName qName = e.getKey();
            final String nsLocalPart = qName.getLocalPart();
            final String nsNamespaceURI = qName.getNamespaceURI();
            if (Objects.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, nsNamespaceURI)) {
                if (!Objects.equals(XMLConstants.XMLNS_ATTRIBUTE, nsLocalPart)) {
                    namespaces.put(nsLocalPart, e.getValue());
                }
            }
        }
        return namespaces;
    }
}
