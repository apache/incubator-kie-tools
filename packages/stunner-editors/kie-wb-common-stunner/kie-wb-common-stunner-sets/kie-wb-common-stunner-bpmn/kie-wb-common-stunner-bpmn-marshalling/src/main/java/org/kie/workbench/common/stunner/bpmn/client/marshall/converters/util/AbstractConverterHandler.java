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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util;

import java.util.Iterator;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

// TODO: Kogito - check why generated code throws UnsupportedOperationException for some methods
public abstract class AbstractConverterHandler {

    protected abstract FeatureMap getMixed();

    protected String get_3_6() {
        return get(3, 6);
    }

    protected String get(int... ids) {
        final FeatureMap featureMap = getMixed();
        if (featureMap != null && !featureMap.isEmpty()) {
            StringBuilder result = new StringBuilder();
            Iterator var3 = featureMap.iterator();
            while (var3.hasNext()) {
                FeatureMap.Entry cur = (FeatureMap.Entry) var3.next();
                for (int id : ids) {
                    if (id == cur.getEStructuralFeature().getFeatureID()) {
                        result.append(cur.getValue());
                    }
                }
            }
            return result.toString();
        } else {
            return "";
        }
    }

    protected void set(String value) {
        set(XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text(), value);
    }

    protected void set(EAttribute attribute, String value) {
        final FeatureMap featureMap = getMixed();
        featureMap.clear();
        featureMap.add(attribute, value);
    }
}
