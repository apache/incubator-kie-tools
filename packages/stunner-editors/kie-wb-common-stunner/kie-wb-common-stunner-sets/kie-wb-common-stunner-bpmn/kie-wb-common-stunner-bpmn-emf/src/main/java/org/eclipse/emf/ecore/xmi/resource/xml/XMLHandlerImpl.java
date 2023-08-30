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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;

public class XMLHandlerImpl extends XMLHandler {

    /**
     * Constructor.
     */
    public XMLHandlerImpl(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
        super(xmiResource, helper, options);
    }

    protected String getXSIType() {
        return isNamespaceAware ? attribs.getValue(ExtendedMetaData.XSI_URI, XMLResource.TYPE) : attribs.getValue(TYPE_ATTRIB);
    }

    /**
     * Process the XML attributes for the newly created object.
     */
    protected void handleObjectAttribs(EObject obj) {
        if (attribs != null) {
            InternalEObject internalEObject = (InternalEObject) obj;
            for (int i = 0, size = attribs.getLength(); i < size; ++i) {
                String name = attribs.getQName(i);
                if (name.equals(idAttribute)) {
                    xmlResource.setID(internalEObject, attribs.getValue(i));
                } else if (name.equals(hrefAttribute) && (!recordUnknownFeature || types.peek() != UNKNOWN_FEATURE_TYPE || obj.eClass() != anyType)) {
                    handleProxy(internalEObject, attribs.getValue(i));
                } else if (isNamespaceAware) {
                    String namespace = attribs.getURI(i);
                    if (!ExtendedMetaData.XSI_URI.equals(namespace)) {
                        setAttribValue(obj, name, attribs.getValue(i));
                    }
                } else if (!name.startsWith(XMLResource.XML_NS) && !notFeatures.contains(name)) {
                    setAttribValue(obj, name, attribs.getValue(i));
                }
            }
        }
    }

    protected void processObject(EObject object) {
        if (object != null) {
            EStructuralFeature valueFeature = getContentFeature(object);

            if (valueFeature != null) {
                text = new StringBuffer();
                objects.push(object);
                types.push(valueFeature);
                return;
            }
        }

        super.processObject(object);
    }

    protected EStructuralFeature getContentFeature(EObject object) {
        if (xmlMap != null) {
            List<EAttribute> eAttributes = object.eClass().getEAllAttributes();
            if (eAttributes.size() >= 1) {
                EAttribute eAttribute = eAttributes.get(0);
                XMLInfo info = xmlMap.getInfo(eAttribute);
                if (info != null && info.getXMLRepresentation() == XMLInfo.CONTENT) {
                    return eAttribute;
                }
            }
        }

        return null;
    }
}
