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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;

import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

public interface AsyncPackageDataModelOracle {

    // ####################################
    // Initialise
    // ####################################

    void init(final Path resourcePath);

    Path getResourcePath();

    // ####################################
    // Editor functions
    // ####################################

    //Package methods
    List<String> getPackageNames();

    //Fact related methods
    String[] getFactTypes();

    String[] getAllFactTypes();

    String[] getInternalFactTypes();

    String[] getExternalFactTypes();

    String getFQCNByFactName(String factName);

    /**
     * Returns fact's name from class type
     *
     * @param fqcnName for example org.test.Person
     * @return Shorter type name org.test.Person returns Person
     */
    String getFactNameFromType(final String fqcnName);

    boolean isFactTypeRecognized(final String factType);

    void isFactTypeAnEvent(final String factType,
                           final Callback<Boolean> callback);

    void getTypeSource(final String factType,
                       final Callback<TypeSource> callback);

    void getSuperType(final String factType,
                      final Callback<String> callback);

    void getSuperTypes(final String factType,
                       final Callback<List<String>> callback);

    void getTypeAnnotations(final String factType,
                            final Callback<Set<Annotation>> callback);

    void getTypeFieldsAnnotations(final String factType,
                                  final Callback<Map<String, Set<Annotation>>> callback);

    /**
     * Validates a proposed field value according to {@link Constraint} defined for the field. Annotation details for
     * the Fact Type are "lazy loaded" if the Fact Type has not been previously referenced by consumers of the DataModelOracle.
     * Consequentially validation results are provided in the callback as an asynchronouse server-round-trip may be needed.
     *
     * @param factType  Simple class name for the Fact Type. If null an empty set is returned.
     * @param fieldName Field name. Cannot be null. If null an empty set is returned.
     * @param value     Proposed value for the field. Can be null.
     * @param callback  Callback passing an empty set if the given value is valid, or a set with
     *                  one or more {@link ConstraintViolation ConstraintViolations} if the given value is invalid.
     *                  If null this method does nothing.
     * @param <T>       Type of value.
     */
    <T> void validateField(final String factType,
                           final String fieldName,
                           final T value,
                           final Callback<Set<ConstraintViolation<T>>> callback);

    //Field related methods
    void getFieldCompletions(final String factType,
                             final Callback<ModelField[]> callback);

    void getFieldCompletions(final String factType,
                             final FieldAccessorsAndMutators accessor,
                             final Callback<ModelField[]> callback);

    String getFieldType(final String variableClass,
                        final String fieldName);

    String getFieldClassName(final String factName,
                             final String fieldName);

    String getParametricFieldType(final String factType,
                                  final String fieldName);

    void getOperatorCompletions(final String factType,
                                final String fieldName,
                                final Callback<String[]> callback);

    void getConnectiveOperatorCompletions(final String factType,
                                          final String fieldName,
                                          final Callback<String[]> callback);

    void getMethodInfos(final String factType,
                        final Callback<List<MethodInfo>> callback);

    void getMethodInfos(final String factType,
                        final int parameterCount,
                        final Callback<List<MethodInfo>> callback);

    void getMethodParams(final String factType,
                         final String methodNameWithParams,
                         final Callback<List<String>> callback);

    void getMethodInfo(final String factName,
                       final String methodName,
                       final Callback<MethodInfo> callback);

    // Global Variable related methods
    String[] getGlobalVariables();

    String getGlobalVariable(final String variable);

    boolean isGlobalVariable(final String variable);

    void getFieldCompletionsForGlobalVariable(final String variable,
                                              final Callback<ModelField[]> callback);

    void getMethodInfosForGlobalVariable(final String variable,
                                         final Callback<List<MethodInfo>> callback);

    String[] getGlobalCollections();

    /**
     * @return List of collection types (i.e. java.util.Collection subtypes) that can be used in the current package.
     */
    List<String> getAvailableCollectionTypes();

    // DSL related methods
    List<DSLSentence> getDSLConditions();

    List<DSLSentence> getDSLActions();

    // Enumeration related methods
    DropDownData getEnums(final String type,
                          final String field);

    DropDownData getEnums(final String factType,
                          final String factField,
                          final Map<String, String> currentValueMap);

    String[] getEnumValues(final String factType,
                           final String factField);

    boolean hasEnums(final String factType,
                     final String factField);

    boolean hasEnums(final String qualifiedFactField);

    boolean isDependentEnum(final String factType,
                            final String factField,
                            final String field);

    //Import related methods
    void filter(final Imports imports);

    void filter();

    // ####################################
    // Population of DMO
    // ####################################

    void setProjectName(final String projectName);

    void setPackageName(final String packageName);

    void addModelFields(final Map<String, ModelField[]> modelFields);

    void addFieldParametersType(final Map<String, String> fieldParametersType);

    void addEventTypes(final Map<String, Boolean> eventTypes);

    void addTypeSources(final Map<String, TypeSource> typeSources);

    void addSuperTypes(final Map<String, List<String>> superTypes);

    void addTypeAnnotations(final Map<String, Set<Annotation>> annotations);

    void addTypeFieldsAnnotations(final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations);

    void addJavaEnumDefinitions(final Map<String, String[]> dataEnumLists);

    void addMethodInformation(final Map<String, List<MethodInfo>> methodInformation);

    void addCollectionTypes(final Map<String, Boolean> collectionTypes);

    void addPackageNames(final List<String> packageNames);

    void addWorkbenchEnumDefinitions(final Map<String, String[]> dataEnumLists);

    void addDslConditionSentences(final List<DSLSentence> dslConditionSentences);

    void addDslActionSentences(final List<DSLSentence> dslActionSentences);

    void addGlobals(final Map<String, String> packageGlobalTypes);
}
