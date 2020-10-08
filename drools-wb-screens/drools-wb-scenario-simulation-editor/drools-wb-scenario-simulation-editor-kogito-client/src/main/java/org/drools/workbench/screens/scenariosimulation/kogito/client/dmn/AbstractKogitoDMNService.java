/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.feel.BuiltInType;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

/**
 * Abstract class to provide methods shared by <b>runtime</b> and <b>testing</b> environments.
 * <p>
 * Most of this code is cloned/adapted from ScenarioSimulation backend and dmn core
 */
public abstract class AbstractKogitoDMNService implements KogitoDMNService {

    public static final String URI_FEEL = "http://www.omg.org/spec/DMN/20180521/FEEL/";
    public static final String WRONG_DMN_MESSAGE = "Wrong DMN Type";
    protected static final QName TYPEREF_QNAME = new QName("", "typeRef", "");

    @Override
    public FactModelTuple getFactModelTuple(final JSITDefinitions jsitDefinitions) {
        SortedMap<String, FactModelTree> visibleFacts = new TreeMap<>();
        SortedMap<String, FactModelTree> hiddenFacts = new TreeMap<>();
        ErrorHolder errorHolder = new ErrorHolder();
        Map<String, ClientDMNType> dmnTypesMap = getDMNDataTypesMap(jsitDefinitions.getItemDefinition(), jsitDefinitions.getNamespace());
        final List<JSITDRGElement> jsitdrgElements = jsitDefinitions.getDrgElement();
        for (int i = 0; i < jsitdrgElements.size(); i++) {
            final JSITDRGElement jsitdrgElement = Js.uncheckedCast(jsitdrgElements.get(i));
            if (isJSITInputData(jsitdrgElement)) {
                JSITInputData jsitInputData = Js.uncheckedCast(jsitdrgElement);
                final JSITInformationItem jsitInputDataVariable = jsitInputData.getVariable();
                ClientDMNType type = getDMNTypeFromMaps(dmnTypesMap, getOtherAttributesMap(jsitInputDataVariable));
                checkTypeSupport(type, errorHolder, jsitInputData.getName());
                visibleFacts.put(jsitInputData.getName(), createTopLevelFactModelTree(jsitInputData.getName(), type, hiddenFacts, FactModelTree.Type.INPUT));
            } else if (isJSITDecision(jsitdrgElement)) {
                JSITDecision jsitDecision = Js.uncheckedCast(jsitdrgElement);
                final JSITInformationItem jsitDecisionVariable = jsitDecision.getVariable();
                ClientDMNType type = getDMNTypeFromMaps(dmnTypesMap, getOtherAttributesMap(jsitDecisionVariable));
                checkTypeSupport(type, errorHolder, jsitdrgElement.getName());
                visibleFacts.put(jsitDecisionVariable.getName(), createTopLevelFactModelTree(jsitDecisionVariable.getName(), type, hiddenFacts, FactModelTree.Type.DECISION));
            }
        }
        FactModelTuple toReturn = new FactModelTuple(visibleFacts, hiddenFacts);
        errorHolder.getMultipleNestedCollection().forEach(toReturn::addMultipleNestedCollectionError);
        errorHolder.getMultipleNestedObject().forEach(toReturn::addMultipleNestedObjectError);
        return toReturn;
    }

    /**
     * It retrieves the <code>ClientDMNType</code> of the current typeRef source (Decision or Input Data).
     * If the source has an empty typeRef, the default value <code>BuiltInType.ANY</code> is assigned.
     * @param dmnTypesMap
     * @param source
     * @return
     */
    protected ClientDMNType getDMNTypeFromMaps(final Map<String, ClientDMNType> dmnTypesMap,
                                               final Map<QName, String> source) {
        String typeRef = source.get(TYPEREF_QNAME);
        if (typeRef == null) {
            typeRef = BuiltInType.ANY.getName();
        }
        return dmnTypesMap.get(typeRef);
    }

    /**
     * This method retrieves all DMN Data Types are composed by
     * - DEFAULT types, listed inside <code>BuiltInType</code> enum
     * - CUSTOM types, which are available inside <code>jsitItemDefinitions</code> list
     * @param jsitItemDefinitions List of CUSTOM Data Type in raw format
     * @param nameSpace namespace
     * @return An UNMODIFIABLE map containing all DEFAULT and CUSTOM Data Types.
     */
    protected Map<String, ClientDMNType> getDMNDataTypesMap(final List<JSITItemDefinition> jsitItemDefinitions,
                                                            final String nameSpace) {
        Map<String, ClientDMNType> dmnDataTypesMap = new HashMap<>();

        /* Adding DEFAULT types */
        for (BuiltInType type : BuiltInType.values()) {

            for (String name : type.getNames()) {
                ClientDMNType feelPrimitiveType;
                /* CONTEXT is a particular case of DEFAULT type */
                if (type == BuiltInType.CONTEXT) {
                    feelPrimitiveType = new ClientDMNType(URI_FEEL, name, null, false, true, Collections.emptyMap(), type);
                } else {
                    feelPrimitiveType = new ClientDMNType(URI_FEEL, name, null, false, type);
                }
                dmnDataTypesMap.put(name, feelPrimitiveType);
            }
        }

        final Map<String, JSITItemDefinition> itemDefinitionMap = indexDefinitionsByName(jsitItemDefinitions);
        /* Adding CUSTOM Types, extracted from jsitItemDefinitions map */
        for (int i = 0; i < jsitItemDefinitions.size(); i++) {
            final JSITItemDefinition jsitItemDefinition = Js.uncheckedCast(jsitItemDefinitions.get(i));
            getOrCreateDMNType(itemDefinitionMap, jsitItemDefinition.getName(), nameSpace, dmnDataTypesMap);
        }

        return Collections.unmodifiableMap(dmnDataTypesMap);
    }

    /**
     * It checks if the given *requiredType*'s <code>ClientDMNType</code> is already present into given createdTypes
     * map If yes, it return it. It creates it otherwise.
     * @param allDefinitions
     * @param requiredType
     * @param namespace
     * @param createdTypes
     * @return *requiredType*'s <code>ClientDMNType</code>
     */
    ClientDMNType getOrCreateDMNType(final Map<String, JSITItemDefinition> allDefinitions,
                                     final String requiredType,
                                     final String namespace,
                                     final Map<String, ClientDMNType> createdTypes) {

        if (createdTypes.containsKey(requiredType)) {
            return createdTypes.get(requiredType);
        }

        /* This method can handle ONLY CUSTOM types (DEFAULT must be already present into createdTypes map) */
        if (!allDefinitions.containsKey(requiredType)) {
            throw new IllegalStateException("Type '" + requiredType + "' not found.");
        }

        final JSITItemDefinition value = Js.uncheckedCast(allDefinitions.get(requiredType));
        return createDMNType(allDefinitions, value, namespace, createdTypes);
    }

    /**
     * It creates an *unmodifiable* map of JSITItemDefinition object, with its name as a key. These are the first
     * level user defined types (CUSTOM Data Type)
     * @param allDefinitions
     * @return
     */
    Map<String, JSITItemDefinition> indexDefinitionsByName(final List<JSITItemDefinition> allDefinitions) {
        final HashMap<String, JSITItemDefinition> index = new HashMap<>();
        for (int i = 0; i < allDefinitions.size(); i++) {
            final JSITItemDefinition value = Js.uncheckedCast(allDefinitions.get(i));
            index.put(value.getName(), value);
        }
        return Collections.unmodifiableMap(index);
    }

    /**
     * This method creates a <code>ClientDMNType</code> object of a given <code>JSITItemDefinition</code> object or
     * a <code>JSITItemComponent</code> as well.
     * @param allDefinitions
     * @param itemDefinition
     * @param namespace
     * @param dmnTypesDataMap
     * @return
     */
    protected ClientDMNType createDMNType(final Map<String, JSITItemDefinition> allDefinitions,
                                          final JSITItemDefinition itemDefinition,
                                          final String namespace,
                                          final Map<String, ClientDMNType> dmnTypesDataMap) {
        /* It initializes a ClientDMNType which represents the given itemDefinition */
        ClientDMNType itemDefinitionDMNType = new ClientDMNType(namespace,
                                                                itemDefinition.getName(),
                                                                itemDefinition.getId(),
                                                                itemDefinition.getIsCollection(),
                                                                itemDefinition.getItemComponent() != null &&
                                                                        !itemDefinition.getItemComponent().isEmpty());
        /* It adds the created ClientDMNType into dmnTypesDataMap ONLY if is a CUSTOM data type, and not a ItemComponent (a field) */
        if (allDefinitions.containsKey(itemDefinition.getName())) {
            dmnTypesDataMap.put(itemDefinition.getName(), itemDefinitionDMNType);
        }

        /* Inheriting fields defined from item's typeRef, which represent its "super item" */
        /* This is required to define DMNType fields and isCollection / isComposite fields */
        String typeRef = itemDefinition.getTypeRef();
        if (typeRef != null) {
            final ClientDMNType superDmnType = getOrCreateDMNType(allDefinitions,
                                                                  typeRef,
                                                                  namespace,
                                                                  dmnTypesDataMap);
            if (superDmnType != null) {
                /* Current clientDmnType item must inherits these properties from its super type */
                itemDefinitionDMNType.addFields(superDmnType.getFields());
                itemDefinitionDMNType.setCollection(superDmnType.isCollection() || itemDefinitionDMNType.isCollection());
                itemDefinitionDMNType.setIsComposite(superDmnType.isComposite() || itemDefinitionDMNType.isComposite());
                itemDefinitionDMNType.setFeelType(superDmnType.getFeelType());
                itemDefinitionDMNType.setBaseType(getBaseTypeForItemDefinition(itemDefinition, superDmnType));
            } else {
                throw new IllegalStateException(
                        "Item: " + itemDefinition.getName() + " refers to typeRef: " + itemDefinition.getTypeRef()
                                + " which can't be found.");
            }
        }
        populateItemDefinitionFields(allDefinitions,
                                     itemDefinition,
                                     namespace,
                                     dmnTypesDataMap,
                                     itemDefinitionDMNType);

        return itemDefinitionDMNType;
   }

    protected ClientDMNType getBaseTypeForItemDefinition(JSITItemDefinition itemDefinition, ClientDMNType superDmnType) {
        final boolean hasAllowedValues = itemDefinition.getAllowedValues() != null
                && !itemDefinition.getAllowedValues().getText().isEmpty();
        return hasAllowedValues ? superDmnType : superDmnType.getBaseType();
    }

    /**
     * This method aim is to populate all fields and subfields present in a given <code>JSITItemDefinitionItem</code>
     * Considering a field can contains subfields, this method is *recursive*. Be sure an exit condition is correctly set.
     * Currently, the methods exits if there are not fields inside the given itemDefinition.
     * @param allItemDefinitions
     * @param itemDefinition
     * @param namespace
     * @param dmnTypesMap
     * @param clientDMNType
     */
    protected void populateItemDefinitionFields(final Map<String, JSITItemDefinition> allItemDefinitions,
                                                final JSITItemDefinition itemDefinition,
                                                final String namespace,
                                                final Map<String, ClientDMNType> dmnTypesMap,
                                                final ClientDMNType clientDMNType) {
        final List<JSITItemDefinition> jsitItemDefinitionFields = itemDefinition.getItemComponent();
        /* Exit condition: if current itemDefinition hasn't fields, it returns */
        if (jsitItemDefinitionFields != null && !jsitItemDefinitionFields.isEmpty()) {

            for (int i = 0; i < jsitItemDefinitionFields.size(); i++) {
                final JSITItemDefinition jsitItemDefinitionField = Js.uncheckedCast(jsitItemDefinitionFields.get(i));
                final String typeRef = jsitItemDefinitionField.getTypeRef();
                final List<JSITItemDefinition> subfields = jsitItemDefinitionField.getItemComponent();
                final boolean hasSubFields = subfields != null && !subfields.isEmpty();
                final boolean hasAllowedValues = jsitItemDefinitionField.getAllowedValues() != null
                        && !jsitItemDefinitionField.getAllowedValues().getText().isEmpty();

                ClientDMNType fieldDMNType;
                /* Retrieving field ClientDMNType */
                if (typeRef != null && !hasSubFields && !hasAllowedValues) {
                    /* The field refers to a DMType which is present in dmnTypesMap or needs to be created */
                    fieldDMNType = getOrCreateDMNType(allItemDefinitions, typeRef, namespace, dmnTypesMap);
                } else if (typeRef != null && !hasSubFields && hasAllowedValues) {
                    /* This case requires to create a new "Anonymous" DMNType to handle the allowed values */
                    fieldDMNType = createDMNType(allItemDefinitions, jsitItemDefinitionField, namespace, dmnTypesMap);
                } else if (typeRef == null && hasSubFields) {
                    /* In this case we are handling an "Anonymous" type not defined in allItemDefinition list.
                     * Therefore, a new DMNType must be created and then it manages its defined subfields in recursive way */
                    fieldDMNType = createDMNType(allItemDefinitions, jsitItemDefinitionField, namespace, dmnTypesMap);
                } else {
                    /* Remaining cases are not managed, because invalid. Eg typeRef empty and no subfields OR typeRef
                     * empty and at least one subfield */
                    continue;
                }

                /* Managing the case where the field in analysis is set as collection where its retrieved DMNType is not
                 * This can happen when the DMNType is declared as notCollection and a field referring to it is declared
                 * as collection. In this case, a new DMNType is *copied* with set collection true. */
                if (jsitItemDefinitionField.getIsCollection() && !fieldDMNType.isCollection()) {
                    fieldDMNType = fieldDMNType.copyAsCollection();
                }

                clientDMNType.addField(jsitItemDefinitionField.getName(), fieldDMNType);
            }
        }
    }

    /**
     * Recursively visit a <i>composite</i> <code>DMNType</code> to eventually detect and add errors to given <code>ErrorHolder</code>
     *
     * @param type
     * @param errorHolder
     * @param fullPropertyPath
     */
    protected void checkTypeSupport(final ClientDMNType type,
                                    final ErrorHolder errorHolder,
                                    final String fullPropertyPath) {
        internalCheckTypeSupport(type, false, errorHolder, fullPropertyPath, new HashSet<>());
    }

    /**
     * @param type
     * @param alreadyInCollection
     * @param errorHolder
     * @param fullPropertyPath
     * @param alreadyVisited
     */
    protected void internalCheckTypeSupport(final ClientDMNType type,
                                            final boolean alreadyInCollection,
                                            final ErrorHolder errorHolder,
                                            final String fullPropertyPath,
                                            final Set<String> alreadyVisited) {
        alreadyVisited.add(type.getName());
        if (type.isComposite()) {
            for (Map.Entry<String, ClientDMNType> entry : type.getFields().entrySet()) {
                String name = entry.getKey();
                ClientDMNType nestedType = entry.getValue();
                String nestedPath = fullPropertyPath + "." + name;
                if (alreadyInCollection && nestedType.isCollection()) {
                    errorHolder.getMultipleNestedCollection().add(nestedPath);
                }
                if (alreadyInCollection && nestedType.isComposite()) {
                    errorHolder.getMultipleNestedObject().add(nestedPath);
                }
                if (!alreadyVisited.contains(nestedType.getName())) {
                    internalCheckTypeSupport(nestedType,
                                             alreadyInCollection || nestedType.isCollection(),
                                             errorHolder,
                                             nestedPath,
                                             alreadyVisited);
                }
            }
        }
    }

    /**
     * This method is the <b>entry point</b> for <code>FactModelTree</code>. It is the one to be called from the very top level <code>DMNType</code>
     *
     * @param factName
     * @param type
     * @param hiddenFacts
     * @param fmType
     * @return
     */
    protected FactModelTree createTopLevelFactModelTree(final String factName,
                                                        final ClientDMNType type,
                                                        final SortedMap<String, FactModelTree> hiddenFacts,
                                                        final FactModelTree.Type fmType) {
        return isToBeManagedAsCollection(type) ?
                createFactModelTreeForCollection(new HashMap<>(), factName, type, hiddenFacts, fmType, new ArrayList<>()) :
                createFactModelTreeForNoCollection(new HashMap<>(), factName, factName, type.getName(), type, hiddenFacts, fmType, new ArrayList<>());
    }

    /**
     * Return <code>true</code> if the given <code>DMNType</code> has to be managed as <b>collection</b>
     *
     * @param type
     * @return <code>true</code> if <code>DMNType.isCollection() == true</code> <b>and</b> <code>BaseDMNTypeImpl.getFeelType() != BuiltInType.UNKNOWN</code>
     */
    protected boolean isToBeManagedAsCollection(final ClientDMNType type) {
        boolean toReturn = type.isCollection();
        if (toReturn) {
            BuiltInType feelType = type.getFeelType();
            // BuiltInType.CONTEXT is a special case: it is instantiated as composite but has no nested fields
            // so it should be considered as simple for editing
            if (Objects.equals(BuiltInType.CONTEXT, feelType)) {
                toReturn = false;
            }
        }
        return toReturn;
    }

    /**
     * Return <code>true</code> if the given <code>DMNType</code> has to be managed as <b>composite</b>
     *
     * @param type
     * @return <code>true</code> if <code>DMNType.isCollection() == true</code> <b>and</b> <code>BaseDMNTypeImpl.getFeelType() != BuiltInType.UNKNOWN</code>
     */
    protected boolean isToBeManagedAsComposite(final ClientDMNType type) {
        boolean toReturn = type.isComposite();
        if (toReturn) {
            BuiltInType feelType = type.getFeelType();
            // BuiltInType.CONTEXT is a special case: it is instantiated as composite but has no nested fields so it should be considered as simple for editing
            if (Objects.equals(BuiltInType.CONTEXT, feelType)) {
                toReturn = false;
            }
        }
        return toReturn;
    }

    /**
     * Creates a <code>FactModelTree</code> for <code>DMNType</code> where <code>DMNType.isCollection()</code> == <code>true</code>
     *
     * @param genericTypeInfoMap
     * @param factName
     * @param type
     * @param hiddenFacts
     * @param fmType
     * @param alreadyVisited
     * @return
     * @throws Exception if <code>DMNType.isCollection()</code> != <code>true</code>
     */
    protected FactModelTree createFactModelTreeForCollection(final Map<String, List<String>> genericTypeInfoMap,
                                                             final String factName,
                                                             final ClientDMNType type,
                                                             final SortedMap<String, FactModelTree> hiddenFacts,
                                                             final FactModelTree.Type fmType,
                                                             final List<String> alreadyVisited) {
        if (!type.isCollection() && !isToBeManagedAsCollection(type)) {
            throw new IllegalStateException(WRONG_DMN_MESSAGE);
        }
        String typeName = type.getName();
        populateGeneric(genericTypeInfoMap, VALUE, typeName);
        FactModelTree toReturn = createFactModelTreeForSimple(genericTypeInfoMap, factName, List.class.getCanonicalName(), fmType);
        if (!hiddenFacts.containsKey(typeName) && !alreadyVisited.contains(typeName)) {
            alreadyVisited.add(typeName);
            FactModelTree genericFactModelTree = createFactModelTreeForGenericType(new HashMap<>(), typeName, typeName, typeName, type, hiddenFacts, fmType, alreadyVisited);
            hiddenFacts.put(typeName, genericFactModelTree);
        }
        return toReturn;
    }

    /**
     * This method map the given <b>name</b> to <b>List.class.getCanonicalName()</b> inside <b>simpleFields</b>,
     * and map <b>name</b>  to a singleton list containing a newly generated <b>key</b> (path + "." + type) inside <b>genericTypeInfoMap</b>
     *
     * @param genericTypeInfoMap
     * @param fullPropertyPath
     * @param type
     * @return
     */
    protected String populateGeneric(final Map<String, List<String>> genericTypeInfoMap,
                                     final String fullPropertyPath,
                                     final String type) {
        String toReturn = fullPropertyPath;
        genericTypeInfoMap.put(fullPropertyPath, Collections.singletonList(type));
        return toReturn;
    }

    /**
     * Creates a <code>FactModelTree</code> for <code>DMNType</code> where <code>DMNType.isCollection()</code> != <code>true</code>
     *
     * @param genericTypeInfoMap
     * @param factName
     * @param propertyName
     * @param fullPropertyPath
     * @param type
     * @param hiddenFacts
     * @param fmType
     * @param alreadyVisited
     * @return
     * @throws Exception if <code>DMNType.isCollection()</code> == <code>true</code>
     */
    protected FactModelTree createFactModelTreeForNoCollection(final Map<String, List<String>> genericTypeInfoMap,
                                                               final String factName,
                                                               final String propertyName,
                                                               final String fullPropertyPath,
                                                               final ClientDMNType type,
                                                               final SortedMap<String, FactModelTree> hiddenFacts,
                                                               final FactModelTree.Type fmType,
                                                               final List<String> alreadyVisited) {
        if (type.isCollection() && isToBeManagedAsCollection(type)) {
            throw new IllegalStateException(WRONG_DMN_MESSAGE);
        }
        return isToBeManagedAsComposite(type) ?
                createFactModelTreeForComposite(genericTypeInfoMap, propertyName, fullPropertyPath, type, hiddenFacts, fmType, alreadyVisited) :
                createFactModelTreeForSimple(genericTypeInfoMap, factName, type.getName(), fmType);
    }

    /**
     * Creates a <code>FactModelTree</code> for <code>DMNType</code>
     *
     * @param genericTypeInfoMap
     * @param factName
     * @param propertyName
     * @param fullPropertyPath
     * @param type
     * @param hiddenFacts
     * @param fmType
     * @param alreadyVisited
     * @return
     * @throws Exception
     */
    protected FactModelTree createFactModelTreeForGenericType(final Map<String, List<String>> genericTypeInfoMap,
                                                              final String factName,
                                                              final String propertyName,
                                                              final String fullPropertyPath,
                                                              final ClientDMNType type,
                                                              final SortedMap<String, FactModelTree> hiddenFacts,
                                                              final FactModelTree.Type fmType,
                                                              final List<String> alreadyVisited) {
        return type.isComposite() ? createFactModelTreeForComposite(genericTypeInfoMap, propertyName, fullPropertyPath, type, hiddenFacts, fmType, alreadyVisited) : createFactModelTreeForSimple(genericTypeInfoMap, factName, type.getName(), fmType);
    }

    /**
     * Creates a <code>FactModelTree</code> for <code>DMNType</code> where <code>DMNType.isComposite()</code> != <code>true</code>.
     * Returned <code>FactModelTree</code> will have only one single property, whose name is <b>VALUE</b> and whose value is the given <b>propertyClass</b>
     *
     * @param genericTypeInfoMap
     * @param factName
     * @param propertyClass
     * @param fmType
     * @return
     */
    protected FactModelTree createFactModelTreeForSimple(final Map<String, List<String>> genericTypeInfoMap,
                                                         final String factName,
                                                         final String propertyClass,
                                                         final FactModelTree.Type fmType) {
        Map<String, FactModelTree.PropertyTypeName> simpleProperties = new HashMap<>();
        FactModelTree simpleFactModelTree = new FactModelTree(factName, "", simpleProperties, genericTypeInfoMap, fmType);
        simpleFactModelTree.addSimpleProperty(VALUE, new FactModelTree.PropertyTypeName(propertyClass));
        simpleFactModelTree.setSimple(true);
        return simpleFactModelTree;
    }

    /**
     * Creates a <code>FactModelTree</code> for <code>DMNType</code> where <code>DMNType.isComposite()</code> == <code>true</code>
     *
     * @param genericTypeInfoMap
     * @param name
     * @param fullPropertyPath
     * @param type
     * @param hiddenFacts
     * @param fmType
     * @param alreadyVisited
     * @return
     * @throws Exception if <code>DMNType.isComposite()</code> != <code>true</code>
     */
    protected FactModelTree createFactModelTreeForComposite(final Map<String, List<String>> genericTypeInfoMap,
                                                            final String name,
                                                            final String fullPropertyPath,
                                                            final ClientDMNType type,
                                                            final SortedMap<String, FactModelTree> hiddenFacts,
                                                            final FactModelTree.Type fmType,
                                                            final List<String> alreadyVisited) {
        if (!type.isComposite() && !isToBeManagedAsComposite(type)) {
            throw new IllegalStateException(WRONG_DMN_MESSAGE);
        }
        Map<String, FactModelTree.PropertyTypeName> simpleFields = new HashMap<>();
        FactModelTree toReturn = new FactModelTree(name, "", simpleFields, genericTypeInfoMap, fmType);
        for (Map.Entry<String, ClientDMNType> entry : type.getFields().entrySet()) {
            String expandablePropertyName = fullPropertyPath + "." + entry.getKey();
            if (isToBeManagedAsCollection(entry.getValue())) {  // if it is a collection, generate the generic and add as hidden fact a simple or composite fact model tree
                FactModelTree fact = createFactModelTreeForCollection(new HashMap<>(), entry.getKey(), entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED, alreadyVisited);
                simpleFields.put(entry.getKey(), new FactModelTree.PropertyTypeName(List.class.getCanonicalName()));
                genericTypeInfoMap.put(entry.getKey(), fact.getGenericTypeInfo(VALUE));
            } else {
                String typeName = entry.getValue().getName();
                if (entry.getValue().isComposite()) { // a complex type needs the expandable property and then in the hidden map, its fact model tree
                    if (!hiddenFacts.containsKey(typeName) && !alreadyVisited.contains(typeName)) {
                        alreadyVisited.add(typeName);
                        FactModelTree fact = createFactModelTreeForNoCollection(genericTypeInfoMap, entry.getKey(), VALUE, expandablePropertyName, entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED, alreadyVisited);
                        hiddenFacts.put(typeName, fact);
                    }
                    toReturn.addExpandableProperty(entry.getKey(), typeName);
                } else {
                    FactModelTree.PropertyTypeName propertyTypeName = entry.getValue().getBaseType() != null ?
                            new FactModelTree.PropertyTypeName(typeName, entry.getValue().getBaseType().getName()) :
                            new FactModelTree.PropertyTypeName(typeName);
                    simpleFields.put(entry.getKey(), propertyTypeName);
                }
            }
        }
        return toReturn;
    }

    // Indirection required for tests
    public boolean isJSITInputData(JSITDRGElement jsitdrgElement) {
        return JSITInputData.instanceOf(jsitdrgElement);
    }

    // Indirection required for tests
    public boolean isJSITDecision(JSITDRGElement jsitdrgElement) {
        return JSITDecision.instanceOf(jsitdrgElement);
    }

    // Indirection required for tests
    public Map<QName, String> getOtherAttributesMap(JSITDMNElement jsitInputDataVariable) {
        return JSITDMNElement.getOtherAttributesMap(jsitInputDataVariable);
    }

    private static class ErrorHolder {

        Set<String> multipleNestedObject = new TreeSet<>();
        Set<String> multipleNestedCollection = new TreeSet<>();

        public Set<String> getMultipleNestedObject() {
            return multipleNestedObject;
        }

        public Set<String> getMultipleNestedCollection() {
            return multipleNestedCollection;
        }
    }
}
