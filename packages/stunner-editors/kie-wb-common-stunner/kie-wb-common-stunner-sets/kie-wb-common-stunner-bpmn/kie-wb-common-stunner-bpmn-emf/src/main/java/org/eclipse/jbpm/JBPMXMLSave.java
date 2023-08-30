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

import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLHelper;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLMap;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLSave;
import org.eclipse.emf.ecore.xmi.util.ElementHandler;

public class JBPMXMLSave extends XMLSave {

    public JBPMXMLSave(XMLHelper helper) {
        super(helper);
    }

    @Override
    protected void init(XMLResource resource,
                        Map<?, ?> options) {
        super.init(resource,
                   options);
        featureTable = new JBPMLookup(map,
                                      extendedMetaData,
                                      elementHandler);
    }

    @Override
    public void traverse(List<? extends EObject> contents) {
        for (EObject e : contents) {
            if (e instanceof Definitions) {
                List<RootElement> roots = ((Definitions) e).getRootElements();
                Process p = null;
                for (RootElement root : roots) {
                    if (root instanceof Process) {
                        p = (Process) root;
                    }
                }
                if (p != null) {
                    ((Definitions) e).getRootElements().remove(p);
                    ((Definitions) e).getRootElements().add(p);
                }
            }
        }
        super.traverse(contents);
    }

    public static class JBPMLookup extends Lookup {

        public JBPMLookup(XMLMap map,
                          ExtendedMetaData extendedMetaData,
                          ElementHandler elementHandler) {
            super(map,
                  extendedMetaData,
                  elementHandler);
        }

        @Override
        public EStructuralFeature[] getFeatures(EClass cls) {
            int index = getIndex(cls);
            EClass c = classes[index];
            if (c == cls) {
                return features[index];
            }
            EStructuralFeature[] featureList = listFeatures(cls);
            if (c == null) {
                classes[index] = cls;
                features[index] = featureList;
                featureKinds[index] = listKinds(featureList);
            }
            if (cls.getName().equalsIgnoreCase("Process")) {
                EStructuralFeature[] modifiedFeatureList = getModifiedProcessFeatureSet(featureList);
                if (c == null) {
                    classes[index] = cls;
                    features[index] = modifiedFeatureList;
                    featureKinds[index] = listKinds(modifiedFeatureList);
                }
                return modifiedFeatureList;
            }
            return featureList;
        }
    }

    private static EStructuralFeature[] getModifiedProcessFeatureSet(EStructuralFeature[] processFeatureList) {
        /**
         Feature list for Process provided by eclipse.bpmn2:
         - extensionDefinitions (0)
         - id (1)
         - anyAttribute (2)
         - name (3)
         - definitionalCollaborationRef (4)
         - isClosed (5)
         - isExecutable (6)
         - processType (7)
         - extensionValues (8)
         - documentation (9)
         - supportedInterfaceRefs (10)
         - ioSpecification (11)
         - ioBinding (12)
         - laneSets (13)
         - flowElements (14)
         - auditing (15)
         - monitoring (16)
         - properties (17)
         - artifacts (18)
         - resources (19)
         - correlationSubscriptions (20)
         - supports (21)
         Semantic.xsd sequence definition for Process:
         <xsd:sequence>
         <xsd:element ref="auditing" minOccurs="0" maxOccurs="1"/>
         <xsd:element ref="monitoring" minOccurs="0" maxOccurs="1"/>
         <xsd:element ref="property" minOccurs="0" maxOccurs="unbounded"/>
         <xsd:element ref="laneSet" minOccurs="0" maxOccurs="unbounded"/>
         <xsd:element ref="flowElement" minOccurs="0" maxOccurs="unbounded"/>
         <xsd:element ref="artifact" minOccurs="0" maxOccurs="unbounded"/>
         <xsd:element ref="resourceRole" minOccurs="0" maxOccurs="unbounded"/>
         <xsd:element ref="correlationSubscription" minOccurs="0" maxOccurs="unbounded"/>
         <xsd:element name="supports" type="xsd:QName" minOccurs="0" maxOccurs="unbounded"/>
         </xsd:sequence>

         Moving auditing, monitoring, property above flowElements...
         */
        EStructuralFeature[] retArray = new EStructuralFeature[processFeatureList.length];
        for (int i = 0; i < 13; i++) {
            retArray[i] = processFeatureList[i];
        }
        retArray[13] = processFeatureList[15]; // auditing
        retArray[14] = processFeatureList[16]; // monitoring
        retArray[15] = processFeatureList[17]; // properties
        retArray[16] = processFeatureList[13]; // lanesets
        retArray[17] = processFeatureList[14]; // flow elements
        retArray[18] = processFeatureList[18]; // artifacts
        retArray[19] = processFeatureList[19]; // resources
        retArray[20] = processFeatureList[20]; // correlationSubscriptions
        retArray[21] = processFeatureList[21]; // supports
        return retArray;
    }
}
