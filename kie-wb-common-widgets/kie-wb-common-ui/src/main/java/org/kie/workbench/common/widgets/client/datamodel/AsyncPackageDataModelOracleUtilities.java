/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.client.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.model.LazyModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;

public class AsyncPackageDataModelOracleUtilities {

    public static void populateDataModelOracle(final AsyncPackageDataModelOracle oracle,
                                               final PackageDataModelOracleIncrementalPayload payload) {
        if (payload == null) {
            return;
        }
        populate(oracle,
                 payload);
        oracle.filter();
    }

    private static void populate(final AsyncPackageDataModelOracle oracle,
                                 final PackageDataModelOracleIncrementalPayload payload) {
        oracle.addModelFields(payload.getModelFields());
        oracle.addFieldParametersType(payload.getFieldParametersType());
        oracle.addEventTypes(payload.getEventTypes());
        oracle.addTypeSources(payload.getTypeSources());
        oracle.addSuperTypes(payload.getSuperTypes());
        oracle.addTypeAnnotations(payload.getTypeAnnotations());
        oracle.addTypeFieldsAnnotations(payload.getTypeFieldsAnnotations());
        oracle.addMethodInformation(payload.getMethodInformation());
        oracle.addCollectionTypes(payload.getCollectionTypes());
    }

    //Filter and rename Model Fields based on package name and imports
    public static Map<String, ModelField[]> filterModelFields(final String packageName,
                                                              final Imports imports,
                                                              final Map<String, ModelField[]> projectModelFields,
                                                              final FactNameToFQCNHandleRegistry registry) {

        final Map<String, ModelField[]> scopedModelFields = new HashMap<String, ModelField[]>();

        for (Map.Entry<String, ModelField[]> entry : projectModelFields.entrySet()) {
            final String mfQualifiedType = entry.getKey();
            final String mfPackageName = getPackageName(mfQualifiedType);
            final String mfTypeName = getTypeName(mfQualifiedType);
            if (registry.contains(mfTypeName)) {
                // Override existing
                final Set<String> importStrings = imports.getImportStrings();
                if (mfPackageName.equals(packageName) || importStrings.contains(mfQualifiedType)) {
                    registry.add(mfTypeName,
                                 mfQualifiedType);
                }
            } else {
                registry.add(mfTypeName,
                             mfQualifiedType);
            }

            if (mfPackageName.equals(packageName) || isImported(mfQualifiedType,
                                                                imports)) {
                scopedModelFields.put(mfTypeName,
                                      correctModelFields(packageName,
                                                         entry.getValue(),
                                                         imports));
            }
        }

        return scopedModelFields;
    }

    //Filter and rename Collection Types based on package name and imports
    public static Map<String, Boolean> filterCollectionTypes(final String packageName,
                                                             final Imports imports,
                                                             final Map<String, Boolean> projectCollectionTypes) {
        final Map<String, Boolean> scopedCollectionTypes = new HashMap<String, Boolean>();
        for (Map.Entry<String, Boolean> e : projectCollectionTypes.entrySet()) {
            final String collectionQualifiedType = e.getKey();
            final String collectionPackageName = getPackageName(collectionQualifiedType);
            final String collectionTypeName = getTypeName(collectionQualifiedType);

            if (collectionPackageName.equals(packageName) || isImported(collectionQualifiedType,
                                                                        imports)) {
                scopedCollectionTypes.put(collectionTypeName,
                                          e.getValue());
            }
        }
        return scopedCollectionTypes;
    }

    //Filter and rename Global Types based on package name and imports
    public static Map<String, String> filterGlobalTypes(final String packageName,
                                                        final Imports imports,
                                                        final Map<String, String> packageGlobalTypes) {
        final Map<String, String> scopedGlobalTypes = new HashMap<String, String>();
        for (Map.Entry<String, String> e : packageGlobalTypes.entrySet()) {
            final String globalQualifiedType = e.getValue();
            final String globalPackageName = getPackageName(globalQualifiedType);
            final String globalTypeName = getTypeName(globalQualifiedType);

            if (globalPackageName.equals(packageName) || isImported(globalQualifiedType,
                                                                    imports)) {
                scopedGlobalTypes.put(e.getKey(),
                                      globalTypeName);
            }
        }
        return scopedGlobalTypes;
    }

    //Filter and rename Event Types based on package name and imports
    public static Map<String, Boolean> filterEventTypes(final String packageName,
                                                        final Imports imports,
                                                        final Map<String, Boolean> projectEventTypes) {
        final Map<String, Boolean> scopedEventTypes = new HashMap<String, Boolean>();
        for (Map.Entry<String, Boolean> e : projectEventTypes.entrySet()) {
            final String eventQualifiedType = e.getKey();
            final String eventPackageName = getPackageName(eventQualifiedType);
            final String eventTypeName = getTypeName(eventQualifiedType);

            if (eventPackageName.equals(packageName) || isImported(eventQualifiedType,
                                                                   imports)) {
                scopedEventTypes.put(eventTypeName,
                                     e.getValue());
            }
        }
        return scopedEventTypes;
    }

    //Filter and rename TypeSource based on package name and imports
    public static Map<String, TypeSource> filterTypeSources(final String packageName,
                                                            final Imports imports,
                                                            final Map<String, TypeSource> projectTypeSources) {
        final Map<String, TypeSource> scopedTypeSources = new HashMap<String, TypeSource>();
        for (Map.Entry<String, TypeSource> e : projectTypeSources.entrySet()) {
            final String typeQualifiedType = e.getKey();
            final String typePackageName = getPackageName(typeQualifiedType);
            final String typeTypeName = getTypeName(typeQualifiedType);

            if (typePackageName.equals(packageName) || isImported(typeQualifiedType,
                                                                  imports)) {
                scopedTypeSources.put(typeTypeName,
                                      e.getValue());
            }
        }
        return scopedTypeSources;
    }

    //Filter and rename Super Types based on package name and imports
    public static Map<String, List<String>> filterSuperTypes(final String packageName,
                                                             final Imports imports,
                                                             final Map<String, List<String>> projectSuperTypes) {
        final Map<String, List<String>> scopedSuperTypes = new HashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> e : projectSuperTypes.entrySet()) {
            final String typeQualifiedType = e.getKey();
            final String typePackageName = getPackageName(typeQualifiedType);
            final String typeTypeName = getTypeName(typeQualifiedType);

            final List<String> superTypeQualifiedTypes = e.getValue();

            if (superTypeQualifiedTypes == null) {
                //Doesn't have a Super Type
                if (typePackageName.equals(packageName) || isImported(typeQualifiedType,
                                                                      imports)) {
                    scopedSuperTypes.put(typeTypeName,
                                         superTypeQualifiedTypes);
                }
            } else {
                //Has a Super Type
                if (typePackageName.equals(packageName) || isImported(typeQualifiedType,
                                                                      imports)) {
                    ArrayList<String> result = new ArrayList<String>();

                    for (String superTypeQualifiedType : superTypeQualifiedTypes) {
                        final String superTypePackageName = getPackageName(superTypeQualifiedType);
                        final String superTypeTypeName = getTypeName(superTypeQualifiedType);

                        if (superTypePackageName.equals(packageName) || isImported(superTypeQualifiedType,
                                                                                   imports)) {
                            result.add(superTypeTypeName);
                        } else {
                            result.add(superTypeQualifiedType);
                        }
                    }

                    scopedSuperTypes.put(typeTypeName,
                                         result);
                }
            }
        }
        return scopedSuperTypes;
    }

    //Filter and rename Type Annotations based on package name and imports
    public static Map<String, Set<Annotation>> filterTypeAnnotations(final String packageName,
                                                                     final Imports imports,
                                                                     final Map<String, Set<Annotation>> projectTypeAnnotations) {
        final Map<String, Set<Annotation>> scopedTypeAnnotations = new HashMap<String, Set<Annotation>>();
        for (Map.Entry<String, Set<Annotation>> e : projectTypeAnnotations.entrySet()) {
            final String typeAnnotationQualifiedType = e.getKey();
            final String typeAnnotationPackageName = getPackageName(typeAnnotationQualifiedType);
            final String typeAnnotationTypeName = getTypeName(typeAnnotationQualifiedType);

            if (typeAnnotationPackageName.equals(packageName) || isImported(typeAnnotationQualifiedType,
                                                                            imports)) {
                scopedTypeAnnotations.put(typeAnnotationTypeName,
                                          e.getValue());
            }
        }
        return scopedTypeAnnotations;
    }

    //Filter and rename Type Fields Annotations based on package name and imports
    public static Map<String, Map<String, Set<Annotation>>> filterTypeFieldsAnnotations(final String packageName,
                                                                                        final Imports imports,
                                                                                        final Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations) {
        final Map<String, Map<String, Set<Annotation>>> scopedTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        for (Map.Entry<String, Map<String, Set<Annotation>>> e : projectTypeFieldsAnnotations.entrySet()) {
            final String typeAnnotationQualifiedType = e.getKey();
            final String typeAnnotationPackageName = getPackageName(typeAnnotationQualifiedType);
            final String typeAnnotationTypeName = getTypeName(typeAnnotationQualifiedType);

            if (typeAnnotationPackageName.equals(packageName) || isImported(typeAnnotationQualifiedType,
                                                                            imports)) {
                scopedTypeFieldsAnnotations.put(typeAnnotationTypeName,
                                                e.getValue());
            }
        }
        return scopedTypeFieldsAnnotations;
    }

    //Filter and rename Enum definitions based on package name and imports
    public static Map<String, String[]> filterEnumDefinitions(final String packageName,
                                                              final Imports imports,
                                                              final Map<String, String[]> enumDefinitions) {
        final Map<String, String[]> scopedEnumLists = new HashMap<String, String[]>();
        for (Map.Entry<String, String[]> e : enumDefinitions.entrySet()) {
            final String enumQualifiedType = getQualifiedTypeFromEnumeration(e.getKey());
            final String enumFieldName = getFieldNameFromEnumeration(e.getKey());
            final String enumPackageName = getPackageName(enumQualifiedType);
            final String enumTypeName = getTypeName(enumQualifiedType);

            if (enumPackageName.equals(packageName) || isImported(enumQualifiedType,
                                                                  imports)) {
                scopedEnumLists.put(enumTypeName + "#" + enumFieldName,
                                    e.getValue());
            }
        }
        return scopedEnumLists;
    }

    // For filling the FQCN-fact name registry
    public static void visitMethodInformation(final Map<String, List<MethodInfo>> projectMethodInformation,
                                              final FactNameToFQCNHandleRegistry registry) {
        for (Map.Entry<String, List<MethodInfo>> e : projectMethodInformation.entrySet()) {
            final String miQualifiedType = e.getKey();
            final String miTypeName = getTypeName(miQualifiedType);
            registry.add(miTypeName,
                         miQualifiedType);
        }
    }

    //Filter and rename Field Parameter Types based on package name and imports
    public static Map<String, String> filterFieldParametersTypes(final String packageName,
                                                                 final Imports imports,
                                                                 final Map<String, String> projectFieldParametersTypes) {
        final Map<String, String> scopedFieldParametersType = new HashMap<String, String>();
        for (Map.Entry<String, String> e : projectFieldParametersTypes.entrySet()) {
            String fieldName = e.getKey();
            String fieldType = e.getValue();
            final String fFieldName = getFieldNameFromEnumeration(fieldName);

            final String fFieldName_QualifiedType = getQualifiedTypeFromEnumeration(fieldName);
            final String fFieldName_PackageName = getPackageName(fFieldName_QualifiedType);
            final String fFieldName_TypeName = getTypeName(fFieldName_QualifiedType);
            if (fFieldName_PackageName.equals(packageName) || isImported(fFieldName_QualifiedType,
                                                                         imports)) {
                fieldName = fFieldName_TypeName;
            }

            final String fFieldType_QualifiedType = getQualifiedTypeFromEnumeration(fieldType);
            final String fFieldType_PackageName = getPackageName(fFieldType_QualifiedType);
            final String fFieldType_TypeName = getTypeName(fFieldType_QualifiedType);
            if (fFieldType_PackageName.equals(packageName) || isImported(fFieldType_QualifiedType,
                                                                         imports)) {
                fieldType = fFieldType_TypeName;
            }

            scopedFieldParametersType.put(fieldName + "#" + fFieldName,
                                          fieldType);
        }
        return scopedFieldParametersType;
    }

    public static String getPackageName(final String qualifiedType) {
        String packageName = qualifiedType;
        int dotIndex = packageName.lastIndexOf(".");
        if (dotIndex != -1) {
            return packageName.substring(0,
                                         dotIndex);
        }
        return "";
    }

    public static String getTypeName(final String qualifiedType) {
        String typeName = qualifiedType;
        int dotIndex = typeName.lastIndexOf(".");
        if (dotIndex != -1) {
            typeName = typeName.substring(dotIndex + 1);
        }
        if (typeName.contains("$")) {
            typeName = typeName.replaceAll("\\$",
                                           ".");
        }
        return typeName;
    }

    private static String getQualifiedTypeFromEnumeration(final String qualifiedType) {
        String typeName = qualifiedType;
        int hashIndex = typeName.lastIndexOf("#");
        if (hashIndex != -1) {
            typeName = typeName.substring(0,
                                          hashIndex);
        }
        return typeName;
    }

    private static String getFieldNameFromEnumeration(final String qualifiedType) {
        String fieldName = qualifiedType;
        int hashIndex = fieldName.lastIndexOf("#");
        if (hashIndex != -1) {
            return fieldName.substring(hashIndex + 1);
        }
        return "";
    }

    public static ModelField[] correctModelFields(final String packageName,
                                                  final ModelField[] originalModelFields,
                                                  final Imports imports) {
        if (originalModelFields == null) {
            return null;
        }
        final List<ModelField> correctedModelFields = new ArrayList<ModelField>();
        for (final ModelField mf : originalModelFields) {
            correctedModelFields.add(correctModelFields(packageName, imports, mf));
        }
        final ModelField[] result = new ModelField[correctedModelFields.size()];
        return correctedModelFields.toArray(result);
    }

    public static ModelField correctModelFields(final String packageName,
                                                final Imports imports,
                                                final ModelField mf) {
        String mfType = mf.getType();
        String mfClassName = mf.getClassName();

        final String mfClassName_QualifiedType = mfClassName;
        final String mfClassName_PackageName = getPackageName(mfClassName_QualifiedType);
        final String mfClassName_TypeName = getTypeName(mfClassName_QualifiedType);
        if (mfClassName_PackageName.equals(packageName) || isImported(mfClassName_QualifiedType,
                                                                      imports)) {
            mfClassName = mfClassName_TypeName;
        }

        final String mfType_QualifiedType = mfType;
        final String mfType_PackageName = getPackageName(mfType_QualifiedType);
        final String mfType_TypeName = getTypeName(mfType_QualifiedType);
        if (mfType_PackageName.equals(packageName) || isImported(mfType_QualifiedType,
                                                                 imports)) {
            mfType = mfType_TypeName;
        }
        return cloneModelField(mf,
                               mfClassName,
                               mfType);
    }

    //Ensure we retain the LazyModelField information when filtering, as it's place-holder
    //for AsyncPackageDataModelOracle to know whether it needs to load additional information
    private static ModelField cloneModelField(final ModelField source,
                                              final String mfClassName,
                                              final String mfType) {
        if (source instanceof LazyModelField) {
            return new LazyModelField(source.getName(),
                                      mfClassName,
                                      source.getClassType(),
                                      source.getOrigin(),
                                      source.getAccessorsAndMutators(),
                                      mfType);
        }
        return new ModelField(source.getName(),
                              mfClassName,
                              source.getClassType(),
                              source.getOrigin(),
                              source.getAccessorsAndMutators(),
                              mfType);
    }

    public static MethodInfo correctMethodInformation(final String packageName,
                                                      final MethodInfo originalMethodInformation,
                                                      final Imports imports) {
        final List<MethodInfo> correctedMethodInformation = correctMethodInformation(packageName,
                                                                                     new ArrayList<MethodInfo>() {{
                                                                                         add(originalMethodInformation);
                                                                                     }},
                                                                                     imports);
        if (correctedMethodInformation == null || correctedMethodInformation.isEmpty()) {
            return null;
        }
        return correctedMethodInformation.get(0);
    }

    public static List<MethodInfo> correctMethodInformation(final String packageName,
                                                            final List<MethodInfo> originalMethodInformation,
                                                            final Imports imports) {
        final List<MethodInfo> correctedMethodInformation = new ArrayList<MethodInfo>();
        for (final MethodInfo mi : originalMethodInformation) {
            String miReturnType = mi.getReturnClassType();
            String miGenericReturnType = mi.getGenericType();
            String miParametricReturnType = mi.getParametricReturnType();

            final String miReturnType_QualifiedType = miReturnType;
            final String miReturnType_PackageName = getPackageName(miReturnType_QualifiedType);
            final String miReturnType_TypeName = getTypeName(miReturnType_QualifiedType);
            if (miReturnType_PackageName.equals(packageName) || isImported(miReturnType_QualifiedType,
                                                                           imports)) {
                miReturnType = miReturnType_TypeName;
            }

            final String miGenericReturnType_QualifiedType = miGenericReturnType;
            final String miGenericReturnType_PackageName = getPackageName(miGenericReturnType_QualifiedType);
            final String miGenericReturnType_TypeName = getTypeName(miGenericReturnType_QualifiedType);
            if (miGenericReturnType_PackageName.equals(packageName) || isImported(miGenericReturnType_QualifiedType,
                                                                                  imports)) {
                miGenericReturnType = miGenericReturnType_TypeName;
            }

            if (miParametricReturnType != null) {
                final String miParametricReturnType_QualifiedType = miParametricReturnType;
                final String miParametricReturnType_PackageName = getPackageName(miParametricReturnType_QualifiedType);
                final String miParametricReturnType_TypeName = getTypeName(miParametricReturnType_QualifiedType);
                if (miParametricReturnType_PackageName.equals(packageName) || isImported(miParametricReturnType_QualifiedType,
                                                                                         imports)) {
                    miParametricReturnType = miParametricReturnType_TypeName;
                }
            }

            correctedMethodInformation.add(new MethodInfo(mi.getName(),
                                                          mi.getParams(),
                                                          miReturnType,
                                                          miParametricReturnType,
                                                          miGenericReturnType));
        }
        return correctedMethodInformation;
    }

    private static boolean isImported(final String qualifiedType,
                                      final Imports imports) {
        final Import item = new Import(qualifiedType.replaceAll("\\$",
                                                                "."));
        return imports.contains(item);
    }
}
