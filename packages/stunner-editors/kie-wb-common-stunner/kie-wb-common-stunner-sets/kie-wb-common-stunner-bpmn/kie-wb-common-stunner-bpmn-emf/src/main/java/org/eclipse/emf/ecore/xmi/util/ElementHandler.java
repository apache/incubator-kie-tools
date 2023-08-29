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


package org.eclipse.emf.ecore.xmi.util;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

public class ElementHandler {

    protected boolean considerSubtypes;
    protected Collection<? extends EPackage> ePackages;

    /**
     * Creates a default instances.
     * @param considerSubtypes whether to consider {@link ExtendedMetaData#getBaseType(EDataType) base types} or {@link EClass#getESuperTypes() super types}
     * when finding a substitution group.
     */
    public ElementHandler(boolean considerSubtypes) {
        this.considerSubtypes = considerSubtypes;
    }

    /**
     * Creates an instance that will search the given packages for candidate features.
     * @param considerSubtypes whether to consider {@link ExtendedMetaData#getBaseType(EDataType) base types} or {@link EClass#getESuperTypes() super types}
     * when finding a substitution group.
     * @param ePackages the packages to search for candidates.
     */
    public ElementHandler(boolean considerSubtypes, Collection<? extends EPackage> ePackages) {
        this.considerSubtypes = considerSubtypes;
        this.ePackages = ePackages;
    }

    public EStructuralFeature getRoot(ExtendedMetaData extendedMetaData, EClassifier eClassifier) {
        if (extendedMetaData == null) {
            return null;
        } else {
            // Walk up the super types until we reach a root.
            //
            while (eClassifier != null) {
                // Look for a matching element in the classifier's package but don't bother with the XML type package's document root.
                //
                EClass eClass = extendedMetaData.getDocumentRoot(eClassifier.getEPackage());
                if (eClass != null && eClass != XMLTypePackage.Literals.XML_TYPE_DOCUMENT_ROOT) {
                    for (EStructuralFeature element : extendedMetaData.getElements(eClass)) {
                        if (element.getEType() == eClassifier && element.isChangeable()) {
                            return element;
                        }
                    }
                }

                // Look for a matching element in the specified packages if there are any.
                //
                if (ePackages != null) {
                    for (EPackage ePackage : ePackages) {
                        eClass = extendedMetaData.getDocumentRoot(ePackage);
                        if (eClass != null) {
                            for (EStructuralFeature element : extendedMetaData.getElements(eClass)) {
                                if (element.getEType() == eClassifier && element.isChangeable()) {
                                    return element;
                                }
                            }
                        }
                    }
                }
                eClassifier = getSuperType(extendedMetaData, eClassifier);
            }
            return null;
        }
    }

    /**
     * Returns the {@link ExtendedMetaData#getBaseType(EDataType) base type} or first {@link EClass#getESuperTypes() super type} of the classifier,
     * depending on there it is a {@link EDataType data type} or a {@link EClass class}.
     * @param extendedMetaData the extended meta data in which to look up type information.
     * @param eClassifier the classifier in question.
     * @return the {@link ExtendedMetaData#getBaseType(EDataType) base type}, the first {@link EClass#getESuperTypes() super type} of the classifier, or <code>null</code>.
     */
    protected EClassifier getSuperType(ExtendedMetaData extendedMetaData, EClassifier eClassifier) {
        if (eClassifier instanceof EDataType) {
            return extendedMetaData.getBaseType((EDataType) eClassifier);
        } else {
            List<EClass> eSuperTypes = ((EClass) eClassifier).getESuperTypes();
            if (eSuperTypes.isEmpty()) {
                return null;
            } else {
                return eSuperTypes.get(0);
            }
        }
    }

    public EStructuralFeature getSubstitutionGroup(ExtendedMetaData extendedMetaData, EStructuralFeature eStructuralFeature, EClassifier eClassifier) {
        if (extendedMetaData == null) {
            return null;
        } else {
            // Look for a substitution group feature in the feature's containing class' containing package.
            //
            EClass eContainingClass = eStructuralFeature.getEContainingClass();
            while (eClassifier != null) {
                EStructuralFeature result = getSubstitutionGroup(extendedMetaData, eContainingClass.getEPackage(), eContainingClass, eStructuralFeature, eClassifier);
                if (result != null) {
                    return result;
                } else {
                    // Look for a substitution group feature in the classifier's containing package.
                    //
                    result = getSubstitutionGroup(extendedMetaData, eClassifier.getEPackage(), eContainingClass, eStructuralFeature, eClassifier);
                    if (result != null) {
                        return result;
                    } else {
                        // Look for a substitution group feature in the additional packages.
                        //
                        if (ePackages != null) {
                            for (EPackage ePackage : ePackages) {
                                result = getSubstitutionGroup(extendedMetaData, ePackage, eContainingClass, eStructuralFeature, eClassifier);
                                if (result != null) {
                                    return result;
                                }
                            }
                        }

                        // Process the super types if that's been specified.
                        //
                        if (considerSubtypes) {
                            eClassifier = getSuperType(extendedMetaData, eClassifier);
                        } else {
                            break;
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Searches the document root object package for a changeable (non-abstract) element
     * that is affiliated with the given feature in the given class
     * and a classifier that exactly matches the given classifier
     * @param extendedMetaData the extended meta data in which to look up type information.
     * @param ePackage the package whose document root to search.
     * @param eContainingClass the containing class of the feature.
     * @param eStructuralFeature the target feature.
     * @param eClassifier the type of object being matched.
     * @return the substitution group feature or <code>null</code>.
     */
    protected EStructuralFeature getSubstitutionGroup
    (ExtendedMetaData extendedMetaData, EPackage ePackage, EClass eContainingClass, EStructuralFeature eStructuralFeature, EClassifier eClassifier) {
        EClass eClass = extendedMetaData.getDocumentRoot(ePackage);
        if (eClass != null) {
            for (EStructuralFeature element : extendedMetaData.getElements(eClass)) {
                if (element.getEType() == eClassifier && element.isChangeable() && extendedMetaData.getAffiliation(eContainingClass, element) == eStructuralFeature) {
                    return element;
                }
            }
        }
        return null;
    }
}

