/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TInputData;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AbstractDMNTest {

    protected DMNModel dmnModelLocal;

    protected static final String SIMPLE_INPUT_DATA_NAME_NO_COLLECTION = "SIMPLE_INPUT_DATA_NAME_NO_COLLECTION";
    protected static final String SIMPLE_INPUT_DATA_NAME_SIMPLE_COLLECTION_OF_SIMPLE = "SIMPLE_INPUT_DATA_NAME_SIMPLE_COLLECTION_OF_SIMPLE";
    protected static final String SIMPLE_INPUT_DATA_NAME_SIMPLE_COLLECTION_OF_COMPOSITE = "SIMPLE_INPUT_DATA_NAME_SIMPLE_COLLECTION_OF_COMPOSITE";
    protected static final String COMPOSITE_INPUT_DATA_NAME_COLLECTION = "COMPOSITE_INPUT_DATA_NAME_COLLECTION";
    protected static final String SIMPLE_DECISION_DATA_NAME = "SIMPLE_DECISION_DATA_NAME";
    protected static final String COMPOSITE_DECISION_DATA_NAME = "COMPOSITE_DECISION_DATA_NAME";
    protected static final String SIMPLE_TYPE_NAME = "string";
    protected static final String COMPOSITE_TYPE_NAME = "COMPOSITE_TYPE_NAME";
    protected static final String EXPANDABLE_PROPERTY_PHONENUMBERS = "phoneNumbers";
    protected static final String EXPANDABLE_PROPERTY_DETAILS = "details";
    protected static final String PHONENUMBER_NUMBER = "number";
    protected static final String PHONENUMBER_PREFIX = "prefix";
    protected static final String NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_CC8924B0-D729-4D70-9588-039B5824FFE9";
    protected static final String MODEL_NAME = "dmn-list";

    protected DMNType simpleTypeNoCollection;
    protected DMNType simpleTypeSimpleCollectionOfSimple;
    protected DMNType simpleTypeSimpleCollectionOfComposite;
    protected DMNType compositeTypeNoCollection;
    protected DMNType compositeTypeCollection;
    protected Set<InputDataNode> inputDataNodes;
    protected Set<DecisionNode> decisionNodes;
    protected Settings settingsLocal;

    protected void init() {
        settingsLocal = new Settings();
        settingsLocal.setType(ScenarioSimulationModel.Type.DMN);
        inputDataNodes = new HashSet<>();
        simpleTypeNoCollection = getSimpleNoCollection();
        InputDataNode inputDataNodeSimpleNoCollection = getInputDataNode(simpleTypeNoCollection, SIMPLE_INPUT_DATA_NAME_NO_COLLECTION);
        inputDataNodes.add(inputDataNodeSimpleNoCollection);
        simpleTypeSimpleCollectionOfSimple = getSimpleCollection();
        InputDataNode inputDataNodeSimpleCollectionOfSimple = getInputDataNode(simpleTypeSimpleCollectionOfSimple, SIMPLE_INPUT_DATA_NAME_SIMPLE_COLLECTION_OF_SIMPLE);
        inputDataNodes.add(inputDataNodeSimpleCollectionOfSimple);
        compositeTypeNoCollection = getSingleCompositeWithSimpleCollection();
        simpleTypeSimpleCollectionOfComposite = getSimpleCollection(compositeTypeNoCollection);
        InputDataNode inputDataNodeSimpleCollectionOfComposite = getInputDataNode(simpleTypeSimpleCollectionOfComposite, SIMPLE_INPUT_DATA_NAME_SIMPLE_COLLECTION_OF_COMPOSITE);
        inputDataNodes.add(inputDataNodeSimpleCollectionOfComposite);
        compositeTypeCollection = getCompositeCollection();
        InputDataNode inputDataNodeCompositeCollection = getInputDataNode(compositeTypeCollection, COMPOSITE_INPUT_DATA_NAME_COLLECTION);
        inputDataNodes.add(inputDataNodeCompositeCollection);

        decisionNodes = new HashSet<>();
        DecisionNode decisionNodeSimpleNoCollection = getDecisionNode(simpleTypeNoCollection, SIMPLE_DECISION_DATA_NAME);
        decisionNodes.add(decisionNodeSimpleNoCollection);
        DecisionNode decisionNodeCompositeNoCollection = getDecisionNode(compositeTypeNoCollection, COMPOSITE_DECISION_DATA_NAME);
        decisionNodes.add(decisionNodeCompositeNoCollection);
        setDmnModelLocal("dmn-list.dmn", NAMESPACE, MODEL_NAME);
    }

    protected void setDmnModelLocal(String resourceName, String namespace, String modelName) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resourceName, AbstractDMNTest.class);
        dmnModelLocal = runtime.getModel(namespace, modelName);
        assertThat(dmnModelLocal, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModelLocal.getMessages()), dmnModelLocal.hasErrors(), is(false));
    }

    protected InputDataNode getInputDataNode(DMNType dmnType, String name) {
        TInputData inputData = new TInputData();
        inputData.setName(name);
        InputDataNode toReturn = new InputDataNodeImpl(inputData, dmnType);
        return toReturn;
    }

    protected DecisionNode getDecisionNode(DMNType dmnType, String name) {
        TDecision decision = new TDecision();
        decision.setName(name);
        DecisionNode toReturn = new DecisionNodeImpl(decision, dmnType);
        return toReturn;
    }

    /**
     * Returns a <b>single</b> <code>SimpleTypeImpl</code>
     * @return
     */
    protected SimpleTypeImpl getSimpleNoCollection() {
        return new SimpleTypeImpl("simpleNameSpace", SIMPLE_TYPE_NAME, null);
    }

    /**
     * Returns a <b>string collection</b> <code>SimpleTypeImpl</code>
     * @return
     */
    protected SimpleTypeImpl getSimpleCollection() {
        // Single property collection retrieve
        SimpleTypeImpl simpleCollectionString = new SimpleTypeImpl("simpleNameSpace", SIMPLE_TYPE_NAME, null);
        simpleCollectionString.setCollection(true);
        return simpleCollectionString;
    }

    /**
     * Returns a <code>DMNType</code>  <b>collection</b> <code>SimpleTypeImpl</code>
     * @param typeOfCollection the <b>type</b> of this collection
     * @return
     */
    protected SimpleTypeImpl getSimpleCollection(DMNType typeOfCollection) {
        // Single property collection retrieve
        String name = typeOfCollection.getName() + "list";
        SimpleTypeImpl simpleCollectionString = new SimpleTypeImpl("simpleNameSpace", name, null);
        simpleCollectionString.setCollection(true);
        simpleCollectionString.setBaseType(typeOfCollection);
        return simpleCollectionString;
    }

    /**
     * Returns a <b>single</b>  <b>person</b> <code>CompositeTypeImpl</code> that in turns contains other <code>CompositeTypeImpl</code>s properties
     * @return
     */
    protected CompositeTypeImpl getSingleCompositeWithSimpleCollection() {
        // Complex object retrieve
        CompositeTypeImpl toReturn = new CompositeTypeImpl("compositeNameSpace", COMPOSITE_TYPE_NAME, null);

        CompositeTypeImpl phoneNumberComposite = getPhoneNumberComposite(false);

        CompositeTypeImpl detailsComposite = new CompositeTypeImpl(null, "tDetails", "tDetails");
        detailsComposite.addField("gender", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        detailsComposite.addField("weight", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));

        SimpleTypeImpl nameSimple = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);

        SimpleTypeImpl friendsSimpleCollection = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);
        friendsSimpleCollection.setCollection(true);

        toReturn.addField("friends", friendsSimpleCollection);
        toReturn.addField(EXPANDABLE_PROPERTY_PHONENUMBERS, phoneNumberComposite);
        toReturn.addField(EXPANDABLE_PROPERTY_DETAILS, detailsComposite);
        toReturn.addField("name", nameSimple);

        return toReturn;
    }

    /**
     * Returns a <b>single</b>  <b>person</b> <code>CompositeTypeImpl</code> that in turns contains other <code>CompositeTypeImpl</code>s properties
     * @return
     */
    protected CompositeTypeImpl getSingleCompositeWithNestedCollection() {
        // Complex object retrieve
        CompositeTypeImpl toReturn = new CompositeTypeImpl("compositeNameSpace", COMPOSITE_TYPE_NAME, null);

        CompositeTypeImpl phoneNumberCompositeCollection = new CompositeTypeImpl(null, "tPhoneNumber", null, true);
        phoneNumberCompositeCollection.addField(PHONENUMBER_PREFIX, new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        SimpleTypeImpl numbers = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);
        numbers.setCollection(true);
        phoneNumberCompositeCollection.addField("numbers", numbers);

        CompositeTypeImpl detailsComposite = new CompositeTypeImpl(null, "tDetails", "tDetails");
        detailsComposite.addField("gender", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        detailsComposite.addField("weight", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));

        SimpleTypeImpl nameSimple = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);

        SimpleTypeImpl friendsSimple = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);

        toReturn.addField("friends", friendsSimple);
        toReturn.addField(EXPANDABLE_PROPERTY_PHONENUMBERS, phoneNumberCompositeCollection);
        toReturn.addField(EXPANDABLE_PROPERTY_DETAILS, detailsComposite);
        toReturn.addField("name", nameSimple);

        return toReturn;
    }

    /**
     * Returns a <b>person collection</b> <code>CompositeTypeImpl</code> that in turns contains other <code>CompositeTypeImpl</code>s properties
     * @return
     */
    protected CompositeTypeImpl getCompositeCollection() {
        // Complex object retrieve
        CompositeTypeImpl toReturn = new CompositeTypeImpl("compositeNameSpace", COMPOSITE_TYPE_NAME, null, true);
        CompositeTypeImpl phoneNumberCompositeCollection = getPhoneNumberComposite(true);

        CompositeTypeImpl detailsComposite = new CompositeTypeImpl(null, "tDetails", "tDetails");
        detailsComposite.addField("gender", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        detailsComposite.addField("weight", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));

        SimpleTypeImpl nameSimple = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);

        SimpleTypeImpl friendsSimpleCollection = new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null);
        friendsSimpleCollection.setCollection(true);

        toReturn.addField("friends", friendsSimpleCollection);
        toReturn.addField(EXPANDABLE_PROPERTY_PHONENUMBERS, phoneNumberCompositeCollection);
        toReturn.addField(EXPANDABLE_PROPERTY_DETAILS, detailsComposite);
        toReturn.addField("name", nameSimple);

        return toReturn;
    }

    /**
     * Returns a <b>phone number</b> <code>CompositeTypeImpl</code>
     * @return
     */
    protected CompositeTypeImpl getPhoneNumberComposite(boolean isCollection) {
        CompositeTypeImpl toReturn = new CompositeTypeImpl(null, "tPhoneNumber", null, isCollection);
        toReturn.addField(PHONENUMBER_PREFIX, new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        toReturn.addField(PHONENUMBER_NUMBER, new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        return toReturn;
    }

    /**
     * Returns a recursive <b>person</b> <code>CompositeTypeImpl</code>
     * @param isCollection
     * @return
     */
    protected CompositeTypeImpl getRecursivePersonComposite(boolean isCollection) {
        CompositeTypeImpl toReturn = new CompositeTypeImpl(null, "tPerson", null, isCollection);
        CompositeTypeImpl tPersonList = new CompositeTypeImpl(null, "tPersonList", null, true, new HashMap<>(), toReturn, null);

        toReturn.addField("name", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        toReturn.addField("parent", toReturn);
        toReturn.addField("ancestors", tPersonList);

        tPersonList.addField("name", new SimpleTypeImpl(null, SIMPLE_TYPE_NAME, null));
        tPersonList.addField("parent", toReturn);
        tPersonList.addField("ancestors", tPersonList);

        return toReturn;
    }
}