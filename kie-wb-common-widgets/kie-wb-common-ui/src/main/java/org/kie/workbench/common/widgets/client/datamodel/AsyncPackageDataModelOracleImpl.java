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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.Annotation;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.MethodInfo;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.datamodel.model.LazyModelField;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

/**
 * Default implementation of DataModelOracle
 */
@Dependent
public class AsyncPackageDataModelOracleImpl implements AsyncPackageDataModelOracle {

    private Caller<IncrementalDataModelService> service;
    private Instance<DynamicValidator> validatorInstance;
    private DynamicValidator validator;

    //Path that this DMO is coupled to
    private Path resourcePath;

    //Module name
    protected String moduleName;

    //Package for which this DMO relates
    private String packageName = "";

    //Imports from the Project into this Package
    private Imports imports = new Imports();

    // List of available package names
    private List<String> packageNames = new ArrayList<String>();

    // ####################################
    // Project Scope
    // ####################################

    //Fact Types and their corresponding fields
    protected Map<String, ModelField[]> projectModelFields = new TreeMap<String, ModelField[]>(SortHelper.ALPHABETICAL_ORDER_COMPARATOR);

    //Map of the field that contains the parametrized type of a collection
    //for example given "List<String> name", key = "name" value = "String"
    protected Map<String, String> projectFieldParametersType = new HashMap<String, String>();

    //Map {factType, isEvent} to determine which Fact Type can be treated as events.
    protected Map<String, Boolean> projectEventTypes = new HashMap<String, Boolean>();

    //Map {factType, TypeSource} to determine where a Fact Type as defined.
    protected Map<String, TypeSource> projectTypeSources = new HashMap<String, TypeSource>();

    //Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, List<String>> projectSuperTypes = new HashMap<String, List<String>>();

    //Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> projectTypeAnnotations = new HashMap<String, Set<Annotation>>();

    //Map {factType, Map<fieldName, Set<Annotation>>} containing the FactType's Field annotations.
    protected Map<String, Map<String, Set<Annotation>>> projectTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Scoped (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    protected Map<String, String[]> projectJavaEnumLists = new HashMap<String, String[]>();

    //Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    protected Map<String, List<MethodInfo>> projectMethodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    protected Map<String, Boolean> projectCollectionTypes = new HashMap<String, Boolean>();

    // ####################################
    // Package Scope
    // ####################################

    // Filtered (current package and imports) Fact Types and their corresponding fields
    private Map<String, ModelField[]> filteredModelFields = new TreeMap<String, ModelField[]>(SortHelper.ALPHABETICAL_ORDER_COMPARATOR);

    // Filtered (current package and imports) map of the field that contains the parametrized type of a collection
    // for example given "List<String> name", key = "name" value = "String"
    private Map<String, String> filteredFieldParametersType = new HashMap<String, String>();

    // Filtered (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    private FilteredEnumLists filteredEnumLists = new FilteredEnumLists();

    // Filtered (current package and imports) Map {factType, isEvent} to determine which Fact Type can be treated as events.
    private Map<String, Boolean> filteredEventTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, isCollection} to determine which Fact Types are Collections.
    private Map<String, Boolean> filteredCollectionTypes = new HashMap<String, Boolean>();

    // Filtered (current package and imports) Map {factType, TypeSource} to determine where a Fact Type as defined.
    private Map<String, TypeSource> filteredTypeSources = new HashMap<String, TypeSource>();

    // Filtered (current package and imports) Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, List<String>> filteredSuperTypes = new HashMap<String, List<String>>();

    // Filtered (current package and imports) Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();

    // Filtered (current package and imports) Map {factType, {fieldName, Set<Annotation>}} containing the FactType's Fields annotations.
    protected Map<String, Map<String, Set<Annotation>>> filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Filtered (current package and imports) map of Globals {alias, class name}.
    private Map<String, String> filteredGlobalTypes = new TreeMap<String, String>(SortHelper.ALPHABETICAL_ORDER_COMPARATOR);

    // Package-level enumeration definitions derived from "Workbench" enumerations.
    private Map<String, String[]> packageWorkbenchEnumLists = new HashMap<String, String[]>();

    // Package-level DSL language extensions.
    private List<DSLSentence> packageDSLConditionSentences = new ArrayList<DSLSentence>();
    private List<DSLSentence> packageDSLActionSentences = new ArrayList<DSLSentence>();

    // Package-level map of Globals {alias, class name}.
    private Map<String, String> packageGlobalTypes = new HashMap<String, String>();

    // Keep the link between fact name and the full qualified class name inside the package
    private FactNameToFQCNHandleRegistry factNameToFQCNHandleRegistry = new FactNameToFQCNHandleRegistry();

    @Inject
    public AsyncPackageDataModelOracleImpl(final Caller<IncrementalDataModelService> service,
                                           final Instance<DynamicValidator> validatorInstance) {
        this.service = service;
        this.validatorInstance = validatorInstance;
    }

    public Map<String, ModelField[]> getFilteredFactTypes() {
        return filteredModelFields;
    }

    @Override
    public void init(final Path resourcePath) {
        this.resourcePath = PortablePreconditions.checkNotNull("resourcePath",
                                                               resourcePath);
    }

    @Override
    public Path getResourcePath() {
        return this.resourcePath;
    }

    // ####################################
    // Packages
    // ####################################

    @Override
    public List<String> getPackageNames() {
        return packageNames;
    }

    // ####################################
    // Fact Types
    // ####################################

    /**
     * Returns fact types available for rule authoring, i.e. those within the same package and those that have been imported.
     * @return
     */
    @Override
    public String[] getFactTypes() {
        final String[] types = filteredModelFields.keySet().toArray(new String[filteredModelFields.size()]);
        return types;
    }

    /**
     * Return all fact types available to the project, i.e. everything type defined within the project or externally imported
     * @return
     */
    @Override
    public String[] getAllFactTypes() {
        final List<String> types = new ArrayList<String>();
        types.addAll(this.projectModelFields.keySet());
        final String[] result = new String[types.size()];
        types.toArray(result);
        return result;
    }

    /**
     * Return all fact types that are internal to the package, i.e. they do not need to be imported to be used
     * @return
     */
    @Override
    public String[] getInternalFactTypes() {
        final String[] allTypes = getAllFactTypes();
        final List<String> internalTypes = new ArrayList<String>();
        for (String type : allTypes) {
            final String packageName = AsyncPackageDataModelOracleUtilities.getPackageName(type);
            if (packageName.equals(this.packageName)) {
                internalTypes.add(type);
            }
        }
        final String[] result = new String[internalTypes.size()];
        internalTypes.toArray(result);
        return result;
    }

    /**
     * Return all fact types that are external to the package, i.e. they need to be imported to be used
     * @return
     */
    @Override
    public String[] getExternalFactTypes() {
        final String[] allTypes = getAllFactTypes();
        final List<String> externalTypes = new ArrayList<String>();
        for (String type : allTypes) {
            final String packageName = AsyncPackageDataModelOracleUtilities.getPackageName(type);
            if (!packageName.equals(this.packageName)) {
                externalTypes.add(type);
            }
        }
        final String[] result = new String[externalTypes.size()];
        externalTypes.toArray(result);
        return result;
    }

    public String getFQCNByFactName(final String factName) {
        if (factNameToFQCNHandleRegistry.contains(factName)) {
            return factNameToFQCNHandleRegistry.get(factName);
        } else {
            return factName;
        }
    }

    /**
     * Returns fact's name from type
     * @param type for example org.test.Person or Person
     * @return Shorter type name Person, not org.test.Person
     */
    @Override
    public String getFactNameFromType(final String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        if (filteredModelFields.containsKey(type)) {
            return type;
        }
        for (Map.Entry<String, ModelField[]> entry : filteredModelFields.entrySet()) {
            for (ModelField mf : entry.getValue()) {
                if (DataType.TYPE_THIS.equals(mf.getName()) && type.equals(mf.getClassName())) {
                    return entry.getKey();
                }
            }
        }

        final String fgcnByFactName = getFQCNByFactName(type);
        if (projectModelFields.containsKey(fgcnByFactName)) {
            return AsyncPackageDataModelOracleUtilities.getTypeName(fgcnByFactName);
        }

        return null;
    }

    /**
     * Is the Fact Type known to the DataModelOracle
     * @param factType
     * @return
     */
    @Override
    public boolean isFactTypeRecognized(final String factType) {
        if (filteredModelFields.containsKey(factType) || factNameToFQCNHandleRegistry.map.containsValue(factType)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether a given FactType is an Event for CEP purposes
     * @param factType
     * @return
     */
    @Override
    public void isFactTypeAnEvent(final String factType,
                                  final Callback<Boolean> callback) {
        if (factType == null || factType.isEmpty()) {
            callback.callback(false);
            return;
        }
        final Boolean isFactTypeAnEvent = filteredEventTypes.get(factType);

        //Load incremental content
        if (isFactTypeAnEvent == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    Boolean isFactTypeAnEvent = filteredEventTypes.get(factType);
                    if (isFactTypeAnEvent == null) {
                        isFactTypeAnEvent = false;
                        filteredEventTypes.put(factType,
                                               isFactTypeAnEvent);
                    }
                    callback.callback(isFactTypeAnEvent);
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(isFactTypeAnEvent);
        }
    }

    /**
     * Return where a given FactType was defined
     * @param factType
     * @return
     */
    @Override
    public void getTypeSource(final String factType,
                              final Callback<TypeSource> callback) {
        final TypeSource typeSource = filteredTypeSources.get(factType);

        //Load incremental content
        if (typeSource == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    final TypeSource typeSource = filteredTypeSources.get(factType);
                    callback.callback(typeSource);
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(typeSource);
        }
    }

    /**
     * Get the Super Type for a given FactType
     * @param factType
     * @return null if no Super Type
     */
    @Override
    public void getSuperType(final String factType,
                             final Callback<String> callback) {

        getSuperTypes(factType,
                      new Callback<List<String>>() {
                          @Override
                          public void callback(List<String> result) {
                              if (result != null) {
                                  callback.callback(result.get(0));
                              } else {
                                  callback.callback(null);
                              }
                          }
                      });
    }

    @Override
    public void getSuperTypes(final String factType,
                              final Callback<List<String>> callback) {
        final List<String> superTypes = filteredSuperTypes.get(factType);

        //Load incremental content
        if (superTypes == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    callback.callback(filteredSuperTypes.get(factType));
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(superTypes);
        }
    }

    /**
     * Get the Annotations for a given FactType
     * @param factType
     * @return Empty set if no annotations exist for the type
     */
    @Override
    public void getTypeAnnotations(final String factType,
                                   final Callback<Set<Annotation>> callback) {
        final Set<Annotation> typeAnnotations = filteredTypeAnnotations.get(factType);

        //Load incremental content
        if (typeAnnotations == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    Set<Annotation> typeAnnotations = filteredTypeAnnotations.get(factType);
                    if (typeAnnotations == null) {
                        typeAnnotations = Collections.EMPTY_SET;
                        filteredTypeAnnotations.put(factType,
                                                    typeAnnotations);
                    }
                    callback.callback(typeAnnotations);
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(typeAnnotations);
        }
    }

    /**
     * Get the Fields Annotations for a given FactType
     * @param factType
     * @return Empty Map if no annotations exist for the type
     */
    @Override
    public void getTypeFieldsAnnotations(final String factType,
                                         final Callback<Map<String, Set<Annotation>>> callback) {
        final Map<String, Set<Annotation>> typeFieldsAnnotations = filteredTypeFieldsAnnotations.get(factType);

        //Load incremental content
        if (typeFieldsAnnotations == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    Map<String, Set<Annotation>> typeFieldsAnnotations = filteredTypeFieldsAnnotations.get(factType);
                    if (typeFieldsAnnotations == null) {
                        typeFieldsAnnotations = Collections.EMPTY_MAP;
                        filteredTypeFieldsAnnotations.put(factType,
                                                          typeFieldsAnnotations);
                    }
                    callback.callback(typeFieldsAnnotations);
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(typeFieldsAnnotations);
        }
    }

    @Override
    public <T> void validateField(final String factType,
                                  final String fieldName,
                                  final T value,
                                  final Callback<Set<ConstraintViolation<T>>> callback) {
        if (factType == null || factType.isEmpty()) {
            callback.callback(Collections.emptySet());
            return;
        }
        if (fieldName == null || fieldName.isEmpty()) {
            callback.callback(Collections.emptySet());
            return;
        }
        if (callback == null) {
            return;
        }

        if (validatorInstance.isUnsatisfied()) {
            callback.callback(Collections.emptySet());
            return;
        } else if (validator == null) {
            validator = validatorInstance.get();
        }

        getTypeFieldsAnnotations(factType,
                                 (Map<String, Set<Annotation>> result) -> {
                                     final Set<ConstraintViolation<T>> violations = new HashSet<>();
                                     final Set<Annotation> fieldAnnotations = result.get(fieldName);
                                     if (fieldAnnotations == null || fieldAnnotations.isEmpty()) {
                                         callback.callback(violations);
                                         return;
                                     }

                                     for (Annotation fieldAnnotation : fieldAnnotations) {
                                         final Map<String, Object> fieldAnnotationAttributes = fieldAnnotation.getParameters();
                                         violations.addAll(validator.validate(fieldAnnotation.getQualifiedTypeName(),
                                                                              fieldAnnotationAttributes,
                                                                              value));
                                     }
                                     callback.callback(violations);
                                 });
    }

    // ####################################
    // Fact Types' Fields
    // ####################################

    @Override
    public void getFieldCompletions(final String factType,
                                    final Callback<ModelField[]> callback) {
        final String fgcnByFactName = getFQCNByFactName(factType);
        ModelField[] fields = getModelFields(factType);

        if (fields == null || fields.length == 0) {
            fields = projectModelFields.get(fgcnByFactName);
            if (fields == null || isLazyProxy(fields)) {
                fields = null;
            } else {
                AsyncPackageDataModelOracleUtilities.correctModelFields(packageName,
                                                                        fields,
                                                                        imports);
            }
        }

        //Load incremental content
        if (fields == null || fields.length == 0) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);

                    // This will stop an infinite loop if there are no fields to be found
                    if (dataModel.getModelFields().get(fgcnByFactName) == null || dataModel.getModelFields().get(fgcnByFactName).length == 0) {
                        callback.callback(new ModelField[0]);
                    } else {
                        getFieldCompletions(factType,
                                            callback);
                    }
                }
            }).getUpdates(resourcePath,
                          imports,
                          fgcnByFactName);
        } else {
            callback.callback(fields);
        }
    }

    private ModelField[] getModelFields(final String modelClassName) {
        final String shortName = getFactNameFromType(modelClassName);
        if (!filteredModelFields.containsKey(shortName)) {
            return new ModelField[0];
        }

        //If fields do not exist return null; so they can be incrementally loaded
        final ModelField[] fields = filteredModelFields.get(shortName);
        if (isLazyProxy(fields)) {
            return null;
        }

        //Otherwise return existing fields
        return fields;
    }

    @Override
    public void getFieldCompletions(final String factType,
                                    final FieldAccessorsAndMutators accessorOrMutator,
                                    final Callback<ModelField[]> callback) {
        getFieldCompletions(factType,
                            new Callback<ModelField[]>() {

                                @Override
                                public void callback(ModelField[] fields) {
                                    ArrayList<ModelField> result = new ArrayList<ModelField>();

                                    for (ModelField field : fields) {
                                        if (FieldAccessorsAndMutators.compare(accessorOrMutator,
                                                                              field.getAccessorsAndMutators())) {
                                            result.add(field);
                                        }
                                    }
                                    callback.callback(result.toArray(new ModelField[result.size()]));
                                }
                            });
    }

    //Check whether the ModelField[] is a place-holder for more information
    private boolean isLazyProxy(final ModelField[] modelFields) {
        if (modelFields == null) {
            return false;
        } else if (modelFields.length != 1) {
            return false;
        }
        return modelFields[0] instanceof LazyModelField;
    }

    @Override
    public String getFieldType(final String modelClassName,
                               final String fieldName) {
        //Check fields
        final ModelField field = getField(modelClassName,
                                          fieldName);
        if (field != null) {
            return field.getType();
        }

        //Check method information
        final String fgcnModelClassName = getFQCNByFactName(modelClassName);
        final List<MethodInfo> mis = projectMethodInformation.get(fgcnModelClassName);
        if (mis != null) {
            for (MethodInfo mi : mis) {
                if (mi.getName().equals(fieldName)) {
                    return mi.getGenericType();
                }
            }
        }

        return null;
    }

    @Override
    public String getFieldClassName(final String modelClassName,
                                    final String fieldName) {
        //Check fields
        final ModelField field = getField(modelClassName,
                                          fieldName);
        if (field != null) {
            return field.getClassName();
        }

        //Check method information
        final String fgcnModelClassName = getFQCNByFactName(modelClassName);
        final List<MethodInfo> mis = projectMethodInformation.get(fgcnModelClassName);
        if (mis != null) {
            for (MethodInfo mi : mis) {
                if (mi.getName().equals(fieldName)) {
                    return mi.getReturnClassType();
                }
            }
        }

        return null;
    }

    private ModelField getField(final String modelClassName,
                                final String fieldName) {
        final String fgcnByFactName = getFQCNByFactName(modelClassName);
        final ModelField[] fields = projectModelFields.get(fgcnByFactName);

        if (fields == null) {
            return null;
        }

        for (ModelField modelField : fields) {
            if (modelField.getName().equals(fieldName)) {
                return AsyncPackageDataModelOracleUtilities.correctModelFields(packageName,
                                                                               imports,
                                                                               modelField);
            }
        }

        return null;
    }

    /**
     * Get the parametric type of a Field.
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public String getParametricFieldType(final String factType,
                                         final String fieldName) {
        final String qualifiedFactFieldName = factType + "#" + fieldName;
        return filteredFieldParametersType.get(qualifiedFactFieldName);
    }

    // ####################################
    // Operators
    // ####################################

    /**
     * Get the Operators applicable Base Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public void getOperatorCompletions(final String factType,
                                       final String fieldName,
                                       final Callback<String[]> callback) {
        final String fieldType = getFieldType(factType,
                                              fieldName);

        if (fieldType == null) {
            callback.callback(OperatorsOracle.STANDARD_OPERATORS);
            return;
        } else if (fieldName.equals(DataType.TYPE_THIS)) {
            isFactTypeAnEvent(factType,
                              new Callback<Boolean>() {
                                  @Override
                                  public void callback(final Boolean isFactTypeAnEvent) {
                                      if (Boolean.TRUE.equals(isFactTypeAnEvent)) {
                                          callback.callback(OracleUtils.joinArrays(OperatorsOracle.STANDARD_OPERATORS,
                                                                                   OperatorsOracle.SIMPLE_CEP_OPERATORS,
                                                                                   OperatorsOracle.COMPLEX_CEP_OPERATORS));
                                          return;
                                      } else {
                                          callback.callback(OperatorsOracle.STANDARD_OPERATORS);
                                          return;
                                      }
                                  }
                              });
        } else if (fieldType.equals(DataType.TYPE_STRING)) {
            callback.callback(OracleUtils.joinArrays(OperatorsOracle.STRING_OPERATORS,
                                                     OperatorsOracle.EXPLICIT_LIST_OPERATORS));
            return;
        } else if (DataType.isNumeric(fieldType)) {
            callback.callback(OracleUtils.joinArrays(OperatorsOracle.COMPARABLE_OPERATORS,
                                                     OperatorsOracle.EXPLICIT_LIST_OPERATORS));
            return;
        } else if (DataType.isDate(fieldType)) {
            callback.callback(OracleUtils.joinArrays(OperatorsOracle.COMPARABLE_OPERATORS,
                                                     OperatorsOracle.EXPLICIT_LIST_OPERATORS,
                                                     OperatorsOracle.SIMPLE_CEP_OPERATORS));
            return;
        } else if (fieldType.equals(DataType.TYPE_COMPARABLE)) {
            callback.callback(OperatorsOracle.COMPARABLE_OPERATORS);
            return;
        } else if (fieldType.equals(DataType.TYPE_COLLECTION)) {
            callback.callback(OperatorsOracle.COLLECTION_OPERATORS);
            return;
        } else {
            callback.callback(OperatorsOracle.STANDARD_OPERATORS);
        }
    }

    /**
     * Get the Operators applicable for Connective Constraints
     * @param factType
     * @param fieldName
     * @return
     */
    @Override
    public void getConnectiveOperatorCompletions(final String factType,
                                                 final String fieldName,
                                                 final Callback<String[]> callback) {
        final String fieldType = getFieldType(factType,
                                              fieldName);

        if (fieldType == null) {
            callback.callback(OperatorsOracle.STANDARD_CONNECTIVES);
            return;
        } else if (fieldName.equals(DataType.TYPE_THIS)) {
            isFactTypeAnEvent(factType,
                              new Callback<Boolean>() {
                                  @Override
                                  public void callback(final Boolean isFactTypeAnEvent) {
                                      if (Boolean.TRUE.equals(isFactTypeAnEvent)) {
                                          callback.callback(OracleUtils.joinArrays(OperatorsOracle.STANDARD_CONNECTIVES,
                                                                                   OperatorsOracle.SIMPLE_CEP_CONNECTIVES,
                                                                                   OperatorsOracle.COMPLEX_CEP_CONNECTIVES));
                                          return;
                                      } else {
                                          callback.callback(OperatorsOracle.STANDARD_CONNECTIVES);
                                          return;
                                      }
                                  }
                              });
        } else if (fieldType.equals(DataType.TYPE_STRING)) {
            callback.callback(OperatorsOracle.STRING_CONNECTIVES);
            return;
        } else if (DataType.isNumeric(fieldType)) {
            callback.callback(OperatorsOracle.COMPARABLE_CONNECTIVES);
            return;
        } else if (DataType.isDate(fieldType)) {
            callback.callback(OracleUtils.joinArrays(OperatorsOracle.COMPARABLE_CONNECTIVES,
                                                     OperatorsOracle.SIMPLE_CEP_CONNECTIVES));
            return;
        } else if (fieldType.equals(DataType.TYPE_COMPARABLE)) {
            callback.callback(OperatorsOracle.COMPARABLE_CONNECTIVES);
            return;
        } else if (fieldType.equals(DataType.TYPE_COLLECTION)) {
            callback.callback(OperatorsOracle.COLLECTION_CONNECTIVES);
            return;
        } else {
            callback.callback(OperatorsOracle.STANDARD_CONNECTIVES);
        }
    }

    // ####################################
    // Methods
    // ####################################

    /**
     * Get a list of MethodInfos for a Fact Type
     * @param factType
     * @param callback
     * @return
     */
    @Override
    public void getMethodInfos(final String factType,
                               final Callback<List<MethodInfo>> callback) {
        getMethodInfos(factType,
                       -1,
                       callback);
    }

    /**
     * Get a list of MethodInfos for a Fact Type that have at least the specified number of parameters
     * @param factType
     * @param parameterCount
     * @param callback
     * @return
     */
    @Override
    public void getMethodInfos(final String factType,
                               final int parameterCount,
                               final Callback<List<MethodInfo>> callback) {
        final String fqcnByFactName = getFQCNByFactName(factType);
        final List<MethodInfo> methodInformation = projectMethodInformation.get(fqcnByFactName);

        //Load incremental content
        if (methodInformation == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    final List<MethodInfo> methodInformation = projectMethodInformation.get(fqcnByFactName);
                    callback.callback(getMethodInfos(parameterCount,
                                                     methodInformation));
                }
            }).getUpdates(resourcePath,
                          imports,
                          fqcnByFactName);
        } else {
            callback.callback(getMethodInfos(parameterCount,
                                             methodInformation));
        }
    }

    private List<MethodInfo> getMethodInfos(final int paramCount,
                                            final List<MethodInfo> allMethodInfos) {
        final List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();
        if (allMethodInfos == null) {
            return methodInfos;
        }
        for (MethodInfo mi : allMethodInfos) {
            if (paramCount == -1 || mi.getParams().size() <= paramCount) {
                methodInfos.add(mi);
            }
        }
        return AsyncPackageDataModelOracleUtilities.correctMethodInformation(packageName,
                                                                             methodInfos,
                                                                             imports);
    }

    /**
     * Get a list of parameters for a Method of a Fact Type
     * @param factType
     * @param methodNameWithParams
     * @return
     */
    @Override
    public void getMethodParams(final String factType,
                                final String methodNameWithParams,
                                final Callback<List<String>> callback) {
        final String fqcnFactName = getFQCNByFactName(factType);
        final List<MethodInfo> methodInformation = projectMethodInformation.get(fqcnFactName);

        //Load incremental content
        if (methodInformation == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    final List<MethodInfo> methodInformation = projectMethodInformation.get(fqcnFactName);
                    callback.callback(getMethodParams(methodInformation,
                                                      methodNameWithParams));
                }
            }).getUpdates(resourcePath,
                          imports,
                          fqcnFactName);
        } else {
            callback.callback(getMethodParams(methodInformation,
                                              methodNameWithParams));
        }
    }

    private List<String> getMethodParams(final List<MethodInfo> methodInfos,
                                         final String methodNameWithParams) {
        final List<String> methodParams = new ArrayList<String>();
        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.getNameWithParameters().startsWith(methodNameWithParams)) {
                methodParams.addAll(methodInfo.getParams());
            }
        }
        return methodParams;
    }

    /**
     * Get information on a Method of a Fact Type
     * @param factType
     * @param methodNameWithParams
     * @return
     */
    @Override
    public void getMethodInfo(final String factType,
                              final String methodNameWithParams,
                              final Callback<MethodInfo> callback) {
        final String fqcnFactName = getFQCNByFactName(factType);
        final List<MethodInfo> methodInformation = projectMethodInformation.get(fqcnFactName);

        //Load incremental content
        if (methodInformation == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    final List<MethodInfo> methodInformation = projectMethodInformation.get(fqcnFactName);
                    callback.callback(getMethodInfo(methodInformation,
                                                    methodNameWithParams));
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(getMethodInfo(methodInformation,
                                            methodNameWithParams));
        }
    }

    private MethodInfo getMethodInfo(final List<MethodInfo> methodInfos,
                                     final String methodNameWithParams) {
        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.getNameWithParameters().equals(methodNameWithParams)) {
                return AsyncPackageDataModelOracleUtilities.correctMethodInformation(packageName,
                                                                                     methodInfo,
                                                                                     imports);
            }
        }
        return null;
    }

    // ####################################
    // Globals
    // ####################################

    @Override
    public String[] getGlobalVariables() {
        return OracleUtils.toStringArray(filteredGlobalTypes.keySet());
    }

    @Override
    public String getGlobalVariable(final String name) {
        return filteredGlobalTypes.get(name);
    }

    @Override
    public boolean isGlobalVariable(final String name) {
        return filteredGlobalTypes.containsKey(name);
    }

    @Override
    public void getFieldCompletionsForGlobalVariable(final String varName,
                                                     final Callback<ModelField[]> callback) {
        getFieldCompletions(
                getGlobalVariable(varName),
                new Callback<ModelField[]>() {
                    @Override
                    public void callback(ModelField[] result) {
                        callback.callback(result);
                    }
                });
    }

    @Override
    public void getMethodInfosForGlobalVariable(final String varName,
                                                final Callback<List<MethodInfo>> callback) {
        final String factType = packageGlobalTypes.get(varName);
        final List<MethodInfo> methodInformation = projectMethodInformation.get(factType);

        //Load incremental content
        if (methodInformation == null) {
            service.call(new RemoteCallback<PackageDataModelOracleIncrementalPayload>() {

                @Override
                public void callback(final PackageDataModelOracleIncrementalPayload dataModel) {
                    AsyncPackageDataModelOracleUtilities.populateDataModelOracle(AsyncPackageDataModelOracleImpl.this,
                                                                                 dataModel);
                    callback.callback(projectMethodInformation.get(factType));
                }
            }).getUpdates(resourcePath,
                          imports,
                          factType);
        } else {
            callback.callback(methodInformation);
        }
    }

    @Override
    public String[] getGlobalCollections() {
        final List<String> globalCollections = new ArrayList<String>();
        for (Map.Entry<String, String> e : filteredGlobalTypes.entrySet()) {
            if (filteredCollectionTypes.containsKey(e.getValue())) {
                if (Boolean.TRUE.equals(filteredCollectionTypes.get(e.getValue()))) {
                    globalCollections.add(e.getKey());
                }
            }
        }
        return OracleUtils.toStringArray(globalCollections);
    }

    @Override
    public List<String> getAvailableCollectionTypes() {
        return Collections.unmodifiableList(filteredCollectionTypes.entrySet()
                                                    .stream()
                                                    .filter(entry -> entry.getValue())
                                                    .map(entry -> entry.getKey())
                                                    .collect(Collectors.toList()));
    }

    // ####################################
    // DSLs
    // ####################################

    @Override
    public List<DSLSentence> getDSLConditions() {
        return Collections.unmodifiableList(packageDSLConditionSentences);
    }

    @Override
    public List<DSLSentence> getDSLActions() {
        return Collections.unmodifiableList(packageDSLActionSentences);
    }

    // ####################################
    // Enums
    // ####################################

    /**
     * Get enums for a Type and Field.
     */
    @Override
    public DropDownData getEnums(final String type,
                                 final String field) {
        return getEnums(type,
                        field,
                        new HashMap<String, String>());
    }

    /**
     * Get enums for a Type and Field where the enum list may depend upon the values of other fields.
     */
    @Override
    public DropDownData getEnums(final String type,
                                 final String field,
                                 final Map<String, String> currentValueMap) {
        return new EnumDropDownDataFactory(filteredEnumLists,
                                           currentValueMap).getEnums(type,
                                                                     field);
    }

    @Override
    public String[] getEnumValues(String factType,
                                  String factField) {
        return filteredEnumLists.getEnumValues(factType,
                                               factField);
    }

    @Override
    public boolean hasEnums(final String factType,
                            final String field) {
        return hasEnums(factType + "#" + field);
    }

    @Override
    public boolean hasEnums(final String qualifiedFactField) {
        return filteredEnumLists.hasEnums(qualifiedFactField);
    }

    /**
     * Check whether the childField is related to the parentField through a
     * chain of enumeration dependencies. Both fields belong to the same Fact
     * Type. Furthermore code consuming this function should ensure both
     * parentField and childField relate to the same Fact Pattern
     * @param factType
     * @param parentField
     * @param childField
     * @return
     */
    @Override
    public boolean isDependentEnum(final String factType,
                                   final String parentField,
                                   final String childField) {
        return filteredEnumLists.isDependentEnum(factType,
                                                 parentField,
                                                 childField);
    }

    // ####################################
    // Imports
    // ####################################

    @Override
    public void filter(final Imports imports) {
        this.imports = imports;
        filter();
    }

    @Override
    public void filter() {

        //Filter and rename Model Fields based on package name and imports
        filteredModelFields = new TreeMap<String, ModelField[]>(SortHelper.ALPHABETICAL_ORDER_COMPARATOR);
        filteredModelFields.putAll(AsyncPackageDataModelOracleUtilities.filterModelFields(packageName,
                                                                                          imports,
                                                                                          projectModelFields,
                                                                                          factNameToFQCNHandleRegistry));

        // For filling the factNameToFQCNHandleRegistry
        AsyncPackageDataModelOracleUtilities.visitMethodInformation(projectMethodInformation,
                                                                    factNameToFQCNHandleRegistry);

        //Filter and rename Global Types based on package name and imports
        filteredGlobalTypes = new TreeMap<String, String>(SortHelper.ALPHABETICAL_ORDER_COMPARATOR);
        filteredGlobalTypes.putAll(AsyncPackageDataModelOracleUtilities.filterGlobalTypes(packageName,
                                                                                          imports,
                                                                                          packageGlobalTypes));

        //Filter and rename Collection Types based on package name and imports
        filteredCollectionTypes = new HashMap<String, Boolean>();
        filteredCollectionTypes.putAll(AsyncPackageDataModelOracleUtilities.filterCollectionTypes(packageName,
                                                                                                  imports,
                                                                                                  projectCollectionTypes));

        //Filter and rename Event Types based on package name and imports
        filteredEventTypes = new HashMap<String, Boolean>();
        filteredEventTypes.putAll(AsyncPackageDataModelOracleUtilities.filterEventTypes(packageName,
                                                                                        imports,
                                                                                        projectEventTypes));

        //Filter and rename TypeSources based on package name and imports
        filteredTypeSources = new HashMap<String, TypeSource>();
        filteredTypeSources.putAll(AsyncPackageDataModelOracleUtilities.filterTypeSources(packageName,
                                                                                          imports,
                                                                                          projectTypeSources));

        //Filter and rename Declared Types based on package name and imports
        filteredSuperTypes = new HashMap<String, List<String>>();
        filteredSuperTypes.putAll(AsyncPackageDataModelOracleUtilities.filterSuperTypes(packageName,
                                                                                        imports,
                                                                                        projectSuperTypes));

        //Filter and rename Type Annotations based on package name and imports
        filteredTypeAnnotations = new HashMap<String, Set<Annotation>>();
        filteredTypeAnnotations.putAll(AsyncPackageDataModelOracleUtilities.filterTypeAnnotations(packageName,
                                                                                                  imports,
                                                                                                  projectTypeAnnotations));

        //Filter and rename Type Field Annotations based on package name and imports
        filteredTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();
        filteredTypeFieldsAnnotations.putAll(AsyncPackageDataModelOracleUtilities.filterTypeFieldsAnnotations(packageName,
                                                                                                              imports,
                                                                                                              projectTypeFieldsAnnotations));

        //Filter and rename Enum definitions based on package name and imports
        filteredEnumLists = new FilteredEnumLists();
        filteredEnumLists.putAll(packageWorkbenchEnumLists);
        filteredEnumLists.putAll(AsyncPackageDataModelOracleUtilities.filterEnumDefinitions(packageName,
                                                                                            imports,
                                                                                            projectJavaEnumLists));

        //Filter and rename based on package name and imports
        filteredFieldParametersType = new HashMap<String, String>();
        filteredFieldParametersType.putAll(AsyncPackageDataModelOracleUtilities.filterFieldParametersTypes(packageName,
                                                                                                           imports,
                                                                                                           projectFieldParametersType));
    }

    // ####################################
    // Population of DMO
    // ####################################

    @Override
    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void addModelFields(final Map<String, ModelField[]> modelFields) {
        for (ModelField[] value : modelFields.values()) {
            if (value != null) {
                Arrays.sort(value,
                            getModelFieldComparator());
            }
        }

        this.projectModelFields.putAll(modelFields);
    }

    @Override
    public void addFieldParametersType(final Map<String, String> fieldParametersType) {
        this.projectFieldParametersType.putAll(fieldParametersType);
    }

    @Override
    public void addEventTypes(final Map<String, Boolean> eventTypes) {
        this.projectEventTypes.putAll(eventTypes);
    }

    @Override
    public void addTypeSources(final Map<String, TypeSource> typeSources) {
        this.projectTypeSources.putAll(typeSources);
    }

    @Override
    public void addSuperTypes(final Map<String, List<String>> superTypes) {
        for (List<String> value : superTypes.values()) {
            if (value != null) {
                Collections.sort(value,
                                 SortHelper.ALPHABETICAL_ORDER_COMPARATOR);
            }
        }

        this.projectSuperTypes.putAll(superTypes);
    }

    @Override
    public void addTypeAnnotations(final Map<String, Set<Annotation>> annotations) {
        this.projectTypeAnnotations.putAll(annotations);
    }

    @Override
    public void addTypeFieldsAnnotations(final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations) {
        this.projectTypeFieldsAnnotations.putAll(typeFieldsAnnotations);
    }

    @Override
    public void addJavaEnumDefinitions(final Map<String, String[]> dataEnumLists) {
        this.projectJavaEnumLists.putAll(dataEnumLists);
    }

    @Override
    public void addMethodInformation(final Map<String, List<MethodInfo>> methodInformation) {
        for (List<MethodInfo> value : methodInformation.values()) {
            if (value != null) {
                Collections.sort(value,
                                 getMethodInfoComparator());
            }
        }

        this.projectMethodInformation.putAll(methodInformation);
    }

    @Override
    public void addCollectionTypes(final Map<String, Boolean> collectionTypes) {
        this.projectCollectionTypes.putAll(collectionTypes);
    }

    @Override
    public void addPackageNames(final List<String> packageNames) {
        Collections.sort(packageNames,
                         SortHelper.ALPHABETICAL_ORDER_COMPARATOR);
        this.packageNames.addAll(packageNames);
    }

    @Override
    public void addWorkbenchEnumDefinitions(final Map<String, String[]> dataEnumLists) {
        this.packageWorkbenchEnumLists.putAll(dataEnumLists);
    }

    @Override
    public void addDslConditionSentences(final List<DSLSentence> dslConditionSentences) {
        this.packageDSLConditionSentences.addAll(dslConditionSentences);
    }

    @Override
    public void addDslActionSentences(final List<DSLSentence> dslActionSentences) {
        this.packageDSLActionSentences.addAll(dslActionSentences);
    }

    @Override
    public void addGlobals(final Map<String, String> packageGlobalTypes) {
        this.packageGlobalTypes.putAll(packageGlobalTypes);
    }

    private Comparator<ModelField> getModelFieldComparator() {
        return new Comparator<ModelField>() {
            @Override
            public int compare(final ModelField modelField1,
                               final ModelField modelField2) {
                return SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare(modelField1.getName(),
                                                                        modelField2.getName());
            }
        };
    }

    private Comparator<MethodInfo> getMethodInfoComparator() {
        return new Comparator<MethodInfo>() {
            @Override
            public int compare(final MethodInfo methodInfo1,
                               final MethodInfo methodInfo2) {
                int result = SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare(methodInfo1.getName(),
                                                                              methodInfo2.getName());

                if (result == 0) {
                    if (methodInfo1.getParams() != null && methodInfo2.getParams() == null) {
                        return 1;
                    } else if (methodInfo1.getParams() == null && methodInfo2.getParams() != null) {
                        return -1;
                    } else if (methodInfo1.getParams() != null && methodInfo2.getParams() != null) {
                        result = methodInfo1.getParams().size() - methodInfo2.getParams().size();

                        if (result == 0) {
                            for (int i = 0; i < methodInfo1.getParams().size() && result == 0; i++) {
                                result = SortHelper.ALPHABETICAL_ORDER_COMPARATOR.compare(methodInfo1.getParams().get(i),
                                                                                          methodInfo2.getParams().get(i));
                            }
                        }
                    }
                }

                return result;
            }
        };
    }
}
