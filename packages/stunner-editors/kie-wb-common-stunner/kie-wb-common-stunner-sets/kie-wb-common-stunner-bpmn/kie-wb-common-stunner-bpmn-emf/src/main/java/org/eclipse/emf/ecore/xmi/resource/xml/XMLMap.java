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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class XMLMap {

    protected static final String XSD2ECORE = "http:///org/eclipse/emf/mapping/xsd2ecore/XSD2Ecore";

    protected Map<ENamedElement, XMLInfo> ecoreToXMLInfo;
    protected EPackage noNamespacePkg;
    protected String idAttributeName;
    protected Map<String, Map<String, EClassifier>> urisToNamesToClassifiers;
    protected Map<EClass, EList<EStructuralFeature>> eClassToFeatures;
    protected Set<EPackage> processedEPackages;
    protected EPackage.Registry packageRegistry = EPackage.Registry.INSTANCE;

    /**
     * Constructor for XMLMap.
     */
    public XMLMap() {
        super();
        ecoreToXMLInfo = new HashMap<ENamedElement, XMLInfo>();
        processedEPackages = new HashSet<EPackage>();
        eClassToFeatures = new HashMap<EClass, EList<EStructuralFeature>>();
    }

    /**
     * Add an XMLInfo object for an Ecore construct to
     * the map.
     */
    public void add(ENamedElement element, XMLInfo info) {
        ecoreToXMLInfo.put(element, info);
    }

    /**
     * Returns the XMLInfo object for the given Ecore
     * construct, if there is one.
     */
    public XMLInfo getInfo(ENamedElement element) {
        XMLInfo result = ecoreToXMLInfo.get(element);
        if (result == null) {
            for (EAnnotation eAnnotation : element.getEAnnotations()) {
                if (XSD2ECORE.equals(eAnnotation.getSource())) {
                    result = new XMLInfo();
                    EMap<String, String> details = eAnnotation.getDetails();
                    result.setName(details.get("name"));
                    result.setTargetNamespace(details.get("targetNamespace"));
                    String representation = details.get("representation");
                    if ("element".equals(representation)) {
                        result.setXMLRepresentation(XMLInfo.ELEMENT);
                    } else if ("attribute".equals(representation)) {
                        result.setXMLRepresentation(XMLInfo.ATTRIBUTE);
                    } else if ("simple-content".equals(representation)) {
                        result.setXMLRepresentation(XMLInfo.CONTENT);
                    }
                }
            }

            if (result == null) {
                // result = new XMLInfoImpl();
                // result.setXMLRepresentation(XMLInfo.UNSPECIFIED);
            } else {
                ecoreToXMLInfo.put(element, result);
            }
        }

        return result;
    }

    public void setNoNamespacePackage(EPackage pkg) {
        noNamespacePkg = pkg;
    }

    public EPackage getNoNamespacePackage() {
        return noNamespacePkg;
    }

    public void setIDAttributeName(String name) {
        idAttributeName = name;
    }

    public String getIDAttributeName() {
        return idAttributeName;
    }

    public void setPackageRegistry(EPackage.Registry packageRegistry) {
        this.packageRegistry = packageRegistry;
    }

    public EPackage.Registry getPackageRegistry() {
        return packageRegistry;
    }

    public EClassifier getClassifier(String namespaceURI, String name) {
        EPackage ePackage = packageRegistry.getEPackage(namespaceURI);
        if (ePackage != null) {
            if (processedEPackages.add(ePackage)) {
                if (urisToNamesToClassifiers == null) {
                    urisToNamesToClassifiers = new HashMap<String, Map<String, EClassifier>>();
                }

                getInfoForClassifiers(ePackage);

                for (Map.Entry<ENamedElement, XMLInfo> entry : ecoreToXMLInfo.entrySet()) {
                    Object key = entry.getKey();

                    // Only handle classifiers from this package.
                    //
                    if (key instanceof EClassifier) {
                        EClassifier eClassifier = (EClassifier) key;
                        if (eClassifier.getEPackage() == ePackage) {
                            XMLInfo info = entry.getValue();
                            String uri = info.getTargetNamespace();
                            if (uri == null) {
                                uri = namespaceURI;
                            }

                            if (info.getName() != null) {
                                Map<String, EClassifier> namesToClassifiers = urisToNamesToClassifiers.get(uri);

                                if (namesToClassifiers == null) {
                                    namesToClassifiers = new HashMap<String, EClassifier>();
                                    urisToNamesToClassifiers.put(uri, namesToClassifiers);
                                }

                                namesToClassifiers.put(info.getName(), eClassifier);
                            }
                        }
                    }
                }
            }

            Map<String, EClassifier> namesToClassifiers = urisToNamesToClassifiers.get(namespaceURI);
            if (namesToClassifiers != null) {
                return namesToClassifiers.get(name);
            }
        }

        return null;
    }

    /**
     * Get the XMLInfo for all of the classifiers of the
     * package with the given name.
     */
    private void getInfoForClassifiers(EPackage ePackage) {
        for (EClassifier eClassifier : ePackage.getEClassifiers()) {
            getInfo(eClassifier);
        }
    }

    public EStructuralFeature getFeature(EClass eClass, String namespaceURI, String name) {
        for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
            EStructuralFeature feature = eClass.getEStructuralFeature(i);
            XMLInfo info = getInfo(feature);

            if (info != null) {
                String infoURI = info.getTargetNamespace();
                String infoName = info.getName();
                if (namespaceURI == null) {
                    // We effectively passed null, "" to look up the general-content feature.
                    //
                    if (infoURI == null && (name.equals(infoName) || infoName == null && name.length() == 0)) {
                        return feature;
                    }
                } else if (namespaceURI.equals(infoURI) && name.equals(infoName)) {
                    return feature;
                }
            }
        }

        return null;
    }

    public List<EStructuralFeature> getFeatures(EClass eClass) {
        EList<EStructuralFeature> result = eClassToFeatures.get(eClass);
        if (result == null) {
            result = new UniqueEList<EStructuralFeature>();
            for (EClass eSuperType : eClass.getESuperTypes()) {
                result.addAll(getFeatures(eSuperType));
            }
            List<EAttribute> eAttributes = eClass.getEAttributes();
            result.addAll(eAttributes);
            List<EReference> eReferences = eClass.getEReferences();
            result.addAll(eReferences);

            EAnnotation eAnnotation = eClass.getEAnnotation(XSD2ECORE);
            if (eAnnotation != null) {
                String featureOrder = eAnnotation.getDetails().get("feature-order");
                if (featureOrder != null) {
                    int size = result.size();
                    int index = size - eReferences.size() - eAttributes.size();
                    String[] features = featureOrder.split(" ");
                    for (int f = 0; f < featureOrder.length(); ++index, ++f) {
                        String featureName = features[f];
                        for (int i = index; i < size; ++i) {
                            EStructuralFeature eStructuralFeature = result.get(i);
                            if (featureName.equals(eStructuralFeature.getName())) {
                                result.move(index, eStructuralFeature);
                                break;
                            }
                        }
                    }
                }
            }
            eClassToFeatures.put(eClass, result);
        }

        return result;
    }
}
