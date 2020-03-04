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
import java.util.Comparator;
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
        Map<String, ClientDMNType> dmnTypesMap = getDMNTypesMap(jsitDefinitions.getItemDefinition(), jsitDefinitions.getNamespace());
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

    protected ClientDMNType getDMNTypeFromMaps(final Map<String, ClientDMNType> dmnTypesMap,
                                               final Map<QName, String> source) {
        String typeRef = source.get(TYPEREF_QNAME);
        return dmnTypesMap.get(typeRef);
    }

    protected Map<String, ClientDMNType> getDMNTypesMap(final List<JSITItemDefinition> jsitItemDefinitions,
                                                        final String nameSpace) {
        Map<String, ClientDMNType> toReturn = new HashMap<>();
        for (BuiltInType type : BuiltInType.values()) {
            for (String name : type.getNames()) {
                ClientDMNType feelPrimitiveType;
                /* CONTEXT is a particular case of primitive type */
                if (type == BuiltInType.CONTEXT) {
                    feelPrimitiveType = new ClientDMNType(URI_FEEL, name, null, false, true, Collections.emptyMap(), type);
                } else {
                    feelPrimitiveType = new ClientDMNType(URI_FEEL, name, null, false, false, null, type);
                }
                toReturn.put(name, feelPrimitiveType);
            }
        }
        /* Evaluating not primitive types defined into DMN file */
        /* A sort of the items is mandatory to start the process. The reason is it needs to have dependent items,
         * referred by typeRef field, BEFORE their referred items.  */
        jsitItemDefinitions.sort(getItemDefinitionComparator());
        for (int i = 0; i < jsitItemDefinitions.size(); i++) {
            final JSITItemDefinition jsitItemDefinition = Js.uncheckedCast(jsitItemDefinitions.get(i));
            toReturn.put(jsitItemDefinition.getName(), getDMNType(jsitItemDefinition, nameSpace, toReturn));
        }
        return toReturn;
    }

    /**
     * This comparator sorts a collection of <code>JSITItemDefinition</code> in order to have dependent items, referred by
     * typeRef field, BEFORE their referred items.
     * @return
     */
    protected Comparator<JSITItemDefinition> getItemDefinitionComparator() {
        return (o1, o2) -> {
            if (o1.getTypeRef() == null && o2.getTypeRef() == null) {
                return 0;
            }
            if (o1.getTypeRef() == null) {
                return -1;
            }
            if (o2.getTypeRef() == null) {
                return 1;
            }
            if (o1.getTypeRef().equals(o2.getName())) {
                return 1;
            }
            if (o2.getTypeRef().equals(o1.getName())) {
                return -1;
            }
            return 0;
        };
    }

    /**
     * This method creates a <code>ClientDMNType</code> object of a given <code>JSITItemDefinition</code> object.
     * To correctly work, it requires a sorted <code>dmnTypesMap</code>, in order to process items which don't refer to
     * a "super items" BEFORE items with a refer to another items.
     * @param itemDefinition
     * @param namespace
     * @param dmnTypesMap
     * @return
     */
    public ClientDMNType getDMNType(final JSITItemDefinition itemDefinition,
                                    final String namespace,
                                    final Map<String, ClientDMNType> dmnTypesMap) {
        final Map<String, ClientDMNType> fields = new HashMap<>();
        boolean isCollection = itemDefinition.getIsCollection();
        /* First Step: inheriting fields defined from item's typeRef, which represent its "super item".
         *  This is required when a typeRef is defined for current itemDefinition */
        if (itemDefinition.getTypeRef() != null) {
            final ClientDMNType clientDmnType = dmnTypesMap.get(itemDefinition.getTypeRef());
            if (clientDmnType != null) {
                /* Fields are added if the referred "super item" it's a composite (eg. it holds user defined fields) */
                if (clientDmnType.isComposite()) {
                    fields.putAll(clientDmnType.getFields());
                }
                /* If "super item" is a collection, current item must inherit this property */
                isCollection = clientDmnType.isCollection() || isCollection;
            } else {
                throw new IllegalStateException(
                        "Item: " + itemDefinition.getName() + " refers to typeRef: " + itemDefinition.getTypeRef()
                                + " which can't be found. The item can be missing or required items sort is wrong");
            }
        }
        /* Second Step: retrieving fields defined into current itemDefinition */
        List<JSITItemDefinition> jsitItemDefinitions = itemDefinition.getItemComponent();
        if (jsitItemDefinitions != null && !jsitItemDefinitions.isEmpty()) {
            for (int i = 0; i < jsitItemDefinitions.size(); i++) {
                final JSITItemDefinition jsitItemDefinition = Js.uncheckedCast(jsitItemDefinitions.get(i));
                final String typeRef = jsitItemDefinition.getTypeRef();
                if (!dmnTypesMap.containsKey(typeRef)) {
                    ClientDMNType nestedClientDMNType = getDMNType(jsitItemDefinition, namespace, dmnTypesMap);
                    dmnTypesMap.put(typeRef, nestedClientDMNType);
                }
                fields.put(jsitItemDefinition.getName(), dmnTypesMap.get(typeRef));
            }
        }
        return new ClientDMNType(namespace,
                                 itemDefinition.getName(),
                                 itemDefinition.getId(),
                                 isCollection,
                                 !fields.isEmpty(),
                                 fields,
                                 null);
    }

    /**
     * Recursively visit a <i>composite</i> <code>DMNType</code> to eventually detect and add errors to given <code>ErrorHolder</code>
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
        Map<String, String> simpleProperties = new HashMap<>();
        FactModelTree simpleFactModelTree = new FactModelTree(factName, "", simpleProperties, genericTypeInfoMap, fmType);
        simpleFactModelTree.addSimpleProperty(VALUE, propertyClass);
        simpleFactModelTree.setSimple(true);
        return simpleFactModelTree;
    }

    /**
     * Creates a <code>FactModelTree</code> for <code>DMNType</code> where <code>DMNType.isComposite()</code> == <code>true</code>
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
        Map<String, String> simpleFields = new HashMap<>();
        FactModelTree toReturn = new FactModelTree(name, "", simpleFields, genericTypeInfoMap, fmType);
        for (Map.Entry<String, ClientDMNType> entry : type.getFields().entrySet()) {
            String expandablePropertyName = fullPropertyPath + "." + entry.getKey();
            if (isToBeManagedAsCollection(entry.getValue())) {  // if it is a collection, generate the generic and add as hidden fact a simple or composite fact model tree
                FactModelTree fact = createFactModelTreeForCollection(new HashMap<>(), entry.getKey(), entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED, alreadyVisited);
                simpleFields.put(entry.getKey(), List.class.getCanonicalName());
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
                } else {  // a simple type is just name -> type
                    simpleFields.put(entry.getKey(), typeName);
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
